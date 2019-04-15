package com.luischavezb.bitso.assistant.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geometrycloud.bitso.assistant.library.Bitso;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.Utilities;
import com.luischavezb.bitso.assistant.android.alarm.Alarm;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by luischavez on 27/01/18.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    public interface AlarmAdapterEvents {

        void onAlarmAdapterItemSelected(Alarm alarm);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private Alarm mAlarm;

        private LinearLayout mItemLinearLayout;
        private ImageView mIconImageView;
        private TextView mBookTextView;
        private TextView mDescriptionTextView;

        public ViewHolder(View v) {
            super(v);

            mItemLinearLayout = v.findViewById(R.id.item_linear_layout);
            mIconImageView = v.findViewById(R.id.icon_image_view);
            mBookTextView = v.findViewById(R.id.book_text_view);
            mDescriptionTextView = v.findViewById(R.id.description_text_view);

            mItemLinearLayout.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            mAlarmAdapterEvents.onAlarmAdapterItemSelected(mAlarm);

            return true;
        }
    }

    private List<Alarm> mAlarms;
    private AlarmAdapterEvents mAlarmAdapterEvents;

    public AlarmAdapter(List<Alarm> alarms, AlarmAdapterEvents alarmAdapterEvents) {
        mAlarms = alarms;
        mAlarmAdapterEvents = alarmAdapterEvents;
    }

    public void setAlarms(List<Alarm> alarms) {
        mAlarms = alarms;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = AssistantApplication.getContext();

        Alarm alarm = mAlarms.get(position);

        Bitso.Book book = alarm.getBook();

        int icon = Utilities.icon(book.majorCoin());
        int color = Utilities.color(book);

        holder.mAlarm = alarm;
        holder.mItemLinearLayout.setAlpha(alarm.isEnabled() ? 1f : 0.5f);
        holder.mIconImageView.setImageResource(icon);
        holder.mIconImageView.setColorFilter(context.getResources().getColor(color));
        holder.mBookTextView.setText(book.name());
        holder.mBookTextView.setTextColor(context.getResources().getColor(color));

        String description = "";

        switch (alarm.getCondition()) {
            case LESS_THAN:
                description = context.getString(R.string.less_than)
                        + " "
                        + Utilities.currencyFormat(new BigDecimal(alarm.getValue()), book.minorCoin(), true);
                break;
            case SAME:
                description = context.getString(R.string.same)
                        + " "
                        + Utilities.currencyFormat(new BigDecimal(alarm.getValue()), book.minorCoin(), true);
                break;
            case GREATER_THAN:
                description = context.getString(R.string.greater_than)
                        + " "
                        + Utilities.currencyFormat(new BigDecimal(alarm.getValue()), book.minorCoin(), true);
                break;
        }

        holder.mDescriptionTextView.setText(description);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
    }
}
