package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

/**
 * Created by luischavez on 14/03/18.
 */

public class CancelOrderTask extends BitsoTask<String> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.CANCEL_ORDER_TASK";

    private final String mOid;

    public CancelOrderTask(String oid, boolean enableDialog, int... targets) {
        super(TAG, enableDialog, true, targets);
        mOid = oid;
    }

    @Override
    protected void onSuccess(String oid) {
        DbHelper.getInstance().deleteOpenOrder(oid);
    }

    @Override
    protected ApiResponse<String> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_cancel_order));

        bitso.waitAvailableCall();

        return bitso.cancel(mOid);
    }
}
