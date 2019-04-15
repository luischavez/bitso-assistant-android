package com.luischavezb.bitso.assistant.android.task.db;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.AccountStatus;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

/**
 * Created by luischavez on 01/03/18.
 */

public class LoadAccountStatusTask extends DbTask<AccountStatus> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.LOAD_ACCOUNT_STATUS_TASK";

    public LoadAccountStatusTask(boolean enableDialog, int... targets) {
        super(TAG, enableDialog, false, targets);
    }

    @Override
    protected AccountStatus execute(DbHelper dbHelper) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_account));

        return dbHelper.readAccountStatus();
    }
}
