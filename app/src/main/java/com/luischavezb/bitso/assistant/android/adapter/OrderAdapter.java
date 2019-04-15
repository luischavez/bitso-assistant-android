package com.luischavezb.bitso.assistant.android.adapter;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Order;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.Utilities;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by luischavez on 13/03/18.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    public interface OrderAdapterEvents {

        void onOrderAdapterItemSelected(Order order);
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private Order mOrder;

        private LinearLayout mItemLinearLayout;
        private ImageView mIconImageView;
        private TextView mAmountTextView;
        private TextView mTotalTextView;
        private TextView mPriceTextView;
        private TextView mHourTextView;

        public OrderViewHolder(View v) {
            super(v);

            mItemLinearLayout = v.findViewById(R.id.item_linear_layout);
            mIconImageView = v.findViewById(R.id.icon_image_view);
            mAmountTextView = v.findViewById(R.id.amount_text_view);
            mTotalTextView = v.findViewById(R.id.total_text_view);
            mPriceTextView = v.findViewById(R.id.price_text_view);
            mHourTextView = v.findViewById(R.id.hour_text_view);

            mItemLinearLayout.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            mOrderAdapterEvents.onOrderAdapterItemSelected(mOrder);

            return true;
        }
    }

    private final List<Order> mOrderList;
    private OrderAdapterEvents mOrderAdapterEvents;

    public OrderAdapter(List<Order> orderList, OrderAdapterEvents orderAdapterEvents) {
        mOrderList = orderList;
        mOrderAdapterEvents = orderAdapterEvents;
    }

    private void calculateDiff(List<Order> newOrderList, boolean add) {
        if (add) {
            for (Order order : mOrderList) {
                if (!newOrderList.contains(order)) {
                    newOrderList.add(order);
                }
            }
        }

        Collections.sort(newOrderList, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o2.getCreatedAt().compareTo(o1.getCreatedAt());
            }
        });

        DiffUtil.calculateDiff(new OrderDiffUtilCallback(mOrderList, newOrderList))
                .dispatchUpdatesTo(this);

        mOrderList.clear();
        mOrderList.addAll(newOrderList);
    }

    public void setOrders(List<Order> orders) {
        calculateDiff(orders, false);
    }

    public void removeOrder(String oid) {
        List<Order> orders = new ArrayList<>();

        for (Order order : mOrderList) {
            if (!oid.equals(order.getOid())) {
                orders.add(order);
            }
        }

        calculateDiff(orders, false);
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        Context context = AssistantApplication.getContext();

        Order order = mOrderList.get(position);

        holder.mOrder = order;

        Bitso.Book book = order.getBook();

        String side = order.getSide();

        String amount = Utilities.currencyFormat(order.getOriginalAmount(), book.majorCoin(), true);
        String unfilled = Utilities.currencyFormat(order.getUnfilledAmount(), book.majorCoin(), true);
        String price = Utilities.currencyFormat(order.getPrice(), book.minorCoin(), true);
        String value = Utilities.currencyFormat(order.getOriginalValue(), book.minorCoin(), true);

        if ("buy".equals(side)) {
            holder.mIconImageView.setImageResource(R.drawable.ic_buy);
            holder.mAmountTextView.setText(context.getString(R.string.amount_in, amount));
            holder.mTotalTextView.setText(value);
        } else {
            String a = Utilities.currencyFormat(
                    order.getOriginalAmount().multiply(order.getPrice()).setScale(8, RoundingMode.DOWN),
                    book.minorCoin(), true);
            holder.mIconImageView.setImageResource(R.drawable.ic_sell);
            holder.mAmountTextView.setText(context.getString(R.string.amount_in, a));
            holder.mTotalTextView.setText(amount);
        }

        holder.mPriceTextView.setText(context.getString(R.string.price_in, price));
        holder.mHourTextView.setText(Utilities.formatTime(order.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }
}
