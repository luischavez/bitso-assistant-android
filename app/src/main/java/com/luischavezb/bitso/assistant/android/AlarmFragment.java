package com.luischavezb.bitso.assistant.android;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Switch;

import com.geometrycloud.bitso.assistant.library.Bitso;
import com.luischavezb.bitso.assistant.android.adapter.BookAdapter;
import com.luischavezb.bitso.assistant.android.adapter.ConditionAdapter;
import com.luischavezb.bitso.assistant.android.alarm.Alarm;
import com.luischavezb.bitso.assistant.android.alarm.Condition;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.Arrays;

/**
 * Created by luischavez on 28/03/18.
 */

public class AlarmFragment extends Fragment {

    private static final String ALARM_ID = "ALARM_ID";

    private Long mAlarmId;

    private Switch mEnabledSwitch;
    private Spinner mBookSpinner;
    private Spinner mConditionSpinner;
    private TextInputLayout mValueTextInputLayout;

    @MainThread
    public void clear() {
        mEnabledSwitch.setChecked(false);
        mValueTextInputLayout.getEditText().setText("");

        mAlarmId = null;
    }

    private void displayValues() {
        if (!isAdded()) return;

        if (null == mAlarmId) {
            clear();

            return;
        }

        Alarm alarm = DbHelper.getInstance().readAlarm(mAlarmId);

        if (null == alarm) {
            clear();

            return;
        }

        mEnabledSwitch.setChecked(alarm.isEnabled());
        mBookSpinner.setSelection(Arrays.binarySearch(Bitso.Book.values(), alarm.getBook()));
        mConditionSpinner.setSelection(Arrays.binarySearch(Condition.values(), alarm.getCondition()));
        mValueTextInputLayout.getEditText().setText(alarm.getValue());
    }

    @MainThread
    public void editAlarm(Long id) {
        mAlarmId = id;

        //displayValues();
    }

    public Alarm getAlarm() {
        boolean enabled = mEnabledSwitch.isChecked();
        Bitso.Book book = (Bitso.Book) mBookSpinner.getSelectedItem();
        Condition condition = (Condition) mConditionSpinner.getSelectedItem();
        String value = mValueTextInputLayout.getEditText().getText().toString();

        if (null != mAlarmId) {
            Alarm alarm = DbHelper.getInstance().readAlarm(mAlarmId);

            if (null != alarm) {
                alarm.setEnabled(enabled);
                alarm.setBook(book);
                alarm.setCondition(condition);
                alarm.setValue(value);

                return alarm;
            }
        }

        return new Alarm(enabled, book, condition, value);
    }

    @MainThread
    public boolean validate() {
        boolean errors = false;

        if (mValueTextInputLayout.getEditText().getText().toString().isEmpty()) {
            errors = true;
            mValueTextInputLayout.setError(getString(R.string.required));
        } else {
            mValueTextInputLayout.setError(null);
        }

        return !errors;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != savedInstanceState) {
            mAlarmId = savedInstanceState.getLong(ALARM_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEnabledSwitch = view.findViewById(R.id.enabled_switch);
        mBookSpinner = view.findViewById(R.id.book_spinner);
        mConditionSpinner = view.findViewById(R.id.condition_spinner);
        mValueTextInputLayout = view.findViewById(R.id.value_text_input_layout);

        mBookSpinner.setAdapter(new BookAdapter());
        mConditionSpinner.setAdapter(new ConditionAdapter());
    }

    @Override
    public void onResume() {
        super.onResume();

        displayValues();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (null != mAlarmId) {
            outState.putLong(ALARM_ID, mAlarmId);
        }

        super.onSaveInstanceState(outState);
    }
}
