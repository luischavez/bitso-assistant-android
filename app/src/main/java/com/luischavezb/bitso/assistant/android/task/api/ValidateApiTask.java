package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.AccountStatus;
import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

/**
 * Created by luischavez on 28/02/18.
 */

public class ValidateApiTask extends BitsoTask<ValidateApiTask.ValidationResult> {

    public static class ValidationResult {

        private final boolean mValid;
        private final boolean mNewAccount;
        private final String mKey;
        private final String mSecret;
        private final String mNip;

        public ValidationResult(boolean mValid, boolean newAccount, String mKey, String mSecret, String mNip) {
            this.mValid = mValid;
            this.mNewAccount = newAccount;
            this.mKey = mKey;
            this.mSecret = mSecret;
            this.mNip = mNip;
        }

        public boolean isValid() {
            return mValid;
        }

        public boolean isNewAccount() {
            return mNewAccount;
        }

        public String getKey() {
            return mKey;
        }

        public String getSecret() {
            return mSecret;
        }

        public String getNip() {
            return mNip;
        }
    }

    private final String mKey;
    private final String mSecret;
    private final String mNip;

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.VALIDATE_API_TASK";

    public ValidateApiTask(final String key, final String secret, String nip, boolean enableDialog, int... targets) {
        super(TAG, new Bitso(AssistantApplication.sApiUrl, new Bitso.Storage() {
            @Override
            public Bitso.Credentials loadCredentials() {
                return new Bitso.Credentials(key, secret);
            }
        }), enableDialog, true, targets);

        mKey = key;
        mSecret = secret;
        mNip = nip;
    }

    @Override
    protected ApiResponse<ValidationResult> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_account));

        bitso.waitAvailableCall();
        ApiResponse<AccountStatus> accountStatus = bitso.accountStatus();

        boolean valid = accountStatus.success();
        boolean newAccount = false;

        if (valid) {
            AccountStatus oldAccountStatus = DbHelper.getInstance().readAccountStatus();

            if (null != oldAccountStatus) {
                newAccount = oldAccountStatus.getClientId() != accountStatus.object().getClientId();
            }
        }

        return new ApiResponse<>(new ValidationResult(valid, newAccount, mKey, mSecret, mNip));
    }
}
