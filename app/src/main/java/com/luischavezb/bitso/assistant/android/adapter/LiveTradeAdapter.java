package com.luischavezb.bitso.assistant.android.adapter;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.WebSocketOrder;
import com.luischavezb.bitso.assistant.android.OrderType;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.Utilities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by luischavez on 16/03/18.
 */

public class LiveTradeAdapter extends RecyclerView.Adapter<LiveTradeAdapter.LiveTradeViewHolder> {

    public class LiveTradeViewHolder extends RecyclerView.ViewHolder {

        private WebSocketOrder mOrder;

        private TextView mPriceTextView;
        private TextView mAmountTextView;
        private TextView mValueTextView;

        public LiveTradeViewHolder(View v) {
            super(v);

            mPriceTextView = v.findViewById(R.id.price_text_view);
            mAmountTextView = v.findViewById(R.id.amount_text_view);
            mValueTextView = v.findViewById(R.id.value_text_view);
        }
    }

    private final OrderType mOrderType;
    private final List<WebSocketOrder> mOrderList;

    public LiveTradeAdapter(OrderType orderType, List<WebSocketOrder> orderList) {
        mOrderType = orderType;
        mOrderList = orderList;
    }

    public void clear() {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new LiveTradeDiffUtilCallback(mOrderList, new ArrayList<WebSocketOrder>()));
        diffResult.dispatchUpdatesTo(this);

        mOrderList.clear();
    }

    public void setOrders(List<WebSocketOrder> orders) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new LiveTradeDiffUtilCallback(mOrderList, orders));
        diffResult.dispatchUpdatesTo(this);

        mOrderList.clear();
        mOrderList.addAll(orders);
    }

    public void addOrder(WebSocketOrder order) {
        ArrayList<WebSocketOrder> newOrders = new ArrayList<>(mOrderList);
        newOrders.add(order);

        Collections.sort(newOrders, new Comparator<WebSocketOrder>() {
            @Override
            public int compare(WebSocketOrder o1, WebSocketOrder o2) {
                if (OrderType.SELL.equals(mOrderType)) {
                    return o1.getRate().compareTo(o2.getRate());
                }

                return o2.getRate().compareTo(o1.getRate());
            }
        });

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new LiveTradeDiffUtilCallback(mOrderList, newOrders));
        diffResult.dispatchUpdatesTo(this);

        mOrderList.clear();
        mOrderList.addAll(newOrders);
    }

    public void removeOrder(WebSocketOrder order) {
        ArrayList<WebSocketOrder> newOrders = new ArrayList<>(mOrderList);
        newOrders.remove(order);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new LiveTradeDiffUtilCallback(mOrderList, newOrders));
        diffResult.dispatchUpdatesTo(this);

        mOrderList.clear();
        mOrderList.addAll(newOrders);
    }

    @Override
    public LiveTradeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_live_trade, parent, false);

        LiveTradeViewHolder holder = new LiveTradeViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(LiveTradeViewHolder holder, int position) {
        WebSocketOrder order = mOrderList.get(position);

        holder.mOrder = order;

        Bitso.Book book = order.getBook();

        BigDecimal rate = order.getRate();
        BigDecimal amount = order.getAmount();
        BigDecimal value = order.getValue();

        String rateString = Utilities.currencyFormat(rate, book.minorCoin());
        String amountString = amount.toPlainString();
        String valueString = Utilities.currencyFormat(value, book.minorCoin());

        holder.mPriceTextView.setText(String.format("%s %s", rateString, book.minorCoin()));
        holder.mAmountTextView.setText(String.format("%s %s", amountString, book.majorCoin()));
        holder.mValueTextView.setText(String.format("%s %s", valueString, book.minorCoin()));
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }
}
