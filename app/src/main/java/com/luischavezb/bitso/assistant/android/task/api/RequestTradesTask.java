package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Trade;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luischavez on 28/02/18.
 */

public class RequestTradesTask extends BitsoTask<List<Trade>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.REQUEST_TRADES_TASK";

    private final String mMarker;

    public RequestTradesTask(String marker, boolean enableDialog, int... targets) {
        super(TAG, enableDialog, true, targets);

        mMarker = marker;
    }

    @Override
    protected void onSuccess(List<Trade> trades) {
        DbHelper.getInstance().storeTrades(trades);
    }

    private List<Trade> getTrades(Bitso bitso, String marker) {
        bitso.waitAvailableCall();
        ApiResponse<List<Trade>> trades = bitso.trades(marker);

        ArrayList<Trade> list = new ArrayList<>();

        if (trades.success()) {
            list.addAll(trades.object());

            Trade lastTrade = trades.object().get(trades.object().size() - 1);

            List<Trade> moreTrades = getTrades(bitso, lastTrade.getOid());

            if (null != moreTrades) {
                list.addAll(moreTrades);
            }

            return list;
        }

        return null;
    }

    @Override
    protected ApiResponse<List<Trade>> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_trades));

        return new ApiResponse<>(getTrades(bitso, mMarker));
    }
}
