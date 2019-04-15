package com.luischavezb.bitso.assistant.android.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Ticker;
import com.luischavezb.bitso.assistant.android.AndroidAssistant;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.Utilities;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by luischavez on 28/03/18.
 */

public class TickerService extends Service {

    public class LocalBinder extends Binder {

        public TickerService getService() {
            return TickerService.this;
        }
    }

    public static class TickerEvent {

        private final List<Ticker> mTickers;

        public TickerEvent(List<Ticker> mTickers) {
            this.mTickers = mTickers;
        }

        public List<Ticker> tickers() {
            return mTickers;
        }
    }

    private static final String INTENT_FILTER_STOP_SERVICE = "com.luischavezb.bitso.assistant.android.service.STOP";

    private static final int FOREGROUND_ID = 1000;

    private PowerManager.WakeLock mWakeLock;

    private boolean mForeground = false;

    private final IBinder mBinder = new LocalBinder();

    private boolean mUseProfiles = false;

    private List<Ticker> mTickers;
    private Set<String> mEnabledTickers;

    private Timer mTimer;

    private BroadcastReceiver mStopServiceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mBinder.isBinderAlive()) {
                stopTicker();
            }

            cancelTickerNotifications();
            stopForeground(true);
            stopSelf();

            mForeground = false;
        }
    };

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, AssistantApplication.FOREGROUND_CHANNEL_ID);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(INTENT_FILTER_STOP_SERVICE), PendingIntent.FLAG_UPDATE_CURRENT);

        String title = getString(R.string.service_notification_title);
        String content = getString(R.string.service_notification_content);

        if (mUseProfiles) {
            title = getString(R.string.service_notification_profiles,
                    AndroidAssistant.getInstance().getData().getProfiles().size());
        }

        builder
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setColor(getResources().getColor(R.color.color_primary))
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logo)
                .setGroup("foreground")
                .setGroupSummary(true);

        return builder.build();
    }

    private Notification buildTickerNotification(Ticker ticker) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, AssistantApplication.TICKERS_CHANNEL_ID);

        String major = ticker.getBook().majorCoin();
        String minor = ticker.getBook().minorCoin();

        String content = Utilities.currencyFormat(ticker.getLast(), minor, true);

        builder
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setColor(getResources().getColor(Utilities.color(major)))
                .setContentTitle(ticker.getBook().name())
                .setContentText(content)
                .setSmallIcon(Utilities.icon(major))
                .setGroup(ticker.getBook().name())
                .setGroupSummary(true);

        return builder.build();
    }

    private void showForegroundNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        AssistantApplication.createForegroundNotificationChannel();

        notificationManager.notify(FOREGROUND_ID, buildForegroundNotification());
    }

    private void showTickerNotification(Ticker ticker) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        AssistantApplication.createTickersNotificationChannel();

        notificationManager.notify(ticker.getBook().name(), 2000, buildTickerNotification(ticker));
    }

    private void cancelTickerNotification(String bookName) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        notificationManager.cancel(bookName, 2000);
    }

    private void cancelTickerNotifications() {
        for (Bitso.Book book : Bitso.Book.values()) {
            cancelTickerNotification(book.name());
        }
    }

    private void handleTickers() {
        if (null == mTickers) return;

        if (null != mEnabledTickers && !mEnabledTickers.isEmpty()) {
            if (!mForeground) {
                startService(new Intent(this, TickerService.class));
                startForeground(FOREGROUND_ID, buildForegroundNotification());
                mForeground = true;
            }
        } else if (!mUseProfiles) {
            stopForeground(true);
            stopService(new Intent(this, TickerService.class));
            mForeground = false;
        }

        ArrayList<String> disabledTickers = new ArrayList<>();

        if (mTickers.isEmpty()) {
            cancelTickerNotifications();
        } else {
            for (Ticker ticker : mTickers) {
                boolean enabled = false;

                if (null != mEnabledTickers) {
                    for (String enabledTicker : mEnabledTickers) {
                        if (ticker.getBook().name().equals(enabledTicker)) {
                            enabled = true;

                            showTickerNotification(ticker);

                            break;
                        }
                    }
                }

                if (!enabled) {
                    disabledTickers.add(ticker.getBook().name());
                }
            }

            for (String disabledTicker : disabledTickers) {
                cancelTickerNotification(disabledTicker);
            }
        }
    }

    public boolean useProfiles() {
        return mUseProfiles;
    }

    public void useProfiles(boolean useProfiles) {
        mUseProfiles = useProfiles;

        if (mUseProfiles) {
            if (!mForeground) {
                startService(new Intent(this, TickerService.class));
                startForeground(FOREGROUND_ID, buildForegroundNotification());
                mForeground = true;
            } else {
                showForegroundNotification();
            }
        } else {
            if (null == mEnabledTickers || mEnabledTickers.isEmpty()) {
                stopForeground(true);
                stopService(new Intent(this, TickerService.class));
                mForeground = false;
            } else {
                showForegroundNotification();
            }
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putBoolean(getString(R.string.use_profiles), useProfiles).commit();
    }

    public void updateTickers() {
        mEnabledTickers = PreferenceManager.getDefaultSharedPreferences(this)
                .getStringSet(getString(R.string.configuration_tickers), new HashSet<String>());

        handleTickers();
    }

    public void stopTicker() {
        if (null != mTimer) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void startTicker() {
        stopTicker();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int interval = Integer.valueOf(preferences.getString(getString(R.string.configuration_update_interval), "20"));

        mTimer = new Timer();
        mTimer.schedule(new TickerTask(), 0, TimeUnit.SECONDS.toMillis(interval));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerReceiver(mStopServiceBroadcastReceiver, new IntentFilter(INTENT_FILTER_STOP_SERVICE));

        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);

        mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TickerService.class.getName());

        updateTickers();
        startTicker();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        boolean useProfiles = preferences.getBoolean(getString(R.string.use_profiles), false);
        useProfiles(useProfiles);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mStopServiceBroadcastReceiver);

        stopTicker();

        cancelTickerNotifications();
        stopForeground(true);
        stopSelf();

        super.onDestroy();
    }

    private class TickerTask extends TimerTask {

        private void doWork() {
            if (null != mWakeLock) {
                mWakeLock.acquire();
            }

            mTickers = AndroidAssistant.getInstance().tick(mUseProfiles);

            if (null != mTickers) {
                EventBus.getDefault().post(new TickerService.TickerEvent(mTickers));
                DbHelper.getInstance().storeTickers(mTickers);

                handleTickers();
            }

            if (null != mWakeLock) {
                mWakeLock.release();
            }
        }

        @Override
        public void run() {
            doWork();
        }
    }
}
