package com.luischavezb.bitso.assistant.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geometrycloud.bitso.assistant.library.Bitso;

/**
 * Created by luischavez on 31/01/18.
 */

public class BookAdapter extends BaseAdapter {

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

        Bitso.Book book = (Bitso.Book) getItem(position);

        holder.mName.setText(book.name());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return Bitso.Book.values()[position];
    }

    @Override
    public int getCount() {
        return Bitso.Book.values().length;
    }

}
