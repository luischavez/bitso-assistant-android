package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.AccountStatus;
import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

/**
 * Created by luischavez on 28/02/18.
 */

public class RequestAccountStatusTask extends BitsoTask<AccountStatus> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.REQUEST_ACCOUNT_STATUS_TASK";

    public RequestAccountStatusTask(boolean enableDialog, int... targets) {
        super(TAG, enableDialog, true, targets);
    }

    @Override
    protected void onSuccess(AccountStatus accountStatus) {
        DbHelper.getInstance().storeAccountStatus(accountStatus);
    }

    @Override
    protected ApiResponse<AccountStatus> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_account));

        bitso.waitAvailableCall();

        return bitso.accountStatus();
    }
}
