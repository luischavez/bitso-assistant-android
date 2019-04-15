package com.luischavezb.bitso.assistant.android;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.geometrycloud.bitso.assistant.library.Order;
import com.luischavezb.bitso.assistant.android.adapter.OrderAdapter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luischavez on 13/03/18.
 */

public abstract class TradeFragment extends Fragment implements View.OnClickListener,
        TextWatcher {

    public static class BuyFragment extends TradeFragment {

        @Override
        protected OrderType orderType() {
            return OrderType.BUY;
        }
    }

    public static class SellFragment extends TradeFragment {

        @Override
        protected OrderType orderType() {
            return OrderType.SELL;
        }
    }

    public interface TradeEvents {

        void onTradeInitialized();

        void onTradeCurrencyChanged(String currency);

        void onTradeEqualsButtonClick();

        void onTradePlaceBuyOrderButtonClick(BigDecimal amount, BigDecimal price, boolean minor);

        void onTradePlaceSellOrderButtonClick(BigDecimal amount, BigDecimal price, boolean minor);

        void onTradeRequestDeleteOrder(Order order);
    }

    private TradeEvents mTradeEvents;

    private TextInputLayout mAmountTextInputLayout;
    private EditText mAmountEditText;

    private TextInputLayout mPriceTextInputLayout;
    private EditText mPriceEditText;

    private ToggleButton mCurrencyToggleButton;

    private TextView mTotalTextView;

    private Button mEqualsPriceButton;
    private Button mPlaceBuyOrderButton;
    private Button mPlaceSellOrderButton;

    private RecyclerView mOrderRecyclerView;
    private RecyclerView.LayoutManager mOrderLayoutManager;
    private OrderAdapter mOrderAdapter;

    protected abstract OrderType orderType();

    @MainThread
    public void setCurrentPrice(BigDecimal price) {
        mPriceEditText.setText(price.toPlainString());
    }

    @MainThread
    public void setCurrency(String major, String minor) {
        mCurrencyToggleButton.setTextOn(minor);
        mCurrencyToggleButton.setTextOff(major);
        mCurrencyToggleButton.setText(mCurrencyToggleButton.isChecked() ? minor : major);

        mPriceEditText.setText("");

        mPriceTextInputLayout.setHint(getString(R.string.price_in, minor));
    }

    @MainThread
    public void setOrders(List<Order> orders) {
        mOrderAdapter.setOrders(orders);
    }

    @MainThread
    public void removeOrder(String oid) {
        mOrderAdapter.removeOrder(oid);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trade, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAmountTextInputLayout = view.findViewById(R.id.amount_text_input_layout);
        mAmountEditText = view.findViewById(R.id.amount_edit_text);

        mPriceTextInputLayout = view.findViewById(R.id.price_text_input_layout);
        mPriceEditText = view.findViewById(R.id.price_edit_text);

        mCurrencyToggleButton = view.findViewById(R.id.currency_toggle_button);

        mTotalTextView = view.findViewById(R.id.total_text_view);

        mEqualsPriceButton = view.findViewById(R.id.equals_price_button);
        mPlaceBuyOrderButton = view.findViewById(R.id.place_buy_order_button);
        mPlaceSellOrderButton = view.findViewById(R.id.place_sell_order_button);

        mOrderRecyclerView = view.findViewById(R.id.order_recycler_view);

        mOrderLayoutManager = new LinearLayoutManager(getActivity());
        mOrderAdapter = new OrderAdapter(new ArrayList<Order>(), new OrderAdapter.OrderAdapterEvents() {
            @Override
            public void onOrderAdapterItemSelected(final Order order) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.order));
                builder.setItems(getResources().getStringArray(R.array.order_menu), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (null != mTradeEvents) {
                                    mTradeEvents.onTradeRequestDeleteOrder(order);
                                }
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        mOrderRecyclerView.setHasFixedSize(false);
        mOrderRecyclerView.setNestedScrollingEnabled(false);

        mOrderRecyclerView.setLayoutManager(mOrderLayoutManager);
        mOrderRecyclerView.setAdapter(mOrderAdapter);

        if (OrderType.BUY.equals(orderType())) {
            mAmountTextInputLayout.setHint(getString(R.string.amount_to_get));

            mPlaceBuyOrderButton.setVisibility(View.VISIBLE);
            mPlaceSellOrderButton.setVisibility(View.GONE);
        } else {
            mAmountTextInputLayout.setHint(getString(R.string.amount_to_sell));

            mPlaceBuyOrderButton.setVisibility(View.GONE);
            mPlaceSellOrderButton.setVisibility(View.VISIBLE);
        }

        mAmountEditText.setText("");
        mPlaceBuyOrderButton.setEnabled(false);
        mPlaceSellOrderButton.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (null != mTradeEvents) {
            mTradeEvents.onTradeInitialized();
        }

        mAmountEditText.addTextChangedListener(this);
        mPriceEditText.addTextChangedListener(this);

        mCurrencyToggleButton.setOnClickListener(this);
        mEqualsPriceButton.setOnClickListener(this);
        mPlaceBuyOrderButton.setOnClickListener(this);
        mPlaceSellOrderButton.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        mAmountEditText.removeTextChangedListener(this);
        mPriceEditText.removeTextChangedListener(this);

        mCurrencyToggleButton.setOnClickListener(null);
        mEqualsPriceButton.setOnClickListener(null);
        mPlaceBuyOrderButton.setOnClickListener(null);
        mPlaceSellOrderButton.setOnClickListener(null);

        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof TradeEvents) {
            mTradeEvents = (TradeEvents) context;
        }
    }

    @Override
    public void onDetach() {
        mTradeEvents = null;

        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.currency_toggle_button:
                if (mCurrencyToggleButton.isChecked()) {
                    if (OrderType.BUY.equals(orderType())) {
                        mAmountTextInputLayout.setHint(getString(R.string.amount_to_buy));
                    } else {
                        mAmountTextInputLayout.setHint(getString(R.string.amount_to_get));
                    }
                } else {
                    if (OrderType.BUY.equals(orderType())) {
                        mAmountTextInputLayout.setHint(getString(R.string.amount_to_get));
                    } else {
                        mAmountTextInputLayout.setHint(getString(R.string.amount_to_sell));
                    }
                }

                mAmountEditText.setText("");

                if (null != mTradeEvents) {
                    mTradeEvents.onTradeCurrencyChanged(mCurrencyToggleButton.getText().toString());
                }
                break;
            case R.id.equals_price_button:
                if (null != mTradeEvents) {
                    mTradeEvents.onTradeEqualsButtonClick();
                }
                break;
            case R.id.place_buy_order_button:
                if (null != mTradeEvents) {
                    BigDecimal amount = new BigDecimal(mAmountEditText.getText().toString());
                    BigDecimal price = new BigDecimal(mPriceEditText.getText().toString());
                    boolean minor = mCurrencyToggleButton.isChecked();

                    mTradeEvents.onTradePlaceBuyOrderButtonClick(amount, price, minor);
                }
                break;
            case R.id.place_sell_order_button:
                if (null != mTradeEvents) {
                    BigDecimal amount = new BigDecimal(mAmountEditText.getText().toString());
                    BigDecimal price = new BigDecimal(mPriceEditText.getText().toString());
                    boolean minor = mCurrencyToggleButton.isChecked();

                    mTradeEvents.onTradePlaceSellOrderButtonClick(amount, price, minor);
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String amountString = mAmountEditText.getText().toString();
        String priceString = mPriceEditText.getText().toString();

        if (amountString.isEmpty() || priceString.isEmpty()) {
            amountString = "0";
            priceString = "0";
        }

        BigDecimal amount = new BigDecimal(amountString);
        BigDecimal price = new BigDecimal(priceString);

        BigDecimal total;

        String unit = mCurrencyToggleButton.isChecked()
                ? mCurrencyToggleButton.getTextOff().toString()
                : mCurrencyToggleButton.getTextOn().toString();

        if (!mCurrencyToggleButton.isChecked()) {
            total = amount.multiply(price);
        } else {
            if (0 == amount.compareTo(BigDecimal.ZERO) || 0 == price.compareTo(BigDecimal.ZERO)) {
                total = BigDecimal.ZERO;
            } else {
                int decimals = 8;

                if ("MXN".equals(unit)) {
                    decimals = 2;
                } else if ("XRP".equals(unit)) {
                    decimals = 6;
                }

                if (OrderType.BUY.equals(orderType())) {
                    total = amount.divide(price, decimals, RoundingMode.DOWN);
                } else {
                    total = amount.divide(price, decimals, RoundingMode.UP);
                }
            }
        }

        String totalString = total.toPlainString();
        if (!totalString.isEmpty()) {
            totalString = Utilities.currencyFormat(total, unit);
        }

        mTotalTextView.setText(getString(R.string.total_in, totalString, unit));

        mPlaceBuyOrderButton.setEnabled(0 < total.compareTo(BigDecimal.ZERO));
        mPlaceSellOrderButton.setEnabled(0 < total.compareTo(BigDecimal.ZERO));
    }
}
