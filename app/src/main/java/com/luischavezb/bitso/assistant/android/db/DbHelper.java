package com.luischavezb.bitso.assistant.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.geometrycloud.bitso.assistant.library.AccountStatus;
import com.geometrycloud.bitso.assistant.library.Balance;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Funding;
import com.geometrycloud.bitso.assistant.library.FundingDestination;
import com.geometrycloud.bitso.assistant.library.History;
import com.geometrycloud.bitso.assistant.library.Order;
import com.geometrycloud.bitso.assistant.library.Ticker;
import com.geometrycloud.bitso.assistant.library.Trade;
import com.geometrycloud.bitso.assistant.library.Withdrawal;
import com.luischavezb.bitso.assistant.android.alarm.Alarm;

import java.util.List;

/**
 * Created by luischavez on 26/01/18.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BitsoAssistant.db";

    private DbHandler mDbHandler;

    private DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mDbHandler = new DbHandler();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        new DbContract.ConfigurationEntry().create(db);
        new DbContract.AlarmEntry().create(db);
        new DbContract.AccountEntry().create(db);
        new DbContract.OrderEntry().create(db);
        new DbContract.FundingDestinationEntry().create(db);
        new DbContract.BalanceEntry().create(db);
        new DbContract.TickerEntry().create(db);
        new DbContract.HistoryEntry().create(db);
        new DbContract.FundingEntry().create(db);
        new DbContract.WithdrawalEntry().create(db);
        new DbContract.TradeEntry().create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        new DbContract.ConfigurationEntry().upgrade(db, oldVersion, newVersion);
        new DbContract.AlarmEntry().upgrade(db, oldVersion, newVersion);
        new DbContract.AccountEntry().upgrade(db, oldVersion, newVersion);
        new DbContract.OrderEntry().upgrade(db, oldVersion, newVersion);
        new DbContract.FundingDestinationEntry().upgrade(db, oldVersion, newVersion);
        new DbContract.BalanceEntry().upgrade(db, oldVersion, newVersion);
        new DbContract.TickerEntry().upgrade(db, oldVersion, newVersion);
        new DbContract.HistoryEntry().upgrade(db, oldVersion, newVersion);
        new DbContract.FundingEntry().upgrade(db, oldVersion, newVersion);
        new DbContract.WithdrawalEntry().upgrade(db, oldVersion, newVersion);
        new DbContract.TradeEntry().upgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        new DbContract.ConfigurationEntry().downgrade(db, oldVersion, newVersion);
        new DbContract.AlarmEntry().downgrade(db, oldVersion, newVersion);
        new DbContract.AccountEntry().downgrade(db, oldVersion, newVersion);
        new DbContract.OrderEntry().downgrade(db, oldVersion, newVersion);
        new DbContract.FundingDestinationEntry().downgrade(db, oldVersion, newVersion);
        new DbContract.BalanceEntry().downgrade(db, oldVersion, newVersion);
        new DbContract.TickerEntry().downgrade(db, oldVersion, newVersion);
        new DbContract.HistoryEntry().downgrade(db, oldVersion, newVersion);
        new DbContract.FundingEntry().downgrade(db, oldVersion, newVersion);
        new DbContract.WithdrawalEntry().downgrade(db, oldVersion, newVersion);
        new DbContract.TradeEntry().downgrade(db, oldVersion, newVersion);
    }

    public AccountStatus readAccountStatus() {
        return mDbHandler.readAccountStatus(this);
    }

    public void storeAccountStatus(AccountStatus accountStatus) {
        mDbHandler.storeAccountStatus(this, accountStatus);
    }

    public void storeConfiguration(String key, String secret, String phrase) {
        mDbHandler.storeConfiguration(this, key, secret, phrase);
    }

    public String readConfigurationField(String field) {
        return mDbHandler.readConfigurationField(this, field);
    }

    public void storeConfigurationField(String field, Object value) {
        mDbHandler.storeConfigurationField(this, field, value);
    }

    public List<Alarm> readAlarms() {
        return mDbHandler.readAlarms(this);
    }

    public Alarm readAlarm(Long id) {
        return mDbHandler.readAlarm(this, id);
    }

    public void storeAlarm(Alarm alarm) {
        mDbHandler.storeAlarm(this, alarm);
    }

    public void deleteAlarm(Alarm alarm) {
        mDbHandler.deleteAlarm(this, alarm);
    }

    public List<Ticker> readTickers() {
        return mDbHandler.readTickers(this);
    }

    public void storeTickers(List<Ticker> tickers) {
        mDbHandler.storeTickers(this, tickers);
    }

    public List<Balance> readBalances() {
        return mDbHandler.readBalances(this);
    }

    public void storeBalances(List<Balance> balances) {
        mDbHandler.storeBalances(this, balances);
    }

    public String lastHistoryDate(Bitso.Book book) {
        return mDbHandler.lastHistoryDate(this, book);
    }

    public List<History> readHistories(Bitso.Book book, int range) {
        return mDbHandler.readHistories(this, book, range);
    }

    public void storeHistories(Bitso.Book book, List<History> histories, String lastHistoryDate) {
        mDbHandler.storeHistories(this, book, histories, lastHistoryDate);
    }

    public FundingDestination readFundingDestination(String currency) {
        return mDbHandler.readFundingDestination(this, currency);
    }

    public void storeFundingDestination(FundingDestination fundingDestination) {
        mDbHandler.storeFundingDestination(this, fundingDestination);
    }

    public List<Order> readOrders(Bitso.Book book) {
        return mDbHandler.readOrders(this, book);
    }

    public void storeOrders(Bitso.Book book, List<Order> orders, boolean deleteIfNotExists) {
        mDbHandler.storeOrders(this, book, orders, deleteIfNotExists);
    }

    public void storeOrder(Order order) {
        mDbHandler.storeOrder(this, order);
    }

    public void deleteOpenOrder(String oid) {
        mDbHandler.deleteOrder(this, oid);
    }

    public List<Funding> readFundings() {
        return mDbHandler.readFundings(this);
    }

    public void storeFundings(List<Funding> fundings) {
        mDbHandler.storeFundings(this, fundings);
    }

    public Funding readFunding(String fid) {
        return mDbHandler.readFunding(this, fid);
    }

    public void storeFunding(Funding funding) {
        mDbHandler.storeFunding(this, funding);
    }

    public List<Withdrawal> readWithdrawals() {
        return mDbHandler.readWithdrawals(this);
    }

    public void storeWithdrawals(List<Withdrawal> withdrawals) {
        mDbHandler.storeWithdrawals(this, withdrawals);
    }

    public Withdrawal readWithdrawal(String wid) {
        return mDbHandler.readWithdrawal(this, wid);
    }

    public void storeWithdrawal(Withdrawal withdrawal) {
        mDbHandler.storeWithdrawal(this, withdrawal);
    }

    public List<Trade> readTrades() {
        return mDbHandler.readTrades(this);
    }

    public void storeTrades(List<Trade> trades) {
        mDbHandler.storeTrades(this, trades);
    }

    public Trade readTrade(String tid) {
        return mDbHandler.readTrade(this, tid);
    }

    public void storeTrade(Trade trade) {
        mDbHandler.storeTrade(this, trade);
    }

    public void clear() {
        mDbHandler.clear(this);
    }

    public static DbHelper getInstance(Context context) {
        if (null == DbHelperHolder.dbHelper) {
            DbHelperHolder.dbHelper = new DbHelper(context);
        }

        return DbHelperHolder.dbHelper;
    }

    public static DbHelper getInstance() {
        return getInstance(null);
    }

    private static class DbHelperHolder {

        private static DbHelper dbHelper;
    }
}
