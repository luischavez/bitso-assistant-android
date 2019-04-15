package com.luischavezb.bitso.assistant.android;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.v4.app.NotificationCompat;

import com.geometrycloud.bitso.assistant.library.Assistant;
import com.geometrycloud.bitso.assistant.library.Balance;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Data;
import com.geometrycloud.bitso.assistant.library.Fee;
import com.geometrycloud.bitso.assistant.library.Profile;
import com.geometrycloud.bitso.assistant.library.Ticker;
import com.luischavezb.bitso.assistant.android.alarm.Alarm;
import com.luischavezb.bitso.assistant.android.db.DbContract;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luischavez on 15/02/18.
 */

public class AndroidAssistant extends Assistant {

    private static final Logger LOGGER = LoggerFactory.getLogger(AndroidAssistant.class);

    private static final String OLD_DATA_FILE = "old_data.assistant";
    private static final String DATA_FILE = "data.assistant";

    private final Context mContext;

    private boolean mValidCredentials = false;
    private Bitso.Credentials mCredentials;

    private AndroidAssistant(Context context, String baseUrl) {
        super(baseUrl);

        this.mContext = context;
    }

    @Override
    public Bitso.Credentials loadCredentials() {
        if (!mValidCredentials) {
            DbHelper dbHelper = DbHelper.getInstance();

            String key = dbHelper.readConfigurationField(
                    DbContract.ConfigurationEntry.COLUMN_NAME_API_KEY);
            String secret = dbHelper.readConfigurationField(
                    DbContract.ConfigurationEntry.COLUMN_NAME_API_SECRET);

            if (null != key && null != secret) {
                mCredentials = new Bitso.Credentials(key, secret);
            }
        }

        if (null == mCredentials) {
            mCredentials = new Bitso.Credentials("key", "secret");
        }

        return mCredentials;
    }

    private static void copyFile(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    @Override
    protected Data readData() {
        try (ObjectInputStream inputStream = new ObjectInputStream(mContext.openFileInput(DATA_FILE))) {
            return (Data) inputStream.readObject();
        } catch (Exception ex) {
            LOGGER.error("", ex);

            try (ObjectInputStream inputStream = new ObjectInputStream(mContext.openFileInput(OLD_DATA_FILE))) {
                Data data = (Data) inputStream.readObject();

                List<Profile> profiles = data.getProfiles();
                for (Profile profile : profiles) {
                    profile.setEnabled(false);
                }
            } catch (Exception ex2) {
                LOGGER.error("", ex2);
            }
        }

        return new Data();
    }

    @Override
    protected void saveData(Data data) {
        File rootDir = mContext.getExternalFilesDir(null);

        File dataFile = new File(rootDir, DATA_FILE);
        File oldDataFile = new File(rootDir, OLD_DATA_FILE);

        if (dataFile.exists()) {
            if (oldDataFile.exists()) {
                oldDataFile.delete();
            }

            try {
                copyFile(dataFile, oldDataFile);
            } catch (IOException ex) {
                LOGGER.error("", ex);
            }
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(mContext.openFileOutput(DATA_FILE, Context.MODE_PRIVATE))) {
            outputStream.writeObject(data);
            outputStream.flush();
        } catch (Exception ex) {
            LOGGER.error("", ex);
        }
    }

    @Override
    public void onError(String code, String message) {
        super.onError(code, message);

        if (Bitso.INVALID_NAME_OR_INVALID_CREDENTIALS.equals(code)) {
            mValidCredentials = false;
        }
    }

    private void showNotification(String tag, int id, @StringRes int noticeId, @StringRes int soundId, Object... format) {
        String channelId = "";

        switch (tag) {
            case "less":
                channelId = AssistantApplication.LT_CHANNEL_ID;
                AssistantApplication.createLtNotificationChannel();
                break;
            case "same":
                channelId = AssistantApplication.EQ_CHANNEL_ID;
                AssistantApplication.createEqNotificationChannel();
                break;
            case "greater":
                channelId = AssistantApplication.GT_CHANNEL_ID;
                AssistantApplication.createGtNotificationChannel();
                break;
            case "buy":
                channelId = AssistantApplication.BUY_CHANNEL_ID;
                AssistantApplication.createBuyNotificationChannel();
                break;
            case "sell":
                channelId = AssistantApplication.SELL_CHANNEL_ID;
                AssistantApplication.createSellNotificationChannel();
                break;
        }

        NotificationManager notificationManager =
                (NotificationManager) mContext.getSystemService(Service.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, channelId);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String soundUri = preferences.getString(mContext.getString(soundId), "");

        builder
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .setColor(mContext.getResources().getColor(R.color.color_primary))
                .setContentText(mContext.getString(noticeId, format))
                .setSmallIcon(R.drawable.ic_alarm)
                .setGroup(tag)
                .setGroupSummary(true);

        if (!soundUri.isEmpty()) {
            builder.setSound(Uri.parse(soundUri));
        }

        notificationManager.notify(tag, id, builder.build());
    }

    @Override
    public void onTickers(List<Ticker> tickers) {
        super.onTickers(tickers);

        List<Alarm> alarms = DbHelper.getInstance().readAlarms();

        if (null != alarms) {
            for (Alarm alarm : alarms) {
                if (!alarm.isEnabled()) continue;

                Bitso.Book book = alarm.getBook();
                BigDecimal value = new BigDecimal(
                        null != alarm.getValue() && !alarm.getValue().isEmpty() ? alarm.getValue() : "0");

                switch (alarm.getCondition()) {
                    case LESS_THAN:
                        for (Ticker ticker : tickers) {
                            if (ticker.getBook().equals(book)) {
                                if (0 > ticker.getLast().compareTo(value)) {
                                    showNotification("less", 1,
                                            R.string.less_than_notice, R.string.configuration_notification_lt_sound,
                                            book.name(),
                                            Utilities.currencyFormat(value, book.minorCoin(), true));
                                }
                                break;
                            }
                        }
                        break;
                    case SAME:
                        for (Ticker ticker : tickers) {
                            if (ticker.getBook().equals(book)) {
                                if (0 == ticker.getLast().compareTo(value)) {
                                    showNotification("same", 2,
                                            R.string.same_notice, R.string.configuration_notification_eq_sound,
                                            book.name(),
                                            Utilities.currencyFormat(value, book.minorCoin(), true));
                                }
                                break;
                            }
                        }
                        break;
                    case GREATER_THAN:
                        for (Ticker ticker : tickers) {
                            if (ticker.getBook().equals(book)) {
                                if (0 < ticker.getLast().compareTo(value)) {
                                    showNotification("greater", 3,
                                            R.string.greater_than_notice, R.string.configuration_notification_gt_sound,
                                            book.name(),
                                            Utilities.currencyFormat(value, book.minorCoin(), true));
                                }
                                break;
                            }
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void onPlaceBuy(Profile profile, String oid, BigDecimal amount, BigDecimal price, Balance majorBalance, Balance minorBalance, Fee fee) {
        super.onPlaceBuy(profile, oid, amount, price, majorBalance, minorBalance, fee);

        Bitso.Book book = profile.getBook();

        showNotification("buy", 4,
                R.string.automatic_buy_notice, R.string.configuration_notification_buy_sound,
                book.name(),
                Utilities.currencyFormat(price, book.minorCoin(), true));
    }

    @Override
    public void onPlaceSell(Profile profile, String oid, BigDecimal amount, BigDecimal price, Balance majorBalance, Balance minorBalance, Fee fee) {
        super.onPlaceSell(profile, oid, amount, price, majorBalance, minorBalance, fee);

        Bitso.Book book = profile.getBook();

        showNotification("sell", 5,
                R.string.automatic_sell_notice, R.string.configuration_notification_sell_sound,
                book.name(),
                Utilities.currencyFormat(price, book.minorCoin(), true));
    }

    public static Assistant getInstance(Context context, String baseUrl) {
        if (null == AssistantHolder.assistant) {
            AssistantHolder.assistant = new AndroidAssistant(context, baseUrl);
        }

        return AssistantHolder.assistant;
    }

    public static Assistant getInstance() {
        return getInstance(null, null);
    }

    private static class AssistantHolder {

        private static Assistant assistant;
    }
}
