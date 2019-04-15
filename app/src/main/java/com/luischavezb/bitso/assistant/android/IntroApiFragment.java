package com.luischavezb.bitso.assistant.android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.github.paolorotolo.appintro.ISlidePolicy;
import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.mtramin.rxfingerprint.RxFingerprint;

/**
 * Created by luischavez on 12/02/18.
 */

public class IntroApiFragment extends Fragment implements ISlidePolicy {

    public interface OnValidate {

        void validate(String key, String secret, String nip, boolean useFingerprint);
    }

    private OnValidate mOnValidate;

    private TextInputLayout mKeyTextInputLayout;
    private TextInputLayout mSecretTextInputLayout;
    private TextInputLayout mNipTextInputLayout;
    private CheckBox mFingerprintCheckBox;

    private void validate() {
        if (null == mOnValidate) return;

        String key = mKeyTextInputLayout.getEditText().getText().toString();
        String secret = mSecretTextInputLayout.getEditText().getText().toString();
        String nip = mNipTextInputLayout.getEditText().getText().toString();
        boolean useFingerprint = mFingerprintCheckBox.isChecked();

        mOnValidate.validate(key, secret, nip, useFingerprint);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_intro_api, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mKeyTextInputLayout = view.findViewById(R.id.key_text_input_layout);
        mSecretTextInputLayout = view.findViewById(R.id.secret_text_input_layout);
        mNipTextInputLayout = view.findViewById(R.id.nip_text_input_layout);
        mFingerprintCheckBox = view.findViewById(R.id.fingerprint_check_box);

        if (!RxFingerprint.isAvailable(getActivity())) {
            mFingerprintCheckBox.setVisibility(View.INVISIBLE);
            mFingerprintCheckBox.setChecked(false);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnValidate) {
            mOnValidate = (OnValidate) context;
        }
    }

    @Override
    public void onDetach() {
        mOnValidate = null;

        super.onDetach();
    }

    @Override
    public boolean isPolicyRespected() {
        boolean valid = null != DbHelper.getInstance().readAccountStatus();

        if (valid) {
            mKeyTextInputLayout.getEditText().setEnabled(false);
            mSecretTextInputLayout.getEditText().setEnabled(false);
            mNipTextInputLayout.getEditText().setEnabled(false);
            mFingerprintCheckBox.setEnabled(false);
        }

        return valid;
    }

    @Override
    public void onUserIllegallyRequestedNextPage() {
        validate();
    }
}
