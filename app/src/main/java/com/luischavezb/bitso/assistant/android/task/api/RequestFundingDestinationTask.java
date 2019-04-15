package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.FundingDestination;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

/**
 * Created by luischavez on 28/02/18.
 */

public class RequestFundingDestinationTask extends BitsoTask<FundingDestination> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.REQUEST_FUNDING_DESTINATION_TASK";

    private final String mCurrency;

    public RequestFundingDestinationTask(String currency, boolean enableDialog, int... targets) {
        super(TAG, enableDialog, true, targets);

        mCurrency = currency;
    }

    @Override
    protected void onSuccess(FundingDestination fundingDestination) {
        DbHelper.getInstance().storeFundingDestination(fundingDestination);
    }

    @Override
    protected ApiResponse<FundingDestination> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_funding_destination, mCurrency));

        if (AssistantApplication.sDebug) {
            return new ApiResponse<>(
                    new FundingDestination(mCurrency, "debug", "xxxxxxxxxxxxxxxxx"));
        }

        bitso.waitAvailableCall();

        return bitso.fundingDestination(mCurrency);
    }
}
