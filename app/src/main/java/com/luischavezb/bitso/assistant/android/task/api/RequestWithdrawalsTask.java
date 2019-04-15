package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Withdrawal;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.List;

/**
 * Created by luischavez on 28/02/18.
 */

public class RequestWithdrawalsTask extends BitsoTask<List<Withdrawal>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.REQUEST_WITHDRAWALS_TASK";

    private final String[] mWids;

    public RequestWithdrawalsTask(String[] wids, boolean enableDialog, int... targets) {
        super(TAG, enableDialog, true, targets);

        mWids = null == wids ? new String[0] : wids;
    }

    @Override
    protected void onSuccess(List<Withdrawal> withdrawals) {
        DbHelper.getInstance().storeWithdrawals(withdrawals);
    }

    @Override
    protected ApiResponse<List<Withdrawal>> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_withdrawals));

        bitso.waitAvailableCall();

        return bitso.withdrawals(mWids);
    }
}
