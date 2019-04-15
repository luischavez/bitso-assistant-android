package com.luischavezb.bitso.assistant.android;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by luischavez on 23/03/18.
 */

public class PremiumFragment extends Fragment implements View.OnClickListener {

    public interface PremiumEvents {

        void onPremiumRequest();
    }

    private PremiumEvents mPremiumEvents;

    private LinearLayout mPremiumLinearLayout;
    private LinearLayout mNotPremiumLinearLayout;
    private Button mPremiumButton;

    @MainThread
    public void updateStatus(boolean premium) {
        mPremiumLinearLayout.setVisibility(premium ? View.VISIBLE : View.GONE);
        mNotPremiumLinearLayout.setVisibility(premium ? View.GONE : View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_premium, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPremiumLinearLayout = view.findViewById(R.id.premium_linear_layout);
        mNotPremiumLinearLayout = view.findViewById(R.id.not_premium_linear_layout);
        mPremiumButton = view.findViewById(R.id.premium_button);
    }

    @Override
    public void onResume() {
        super.onResume();

        updateStatus(AssistantApplication.sPremium);

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

        if (context instanceof PremiumEvents) {
            mPremiumEvents = (PremiumEvents) context;
        }
    }

    @Override
    public void onDetach() {
        mPremiumEvents = null;

        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if (null != mPremiumEvents) {
            mPremiumEvents.onPremiumRequest();
        }
    }
}
