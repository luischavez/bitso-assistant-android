package com.luischavezb.bitso.assistant.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.alarm.Condition;

/**
 * Created by luischavez on 31/01/18.
 */

public class ConditionAdapter extends BaseAdapter {

    private static class ViewHolder {

        private TextView mName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();

        if (null == convertView) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);

            holder.mName = (TextView) convertView;

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Condition condition = (Condition) getItem(position);

        String name = "";

        switch (condition) {
            case LESS_THAN:
                name = AssistantApplication.getContext().getString(R.string.less_than);
                break;
            case GREATER_THAN:
                name = AssistantApplication.getContext().getString(R.string.greater_than);
                break;
            case SAME:
                name = AssistantApplication.getContext().getString(R.string.same);
                break;
        }

        holder.mName.setText(name);

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return Condition.values()[position];
    }

    @Override
    public int getCount() {
        return Condition.values().length;
    }

}
