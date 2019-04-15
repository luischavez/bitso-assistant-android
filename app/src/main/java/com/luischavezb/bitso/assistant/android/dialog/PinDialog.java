package com.luischavezb.bitso.assistant.android.dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.Utilities;
import com.luischavezb.bitso.assistant.android.db.DbContract;
import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.mtramin.rxfingerprint.RxFingerprint;
import com.mtramin.rxfingerprint.data.FingerprintAuthenticationResult;
import com.shashank.sony.fancytoastlib.FancyToast;

import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;

/**
 * Created by luischavez on 15/02/18.
 */

public class PinDialog extends DialogFragment implements TextWatcher {

    public static final String EDIT_CREDENTIALS = "edit_credentials";
    public static final String SAVE_CREDENTIALS = "save_credentials";
    public static final String NEW_ACCOUNT = "new_account";
    public static final String NEW_PROFILE = "new_profile";
    public static final String EDIT_PROFILE = "edit_profile";
    public static final String DELETE_PROFILE = "delete_profile";
    public static final String PLACE_ORDER = "place_order";
    public static final String CANCEL_ORDER = "cancel_order";
    public static final String CHANGE_SERVICE = "change_service";

    private static final String TAG = "tag";
    private static final String ARGS = "args";
    private static final String MESSAGE = "message";

    public interface OnPinDialogResult {

        void onPinDialogResult(String tag, Bundle args, boolean confirmed);
    }

    private Disposable mFingerprintDisposable = Disposables.empty();

    private String mTag;
    private Bundle mArgs;

    private String mMessage;
    private OnPinDialogResult mOnPinDialogResult;

    private TextView mMessageTextView;

    private EditText mNipEditText;
    private TextView mFingerprintTextView;
    private ImageView mFingerprintImageView;

    private void onResult(boolean valid) {
        Activity activity = getActivity();

        if (null == activity) return;

        if (!valid) {
            FancyToast.makeText(getActivity(),
                    getString(R.string.pin_dialog_error), Toast.LENGTH_SHORT, FancyToast.ERROR, false)
                    .show();
        }

        if (null != mOnPinDialogResult) {
            mOnPinDialogResult.onPinDialogResult(mTag, mArgs, valid);
        }

        dismiss();
    }

    private void validate() {
        String nip = mNipEditText.getText().toString();
        String nipEncoded = DbHelper.getInstance()
                .readConfigurationField(DbContract.ConfigurationEntry.COLUMN_NAME_NIP_PHRASE_ENCODED);

        String inputNipEncoded = Utilities.encode(nip, "BITSO");

        boolean equals = nipEncoded.equals(inputNipEncoded);

        onResult(equals);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            mTag = savedInstanceState.getString(TAG);
            mArgs = savedInstanceState.getBundle(ARGS);
            mMessage = savedInstanceState.getString(MESSAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_pin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMessageTextView = view.findViewById(R.id.message_text_view);

        mNipEditText = view.findViewById(R.id.nip_edit_text);
        mFingerprintTextView = view.findViewById(R.id.fingerprint_text_view);
        mFingerprintImageView = view.findViewById(R.id.fingerprint_image_view);

        if (null == mMessage || mMessage.isEmpty()) {
            mMessageTextView.setVisibility(View.GONE);
        } else {
            mMessageTextView.setText(mMessage);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TAG, mTag);
        outState.putBundle(ARGS, mArgs);

        if (null != mMessage) {
            outState.putString(MESSAGE, mMessage);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();

        mNipEditText.addTextChangedListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        boolean fingerprintEnabled = preferences.getBoolean(getString(R.string.configuration_use_fingerprint), false);

        if (!fingerprintEnabled || RxFingerprint.isUnavailable(getActivity())) {
            mFingerprintTextView.setVisibility(View.GONE);
            mFingerprintImageView.setVisibility(View.GONE);
            return;
        }

        mFingerprintDisposable = RxFingerprint.authenticate(getActivity())
                .subscribe(new Consumer<FingerprintAuthenticationResult>() {
                    @Override
                    public void accept(FingerprintAuthenticationResult result) throws Exception {
                        onResult(result.isSuccess());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        onResult(false);
                    }
                });
    }

    @Override
    public void onPause() {
        mFingerprintDisposable.dispose();

        mNipEditText.removeTextChangedListener(this);

        mOnPinDialogResult = null;

        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnPinDialogResult) {
            mOnPinDialogResult = (OnPinDialogResult) context;
        }
    }

    @Override
    public void onDetach() {
        mOnPinDialogResult = null;

        super.onDetach();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (4 <= s.toString().length()) {
            validate();
        }
    }

    public static PinDialog getInstance(String message, String tag, Bundle args) {
        PinDialog dialog = new PinDialog();
        dialog.mMessage = message;
        dialog.mTag = tag;
        dialog.mArgs = args;

        return dialog;
    }

    public static PinDialog getInstance(String message, String tag) {
        return getInstance(message, tag, new Bundle());
    }

    public static PinDialog getInstance(String tag, Bundle args) {
        return getInstance("", tag, args);
    }

    public static PinDialog getInstance(String tag) {
        return getInstance(tag, new Bundle());
    }
}
