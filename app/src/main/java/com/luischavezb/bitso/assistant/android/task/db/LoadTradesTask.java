package com.luischavezb.bitso.assistant.android.task.db;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.Trade;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.List;

/**
 * Created by luischavez on 01/03/18.
 */

public class LoadTradesTask extends DbTask<List<Trade>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.LOAD_TRADES_TASK";

    public LoadTradesTask(boolean enableDialog, int... targets) {
        super(TAG, enableDialog, false, targets);
    }

    @Override
    protected List<Trade> execute(DbHelper dbHelper) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_trades));

        return dbHelper.readTrades();
    }
}
