package com.luischavezb.bitso.assistant.android.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.geometrycloud.bitso.assistant.library.AccountStatus;
import com.geometrycloud.bitso.assistant.library.Balance;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Funding;
import com.geometrycloud.bitso.assistant.library.FundingDestination;
import com.geometrycloud.bitso.assistant.library.History;
import com.geometrycloud.bitso.assistant.library.Order;
import com.geometrycloud.bitso.assistant.library.Ticker;
import com.geometrycloud.bitso.assistant.library.Trade;
import com.geometrycloud.bitso.assistant.library.Utilities;
import com.geometrycloud.bitso.assistant.library.Withdrawal;
import com.luischavezb.bitso.assistant.android.alarm.Alarm;
import com.luischavezb.bitso.assistant.android.alarm.Condition;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by luischavez on 01/03/18.
 */

public class DbHandler {

    public static class ConfigurationChangeEvent {

        private final String mField;
        private final Object mValue;

        public ConfigurationChangeEvent(String field, Object value) {
            mField = field;
            mValue = value;
        }

        public String getField() {
            return mField;
        }

        public Object getValue() {
            return mValue;
        }
    }

    public AccountStatus readAccountStatus(DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.AccountEntry.TABLE_NAME,
                new String[]{
                        DbContract.AccountEntry._ID,
                        DbContract.AccountEntry.COLUMN_NAME_CLIENT_ID,
                        DbContract.AccountEntry.COLUMN_NAME_FIRST_NAME,
                        DbContract.AccountEntry.COLUMN_NAME_LAST_NAME,
                        DbContract.AccountEntry.COLUMN_NAME_STATUS,
                        DbContract.AccountEntry.COLUMN_NAME_DAILY_LIMIT,
                        DbContract.AccountEntry.COLUMN_NAME_MONTHLY_LIMIT,
                        DbContract.AccountEntry.COLUMN_NAME_DAILY_REMAINING,
                        DbContract.AccountEntry.COLUMN_NAME_MONTHLY_REMAINING,
                        DbContract.AccountEntry.COLUMN_NAME_CELLPHONE_NUMBER,
                        DbContract.AccountEntry.COLUMN_NAME_CELLPHONE_NUMBER_STORED,
                        DbContract.AccountEntry.COLUMN_NAME_EMAIL_STORED,
                        DbContract.AccountEntry.COLUMN_NAME_OFFICIAL_ID,
                        DbContract.AccountEntry.COLUMN_NAME_PROOF_OF_RESIDENCY,
                        DbContract.AccountEntry.COLUMN_NAME_SIGNED_CONTRACT,
                        DbContract.AccountEntry.COLUMN_NAME_ORIGIN_OF_FUNDS
                },
                null, null,
                null, null, null);

        AccountStatus accountStatus = null;

        if (cursor.moveToNext()) {
            long clientId = Long.valueOf(cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_CLIENT_ID)));
            String firstName = cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_FIRST_NAME));
            String lastName = cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_LAST_NAME));
            String status = cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_STATUS));
            BigDecimal dailyLimit = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_DAILY_LIMIT)));
            BigDecimal monthlyLimit = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_MONTHLY_LIMIT)));
            BigDecimal dailyRemaining = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_DAILY_REMAINING)));
            BigDecimal monthlyRemaining = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_MONTHLY_REMAINING)));
            String cellphoneNumber = cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_CELLPHONE_NUMBER));
            String cellphoneNumberStored = cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_CELLPHONE_NUMBER_STORED));
            String emailStored = cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_EMAIL_STORED));
            String officialId = cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_OFFICIAL_ID));
            String proofOfResidency = cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_PROOF_OF_RESIDENCY));
            String signedContract = cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_SIGNED_CONTRACT));
            String originOfFunds = cursor.getString(
                    cursor.getColumnIndex(DbContract.AccountEntry.COLUMN_NAME_ORIGIN_OF_FUNDS));

            accountStatus = new AccountStatus(clientId, firstName, lastName, status,
                    dailyLimit, monthlyLimit, dailyRemaining, monthlyRemaining,
                    cellphoneNumber, cellphoneNumberStored, emailStored,
                    officialId, proofOfResidency, signedContract, originOfFunds);
        }

        cursor.close();

        return accountStatus;
    }

    public void storeAccountStatus(DbHelper dbHelper, AccountStatus accountStatus) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.AccountEntry.COLUMN_NAME_CLIENT_ID, accountStatus.getClientId());
        values.put(DbContract.AccountEntry.COLUMN_NAME_FIRST_NAME, accountStatus.getFirstName());
        values.put(DbContract.AccountEntry.COLUMN_NAME_LAST_NAME, accountStatus.getLastName());
        values.put(DbContract.AccountEntry.COLUMN_NAME_STATUS, accountStatus.getStatus());
        values.put(DbContract.AccountEntry.COLUMN_NAME_DAILY_LIMIT, accountStatus.getDailyLimit().toPlainString());
        values.put(DbContract.AccountEntry.COLUMN_NAME_MONTHLY_LIMIT, accountStatus.getMonthlyLimit().toPlainString());
        values.put(DbContract.AccountEntry.COLUMN_NAME_DAILY_REMAINING, accountStatus.getDailyRemaining().toPlainString());
        values.put(DbContract.AccountEntry.COLUMN_NAME_MONTHLY_REMAINING, accountStatus.getMonthlyRemaining().toPlainString());
        values.put(DbContract.AccountEntry.COLUMN_NAME_CELLPHONE_NUMBER, accountStatus.getCellphoneNumber());
        values.put(DbContract.AccountEntry.COLUMN_NAME_CELLPHONE_NUMBER_STORED, accountStatus.getCellphoneNumberStored());
        values.put(DbContract.AccountEntry.COLUMN_NAME_EMAIL_STORED, accountStatus.getEmailStored());
        values.put(DbContract.AccountEntry.COLUMN_NAME_OFFICIAL_ID, accountStatus.getOfficialId());
        values.put(DbContract.AccountEntry.COLUMN_NAME_PROOF_OF_RESIDENCY, accountStatus.getProofOfResidency());
        values.put(DbContract.AccountEntry.COLUMN_NAME_SIGNED_CONTRACT, accountStatus.getSignedContract());
        values.put(DbContract.AccountEntry.COLUMN_NAME_ORIGIN_OF_FUNDS, accountStatus.getOriginOfFunds());

        database.delete(DbContract.AccountEntry.TABLE_NAME, null, null);
        database.insert(DbContract.AccountEntry.TABLE_NAME, null, values);
    }

    public void storeConfiguration(DbHelper dbHelper, String key, String secret, String phrase) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.ConfigurationEntry.COLUMN_NAME_API_KEY, key);
        values.put(DbContract.ConfigurationEntry.COLUMN_NAME_API_SECRET, secret);
        values.put(DbContract.ConfigurationEntry.COLUMN_NAME_NIP_PHRASE_ENCODED, phrase);

        database.delete(DbContract.ConfigurationEntry.TABLE_NAME, null, null);
        database.insert(DbContract.ConfigurationEntry.TABLE_NAME, null, values);
    }

    public String readConfigurationField(DbHelper dbHelper, String field) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                DbContract.ConfigurationEntry.TABLE_NAME,
                new String[]{
                        field
                },
                null, null,
                null, null, null);

        String value = null;

        if (cursor.moveToNext()) {
            value = cursor.getString(cursor.getColumnIndex(field));
        }

        cursor.close();

        return value;
    }

    public void storeConfigurationField(DbHelper dbHelper, String field, Object value) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(field, String.valueOf(value));

        database.update(DbContract.ConfigurationEntry.TABLE_NAME, values, null, null);

        EventBus.getDefault().post(new ConfigurationChangeEvent(field, value));
    }

    public List<Alarm> readAlarms(DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                DbContract.AlarmEntry.TABLE_NAME,
                new String[]{
                        DbContract.AlarmEntry._ID,
                        DbContract.AlarmEntry.COLUMN_NAME_ENABLED,
                        DbContract.AlarmEntry.COLUMN_NAME_BOOK,
                        DbContract.AlarmEntry.COLUMN_NAME_CONDITION,
                        DbContract.AlarmEntry.COLUMN_NAME_VALUE
                },
                null, null,
                null, null, null);

        ArrayList<Alarm> alarms = new ArrayList<>();

        while (cursor.moveToNext()) {
            Long id = cursor.getLong(cursor.getColumnIndex(DbContract.AlarmEntry._ID));
            boolean enabled = "1".equals(
                    cursor.getString(cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_ENABLED)));
            Bitso.Book book = Bitso.Book.valueOf(cursor.getString(
                    cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_BOOK)));
            String condition = cursor.getString(
                    cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_CONDITION));
            String value = cursor.getString(
                    cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_VALUE));

            alarms.add(new Alarm(id, enabled, book, Condition.valueOf(condition), value));
        }

        cursor.close();

        return alarms.isEmpty() ? null : alarms;
    }

    public Alarm readAlarm(DbHelper dbHelper, Long id) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                DbContract.AlarmEntry.TABLE_NAME,
                new String[]{
                        DbContract.AlarmEntry._ID,
                        DbContract.AlarmEntry.COLUMN_NAME_ENABLED,
                        DbContract.AlarmEntry.COLUMN_NAME_BOOK,
                        DbContract.AlarmEntry.COLUMN_NAME_CONDITION,
                        DbContract.AlarmEntry.COLUMN_NAME_VALUE
                },
                DbContract.AlarmEntry._ID + " =?", new String[]{String.valueOf(id)},
                null, null, null);

        Alarm alarm = null;

        if (cursor.moveToNext()) {
            boolean enabled = "1".equals(
                    cursor.getString(cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_ENABLED)));
            Bitso.Book book = Bitso.Book.valueOf(cursor.getString(
                    cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_BOOK)));
            String condition = cursor.getString(
                    cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_CONDITION));
            String value = cursor.getString(
                    cursor.getColumnIndex(DbContract.AlarmEntry.COLUMN_NAME_VALUE));

            alarm = new Alarm(id, enabled, book, Condition.valueOf(condition), value);
        }

        cursor.close();

        return alarm;
    }

    public void storeAlarm(DbHelper dbHelper, Alarm alarm) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.AlarmEntry.COLUMN_NAME_ENABLED, alarm.isEnabled());
        values.put(DbContract.AlarmEntry.COLUMN_NAME_BOOK, alarm.getBook().name());
        values.put(DbContract.AlarmEntry.COLUMN_NAME_CONDITION, alarm.getCondition().name());
        values.put(DbContract.AlarmEntry.COLUMN_NAME_VALUE, alarm.getValue());

        if (null == alarm.getId()) {
            database.insert(DbContract.AlarmEntry.TABLE_NAME, null, values);
        } else {
            String id = String.valueOf(alarm.getId());

            database.update(DbContract.AlarmEntry.TABLE_NAME, values,
                    DbContract.AlarmEntry._ID + " =?", new String[]{id});
        }
    }

    public void deleteAlarm(DbHelper dbHelper, Alarm alarm) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String id = String.valueOf(alarm.getId());

        database.delete(DbContract.AlarmEntry.TABLE_NAME,
                DbContract.AlarmEntry._ID + " =?", new String[]{id});
    }

    public List<Ticker> readTickers(DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                DbContract.TickerEntry.TABLE_NAME,
                new String[]{
                        DbContract.TickerEntry._ID,
                        DbContract.TickerEntry.COLUMN_NAME_BOOK,
                        DbContract.TickerEntry.COLUMN_NAME_VOLUME,
                        DbContract.TickerEntry.COLUMN_NAME_VWAP,
                        DbContract.TickerEntry.COLUMN_NAME_LOW,
                        DbContract.TickerEntry.COLUMN_NAME_HIGH,
                        DbContract.TickerEntry.COLUMN_NAME_ASK,
                        DbContract.TickerEntry.COLUMN_NAME_BID,
                        DbContract.TickerEntry.COLUMN_NAME_LAST,
                        DbContract.TickerEntry.COLUMN_NAME_CREATED_AT,
                },
                null, null,
                null, null, null);

        ArrayList<Ticker> tickers = new ArrayList<>();

        while (cursor.moveToNext()) {
            Bitso.Book book = Bitso.Book.valueOf(cursor.getString(
                    cursor.getColumnIndex(DbContract.TickerEntry.COLUMN_NAME_BOOK)));
            BigDecimal volume = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TickerEntry.COLUMN_NAME_VOLUME)));
            BigDecimal vwap = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TickerEntry.COLUMN_NAME_VWAP)));
            BigDecimal low = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TickerEntry.COLUMN_NAME_LOW)));
            BigDecimal high = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TickerEntry.COLUMN_NAME_HIGH)));
            BigDecimal ask = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TickerEntry.COLUMN_NAME_ASK)));
            BigDecimal bid = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TickerEntry.COLUMN_NAME_BID)));
            BigDecimal last = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TickerEntry.COLUMN_NAME_LAST)));
            Date createdAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.TickerEntry.COLUMN_NAME_CREATED_AT)));

            tickers.add(new Ticker(book, volume, vwap, low, high, ask, bid, last, createdAt));
        }

        cursor.close();

        return tickers.isEmpty() ? null : tickers;
    }

    public void storeTickers(DbHelper dbHelper, List<Ticker> tickers) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DbContract.TickerEntry.TABLE_NAME, null, null);

        for (Ticker ticker : tickers) {
            ContentValues values = new ContentValues();
            values.put(DbContract.TickerEntry.COLUMN_NAME_BOOK, ticker.getBook().name());
            values.put(DbContract.TickerEntry.COLUMN_NAME_VOLUME, ticker.getVolume().toPlainString());
            values.put(DbContract.TickerEntry.COLUMN_NAME_VWAP, ticker.getVwap().toPlainString());
            values.put(DbContract.TickerEntry.COLUMN_NAME_LOW, ticker.getLow().toPlainString());
            values.put(DbContract.TickerEntry.COLUMN_NAME_HIGH, ticker.getHigh().toPlainString());
            values.put(DbContract.TickerEntry.COLUMN_NAME_ASK, ticker.getAsk().toPlainString());
            values.put(DbContract.TickerEntry.COLUMN_NAME_BID, ticker.getBid().toPlainString());
            values.put(DbContract.TickerEntry.COLUMN_NAME_LAST, ticker.getLast().toPlainString());
            values.put(DbContract.TickerEntry.COLUMN_NAME_CREATED_AT, Utilities.formatDateTime(ticker.getCreatedAt()));

            database.insert(DbContract.TickerEntry.TABLE_NAME, null, values);
        }
    }

    public List<Balance> readBalances(DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query(
                DbContract.BalanceEntry.TABLE_NAME,
                new String[]{
                        DbContract.BalanceEntry._ID,
                        DbContract.BalanceEntry.COLUMN_NAME_CURRENCY,
                        DbContract.BalanceEntry.COLUMN_NAME_AVAILABLE,
                        DbContract.BalanceEntry.COLUMN_NAME_LOCKED,
                        DbContract.BalanceEntry.COLUMN_NAME_TOTAL
                },
                null, null,
                null, null, null);

        ArrayList<Balance> balances = new ArrayList<>();

        while (cursor.moveToNext()) {
            String currency = cursor.getString(
                    cursor.getColumnIndex(DbContract.BalanceEntry.COLUMN_NAME_CURRENCY));
            BigDecimal available = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.BalanceEntry.COLUMN_NAME_AVAILABLE)));
            BigDecimal locked = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.BalanceEntry.COLUMN_NAME_LOCKED)));
            BigDecimal total = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.BalanceEntry.COLUMN_NAME_TOTAL)));

            balances.add(new Balance(currency, available, locked, total));
        }

        cursor.close();

        return balances.isEmpty() ? null : balances;
    }

    public void storeBalances(DbHelper dbHelper, List<Balance> balances) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DbContract.BalanceEntry.TABLE_NAME, null, null);

        for (Balance balance : balances) {
            BigDecimal total = balance.getTotal();

            ContentValues values = new ContentValues();
            values.put(DbContract.BalanceEntry.COLUMN_NAME_CURRENCY, balance.getCurrency());
            values.put(DbContract.BalanceEntry.COLUMN_NAME_AVAILABLE, balance.getAvailable().toPlainString());
            values.put(DbContract.BalanceEntry.COLUMN_NAME_LOCKED, balance.getLocked().toPlainString());
            values.put(DbContract.BalanceEntry.COLUMN_NAME_TOTAL, total.toPlainString());

            database.insert(DbContract.BalanceEntry.TABLE_NAME, null, values);
        }
    }

    public String lastHistoryDate(DbHelper dbHelper, Bitso.Book book) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.HistoryEntry.TABLE_NAME,
                new String[]{
                        DbContract.HistoryEntry._ID,
                        DbContract.HistoryEntry.COLUMN_NAME_DATE
                },
                DbContract.HistoryEntry.COLUMN_NAME_BOOK + " =?", new String[]{book.name()},
                null, null,
                String.format("date(%s) DESC", DbContract.HistoryEntry.COLUMN_NAME_DATE));

        String date = null;

        if (cursor.moveToNext() && cursor.moveToNext()) {
            date = cursor.getString(
                    cursor.getColumnIndex(DbContract.HistoryEntry.COLUMN_NAME_DATE));
        }

        cursor.close();

        return date;
    }

    public List<History> readHistories(DbHelper dbHelper, Bitso.Book book, int range) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        String selection = DbContract.HistoryEntry.COLUMN_NAME_BOOK + " =?";
        String[] selectionArgs = new String[]{book.name()};

        if (0 < range) {
            Calendar calendar = Calendar.getInstance();

            String beforeDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
            calendar.add(Calendar.MONTH, -1 * range);
            String afterDate = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            selectionArgs = new String[]{
                    book.name(), afterDate, beforeDate
            };

            selection += String.format(" AND date(%s) >= ? AND date(%s) <= ?",
                    DbContract.HistoryEntry.COLUMN_NAME_DATE,
                    DbContract.HistoryEntry.COLUMN_NAME_DATE);
        }

        Cursor cursor = database.query(
                DbContract.HistoryEntry.TABLE_NAME,
                new String[]{
                        DbContract.HistoryEntry._ID,
                        DbContract.HistoryEntry.COLUMN_NAME_BOOK,
                        DbContract.HistoryEntry.COLUMN_NAME_DATE,
                        DbContract.HistoryEntry.COLUMN_NAME_DATED,
                        DbContract.HistoryEntry.COLUMN_NAME_LOW,
                        DbContract.HistoryEntry.COLUMN_NAME_HIGH,
                        DbContract.HistoryEntry.COLUMN_NAME_OPEN,
                        DbContract.HistoryEntry.COLUMN_NAME_CLOSE,
                        DbContract.HistoryEntry.COLUMN_NAME_VALUE,
                        DbContract.HistoryEntry.COLUMN_NAME_VOLUME,
                        DbContract.HistoryEntry.COLUMN_NAME_VWAP
                }, selection, selectionArgs,
                null, null,
                String.format("date(%s) ASC", DbContract.HistoryEntry.COLUMN_NAME_DATE));

        ArrayList<History> histories = new ArrayList<>();

        while (cursor.moveToNext()) {
            String date = cursor.getString(cursor.getColumnIndex(DbContract.HistoryEntry.COLUMN_NAME_DATE));
            String dated = cursor.getString(cursor.getColumnIndex(DbContract.HistoryEntry.COLUMN_NAME_DATED));
            BigDecimal value = new BigDecimal(
                    cursor.getString(cursor.getColumnIndex(DbContract.HistoryEntry.COLUMN_NAME_VALUE)));
            BigDecimal volume = new BigDecimal(
                    cursor.getString(cursor.getColumnIndex(DbContract.HistoryEntry.COLUMN_NAME_VOLUME)));
            BigDecimal open = new BigDecimal(
                    cursor.getString(cursor.getColumnIndex(DbContract.HistoryEntry.COLUMN_NAME_OPEN)));
            BigDecimal low = new BigDecimal(
                    cursor.getString(cursor.getColumnIndex(DbContract.HistoryEntry.COLUMN_NAME_LOW)));
            BigDecimal high = new BigDecimal(
                    cursor.getString(cursor.getColumnIndex(DbContract.HistoryEntry.COLUMN_NAME_HIGH)));
            BigDecimal close = new BigDecimal(
                    cursor.getString(cursor.getColumnIndex(DbContract.HistoryEntry.COLUMN_NAME_CLOSE)));
            BigDecimal vwap = new BigDecimal(
                    cursor.getString(cursor.getColumnIndex(DbContract.HistoryEntry.COLUMN_NAME_VWAP)));

            histories.add(new History(book, date, dated, value, volume, open, low, high, close, vwap));
        }

        cursor.close();

        return histories.isEmpty() ? null : histories;
    }

    public void storeHistories(DbHelper dbHelper, Bitso.Book book, List<History> histories, String lastHistoryDate) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        if (null != lastHistoryDate) {
            database.delete(DbContract.HistoryEntry.TABLE_NAME,
                    String.format("date(%s) > ? and %s = ?",
                            DbContract.HistoryEntry.COLUMN_NAME_DATE, DbContract.HistoryEntry.COLUMN_NAME_BOOK),
                    new String[]{lastHistoryDate, book.name()});
        }

        for (History history : histories) {
            ContentValues values = new ContentValues();
            values.put(DbContract.HistoryEntry.COLUMN_NAME_BOOK, book.name());
            values.put(DbContract.HistoryEntry.COLUMN_NAME_DATE, history.getDate());
            values.put(DbContract.HistoryEntry.COLUMN_NAME_DATED, history.getDated());
            values.put(DbContract.HistoryEntry.COLUMN_NAME_LOW, history.getLow().toPlainString());
            values.put(DbContract.HistoryEntry.COLUMN_NAME_HIGH, history.getHigh().toPlainString());
            values.put(DbContract.HistoryEntry.COLUMN_NAME_OPEN, history.getOpen().toPlainString());
            values.put(DbContract.HistoryEntry.COLUMN_NAME_CLOSE, history.getClose().toPlainString());
            values.put(DbContract.HistoryEntry.COLUMN_NAME_VALUE, history.getValue().toPlainString());
            values.put(DbContract.HistoryEntry.COLUMN_NAME_VOLUME, history.getVolume().toPlainString());
            values.put(DbContract.HistoryEntry.COLUMN_NAME_VWAP, history.getVwap().toPlainString());

            database.insert(DbContract.HistoryEntry.TABLE_NAME, null, values);
        }
    }

    public FundingDestination readFundingDestination(DbHelper dbHelper, String currency) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.FundingDestinationEntry.TABLE_NAME,
                new String[]{
                        DbContract.FundingDestinationEntry._ID,
                        DbContract.FundingDestinationEntry.COLUMN_NAME_CURRENCY,
                        DbContract.FundingDestinationEntry.COLUMN_NAME_ACCOUNT_NAME,
                        DbContract.FundingDestinationEntry.COLUMN_NAME_ACCOUNT
                },
                DbContract.FundingDestinationEntry.COLUMN_NAME_CURRENCY + " =?",
                new String[]{currency.toUpperCase()},
                null, null, null);

        FundingDestination fundingDestination = null;

        if (cursor.moveToNext()) {
            String accountName = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingDestinationEntry.COLUMN_NAME_ACCOUNT_NAME));
            String account = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingDestinationEntry.COLUMN_NAME_ACCOUNT));

            fundingDestination = new FundingDestination(currency, accountName, account);
        }

        cursor.close();

        return fundingDestination;
    }

    public void storeFundingDestination(DbHelper dbHelper, FundingDestination fundingDestination) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DbContract.FundingDestinationEntry.TABLE_NAME,
                DbContract.FundingDestinationEntry.COLUMN_NAME_CURRENCY + " =?",
                new String[]{fundingDestination.getCurrency()});

        ContentValues values = new ContentValues();
        values.put(DbContract.FundingDestinationEntry.COLUMN_NAME_CURRENCY, fundingDestination.getCurrency().toUpperCase());
        values.put(DbContract.FundingDestinationEntry.COLUMN_NAME_ACCOUNT_NAME, fundingDestination.getId());
        values.put(DbContract.FundingDestinationEntry.COLUMN_NAME_ACCOUNT, fundingDestination.getAccount());

        database.insert(DbContract.FundingDestinationEntry.TABLE_NAME, null, values);
    }

    public List<Order> readOrders(DbHelper dbHelper, Bitso.Book book) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.OrderEntry.TABLE_NAME,
                new String[]{
                        DbContract.OrderEntry._ID,
                        DbContract.OrderEntry.COLUMN_NAME_BOOK,
                        DbContract.OrderEntry.COLUMN_NAME_ORIGINAL_AMOUNT,
                        DbContract.OrderEntry.COLUMN_NAME_UNFILLED_AMOUNT,
                        DbContract.OrderEntry.COLUMN_NAME_ORIGINAL_VALUE,
                        DbContract.OrderEntry.COLUMN_NAME_PRICE,
                        DbContract.OrderEntry.COLUMN_NAME_OID,
                        DbContract.OrderEntry.COLUMN_NAME_SIDE,
                        DbContract.OrderEntry.COLUMN_NAME_STATUS,
                        DbContract.OrderEntry.COLUMN_NAME_TYPE,
                        DbContract.OrderEntry.COLUMN_NAME_CREATED_AT,
                        DbContract.OrderEntry.COLUMN_NAME_UPDATED_AT
                },
                DbContract.OrderEntry.COLUMN_NAME_BOOK + " =?", new String[]{book.name()},
                null, null, null);

        ArrayList<Order> orders = new ArrayList<>();

        while (cursor.moveToNext()) {
            BigDecimal originalAmoun = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_ORIGINAL_AMOUNT)));
            BigDecimal unfilledAmount = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_UNFILLED_AMOUNT)));
            BigDecimal originalValue = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_ORIGINAL_VALUE)));
            BigDecimal price = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_PRICE)));
            String oid = cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_OID));
            String side = cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_SIDE));
            String status = cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_STATUS));
            String type = cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_TYPE));
            Date createdAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_CREATED_AT)));
            Date updatedAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_UPDATED_AT)));

            orders.add(new Order(book, originalAmoun, unfilledAmount, originalValue, price,
                    oid, side, status, type, createdAt, updatedAt));
        }

        cursor.close();

        return orders.isEmpty() ? null : orders;
    }

    public void storeOrders(DbHelper dbHelper, Bitso.Book book, List<Order> orders, boolean deleteIfNotExists) {
        if (deleteIfNotExists && orders.isEmpty()) {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            database.delete(DbContract.OrderEntry.TABLE_NAME,
                    DbContract.OrderEntry.COLUMN_NAME_BOOK + " =?",
                    new String[]{book.name()});
            return;
        }

        String oids = "";

        for (Order order : orders) {
            if (!oids.isEmpty()) {
                oids += ",";
            }

            oids += order.getOid();

            storeOrder(dbHelper, order);
        }

        if (deleteIfNotExists && null != book && !oids.isEmpty()) {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            database.delete(DbContract.OrderEntry.TABLE_NAME,
                    DbContract.OrderEntry.COLUMN_NAME_OID + " NOT IN (?) AND " + DbContract.OrderEntry.COLUMN_NAME_BOOK + " =?",
                    new String[]{oids, book.name()});
        }
    }

    public Order readOrder(DbHelper dbHelper, String oid) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.OrderEntry.TABLE_NAME,
                new String[]{
                        DbContract.OrderEntry._ID,
                        DbContract.OrderEntry.COLUMN_NAME_BOOK,
                        DbContract.OrderEntry.COLUMN_NAME_ORIGINAL_AMOUNT,
                        DbContract.OrderEntry.COLUMN_NAME_UNFILLED_AMOUNT,
                        DbContract.OrderEntry.COLUMN_NAME_ORIGINAL_VALUE,
                        DbContract.OrderEntry.COLUMN_NAME_PRICE,
                        DbContract.OrderEntry.COLUMN_NAME_OID,
                        DbContract.OrderEntry.COLUMN_NAME_SIDE,
                        DbContract.OrderEntry.COLUMN_NAME_STATUS,
                        DbContract.OrderEntry.COLUMN_NAME_TYPE,
                        DbContract.OrderEntry.COLUMN_NAME_CREATED_AT,
                        DbContract.OrderEntry.COLUMN_NAME_UPDATED_AT
                },
                DbContract.OrderEntry.COLUMN_NAME_OID + " =?", new String[]{oid},
                null, null, null);

        Order order = null;

        if (cursor.moveToNext()) {
            Bitso.Book book = Bitso.Book.valueOf(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_BOOK)));
            BigDecimal originalAmoun = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_ORIGINAL_AMOUNT)));
            BigDecimal unfilledAmount = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_UNFILLED_AMOUNT)));
            BigDecimal originalValue = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_ORIGINAL_VALUE)));
            BigDecimal price = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_PRICE)));
            String side = cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_SIDE));
            String status = cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_STATUS));
            String type = cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_TYPE));
            Date createdAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_CREATED_AT)));
            Date updatedAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.OrderEntry.COLUMN_NAME_UPDATED_AT)));

            order = new Order(book, originalAmoun, unfilledAmount, originalValue, price,
                    oid, side, status, type, createdAt, updatedAt);
        }

        cursor.close();

        return order;
    }

    public void storeOrder(DbHelper dbHelper, Order order) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.OrderEntry.COLUMN_NAME_BOOK, order.getBook().name());
        values.put(DbContract.OrderEntry.COLUMN_NAME_ORIGINAL_AMOUNT,
                order.getOriginalAmount().toPlainString());
        values.put(DbContract.OrderEntry.COLUMN_NAME_UNFILLED_AMOUNT,
                order.getUnfilledAmount().toPlainString());
        values.put(DbContract.OrderEntry.COLUMN_NAME_ORIGINAL_VALUE,
                order.getOriginalValue().toPlainString());
        values.put(DbContract.OrderEntry.COLUMN_NAME_PRICE,
                order.getPrice().toPlainString());
        values.put(DbContract.OrderEntry.COLUMN_NAME_OID, order.getOid());
        values.put(DbContract.OrderEntry.COLUMN_NAME_SIDE, order.getSide());
        values.put(DbContract.OrderEntry.COLUMN_NAME_STATUS, order.getStatus());
        values.put(DbContract.OrderEntry.COLUMN_NAME_TYPE, order.getType());
        values.put(DbContract.OrderEntry.COLUMN_NAME_CREATED_AT, Utilities.formatDateTime(order.getCreatedAt()));
        values.put(DbContract.OrderEntry.COLUMN_NAME_UPDATED_AT, Utilities.formatDateTime(order.getUpdatedAt()));

        if (null == readOrder(dbHelper, order.getOid())) {
            database.insert(DbContract.OrderEntry.TABLE_NAME, null, values);
        } else {
            database.update(DbContract.OrderEntry.TABLE_NAME, values,
                    DbContract.OrderEntry.COLUMN_NAME_OID + " =?", new String[]{order.getOid()});
        }
    }

    public void deleteOrder(DbHelper dbHelper, String oid) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DbContract.OrderEntry.TABLE_NAME,
                DbContract.OrderEntry.COLUMN_NAME_OID + " =?", new String[]{oid});
    }

    public List<Funding> readFundings(DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.FundingEntry.TABLE_NAME,
                new String[]{
                        DbContract.FundingEntry._ID,
                        DbContract.FundingEntry.COLUMN_NAME_FID,
                        DbContract.FundingEntry.COLUMN_NAME_STATUS,
                        DbContract.FundingEntry.COLUMN_NAME_CURRENCY,
                        DbContract.FundingEntry.COLUMN_NAME_METHOD,
                        DbContract.FundingEntry.COLUMN_NAME_AMOUNT,
                        DbContract.FundingEntry.COLUMN_NAME_DETAILS,
                        DbContract.FundingEntry.COLUMN_NAME_CREATED_AT,
                },
                null, null,
                null, null, null);

        ArrayList<Funding> fundings = new ArrayList<>();

        while (cursor.moveToNext()) {
            String fid = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_FID));
            String status = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_STATUS));
            String currency = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_CURRENCY));
            String method = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_METHOD));
            BigDecimal amount = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_AMOUNT)));
            String details = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_DETAILS));
            Date createdAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_CREATED_AT)));

            fundings.add(new Funding(fid, status, currency, method, amount, details, createdAt));
        }

        cursor.close();

        return fundings.isEmpty() ? null : fundings;
    }

    public void storeFundings(DbHelper dbHelper, List<Funding> fundings) {
        for (Funding funding : fundings) {
            storeFunding(dbHelper, funding);
        }
    }

    public Funding readFunding(DbHelper dbHelper, String fid) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.FundingEntry.TABLE_NAME,
                new String[]{
                        DbContract.FundingEntry._ID,
                        DbContract.FundingEntry.COLUMN_NAME_FID,
                        DbContract.FundingEntry.COLUMN_NAME_STATUS,
                        DbContract.FundingEntry.COLUMN_NAME_CURRENCY,
                        DbContract.FundingEntry.COLUMN_NAME_METHOD,
                        DbContract.FundingEntry.COLUMN_NAME_AMOUNT,
                        DbContract.FundingEntry.COLUMN_NAME_DETAILS,
                        DbContract.FundingEntry.COLUMN_NAME_CREATED_AT,
                },
                DbContract.FundingEntry.COLUMN_NAME_FID + " =?", new String[]{fid},
                null, null, null);

        Funding funding = null;

        if (cursor.moveToNext()) {
            String status = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_STATUS));
            String currency = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_CURRENCY));
            String method = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_METHOD));
            BigDecimal amount = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_AMOUNT)));
            String details = cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_DETAILS));
            Date createdAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.FundingEntry.COLUMN_NAME_CREATED_AT)));

            funding = new Funding(fid, status, currency, method, amount, details, createdAt);
        }

        cursor.close();

        return funding;
    }

    public void storeFunding(DbHelper dbHelper, Funding funding) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.FundingEntry.COLUMN_NAME_FID, funding.getFid());
        values.put(DbContract.FundingEntry.COLUMN_NAME_STATUS, funding.getStatus());
        values.put(DbContract.FundingEntry.COLUMN_NAME_CURRENCY, funding.getCurrency());
        values.put(DbContract.FundingEntry.COLUMN_NAME_METHOD, funding.getMethod());
        values.put(DbContract.FundingEntry.COLUMN_NAME_AMOUNT, funding.getAmount().toPlainString());
        values.put(DbContract.FundingEntry.COLUMN_NAME_DETAILS, funding.getDetails());
        values.put(DbContract.FundingEntry.COLUMN_NAME_CREATED_AT, Utilities.formatDateTime(funding.getCreatedAt()));

        if (null == readFunding(dbHelper, funding.getFid())) {
            database.insert(DbContract.FundingEntry.TABLE_NAME, null, values);
        } else {
            database.update(DbContract.FundingEntry.TABLE_NAME, values,
                    DbContract.FundingEntry.COLUMN_NAME_FID + " =?", new String[]{funding.getFid()});
        }
    }

    public List<Withdrawal> readWithdrawals(DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.WithdrawalEntry.TABLE_NAME,
                new String[]{
                        DbContract.WithdrawalEntry._ID,
                        DbContract.WithdrawalEntry.COLUMN_NAME_WID,
                        DbContract.WithdrawalEntry.COLUMN_NAME_STATUS,
                        DbContract.WithdrawalEntry.COLUMN_NAME_CURRENCY,
                        DbContract.WithdrawalEntry.COLUMN_NAME_METHOD,
                        DbContract.WithdrawalEntry.COLUMN_NAME_AMOUNT,
                        DbContract.WithdrawalEntry.COLUMN_NAME_DETAILS,
                        DbContract.WithdrawalEntry.COLUMN_NAME_CREATED_AT,
                },
                null, null,
                null, null, null);

        ArrayList<Withdrawal> withdrawals = new ArrayList<>();

        while (cursor.moveToNext()) {
            String wid = cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_WID));
            String status = cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_STATUS));
            String currency = cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_CURRENCY));
            String method = cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_METHOD));
            BigDecimal amount = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_AMOUNT)));
            String details = cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_DETAILS));
            Date createdAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_CREATED_AT)));

            withdrawals.add(new Withdrawal(wid, status, currency, method, amount, details, createdAt));
        }

        cursor.close();

        return withdrawals.isEmpty() ? null : withdrawals;
    }

    public void storeWithdrawals(DbHelper dbHelper, List<Withdrawal> withdrawals) {
        for (Withdrawal withdrawal : withdrawals) {
            storeWithdrawal(dbHelper, withdrawal);
        }
    }

    public Withdrawal readWithdrawal(DbHelper dbHelper, String wid) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.WithdrawalEntry.TABLE_NAME,
                new String[]{
                        DbContract.WithdrawalEntry._ID,
                        DbContract.WithdrawalEntry.COLUMN_NAME_WID,
                        DbContract.WithdrawalEntry.COLUMN_NAME_STATUS,
                        DbContract.WithdrawalEntry.COLUMN_NAME_CURRENCY,
                        DbContract.WithdrawalEntry.COLUMN_NAME_METHOD,
                        DbContract.WithdrawalEntry.COLUMN_NAME_AMOUNT,
                        DbContract.WithdrawalEntry.COLUMN_NAME_DETAILS,
                        DbContract.WithdrawalEntry.COLUMN_NAME_CREATED_AT,
                },
                DbContract.WithdrawalEntry.COLUMN_NAME_WID + " =?", new String[]{wid},
                null, null, null);

        Withdrawal withdrawal = null;

        if (cursor.moveToNext()) {
            String status = cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_STATUS));
            String currency = cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_CURRENCY));
            String method = cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_METHOD));
            BigDecimal amount = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_AMOUNT)));
            String details = cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_DETAILS));
            Date createdAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.WithdrawalEntry.COLUMN_NAME_CREATED_AT)));

            withdrawal = new Withdrawal(wid, status, currency, method, amount, details, createdAt);
        }

        cursor.close();

        return withdrawal;
    }

    public void storeWithdrawal(DbHelper dbHelper, Withdrawal withdrawal) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.WithdrawalEntry.COLUMN_NAME_WID, withdrawal.getWid());
        values.put(DbContract.WithdrawalEntry.COLUMN_NAME_STATUS, withdrawal.getStatus());
        values.put(DbContract.WithdrawalEntry.COLUMN_NAME_CURRENCY, withdrawal.getCurrency());
        values.put(DbContract.WithdrawalEntry.COLUMN_NAME_METHOD, withdrawal.getMethod());
        values.put(DbContract.WithdrawalEntry.COLUMN_NAME_AMOUNT, withdrawal.getAmount().toPlainString());
        values.put(DbContract.WithdrawalEntry.COLUMN_NAME_DETAILS, withdrawal.getDetails());
        values.put(DbContract.WithdrawalEntry.COLUMN_NAME_CREATED_AT, Utilities.formatDateTime(withdrawal.getCreatedAt()));

        if (null == readWithdrawal(dbHelper, withdrawal.getWid())) {
            database.insert(DbContract.WithdrawalEntry.TABLE_NAME, null, values);
        } else {
            database.update(DbContract.WithdrawalEntry.TABLE_NAME, values,
                    DbContract.WithdrawalEntry.COLUMN_NAME_WID + " =?", new String[]{withdrawal.getWid()});
        }
    }

    public List<Trade> readTrades(DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.TradeEntry.TABLE_NAME,
                new String[]{
                        DbContract.TradeEntry._ID,
                        DbContract.TradeEntry.COLUMN_NAME_BOOK,
                        DbContract.TradeEntry.COLUMN_NAME_TID,
                        DbContract.TradeEntry.COLUMN_NAME_MAJOR,
                        DbContract.TradeEntry.COLUMN_NAME_MINOR,
                        DbContract.TradeEntry.COLUMN_NAME_FEES_AMOUNT,
                        DbContract.TradeEntry.COLUMN_NAME_FEES_CURRENCY,
                        DbContract.TradeEntry.COLUMN_NAME_PRICE,
                        DbContract.TradeEntry.COLUMN_NAME_OID,
                        DbContract.TradeEntry.COLUMN_NAME_SIDE,
                        DbContract.TradeEntry.COLUMN_NAME_CREATED_AT
                },
                null, null,
                null, null, null);

        ArrayList<Trade> trades = new ArrayList<>();

        while (cursor.moveToNext()) {
            Bitso.Book book = Bitso.Book.valueOf(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_BOOK)));
            String tid = cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_TID));
            BigDecimal major = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_MAJOR)));
            BigDecimal minor = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_MINOR)));
            BigDecimal feesAmount = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_FEES_AMOUNT)));
            String feesCurrency = cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_FEES_CURRENCY));
            BigDecimal price = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_PRICE)));
            String oid = cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_OID));
            String side = cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_SIDE));
            Date createdAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_CREATED_AT)));

            trades.add(new Trade(book, major, minor,
                    feesAmount, feesCurrency, price, tid, oid, side, createdAt));
        }

        cursor.close();

        return trades.isEmpty() ? null : trades;
    }

    public void storeTrades(DbHelper dbHelper, List<Trade> trades) {
        for (Trade trade : trades) {
            storeTrade(dbHelper, trade);
        }
    }

    public Trade readTrade(DbHelper dbHelper, String tid) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor cursor = database.query(
                DbContract.TradeEntry.TABLE_NAME,
                new String[]{
                        DbContract.TradeEntry._ID,
                        DbContract.TradeEntry.COLUMN_NAME_BOOK,
                        DbContract.TradeEntry.COLUMN_NAME_TID,
                        DbContract.TradeEntry.COLUMN_NAME_MAJOR,
                        DbContract.TradeEntry.COLUMN_NAME_MINOR,
                        DbContract.TradeEntry.COLUMN_NAME_FEES_AMOUNT,
                        DbContract.TradeEntry.COLUMN_NAME_FEES_CURRENCY,
                        DbContract.TradeEntry.COLUMN_NAME_PRICE,
                        DbContract.TradeEntry.COLUMN_NAME_OID,
                        DbContract.TradeEntry.COLUMN_NAME_SIDE,
                        DbContract.TradeEntry.COLUMN_NAME_CREATED_AT
                },
                DbContract.TradeEntry.COLUMN_NAME_TID + " =?", new String[]{tid},
                null, null, null);

        Trade trade = null;

        if (cursor.moveToNext()) {
            Bitso.Book book = Bitso.Book.valueOf(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_BOOK)));
            BigDecimal major = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_MAJOR)));
            BigDecimal minor = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_MINOR)));
            BigDecimal feesAmount = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_FEES_AMOUNT)));
            String feesCurrency = cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_FEES_CURRENCY));
            BigDecimal price = new BigDecimal(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_PRICE)));
            String oid = cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_OID));
            String side = cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_SIDE));
            Date createdAt = Utilities.parseDateTime(cursor.getString(
                    cursor.getColumnIndex(DbContract.TradeEntry.COLUMN_NAME_CREATED_AT)));

            trade = new Trade(book, major, minor,
                    feesAmount, feesCurrency, price, tid, oid, side, createdAt);
        }

        cursor.close();

        return trade;
    }

    public void storeTrade(DbHelper dbHelper, Trade trade) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DbContract.TradeEntry.COLUMN_NAME_BOOK, trade.getBook().name());
        values.put(DbContract.TradeEntry.COLUMN_NAME_TID, trade.getTid());
        values.put(DbContract.TradeEntry.COLUMN_NAME_MAJOR, trade.getMajor().toPlainString());
        values.put(DbContract.TradeEntry.COLUMN_NAME_MINOR, trade.getMinor().toPlainString());
        values.put(DbContract.TradeEntry.COLUMN_NAME_FEES_AMOUNT, trade.getFeesAmount().toPlainString());
        values.put(DbContract.TradeEntry.COLUMN_NAME_FEES_CURRENCY, trade.getFeesCurrency());
        values.put(DbContract.TradeEntry.COLUMN_NAME_PRICE, trade.getPrice().toPlainString());
        values.put(DbContract.TradeEntry.COLUMN_NAME_OID, trade.getOid());
        values.put(DbContract.TradeEntry.COLUMN_NAME_SIDE, trade.getSide());
        values.put(DbContract.TradeEntry.COLUMN_NAME_CREATED_AT, Utilities.formatDateTime(trade.getCreatedAt()));

        if (null == readTrade(dbHelper, trade.getTid())) {
            database.insert(DbContract.TradeEntry.TABLE_NAME, null, values);
        } else {
            database.update(DbContract.TradeEntry.TABLE_NAME, values,
                    DbContract.TradeEntry.COLUMN_NAME_TID + " =?", new String[]{trade.getTid()});
        }
    }

    public void clear(DbHelper dbHelper) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        database.delete(DbContract.ConfigurationEntry.TABLE_NAME, null, null);
        database.delete(DbContract.TradeEntry.TABLE_NAME, null, null);
        database.delete(DbContract.WithdrawalEntry.TABLE_NAME, null, null);
        database.delete(DbContract.FundingEntry.TABLE_NAME, null, null);
        database.delete(DbContract.OrderEntry.TABLE_NAME, null, null);
        database.delete(DbContract.AccountEntry.TABLE_NAME, null, null);
        database.delete(DbContract.BalanceEntry.TABLE_NAME, null, null);
        database.delete(DbContract.FundingDestinationEntry.TABLE_NAME, null, null);
    }
}
