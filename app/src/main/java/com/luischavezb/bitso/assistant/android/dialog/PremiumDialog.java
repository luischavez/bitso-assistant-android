package com.luischavezb.bitso.assistant.android.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.luischavezb.bitso.assistant.android.R;

/**
 * Created by luischavez on 23/03/18.
 */

public class PremiumDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "tag";

    public interface OnPremiumDialogResult {

        void onPremiumDialogResult(String tag, boolean confirmed);
    }

    private String mTag;

    private String mMessage;
    private OnPremiumDialogResult mOnPremiumDialogResult;

    private TextView mPremiumTextView;
    private Button mPremiumButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            mTag = savedInstanceState.getString(TAG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_premium, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPremiumTextView = view.findViewById(R.id.premium_text_view);
        mPremiumButton = view.findViewById(R.id.premium_button);

        mPremiumTextView.setText(mMessage);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(TAG, mTag);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);

        if (null != mOnPremiumDialogResult) {
            mOnPremiumDialogResult.onPremiumDialogResult(mTag, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mPremiumButton.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        mPremiumButton.setOnClickListener(null);

        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnPremiumDialogResult) {
            mOnPremiumDialogResult = (OnPremiumDialogResult) context;
        }
    }

    @Override
    public void onDetach() {
        mOnPremiumDialogResult = null;

        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if (null != mOnPremiumDialogResult) {
            mOnPremiumDialogResult.onPremiumDialogResult(mTag, true);
            mOnPremiumDialogResult = null;
            dismiss();
        }
    }

    public static PremiumDialog getInstance(String message, String tag) {
        PremiumDialog dialog = new PremiumDialog();
        dialog.mMessage = message;
        dialog.mTag = tag;

        return dialog;
    }

    public static PremiumDialog getInstance(String tag) {
        return getInstance("", tag);
    }
}
