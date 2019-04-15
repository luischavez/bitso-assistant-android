package com.luischavezb.bitso.assistant.android.adapter;

import android.support.v7.util.DiffUtil;

import com.geometrycloud.bitso.assistant.library.WebSocketOrder;

import java.util.List;

/**
 * Created by luischavez on 11/02/18.
 */

public class LiveTradeDiffUtilCallback extends DiffUtil.Callback {

    private List<WebSocketOrder> oldList;
    private List<WebSocketOrder> newList;

    public LiveTradeDiffUtilCallback(List<WebSocketOrder> oldList, List<WebSocketOrder> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        WebSocketOrder oldItem = oldList.get(oldItemPosition);
        WebSocketOrder newItem = newList.get(newItemPosition);

        return oldItem.equals(newItem);
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        WebSocketOrder oldItem = oldList.get(oldItemPosition);
        WebSocketOrder newItem = newList.get(newItemPosition);

        return oldItem.equals(newItem);
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }
}
