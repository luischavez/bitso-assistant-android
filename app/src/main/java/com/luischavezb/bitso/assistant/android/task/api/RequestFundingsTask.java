package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Funding;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.List;

/**
 * Created by luischavez on 28/02/18.
 */

public class RequestFundingsTask extends BitsoTask<List<Funding>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.REQUEST_FUNDINGS_TASK";

    private final String[] mFids;

    public RequestFundingsTask(String[] fids, boolean enableDialog, int... targets) {
        super(TAG, enableDialog, true, targets);

        mFids = null == fids ? new String[0] : fids;
    }

    @Override
    protected void onSuccess(List<Funding> fundings) {
        DbHelper.getInstance().storeFundings(fundings);
    }

    @Override
    protected ApiResponse<List<Funding>> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_fundings));

        bitso.waitAvailableCall();

        return bitso.fundings(mFids);
    }
}
