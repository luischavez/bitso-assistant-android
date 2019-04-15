package com.luischavezb.bitso.assistant.android.adapter;

import android.support.v7.util.DiffUtil;

import com.geometrycloud.bitso.assistant.library.Order;

import java.util.List;

/**
 * Created by luischavez on 13/03/18.
 */

public class OrderDiffUtilCallback extends DiffUtil.Callback {

    private List<Order> mOldOrderList;
    private List<Order> mNewOrderList;

    public OrderDiffUtilCallback(List<Order> oldOrderList,
                                 List<Order> newOrderList) {
        mOldOrderList = oldOrderList;
        mNewOrderList = newOrderList;
    }

    @Override
    public int getOldListSize() {
        return mOldOrderList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewOrderList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Order oldOrder = mOldOrderList.get(oldItemPosition);
        Order newOrder = mNewOrderList.get(newItemPosition);

        return oldOrder.equals(newOrder);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }
}
