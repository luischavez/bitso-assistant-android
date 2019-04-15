package com.luischavezb.bitso.assistant.android;

import android.app.Fragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.geometrycloud.bitso.assistant.library.AccountStatus;
import com.geometrycloud.bitso.assistant.library.FundingDestination;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by luischavez on 17/03/18.
 */

public class AccountFragment extends Fragment implements View.OnClickListener {

    public interface AccountEvents {

        void onAccountInitialized();

        void onAccountCredentialsEdit();
    }

    private AccountEvents mAccountEvents;

    private ImageView mAvatarImageView;

    private TextView mNameTextView;
    private TextView mEmailTextView;
    private TextView mPhoneTextView;
    private TextView mStatusTextView;

    private TextView mAccountTextView;

    private Button mUpdateAccountButton;

    private PieChart mDailyPieChart;
    private PieChart mMonthlyPieChart;

    @MainThread
    private void updateDailyChart(BigDecimal dailyLimit, BigDecimal dailyRemaining) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (0 < dailyLimit.subtract(dailyRemaining).compareTo(BigDecimal.ZERO)) {
            entries.add(new PieEntry(dailyLimit.subtract(dailyRemaining).floatValue(), "Utilizado", dailyLimit));
        }

        entries.add(new PieEntry(dailyRemaining.floatValue(), "Disponible", dailyRemaining));

        colors.add(getResources().getColor(R.color.color_primary));
        colors.add(getResources().getColor(R.color.color_primary_dark));

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.daily_chart_label));

        dataSet.setDrawValues(true);

        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.chart_text, outValue, true);
        float textSize = outValue.getFloat();

        dataSet.setColors(colors);
        dataSet.setValueTextColor(getResources().getColor(android.R.color.white));
        dataSet.setValueTextSize(textSize);
        //dataSet.setValueFormatter(new CurrencyValueFormatter(8));

        PieData pieData = new PieData();
        pieData.setDataSet(dataSet);

        mDailyPieChart.setData(pieData);

        pieData.notifyDataChanged();
        mDailyPieChart.invalidate();
    }

    @MainThread
    private void updateMonthlyChart(BigDecimal monthlyLimit, BigDecimal monthlyRemaining) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (0 < monthlyLimit.subtract(monthlyRemaining).compareTo(BigDecimal.ZERO)) {
            entries.add(new PieEntry(monthlyLimit.subtract(monthlyRemaining).floatValue(), "Utilizado", monthlyLimit));
        }

        entries.add(new PieEntry(monthlyRemaining.floatValue(), "Disponible", monthlyRemaining));

        colors.add(getResources().getColor(R.color.color_primary));
        colors.add(getResources().getColor(R.color.color_primary_dark));

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.monthly_chart_label));

        dataSet.setDrawValues(true);

        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.chart_text, outValue, true);
        float textSize = outValue.getFloat();

        dataSet.setColors(colors);
        dataSet.setValueTextColor(getResources().getColor(android.R.color.white));
        dataSet.setValueTextSize(textSize);
        //dataSet.setValueFormatter(new CurrencyValueFormatter(8));

        PieData pieData = new PieData();
        pieData.setDataSet(dataSet);

        mMonthlyPieChart.setData(pieData);

        pieData.notifyDataChanged();
        mMonthlyPieChart.invalidate();
    }

    @MainThread
    public void updateAccount(AccountStatus accountStatus) {
        mNameTextView.setText(accountStatus.getFirstName() + " " + accountStatus.getLastName());
        mEmailTextView.setText(accountStatus.getEmailStored());
        mPhoneTextView.setText(accountStatus.getCellphoneNumberStored() + " " + accountStatus.getCellphoneNumber());
        mStatusTextView.setText(accountStatus.getStatus());

        updateDailyChart(accountStatus.getDailyLimit(), accountStatus.getDailyRemaining());
        updateMonthlyChart(accountStatus.getMonthlyLimit(), accountStatus.getMonthlyRemaining());
    }

    @MainThread
    public void updateFundingDestination(FundingDestination fundingDestination) {
        mAccountTextView.setText(fundingDestination.getAccount());
    }

    @MainThread
    public void setAvatar(Bitmap bitmap) {
        mAvatarImageView.setImageBitmap(bitmap);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAvatarImageView = view.findViewById(R.id.avatar_image_view);

        mNameTextView = view.findViewById(R.id.name_text_view);
        mEmailTextView = view.findViewById(R.id.email_text_view);
        mPhoneTextView = view.findViewById(R.id.phone_text_view);
        mStatusTextView = view.findViewById(R.id.status_text_view);

        mAccountTextView = view.findViewById(R.id.account_text_view);

        mUpdateAccountButton = view.findViewById(R.id.update_credentials_button);

        mDailyPieChart = view.findViewById(R.id.daily_pie_chart);
        mMonthlyPieChart = view.findViewById(R.id.monthly_pie_chart);

        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.chart_text, outValue, true);
        float textSize = outValue.getFloat();

        Legend legend = mDailyPieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setFormSize(textSize);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(10f);

        mDailyPieChart.setDescription(null);
        mDailyPieChart.setHighlightPerTapEnabled(false);
        mDailyPieChart.setTouchEnabled(false);

        mDailyPieChart.setDrawEntryLabels(false);

        legend = mMonthlyPieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setFormSize(textSize);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(10f);

        mMonthlyPieChart.setDescription(null);
        mMonthlyPieChart.setHighlightPerTapEnabled(false);
        mMonthlyPieChart.setTouchEnabled(false);

        mMonthlyPieChart.setDrawEntryLabels(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (null != mAccountEvents) {
            mAccountEvents.onAccountInitialized();
        }

        mAccountTextView.setOnClickListener(this);
        mUpdateAccountButton.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        mAccountTextView.setOnClickListener(null);
        mUpdateAccountButton.setOnClickListener(null);

        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AccountEvents) {
            mAccountEvents = (AccountEvents) context;
        }
    }

    @Override
    public void onDetach() {
        mAccountEvents = null;

        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if (mUpdateAccountButton == v) {
            if (null != mAccountEvents) {
                mAccountEvents.onAccountCredentialsEdit();
            }
        } else if (mAccountTextView == v) {
            String id = getString(R.string.spei);
            String account = mAccountTextView.getText().toString();

            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(id, account);
            clipboard.setPrimaryClip(clip);

            FancyToast.makeText(getActivity(),
                    getString(R.string.copy_to_clipboard, id), Toast.LENGTH_SHORT, FancyToast.SUCCESS, false)
                    .show();
        }
    }
}
