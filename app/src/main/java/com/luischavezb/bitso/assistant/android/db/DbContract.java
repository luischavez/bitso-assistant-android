package com.luischavezb.bitso.assistant.android.db;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by luischavez on 26/01/18.
 */

public class DbContract {

    private DbContract() {
    }

    public static abstract class BaseEntry implements BaseColumns {

        public abstract void create(SQLiteDatabase db);

        public abstract void upgrade(SQLiteDatabase db, int oldVersion, int newVersion);

        public abstract void downgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    }

    public static class ConfigurationEntry extends BaseEntry {

        public static final String TABLE_NAME = "configuration";

        public static final String COLUMN_NAME_API_KEY = "api_key";
        public static final String COLUMN_NAME_API_SECRET = "api_secret";
        public static final String COLUMN_NAME_API_VALID = "api_valid";
        public static final String COLUMN_NAME_NIP_PHRASE_ENCODED = "nip_phrase_encoded";
        public static final String COLUMN_NAME_PREMIUM = "premium";

        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s BOOL, %s TEXT, %s BOOL)",
                    TABLE_NAME, _ID,
                    COLUMN_NAME_API_KEY, COLUMN_NAME_API_SECRET, COLUMN_NAME_API_VALID,
                    COLUMN_NAME_NIP_PHRASE_ENCODED,
                    COLUMN_NAME_PREMIUM);

            db.execSQL(sql);
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class AlarmEntry extends BaseEntry {

        public static final String TABLE_NAME = "alarms";

        public static final String COLUMN_NAME_ENABLED = "enabled";
        public static final String COLUMN_NAME_BOOK = "book";
        public static final String COLUMN_NAME_CONDITION = "condition";
        public static final String COLUMN_NAME_VALUE = "value";

        @Override
        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s BOOL, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, _ID,
                    COLUMN_NAME_ENABLED,
                    COLUMN_NAME_BOOK, COLUMN_NAME_CONDITION, COLUMN_NAME_VALUE);

            db.execSQL(sql);
        }

        @Override
        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        @Override
        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class AccountEntry extends BaseEntry {

        public static final String TABLE_NAME = "account";

        public static final String COLUMN_NAME_CLIENT_ID = "client_id";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_DAILY_LIMIT = "daily_limit";
        public static final String COLUMN_NAME_MONTHLY_LIMIT = "monthly_limit";
        public static final String COLUMN_NAME_DAILY_REMAINING = "daily_remaining";
        public static final String COLUMN_NAME_MONTHLY_REMAINING = "monthly_remaining";
        public static final String COLUMN_NAME_CELLPHONE_NUMBER = "cellphoneNumber";
        public static final String COLUMN_NAME_CELLPHONE_NUMBER_STORED = "cellphoneNumberStored";
        public static final String COLUMN_NAME_EMAIL_STORED = "emailStored";
        public static final String COLUMN_NAME_OFFICIAL_ID = "officialId";
        public static final String COLUMN_NAME_PROOF_OF_RESIDENCY = "proofOfResidency";
        public static final String COLUMN_NAME_SIGNED_CONTRACT = "signedContract";
        public static final String COLUMN_NAME_ORIGIN_OF_FUNDS = "originOfFunds";


        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s INT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, _ID, COLUMN_NAME_CLIENT_ID,
                    COLUMN_NAME_FIRST_NAME, COLUMN_NAME_LAST_NAME, COLUMN_NAME_STATUS,
                    COLUMN_NAME_DAILY_LIMIT, COLUMN_NAME_MONTHLY_LIMIT,
                    COLUMN_NAME_DAILY_REMAINING, COLUMN_NAME_MONTHLY_REMAINING,
                    COLUMN_NAME_CELLPHONE_NUMBER, COLUMN_NAME_CELLPHONE_NUMBER_STORED,
                    COLUMN_NAME_EMAIL_STORED,
                    COLUMN_NAME_OFFICIAL_ID, COLUMN_NAME_PROOF_OF_RESIDENCY,
                    COLUMN_NAME_SIGNED_CONTRACT, COLUMN_NAME_ORIGIN_OF_FUNDS);

            db.execSQL(sql);
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class OrderEntry extends BaseEntry {

        public static final String TABLE_NAME = "orders";

        public static final String COLUMN_NAME_BOOK = "book";
        public static final String COLUMN_NAME_ORIGINAL_AMOUNT = "original_amount";
        public static final String COLUMN_NAME_UNFILLED_AMOUNT = "unfilled_amount";
        public static final String COLUMN_NAME_ORIGINAL_VALUE = "original_value";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_OID = "oid";
        public static final String COLUMN_NAME_SIDE = "side";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";
        public static final String COLUMN_NAME_UPDATED_AT = "updated_at";


        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, _ID,
                    COLUMN_NAME_BOOK,
                    COLUMN_NAME_ORIGINAL_AMOUNT, COLUMN_NAME_UNFILLED_AMOUNT, COLUMN_NAME_ORIGINAL_VALUE,
                    COLUMN_NAME_PRICE,
                    COLUMN_NAME_OID, COLUMN_NAME_SIDE, COLUMN_NAME_STATUS, COLUMN_NAME_TYPE,
                    COLUMN_NAME_CREATED_AT, COLUMN_NAME_UPDATED_AT);

            db.execSQL(sql);
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class FundingDestinationEntry extends BaseEntry {

        public static final String TABLE_NAME = "funding_destinations";

        public static final String COLUMN_NAME_CURRENCY = "currency";
        public static final String COLUMN_NAME_ACCOUNT_NAME = "account_name";
        public static final String COLUMN_NAME_ACCOUNT = "account";


        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, _ID,
                    COLUMN_NAME_CURRENCY, COLUMN_NAME_ACCOUNT_NAME, COLUMN_NAME_ACCOUNT);

            db.execSQL(sql);
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class BalanceEntry extends BaseEntry {

        public static final String TABLE_NAME = "balances";

        public static final String COLUMN_NAME_CURRENCY = "currency";
        public static final String COLUMN_NAME_AVAILABLE = "available";
        public static final String COLUMN_NAME_LOCKED = "locked";
        public static final String COLUMN_NAME_TOTAL = "total";


        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, _ID,
                    COLUMN_NAME_CURRENCY,
                    COLUMN_NAME_AVAILABLE, COLUMN_NAME_LOCKED,
                    COLUMN_NAME_TOTAL);

            db.execSQL(sql);
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class TickerEntry extends BaseEntry {

        public static final String TABLE_NAME = "tickers";

        public static final String COLUMN_NAME_BOOK = "book";
        public static final String COLUMN_NAME_VOLUME = "volume";
        public static final String COLUMN_NAME_VWAP = "vwap";
        public static final String COLUMN_NAME_LOW = "low";
        public static final String COLUMN_NAME_HIGH = "high";
        public static final String COLUMN_NAME_ASK = "ask";
        public static final String COLUMN_NAME_BID = "bid";
        public static final String COLUMN_NAME_LAST = "last";
        public static final String COLUMN_NAME_CREATED_AT = "createdAt";

        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, _ID,
                    COLUMN_NAME_BOOK,
                    COLUMN_NAME_VOLUME, COLUMN_NAME_VWAP,
                    COLUMN_NAME_LOW, COLUMN_NAME_HIGH,
                    COLUMN_NAME_ASK, COLUMN_NAME_BID,
                    COLUMN_NAME_LAST,
                    COLUMN_NAME_CREATED_AT);

            db.execSQL(sql);
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class HistoryEntry extends BaseEntry {

        public static final String TABLE_NAME = "history";

        public static final String COLUMN_NAME_BOOK = "book";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DATED = "dated";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_LOW = "low";
        public static final String COLUMN_NAME_HIGH = "high";
        public static final String COLUMN_NAME_OPEN = "open";
        public static final String COLUMN_NAME_CLOSE = "close";
        public static final String COLUMN_NAME_VOLUME = "volume";
        public static final String COLUMN_NAME_VWAP = "vwap";

        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, _ID,
                    COLUMN_NAME_BOOK,
                    COLUMN_NAME_DATE, COLUMN_NAME_DATED,
                    COLUMN_NAME_VALUE, COLUMN_NAME_LOW, COLUMN_NAME_HIGH,
                    COLUMN_NAME_OPEN, COLUMN_NAME_CLOSE,
                    COLUMN_NAME_VOLUME, COLUMN_NAME_VWAP);

            db.execSQL(sql);
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class FundingEntry extends BaseEntry {

        public static final String TABLE_NAME = "fundings";

        public static final String COLUMN_NAME_FID = "fid";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_CURRENCY = "currency";
        public static final String COLUMN_NAME_METHOD = "method";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_DETAILS = "details";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";


        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, _ID,
                    COLUMN_NAME_FID,
                    COLUMN_NAME_STATUS, COLUMN_NAME_CURRENCY, COLUMN_NAME_METHOD,
                    COLUMN_NAME_AMOUNT, COLUMN_NAME_DETAILS,
                    COLUMN_NAME_CREATED_AT);

            db.execSQL(sql);
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class WithdrawalEntry extends BaseEntry {

        public static final String TABLE_NAME = "withdrawals";

        public static final String COLUMN_NAME_WID = "wid";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_CURRENCY = "currency";
        public static final String COLUMN_NAME_METHOD = "method";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_DETAILS = "details";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";


        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, _ID,
                    COLUMN_NAME_WID,
                    COLUMN_NAME_STATUS, COLUMN_NAME_CURRENCY, COLUMN_NAME_METHOD,
                    COLUMN_NAME_AMOUNT, COLUMN_NAME_DETAILS,
                    COLUMN_NAME_CREATED_AT);

            db.execSQL(sql);
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public static class TradeEntry extends BaseEntry {

        public static final String TABLE_NAME = "trades";

        public static final String COLUMN_NAME_BOOK = "book";
        public static final String COLUMN_NAME_TID = "tid";
        public static final String COLUMN_NAME_MAJOR = "major";
        public static final String COLUMN_NAME_MINOR = "minor";
        public static final String COLUMN_NAME_FEES_AMOUNT = "fees_amount";
        public static final String COLUMN_NAME_FEES_CURRENCY = "fees_currency";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_OID = "oid";
        public static final String COLUMN_NAME_SIDE = "side";
        public static final String COLUMN_NAME_CREATED_AT = "created_at";

        public void create(SQLiteDatabase db) {
            String sql = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                    TABLE_NAME, _ID,
                    COLUMN_NAME_BOOK, COLUMN_NAME_TID,
                    COLUMN_NAME_MAJOR, COLUMN_NAME_MINOR,
                    COLUMN_NAME_FEES_AMOUNT, COLUMN_NAME_FEES_CURRENCY,
                    COLUMN_NAME_PRICE, COLUMN_NAME_OID, COLUMN_NAME_SIDE,
                    COLUMN_NAME_CREATED_AT);

            db.execSQL(sql);
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void downgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
