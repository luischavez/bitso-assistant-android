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
import android.widget.TextView;

import com.geometrycloud.bitso.assistant.library.History;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luischavez on 15/03/18.
 */

public class ChartFragment extends Fragment implements View.OnClickListener,
        OnChartValueSelectedListener {

    public interface ChartEvents {

        void onChartInitialized();

        void onChartRangeChanged(int range);
    }

    private static final String RANGE = "range";

    private ChartEvents mChartEvents;

    private LineChart mMarketLineChart;
    private BarChart mMarketVolumeBarChart;

    private Button mOneYearButton;
    private Button mThreeMonthsButton;
    private Button mOneMonthButton;
    private Button mAllButton;

    private TextView mMarketValueTextView;
    private TextView mMarketVolumeTextView;
    private TextView mMarketDateTextView;

    public int range() {
        int range = 0;

        if (!mOneYearButton.isEnabled()) {
            range = 12;
        } else if (!mThreeMonthsButton.isEnabled()) {
            range = 3;
        } else if (!mOneMonthButton.isEnabled()) {
            range = 1;
        }

        return range;
    }

    @MainThread
    public void updateCharts(List<History> histories) {
        mMarketValueTextView.setText("");
        mMarketVolumeTextView.setText("");
        mMarketDateTextView.setText("");

        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        float x = 0;

        for (History history : histories) {
            float value = history.getValue().floatValue();
            float volume = history.getVolume().floatValue();

            entries.add(new Entry(x++, value, history));
            barEntries.add(new BarEntry(x - 1, volume, history));
        }

        if (entries.isEmpty()) {
            return;
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setValueTextColor(ColorTemplate.getHoloBlue());
        lineDataSet.setLineWidth(1.5f);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setDrawValues(false);
        lineDataSet.setFillAlpha(65);
        lineDataSet.setDrawCircleHole(false);

        lineDataSet.setColor(AssistantApplication.getContext().getResources().getColor(R.color.color_primary));

        LineData lineData = new LineData(lineDataSet);
        lineData.setValueTextSize(9f);

        Highlight[] highlighted = mMarketLineChart.getHighlighted();

        if (null != highlighted && 0 < highlighted.length) {
            x = highlighted[0].getX();

            if (x < entries.size()) {
                Entry entry = entries.get((int) x);

                History history = (History) entry.getData();

                mMarketValueTextView.setText(Utilities.currencyFormat(history.getValue(), history.getBook().minorCoin(), true));
                mMarketDateTextView.setText(history.getDate());
            }
        }

        mMarketLineChart.setData(lineData);
        mMarketLineChart.invalidate();
        mMarketLineChart.notifyDataSetChanged();

        highlighted = mMarketVolumeBarChart.getHighlighted();

        if (null != highlighted && 0 < highlighted.length) {
            x = highlighted[0].getX();

            if (x < entries.size()) {
                Entry entry = entries.get((int) x);

                History history = (History) entry.getData();

                mMarketVolumeTextView.setText(Utilities.currencyFormat(history.getVolume(), history.getBook().majorCoin(), true));
            }
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "");

        barDataSet.setColor(AssistantApplication.getContext().getResources().getColor(R.color.color_primary));

        BarData barData = new BarData(barDataSet);

        mMarketVolumeBarChart.setData(barData);
        mMarketVolumeBarChart.invalidate();
        mMarketVolumeBarChart.notifyDataSetChanged();

        mMarketLineChart.highlightValue(x, 0);
        mMarketVolumeBarChart.highlightValue(x, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMarketLineChart = view.findViewById(R.id.market_line_chart);
        mMarketVolumeBarChart = view.findViewById(R.id.marke_volume_bar_chart);

        mOneYearButton = view.findViewById(R.id.one_year_button);
        mThreeMonthsButton = view.findViewById(R.id.three_months_button);
        mOneMonthButton = view.findViewById(R.id.one_month_button);
        mAllButton = view.findViewById(R.id.all_button);

        mMarketValueTextView = view.findViewById(R.id.market_value_text_view);
        mMarketVolumeTextView = view.findViewById(R.id.market_volume_text_view);
        mMarketDateTextView = view.findViewById(R.id.market_date_text_view);

        mMarketLineChart.getDescription().setEnabled(false);
        mMarketVolumeBarChart.getDescription().setEnabled(false);

        mMarketVolumeBarChart.setMaxVisibleValueCount(0);

        mMarketLineChart.setPinchZoom(false);
        mMarketVolumeBarChart.setPinchZoom(false);

        mMarketLineChart.disableScroll();
        mMarketVolumeBarChart.disableScroll();

        mMarketLineChart.setScaleEnabled(false);
        mMarketVolumeBarChart.setScaleEnabled(false);

        mMarketLineChart.getXAxis().setDrawGridLines(false);
        mMarketLineChart.getXAxis().setDrawLabels(false);
        mMarketLineChart.getAxisLeft().setEnabled(false);
        mMarketLineChart.getAxisRight().setEnabled(false);
        mMarketVolumeBarChart.getXAxis().setDrawGridLines(false);
        mMarketVolumeBarChart.getXAxis().setDrawLabels(false);
        mMarketVolumeBarChart.getAxisLeft().setEnabled(false);
        mMarketVolumeBarChart.getAxisRight().setEnabled(false);

        mMarketLineChart.getLegend().setEnabled(false);
        mMarketVolumeBarChart.getLegend().setEnabled(false);

        if (null != savedInstanceState) {
            switch (savedInstanceState.getInt(RANGE)) {
                case 1:
                    mOneMonthButton.setEnabled(false);
                    break;
                case 3:
                    mThreeMonthsButton.setEnabled(false);
                    break;
                case 12:
                    mOneYearButton.setEnabled(false);
                    break;
                default:
                    mAllButton.setEnabled(false);
            }
        } else {
            mOneMonthButton.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (null != mChartEvents) {
            mChartEvents.onChartInitialized();
        }

        mMarketLineChart.setOnChartValueSelectedListener(this);
        mMarketVolumeBarChart.setOnChartValueSelectedListener(this);

        mOneYearButton.setOnClickListener(this);
        mThreeMonthsButton.setOnClickListener(this);
        mOneMonthButton.setOnClickListener(this);
        mAllButton.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        mMarketLineChart.setOnChartValueSelectedListener(null);
        mMarketVolumeBarChart.setOnChartValueSelectedListener(null);

        mOneYearButton.setOnClickListener(null);
        mThreeMonthsButton.setOnClickListener(null);
        mOneMonthButton.setOnClickListener(null);
        mAllButton.setOnClickListener(null);

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(RANGE, range());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ChartEvents) {
            mChartEvents = (ChartEvents) context;
        }
    }

    @Override
    public void onDetach() {
        mChartEvents = null;

        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        mOneYearButton.setEnabled(true);
        mThreeMonthsButton.setEnabled(true);
        mOneMonthButton.setEnabled(true);
        mAllButton.setEnabled(true);

        v.setEnabled(false);

        if (null != mChartEvents) {
            mChartEvents.onChartRangeChanged(range());
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        History history = (History) e.getData();

        mMarketValueTextView.setText(
                Utilities.currencyFormat(history.getValue(), history.getBook().minorCoin(), true));
        mMarketVolumeTextView.setText(
                Utilities.currencyFormat(history.getVolume(), history.getBook().majorCoin(), true));
        mMarketDateTextView.setText(history.getDate());

        float x = e.getX();

        if (e instanceof BarEntry) {
            mMarketLineChart.setOnChartValueSelectedListener(null);
            mMarketLineChart.highlightValue(x, 0);
            mMarketLineChart.setOnChartValueSelectedListener(this);
        } else {
            mMarketVolumeBarChart.setOnChartValueSelectedListener(null);
            mMarketVolumeBarChart.highlightValue(x, 0);
            mMarketVolumeBarChart.setOnChartValueSelectedListener(this);
        }
    }

    @Override
    public void onNothingSelected() {

    }
}
