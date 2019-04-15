package com.luischavezb.bitso.assistant.android;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geometrycloud.bitso.assistant.library.Balance;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Funding;
import com.geometrycloud.bitso.assistant.library.Ticker;
import com.geometrycloud.bitso.assistant.library.Trade;
import com.geometrycloud.bitso.assistant.library.Withdrawal;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.luischavezb.bitso.assistant.android.adapter.MovementAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luischavez on 28/02/18.
 */

public class WalletFragment extends Fragment {

    public interface WalletEvents {

        void onWalletInitialized();
    }

    private List<Balance> mBalances;

    private WalletEvents mWalletEvents;

    private PieChart mBalancePieChart;
    private PieData mBalancePieData;

    private TextView mBalanceTotalTextView;

    private RecyclerView mMovementRecyclerView;
    private RecyclerView.LayoutManager mMovementLayoutManager;
    private MovementAdapter mMovementAdapter;

    @MainThread
    public void updateTotalBalance(List<Ticker> tickers) {
        if (null == mBalances) return;

        BigDecimal btcPrice = BigDecimal.ZERO;

        for (Ticker ticker : tickers) {
            Bitso.Book book = ticker.getBook();
            if (Bitso.Book.BTC_MXN.equals(book)) {
                btcPrice = ticker.getLast();
                break;
            }
        }

        BigDecimal total = BigDecimal.ZERO;

        for (Balance balance : mBalances) {
            String currency = balance.getCurrency().toUpperCase();
            BigDecimal balanceTotal = balance.getTotal();

            if ("MXN".equals(currency)) {
                total = total.add(balanceTotal);
                continue;
            }

            for (Ticker ticker : tickers) {
                Bitso.Book book = ticker.getBook();
                BigDecimal last = ticker.getLast();

                if ("BCH".equals(currency)) {
                    total = total.add(balanceTotal.multiply(last).multiply(btcPrice));
                    break;
                }

                if (currency.equals(book.majorCoin()) && "MXN".equals(book.minorCoin())) {
                    total = total.add(balanceTotal.multiply(last));
                    break;
                }
            }
        }

        mBalanceTotalTextView.setText(Utilities.currencyFormat(total, "MXN"));

        updateBalanceChart(mBalances, tickers);
    }

    @MainThread
    public void updateBalanceChart(List<Balance> balances, List<Ticker> tickers) {
        mBalances = balances;

        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (null != tickers) {
            BigDecimal btcPrice = BigDecimal.ZERO;

            for (Ticker ticker : tickers) {
                Bitso.Book book = ticker.getBook();
                if (Bitso.Book.BTC_MXN.equals(book)) {
                    btcPrice = ticker.getLast();
                    break;
                }
            }

            ArrayList<Balance> convertedBalances = new ArrayList<>();

            for (Balance balance : mBalances) {
                String currency = balance.getCurrency().toUpperCase();

                if ("MXN".equals(currency)) {
                    convertedBalances.add(balance);
                    continue;
                }

                for (Ticker ticker : tickers) {
                    Bitso.Book book = ticker.getBook();
                    BigDecimal last = ticker.getLast();

                    if ("BCH".equals(currency)) {
                        convertedBalances.add(new Balance(
                                currency,
                                balance.getAvailable().multiply(last).multiply(btcPrice),
                                balance.getLocked().multiply(last).multiply(btcPrice),
                                balance.getTotal().multiply(last).multiply(btcPrice)));
                        break;
                    }

                    if (currency.equals(book.majorCoin()) && "MXN".equals(book.minorCoin())) {
                        convertedBalances.add(new Balance(
                                currency,
                                balance.getAvailable().multiply(last),
                                balance.getLocked().multiply(last),
                                balance.getTotal().multiply(last)));
                        break;
                    }
                }
            }

            for (Balance balance : convertedBalances) {
                String currency = balance.getCurrency();
                float y = balance.getTotal().floatValue();

                if (0 == y) continue;

                PieEntry entry = new PieEntry(y, currency, "MXN");

                entries.add(entry);
                colors.add(getResources().getColor(Utilities.color(currency)));
            }
        } else {
            for (Balance balance : balances) {
                String currency = balance.getCurrency();
                float y = balance.getTotal().floatValue();

                if (0 == y) continue;

                PieEntry entry = new PieEntry(y, currency, currency);

                entries.add(entry);
                colors.add(getResources().getColor(Utilities.color(currency)));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.balance_chart_label));

        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.chart_text, outValue, true);
        float textSize = outValue.getFloat();

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        dataSet.setColors(colors);
        dataSet.setValueTextColor(getResources().getColor(R.color.graph_value));
        dataSet.setValueTextSize(textSize);
        dataSet.setValueFormatter(new CurrencyValueFormatter(8));

        mBalancePieData = new PieData();
        mBalancePieData.setDataSet(dataSet);

        mBalancePieChart.setData(mBalancePieData);

        mBalancePieData.notifyDataChanged();
        mBalancePieChart.invalidate();
    }

    @MainThread
    public void setMovements(List<Trade> trades, List<Funding> fundings, List<Withdrawal> withdrawals) {
        mMovementAdapter.setMovements(trades, fundings, withdrawals);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wallet, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBalancePieChart = view.findViewById(R.id.balance_pie_chart);

        mBalanceTotalTextView = view.findViewById(R.id.balance_total_text_view);

        mMovementRecyclerView = view.findViewById(R.id.movement_recycler_view);

        mBalancePieChart.setDragDecelerationFrictionCoef(0.99f);

        mBalancePieChart.setDescription(null);
        mBalancePieChart.setHighlightPerTapEnabled(false);

        mBalancePieChart.setDrawEntryLabels(false);

        TypedValue outValue = new TypedValue();
        getResources().getValue(R.dimen.chart_text, outValue, true);
        float textSize = outValue.getFloat();

        Legend legend = mBalancePieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setFormSize(textSize);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(10f);

        mMovementLayoutManager = new LinearLayoutManager(getActivity());
        mMovementAdapter = new MovementAdapter(new ArrayList<MovementAdapter.Movement>());

        mMovementRecyclerView.setHasFixedSize(false);
        mMovementRecyclerView.setNestedScrollingEnabled(false);

        mMovementRecyclerView.setLayoutManager(mMovementLayoutManager);
        mMovementRecyclerView.setAdapter(mMovementAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (null != mWalletEvents) {
            mWalletEvents.onWalletInitialized();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof WalletEvents) {
            mWalletEvents = (WalletEvents) context;
        }
    }

    @Override
    public void onDetach() {
        mWalletEvents = null;

        super.onDetach();
    }
}
