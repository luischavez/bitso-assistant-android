package com.luischavezb.bitso.assistant.android.task.db;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.Funding;
import com.geometrycloud.bitso.assistant.library.Trade;
import com.geometrycloud.bitso.assistant.library.Withdrawal;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.luischavezb.bitso.assistant.android.task.MovementsResult;

import java.util.List;

/**
 * Created by luischavez on 01/03/18.
 */

public class LoadMovementsTask extends DbTask<MovementsResult> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.LOAD_MOVEMENTS_TASK";

    public LoadMovementsTask(boolean enableDialog, int... targets) {
        super(TAG, enableDialog, false, targets);
    }

    @Override
    protected MovementsResult execute(DbHelper dbHelper) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_fundings));

        List<Funding> fundings = dbHelper.readFundings();

        publishProgress(context.getString(R.string.progress_get_trades));

        List<Trade> trades = dbHelper.readTrades();

        publishProgress(context.getString(R.string.progress_get_withdrawals));

        List<Withdrawal> withdrawals = dbHelper.readWithdrawals();

        return new MovementsResult(fundings, trades, withdrawals);
    }
}
