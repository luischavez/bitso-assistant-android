package com.luischavezb.bitso.assistant.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.TextView;

import com.luischavezb.bitso.assistant.android.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by luischavez on 15/02/18.
 */

public class LoadingDialog extends Dialog {

    private AVLoadingIndicatorView mIndicatorView;
    private TextView mStatusTextView;

    public LoadingDialog(@NonNull Context context) {
        super(context, false, null);
    }

    @MainThread
    public void setStatus(String status) {
        mStatusTextView.setText(status);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_loading);

        mIndicatorView = findViewById(R.id.indicator_view);
        mStatusTextView = findViewById(R.id.status_text_view);
    }

    @Override
    public void onStart() {
        super.onStart();

        mIndicatorView.show();
    }

    @Override
    public void onStop() {
        mIndicatorView.hide();

        super.onStop();
    }
}
