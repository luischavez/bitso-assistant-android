package com.luischavezb.bitso.assistant.android.preferences;

import android.content.Context;
import android.preference.MultiSelectListPreference;
import android.util.AttributeSet;

import com.geometrycloud.bitso.assistant.library.Bitso;

import java.util.ArrayList;

/**
 * Created by luischavez on 18/03/18.
 */

public class BookMultiListPreference extends MultiSelectListPreference {

    public BookMultiListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        ArrayList<CharSequence> entries = new ArrayList<>();

        for (Bitso.Book book : Bitso.Book.values()) {
            entries.add(book.name());
        }

        setEntries(entries.toArray(new CharSequence[0]));
        setEntryValues(entries.toArray(new CharSequence[0]));
    }
}
