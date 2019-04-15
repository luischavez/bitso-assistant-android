package com.luischavezb.bitso.assistant.android.task;

import com.geometrycloud.bitso.assistant.library.Funding;
import com.geometrycloud.bitso.assistant.library.Trade;
import com.geometrycloud.bitso.assistant.library.Withdrawal;

import java.util.List;

/**
 * Created by luischavez on 20/03/18.
 */

public class MovementsResult {

    private final List<Funding> mFundings;
    private final List<Trade> mTrades;
    private final List<Withdrawal> mWithdrawals;

    public MovementsResult(List<Funding> mFundings, List<Trade> mTrades, List<Withdrawal> mWithdrawals) {
        this.mFundings = mFundings;
        this.mTrades = mTrades;
        this.mWithdrawals = mWithdrawals;
    }

    public List<Funding> getFundings() {
        return mFundings;
    }

    public List<Trade> getTrades() {
        return mTrades;
    }

    public List<Withdrawal> getWithdrawals() {
        return mWithdrawals;
    }
}
