package com.luischavezb.bitso.assistant.android.adapter;

import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by luischavez on 04/03/18.
 */

public class MovementDiffUtilCallback extends DiffUtil.Callback {

    private List<MovementAdapter.Movement> mOldMovementList;
    private List<MovementAdapter.Movement> mNewMovementList;

    public MovementDiffUtilCallback(List<MovementAdapter.Movement> oldMovementList,
                                    List<MovementAdapter.Movement> newMovementList) {
        mOldMovementList = oldMovementList;
        mNewMovementList = newMovementList;
    }

    @Override
    public int getOldListSize() {
        return mOldMovementList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewMovementList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        MovementAdapter.Movement oldMovement = mOldMovementList.get(oldItemPosition);
        MovementAdapter.Movement newMovement = mNewMovementList.get(newItemPosition);

        return oldMovement.equals(newMovement);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return false;
    }
}
