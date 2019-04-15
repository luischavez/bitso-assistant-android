package com.luischavezb.bitso.assistant.android;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Profile;
import com.luischavezb.bitso.assistant.android.adapter.BookAdapter;
import com.luischavezb.bitso.assistant.android.adapter.ProfileTypeAdapter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by luischavez on 20/03/18.
 */

public class ProfileFragment extends DialogFragment implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener {

    public interface ProfileEvents {

        void onProfilePremiumError(@StringRes int noticeId, Object... formats);
    }

    private static final String PROFILE_ID = "PROFILE_ID";

    private static final BigDecimal MXN_LIMIT = new BigDecimal("500");

    private Long mProfileId;

    private ProfileEvents mProfileEvents;

    private Spinner mTypeSpinner;
    private Spinner mBookSpinner;
    private Switch mEnabledSwitch;
    private TextInputLayout mAmountTextInputLayout;
    private TextInputLayout mBuyIfBelowTextInputLayout;
    private Switch mAutomaticSwitch;
    private TextInputLayout mMaxTimeResetBuyValueTextInputLayout;
    private TextInputLayout mTimeBetweenBuysTextInputLayout;
    private Switch mUseFeeSwitch;
    private Switch mDisableSwitch;

    @MainThread
    public void clear() {
        mProfileId = null;

        mBookSpinner.setEnabled(true);
        mTypeSpinner.setEnabled(true);

        mEnabledSwitch.setChecked(false);
        mAmountTextInputLayout.getEditText().setText("");
        mBuyIfBelowTextInputLayout.getEditText().setText("");
        mAutomaticSwitch.setChecked(false);
        mMaxTimeResetBuyValueTextInputLayout.getEditText().setText("");
        mTimeBetweenBuysTextInputLayout.getEditText().setText("");
        mUseFeeSwitch.setChecked(false);
        mDisableSwitch.setChecked(false);
    }

    private void displayValues() {
        if (!isAdded()) return;

        if (null == mProfileId) {
            clear();

            return;
        }

        Profile profile = AndroidAssistant.getInstance().getProfile(mProfileId);

        if (null == profile) {
            clear();

            return;
        }

        mBookSpinner.setEnabled(false);
        mTypeSpinner.setEnabled(false);

        mEnabledSwitch.setChecked(profile.isEnabled());
        mBookSpinner.setSelection(Arrays.binarySearch(Bitso.Book.values(), profile.getBook()));
        mTypeSpinner.setSelection(Arrays.binarySearch(Profile.Type.values(), profile.getType()));
        mAmountTextInputLayout.getEditText().setText(profile.getMaxAmount().toPlainString());
        mBuyIfBelowTextInputLayout.getEditText().setText(profile.getPrice().toPlainString());
        mAutomaticSwitch.setChecked(profile.isAutomatic());
        mMaxTimeResetBuyValueTextInputLayout.getEditText().setText(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(profile.getTimeResetPrice())));
        mTimeBetweenBuysTextInputLayout.getEditText().setText(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(profile.getTimeBetweenOperations())));
        mUseFeeSwitch.setChecked(profile.isUseFee());
        mDisableSwitch.setChecked(profile.isDisable());
    }

    @MainThread
    public void editProfile(Long id) {
        mProfileId = id;

        displayValues();
    }

    public Profile getProfile() {
        Profile.Type type = (Profile.Type) mTypeSpinner.getSelectedItem();
        Bitso.Book book = (Bitso.Book) mBookSpinner.getSelectedItem();
        boolean enabled = mEnabledSwitch.isChecked();
        BigDecimal amount = new BigDecimal(mAmountTextInputLayout.getEditText().getText().toString());
        BigDecimal price = new BigDecimal(mBuyIfBelowTextInputLayout.getEditText().getText().toString());
        boolean automatic = mAutomaticSwitch.isChecked();

        long timeResetPrice = mMaxTimeResetBuyValueTextInputLayout.getEditText().getText().toString().isEmpty()
                ? 0 : Long.valueOf(mMaxTimeResetBuyValueTextInputLayout.getEditText().getText().toString());
        timeResetPrice = TimeUnit.SECONDS.toMillis(timeResetPrice);

        long timeBetweenOperations = Long.valueOf(mTimeBetweenBuysTextInputLayout.getEditText().getText().toString());
        timeBetweenOperations = TimeUnit.SECONDS.toMillis(timeBetweenOperations);

        boolean useFee = mUseFeeSwitch.isChecked();
        boolean disable = mDisableSwitch.isChecked();

        if (null != mProfileId) {
            Profile profile = AndroidAssistant.getInstance().getProfile(mProfileId);

            if (null != profile) {
                profile.setEnabled(enabled);
                profile.setMaxAmount(amount);
                profile.setPrice(price);
                profile.setAutomatic(automatic);
                profile.setTimeResetPrice(timeResetPrice);
                profile.setTimeBetweenOperations(timeBetweenOperations);
                profile.setUseFee(useFee);
                profile.setDisable(disable);

                return null;
            }
        }

        return new Profile(type, book, enabled, amount, price, automatic,
                timeResetPrice, timeBetweenOperations, useFee, disable);
    }

    @MainThread
    public boolean validate() {
        boolean errors = false;

        Bitso.Book book = (Bitso.Book) mBookSpinner.getSelectedItem();

        if (!AssistantApplication.sPremium) {
            if (!Bitso.Book.BTC_MXN.equals(book)) {
                if (null != mProfileEvents) {
                    mProfileEvents.onProfilePremiumError(R.string.not_premium_book_notice, book.name());
                }

                errors = true;
            } else {
                BigDecimal amount = new BigDecimal(
                        mAmountTextInputLayout.getEditText().getText().toString().isEmpty()
                                ? "0" : mAmountTextInputLayout.getEditText().getText().toString());

                if (0 < amount.compareTo(MXN_LIMIT)) {
                    if (null != mProfileEvents) {
                        mProfileEvents.onProfilePremiumError(
                                R.string.not_premium_amount_notice,
                                Utilities.currencyFormat(MXN_LIMIT, book.minorCoin(), true));
                    }

                    errors = true;
                }
            }
        }

        if (mAmountTextInputLayout.getEditText().getText().toString().isEmpty()) {
            errors = true;
            mAmountTextInputLayout.setError(getString(R.string.required));
        } else {
            mAmountTextInputLayout.setError(null);
        }

        if (mBuyIfBelowTextInputLayout.getEditText().getText().toString().isEmpty()) {
            errors = true;
            mBuyIfBelowTextInputLayout.setError(getString(R.string.required));
        } else {
            mBuyIfBelowTextInputLayout.setError(null);
        }

        if (mAutomaticSwitch.isChecked() && mMaxTimeResetBuyValueTextInputLayout.getEditText().getText().toString().isEmpty()) {
            errors = true;
            mMaxTimeResetBuyValueTextInputLayout.setError(getString(R.string.required));
        } else {
            mMaxTimeResetBuyValueTextInputLayout.setError(null);
        }

        if (mTimeBetweenBuysTextInputLayout.getEditText().getText().toString().isEmpty()) {
            errors = true;
            mTimeBetweenBuysTextInputLayout.setError(getString(R.string.required));
        } else {
            mTimeBetweenBuysTextInputLayout.setError(null);
        }

        return !errors;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            mProfileId = savedInstanceState.getLong(PROFILE_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTypeSpinner = view.findViewById(R.id.type_spinner);
        mBookSpinner = view.findViewById(R.id.book_spinner);
        mEnabledSwitch = view.findViewById(R.id.enabled);
        mAmountTextInputLayout = view.findViewById(R.id.amount_text_input_layout);
        mBuyIfBelowTextInputLayout = view.findViewById(R.id.buy_if_below_text_input_layout);
        mAutomaticSwitch = view.findViewById(R.id.automatic_switch);
        mMaxTimeResetBuyValueTextInputLayout = view.findViewById(R.id.max_time_reset_buy_value_text_input_layout);
        mTimeBetweenBuysTextInputLayout = view.findViewById(R.id.time_between_buys_text_input_layout);
        mUseFeeSwitch = view.findViewById(R.id.use_fee_switch);
        mDisableSwitch = view.findViewById(R.id.disable_switch);

        mTypeSpinner.setAdapter(new ProfileTypeAdapter());
        mBookSpinner.setAdapter(new BookAdapter());
    }

    @Override
    public void onResume() {
        super.onResume();

        displayValues();

        mMaxTimeResetBuyValueTextInputLayout.getEditText().setEnabled(mAutomaticSwitch.isChecked());

        mAutomaticSwitch.setOnCheckedChangeListener(this);
        mTypeSpinner.setOnItemSelectedListener(this);
        mBookSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onPause() {
        mAutomaticSwitch.setOnCheckedChangeListener(null);
        mTypeSpinner.setOnItemSelectedListener(null);
        mBookSpinner.setOnItemSelectedListener(null);

        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ProfileEvents) {
            mProfileEvents = (ProfileEvents) context;
        }
    }

    @Override
    public void onDetach() {
        mProfileEvents = null;

        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != mProfileId) {
            outState.putLong(PROFILE_ID, mProfileId);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mMaxTimeResetBuyValueTextInputLayout.getEditText().setEnabled(mAutomaticSwitch.isChecked());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mDisableSwitch.setText(
                getString(
                        Profile.Type.BUY.equals(mTypeSpinner.getSelectedItem())
                                ? R.string.disable_at_buy : R.string.disable_at_sell));

        Bitso.Book book = (Bitso.Book) mBookSpinner.getSelectedItem();

        if (Profile.Type.BUY.equals(mTypeSpinner.getSelectedItem())) {
            mAmountTextInputLayout.setHint(getString(R.string.max_amount) + " (" + book.minorCoin() + ")");
            mBuyIfBelowTextInputLayout.setHint(getString(R.string.max_price) + " (" + book.minorCoin() + ")");
        } else {
            mAmountTextInputLayout.setHint(getString(R.string.max_amount) + " (" + book.majorCoin() + ")");
            mBuyIfBelowTextInputLayout.setHint(getString(R.string.max_price) + " (" + book.minorCoin() + ")");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
