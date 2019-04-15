package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Balance;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.List;

/**
 * Created by luischavez on 28/02/18.
 */

public class RequestBalancesTask extends BitsoTask<List<Balance>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.REQUEST_BALANCES_TASK";

    public RequestBalancesTask(boolean enableDialog, int... targets) {
        super(TAG, enableDialog, true, targets);
    }

    @Override
    protected void onSuccess(List<Balance> balances) {
        DbHelper.getInstance().storeBalances(balances);
    }

    @Override
    protected ApiResponse<List<Balance>> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_balances));

        bitso.waitAvailableCall();

        return bitso.balances();
    }
}
