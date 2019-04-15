package com.luischavezb.bitso.assistant.android.task.db;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.Funding;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.List;

/**
 * Created by luischavez on 01/03/18.
 */

public class LoadFundingsTask extends DbTask<List<Funding>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.LOAD_FUNDINGS_TASK";

    public LoadFundingsTask(boolean enableDialog, int... targets) {
        super(TAG, enableDialog, false, targets);
    }

    @Override
    protected List<Funding> execute(DbHelper dbHelper) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_fundings));

        return dbHelper.readFundings();
    }
}
