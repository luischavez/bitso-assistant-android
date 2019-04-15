package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Ticker;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.List;

/**
 * Created by luischavez on 28/02/18.
 */

public class RequestTickersTask extends BitsoTask<List<Ticker>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.REQUEST_TICKERS_TASK";

    public RequestTickersTask(boolean enableDialog, int... targets) {
        super(TAG, enableDialog, false, targets);
    }

    @Override
    protected void onSuccess(List<Ticker> tickers) {
        DbHelper.getInstance().storeTickers(tickers);
    }

    @Override
    protected ApiResponse<List<Ticker>> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_tickers));

        return bitso.tickers();
    }
}
