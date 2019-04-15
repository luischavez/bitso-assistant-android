package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.History;
import com.luischavezb.bitso.assistant.android.AndroidAssistant;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.luischavezb.bitso.assistant.android.task.Task;

import java.util.List;

/**
 * Created by luischavez on 28/02/18.
 */

public class RequestHistoriesTask extends Task<Bitso, List<History>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.REQUEST_HISTORIES_TASK";

    private final Bitso.Book mBook;
    private final String mLastHistoryDate;
    private final int mRange;

    public RequestHistoriesTask(Bitso.Book book, String lastHistoryDate, int range, boolean enableDialog, int... targets) {
        super(TAG, AndroidAssistant.getInstance().bitso(), enableDialog, false, targets);

        mBook = book;
        mLastHistoryDate = lastHistoryDate;
        mRange = range;
    }

    @Override
    protected List<History> execute(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_histories));

        List<History> histories = bitso.history(mBook, mLastHistoryDate);

        if (null != histories) {
            DbHelper.getInstance().storeHistories(mBook, histories, mLastHistoryDate);
        }

        return DbHelper.getInstance().readHistories(mBook, mRange);
    }
}
