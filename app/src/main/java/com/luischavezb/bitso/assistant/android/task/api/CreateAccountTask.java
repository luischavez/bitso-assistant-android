package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.geometrycloud.bitso.assistant.library.AccountStatus;
import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Balance;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Funding;
import com.geometrycloud.bitso.assistant.library.FundingDestination;
import com.geometrycloud.bitso.assistant.library.Ticker;
import com.geometrycloud.bitso.assistant.library.Trade;
import com.geometrycloud.bitso.assistant.library.Withdrawal;
import com.luischavezb.bitso.assistant.android.AndroidAssistant;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.Utilities;
import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luischavez on 19/03/18.
 */

public class CreateAccountTask extends BitsoTask<Boolean> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.CREATE_ACCOUNT_TASK";

    private final String mKey;
    private final String mSecret;
    private final String mNip;
    private final boolean mNewAccount;

    public CreateAccountTask(final String key, final String secret, String nip, boolean newAccount, boolean enableDialog, int... targets) {
        super(TAG, new Bitso(AssistantApplication.sApiUrl, new Bitso.Storage() {
                    @Override
                    public Bitso.Credentials loadCredentials() {
                        return new Bitso.Credentials(key, secret);
                    }
                }),
                enableDialog, true, targets);

        mKey = key;
        mSecret = secret;
        mNip = nip;
        mNewAccount = newAccount;
    }

    @Override
    protected ApiResponse<Boolean> executeBitso(Bitso bitso) {
        final Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_validating_api));

        if (mKey.isEmpty() || mSecret.isEmpty()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    FancyToast.makeText(context,
                            context.getString(R.string.invalid_api_key_secret),
                            FancyToast.LENGTH_SHORT, FancyToast.ERROR, false)
                            .show();
                }
            });
            return new ApiResponse<>(false);
        }

        String phrase;

        publishProgress(context.getString(R.string.progress_validating_nip));

        if (mNip.isEmpty() || 4 > mNip.length() || null == (phrase = Utilities.encode(mNip, "BITSO"))) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    FancyToast.makeText(context,
                            context.getString(R.string.invalid_nip),
                            FancyToast.LENGTH_SHORT, FancyToast.ERROR, false)
                            .show();
                }
            });
            return new ApiResponse<>(false);
        }

        publishProgress(context.getString(R.string.progress_get_account));

        bitso.waitAvailableCall();
        ApiResponse<AccountStatus> accountStatus = bitso.accountStatus();

        if (!accountStatus.success()) {
            return new ApiResponse<>(false);
        }

        publishProgress(context.getString(R.string.progress_get_balances));

        bitso.waitAvailableCall();
        ApiResponse<List<Balance>> balances = bitso.balances();

        if (!balances.success()) {
            return new ApiResponse<>(false);
        }

        DbHelper dbHelper = DbHelper.getInstance();

        if (mNewAccount) {
            publishProgress(context.getString(R.string.progress_deleting_account));
            dbHelper.clear();

            AndroidAssistant.getInstance().getData().getProfiles().clear();
            AndroidAssistant.getInstance().saveData();
        }

        dbHelper.storeConfiguration(mKey, mSecret, phrase);
        dbHelper.storeAccountStatus(accountStatus.object());
        dbHelper.storeBalances(balances.object());

        publishProgress(context.getString(R.string.progress_get_tickers));

        ApiResponse<List<Ticker>> tickers = bitso.tickers();
        if (tickers.success()) {
            dbHelper.storeTickers(tickers.object());
        }

        if (!AssistantApplication.sDebug) {
            ArrayList<String> currencies = new ArrayList<>();
            currencies.add("mxn");

            for (Bitso.Book book : Bitso.Book.values()) {
                if (!currencies.contains(book.majorCoin().toLowerCase())) {
                    currencies.add(book.majorCoin().toLowerCase());
                }
            }

            for (String currency : currencies) {
                publishProgress(context.getString(R.string.progress_get_funding_destination, currency));

                bitso.waitAvailableCall();
                ApiResponse<FundingDestination> fundingDestination = bitso.fundingDestination(currency);

                if (fundingDestination.success()) {
                    dbHelper.storeFundingDestination(fundingDestination.object());
                }
            }
        }

        publishProgress(context.getString(R.string.progress_get_fundings));

        bitso.waitAvailableCall();
        ApiResponse<List<Funding>> fundings = bitso.fundings(new String[0]);

        if (fundings.success()) {
            DbHelper.getInstance().storeFundings(fundings.object());
        }

        publishProgress(context.getString(R.string.progress_get_trades));

        List<Trade> trades = RequestMovementsTask.getTrades(bitso, null);

        if (null != trades) {
            DbHelper.getInstance().storeTrades(trades);
        }

        publishProgress(context.getString(R.string.progress_get_withdrawals));

        bitso.waitAvailableCall();
        ApiResponse<List<Withdrawal>> withdrawals = bitso.withdrawals(new String[0]);

        if (withdrawals.success()) {
            DbHelper.getInstance().storeWithdrawals(withdrawals.object());
        }

        return new ApiResponse<>(true);
    }
}
