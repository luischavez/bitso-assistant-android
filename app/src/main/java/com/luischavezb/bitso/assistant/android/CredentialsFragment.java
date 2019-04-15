package com.luischavezb.bitso.assistant.android;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luischavezb.bitso.assistant.android.db.DbContract;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

/**
 * Created by luischavez on 19/03/18.
 */

public class CredentialsFragment extends Fragment {

    private TextInputLayout mKeyTextInputLayout;
    private TextInputLayout mSecretTextInputLayout;
    private TextInputLayout mNipTextInputLayout;

    public String getKey() {
        return mKeyTextInputLayout.getEditText().getText().toString();
    }

    public String getSecret() {
        return mSecretTextInputLayout.getEditText().getText().toString();
    }

    public String getNip() {
        return mNipTextInputLayout.getEditText().getText().toString();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_credentials, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mKeyTextInputLayout = view.findViewById(R.id.key_text_input_layout);
        mSecretTextInputLayout = view.findViewById(R.id.secret_text_input_layout);
        mNipTextInputLayout = view.findViewById(R.id.nip_text_input_layout);

        DbHelper dbHelper = DbHelper.getInstance();
        String key = dbHelper.readConfigurationField(DbContract.ConfigurationEntry.COLUMN_NAME_API_KEY);
        String secret = dbHelper.readConfigurationField(DbContract.ConfigurationEntry.COLUMN_NAME_API_SECRET);

        mKeyTextInputLayout.getEditText().setText(key);
        mSecretTextInputLayout.getEditText().setText(secret);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
