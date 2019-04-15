package com.luischavezb.bitso.assistant.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geometrycloud.bitso.assistant.library.Profile;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;

/**
 * Created by luischavez on 31/01/18.
 */

public class ProfileTypeAdapter extends BaseAdapter {

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

        Profile.Type type = (Profile.Type) getItem(position);

        String name = "";

        switch (type) {
            case BUY:
                name = AssistantApplication.getContext().getString(R.string.buy);
                break;
            case SELL:
                name = AssistantApplication.getContext().getString(R.string.sell);
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
        return Profile.Type.values()[position];
    }

    @Override
    public int getCount() {
        return Profile.Type.values().length;
    }

}
