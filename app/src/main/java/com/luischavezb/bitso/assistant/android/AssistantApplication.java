package com.luischavezb.bitso.assistant.android;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.widget.Toast;

import com.luischavezb.bitso.assistant.android.db.DbContract;
import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.shashank.sony.fancytoastlib.FancyToast;

/**
 * Created by luischavez on 28/02/18.
 */

public class AssistantApplication extends Application {

    public static final String FOREGROUND_CHANNEL_ID = "foreground_channel";
    public static final String TICKERS_CHANNEL_ID = "tickers_channel";

    public static final String LT_CHANNEL_ID = "alarm_lt_channel";
    public static final String EQ_CHANNEL_ID = "alarm_eq_channel";
    public static final String GT_CHANNEL_ID = "alarm_gt_channel";
    public static final String BUY_CHANNEL_ID = "alarm_buy_channel";
    public static final String SELL_CHANNEL_ID = "alarm_sell_channel";

    public static String sApiUrl;

    public static boolean sDebug;
    public static boolean sPremium;

    private static Context sContext;

    public static String appVersion() throws PackageManager.NameNotFoundException {
        Context context = getContext();

        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return packageInfo.versionName;
    }

    public static void createForegroundNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Service.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = notificationManager.getNotificationChannel(FOREGROUND_CHANNEL_ID);
            if (null == notificationChannel) {
                notificationChannel = new NotificationChannel(
                        FOREGROUND_CHANNEL_ID,
                        getContext().getString(R.string.notification_foreground),
                        NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setSound(null, null);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public static void createTickersNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Service.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = notificationManager.getNotificationChannel(TICKERS_CHANNEL_ID);
            if (null == notificationChannel) {
                notificationChannel = new NotificationChannel(
                        TICKERS_CHANNEL_ID,
                        getContext().getString(R.string.notification_tickers),
                        NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setSound(null, null);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public static void createLtNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Service.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = notificationManager.getNotificationChannel(LT_CHANNEL_ID);
            if (null == notificationChannel) {
                notificationChannel = new NotificationChannel(
                        LT_CHANNEL_ID,
                        getContext().getString(R.string.notification_alarm_lt),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public static void createEqNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Service.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = notificationManager.getNotificationChannel(EQ_CHANNEL_ID);
            if (null == notificationChannel) {
                notificationChannel = new NotificationChannel(
                        EQ_CHANNEL_ID,
                        getContext().getString(R.string.notification_alarm_eq),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public static void createGtNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Service.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = notificationManager.getNotificationChannel(GT_CHANNEL_ID);
            if (null == notificationChannel) {
                notificationChannel = new NotificationChannel(
                        GT_CHANNEL_ID,
                        getContext().getString(R.string.notification_alarm_gt),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public static void createBuyNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Service.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = notificationManager.getNotificationChannel(BUY_CHANNEL_ID);
            if (null == notificationChannel) {
                notificationChannel = new NotificationChannel(
                        BUY_CHANNEL_ID,
                        getContext().getString(R.string.notification_alarm_buy),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    public static void createSellNotificationChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Service.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel
                    = notificationManager.getNotificationChannel(SELL_CHANNEL_ID);
            if (null == notificationChannel) {
                notificationChannel = new NotificationChannel(
                        SELL_CHANNEL_ID,
                        getContext().getString(R.string.notification_alarm_sell),
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = this;

        DbHelper dbHelper = DbHelper.getInstance(this);

        String testLabSetting = Settings.System.getString(getContentResolver(), "firebase.test.lab");
        if (BuildConfig.DEBUG || "true".equals(testLabSetting)) {
            sDebug = true;
            sApiUrl = getString(R.string.api_dev_base_url);

            FancyToast.makeText(this, getString(R.string.debug_on),
                    Toast.LENGTH_SHORT, FancyToast.INFO, false)
                    .show();
        } else {
            sDebug = true;
            sApiUrl = getString(R.string.api_dev_base_url);
        }

        String premium = dbHelper.readConfigurationField(DbContract.ConfigurationEntry.COLUMN_NAME_PREMIUM);

        if (null == premium) {
            DbHelper.getInstance().storeConfigurationField(DbContract.ConfigurationEntry.COLUMN_NAME_PREMIUM, false);
            premium = "false";
        }

        sPremium = Boolean.valueOf(premium);

        AndroidAssistant.getInstance(this, sApiUrl);

        createForegroundNotificationChannel();
        createTickersNotificationChannel();
        createLtNotificationChannel();
        createEqNotificationChannel();
        createGtNotificationChannel();
        createBuyNotificationChannel();
        createSellNotificationChannel();
    }

    public static Context getContext() {
        return sContext;
    }
}
