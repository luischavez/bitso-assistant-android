package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Funding;
import com.geometrycloud.bitso.assistant.library.Trade;
import com.geometrycloud.bitso.assistant.library.Withdrawal;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.luischavezb.bitso.assistant.android.task.MovementsResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luischavez on 20/03/18.
 */

public class RequestMovementsTask extends BitsoTask<MovementsResult> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.REQUEST_MOVEMENTS_TASK";

    private final String[] mFids;
    private final String mTradeMarker;
    private final String[] mWids;

    public RequestMovementsTask(String[] fids, String tradeMarker, String[] wids, boolean enableDialog, int... targets) {
        super(TAG, enableDialog, true, targets);

        mFids = null == fids ? new String[0] : fids;
        mTradeMarker = tradeMarker;
        mWids = null == wids ? new String[0] : wids;
    }

    public static List<Trade> getTrades(Bitso bitso, String marker) {
        bitso.waitAvailableCall();
        ApiResponse<List<Trade>> trades = bitso.trades(marker);

        ArrayList<Trade> list = new ArrayList<>();

        if (trades.success() && 0 < trades.object().size()) {
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
    protected ApiResponse<MovementsResult> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_fundings));

        bitso.waitAvailableCall();
        ApiResponse<List<Funding>> fundings = bitso.fundings(mFids);

        if (fundings.success()) {
            DbHelper.getInstance().storeFundings(fundings.object());
        }

        publishProgress(context.getString(R.string.progress_get_trades));

        List<Trade> trades = getTrades(bitso, mTradeMarker);

        if (null != trades) {
            DbHelper.getInstance().storeTrades(trades);
        }

        publishProgress(context.getString(R.string.progress_get_withdrawals));

        bitso.waitAvailableCall();
        ApiResponse<List<Withdrawal>> withdrawals = bitso.withdrawals(mWids);

        if (withdrawals.success()) {
            DbHelper.getInstance().storeWithdrawals(withdrawals.object());
        }

        return new ApiResponse<>(
                new MovementsResult(fundings.object(), trades, withdrawals.object()));
    }
}
