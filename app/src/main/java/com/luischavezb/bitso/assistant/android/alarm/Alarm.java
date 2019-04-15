package com.luischavezb.bitso.assistant.android.alarm;

import com.geometrycloud.bitso.assistant.library.Bitso;

/**
 * Created by luischavez on 27/03/18.
 */

public class Alarm {

    private final Long mId;
    private boolean mEnabled;
    private Bitso.Book mBook;
    private Condition mCondition;
    private String mValue;

    public Alarm(long id, boolean enabled, Bitso.Book book, Condition condition, String value) {
        mId = id;
        mEnabled = enabled;
        mBook = book;
        mCondition = condition;
        mValue = value;
    }

    public Alarm(boolean enabled, Bitso.Book book, Condition condition, String value) {
        mId = null;
        mEnabled = enabled;
        mBook = book;
        mCondition = condition;
        mValue = value;
    }

    public Long getId() {
        return mId;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public Bitso.Book getBook() {
        return mBook;
    }

    public void setBook(Bitso.Book mBook) {
        this.mBook = mBook;
    }

    public Condition getCondition() {
        return mCondition;
    }

    public void setCondition(Condition condition) {
        this.mCondition = condition;
    }

    public String getValue() {
        return mValue;
    }

    public void setValue(String value) {
        this.mValue = value;
    }
}
