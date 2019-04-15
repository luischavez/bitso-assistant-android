package com.luischavezb.bitso.assistant.android.task.db;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.History;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.List;

/**
 * Created by luischavez on 01/03/18.
 */

public class LoadHistoriesTask extends DbTask<List<History>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.LOAD_HISTORIES_TASK";

    private final Bitso.Book mBook;
    private final int mRange;

    public LoadHistoriesTask(Bitso.Book book, int range, boolean enableDialog, int... targets) {
        super(TAG, enableDialog, false, targets);

        mBook = book;
        mRange = range;
    }

    @Override
    protected List<History> execute(DbHelper dbHelper) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_histories));

        return dbHelper.readHistories(mBook, mRange);
    }
}
