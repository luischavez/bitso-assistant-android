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
import com.geometrycloud.bitso.assistant.library.Funding;
import com.geometrycloud.bitso.assistant.library.Trade;
import com.geometrycloud.bitso.assistant.library.Withdrawal;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.Utilities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by luischavez on 03/03/18.
 */

public class MovementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int DATE_VIEW = 0;
    private static final int TRADE_VIEW = 1;
    private static final int FUNDING_VIEW = 2;
    private static final int WITHDRAWAL_VIEW = 3;

    public class DateViewHolder extends RecyclerView.ViewHolder {

        private Date mDate;

        private TextView mDateTextView;

        public DateViewHolder(View v) {
            super(v);

            mDateTextView = v.findViewById(R.id.date_text_view);
        }
    }

    public class MovementViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Movement mMovement;

        private ImageView mIconImageView;
        private TextView mTotalTextView;
        private TextView mHourTextView;
        private ImageView mInfoImageView;
        private LinearLayout mInfoLinearLayout;

        private TextView mInfoLine1TextView;
        private TextView mInfoLine2TextView;
        private TextView mInfoLine3TextView;
        private TextView mInfoLine4TextView;
        private TextView mInfoLine5TextView;

        public MovementViewHolder(View v) {
            super(v);

            mIconImageView = v.findViewById(R.id.icon_image_view);
            mTotalTextView = v.findViewById(R.id.total_text_view);
            mHourTextView = v.findViewById(R.id.hour_text_view);
            mInfoImageView = v.findViewById(R.id.info_image_view);
            mInfoLinearLayout = v.findViewById(R.id.info_linear_layout);

            mInfoLine1TextView = v.findViewById(R.id.info_line_1);
            mInfoLine2TextView = v.findViewById(R.id.info_line_2);
            mInfoLine3TextView = v.findViewById(R.id.info_line_3);
            mInfoLine4TextView = v.findViewById(R.id.info_line_4);
            mInfoLine5TextView = v.findViewById(R.id.info_line_5);

            mInfoImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (View.GONE == mInfoLinearLayout.getVisibility()) {
                mInfoLinearLayout.setVisibility(View.VISIBLE);
            } else {
                mInfoLinearLayout.setVisibility(View.GONE);
            }
        }
    }

    public static class Movement {

        private Object mObject;

        public Movement(Object object) {
            mObject = object;
        }

        public Object getObject() {
            return mObject;
        }

        public Date dateOf() {
            if (mObject instanceof Trade) {
                return ((Trade) mObject).getCreatedAt();
            }

            if (mObject instanceof Funding) {
                return ((Funding) mObject).getCreatedAt();
            }

            if (mObject instanceof Withdrawal) {
                return ((Withdrawal) mObject).getCreatedAt();
            }

            if (mObject instanceof Date) {
                return (Date) mObject;
            }

            return null;
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj) return false;

            if (!(obj instanceof Movement)) return false;

            Movement movement = (Movement) obj;

            if (mObject instanceof Date && movement.getObject() instanceof Date) {
                Calendar calendar = Calendar.getInstance();

                Date date1 = dateOf();
                Date date2 = movement.dateOf();

                calendar.setTime(date1);
                int y1 = calendar.get(Calendar.YEAR);
                int m1 = calendar.get(Calendar.MONTH);
                int d1 = calendar.get(Calendar.DAY_OF_MONTH);

                calendar.setTime(date2);
                int y2 = calendar.get(Calendar.YEAR);
                int m2 = calendar.get(Calendar.MONTH);
                int d2 = calendar.get(Calendar.DAY_OF_MONTH);

                return y1 == y2 && m1 == m2 && d1 == d2;
            }

            return mObject.equals(movement.getObject());
        }

        @Override
        public int hashCode() {
            return mObject.hashCode();
        }
    }

    private final List<Movement> mMovementList;

    public MovementAdapter(List<Movement> movementList) {
        mMovementList = movementList;
    }

    private void calculateDiff(List<Movement> newMovementList) {
        Calendar calendar = Calendar.getInstance();
        ArrayList<Date> dates = new ArrayList<>();

        for (Movement movement : newMovementList) {
            calendar.setTime(movement.dateOf());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            dates.add(calendar.getTime());
        }

        for (Date date : dates) {
            Movement movement = new Movement(date);

            if (!newMovementList.contains(movement)) {
                newMovementList.add(movement);
            }
        }

        Collections.sort(newMovementList, new Comparator<Movement>() {
            @Override
            public int compare(Movement o1, Movement o2) {
                return o2.dateOf().compareTo(o1.dateOf());
            }
        });

        DiffUtil.calculateDiff(new MovementDiffUtilCallback(mMovementList, newMovementList))
                .dispatchUpdatesTo(this);

        mMovementList.clear();
        mMovementList.addAll(newMovementList);
    }

    public void setMovements(List<Trade> trades, List<Funding> fundings, List<Withdrawal> withdrawals) {
        ArrayList<Movement> newMovementList = new ArrayList<>();

        if (null != trades) {
            for (Trade trade : trades) {
                newMovementList.add(new Movement(trade));
            }
        }

        if (null != fundings) {
            for (Funding funding : fundings) {
                newMovementList.add(new Movement(funding));
            }
        }

        if (null != withdrawals) {
            for (Withdrawal withdrawal : withdrawals) {
                newMovementList.add(new Movement(withdrawal));
            }
        }

        calculateDiff(newMovementList);
    }

    @Override
    public int getItemViewType(int position) {
        Movement movement = mMovementList.get(position);

        Object object = movement.getObject();

        if (object instanceof Trade) {
            return TRADE_VIEW;
        }

        if (object instanceof Funding) {
            return FUNDING_VIEW;
        }

        if (object instanceof Withdrawal) {
            return WITHDRAWAL_VIEW;
        }

        if (object instanceof Date) {
            return DATE_VIEW;
        }

        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder;

        switch (viewType) {
            case DATE_VIEW:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_date, parent, false);
                viewHolder = new DateViewHolder(view);
                break;
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_movement, parent, false);
                viewHolder = new MovementViewHolder(view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Context context = AssistantApplication.getContext();

        Movement movement = mMovementList.get(position);

        if (holder instanceof DateViewHolder) {
            DateViewHolder dateViewHolder = (DateViewHolder) holder;

            Date date = (Date) movement.getObject();

            dateViewHolder.mDate = date;

            dateViewHolder.mDateTextView.setText(Utilities.formatDate(date));
        } else {
            MovementViewHolder movementViewHolder = (MovementViewHolder) holder;

            movementViewHolder.mInfoLine1TextView.setText("");
            movementViewHolder.mInfoLine2TextView.setText("");
            movementViewHolder.mInfoLine3TextView.setText("");
            movementViewHolder.mInfoLine4TextView.setText("");
            movementViewHolder.mInfoLine5TextView.setText("");

            movementViewHolder.mInfoLine1TextView.setVisibility(View.GONE);
            movementViewHolder.mInfoLine2TextView.setVisibility(View.GONE);
            movementViewHolder.mInfoLine3TextView.setVisibility(View.GONE);
            movementViewHolder.mInfoLine4TextView.setVisibility(View.GONE);
            movementViewHolder.mInfoLine5TextView.setVisibility(View.GONE);

            movementViewHolder.mMovement = movement;
            Object object = movement.mObject;

            int iconId;
            int color;
            String total;
            String hour = Utilities.formatTime(movement.dateOf());

            if (object instanceof Trade) {
                Trade trade = (Trade) object;

                Bitso.Book book = trade.getBook();

                color = Utilities.color(book);

                String cost;

                if (0 > trade.getMajor().compareTo(BigDecimal.ZERO)) {
                    total = Utilities.currencyFormat(trade.getMinor(), book.minorCoin(), true);
                    cost = Utilities.currencyFormat(trade.getMajor(), book.majorCoin(), true);
                } else {
                    total = Utilities.currencyFormat(trade.getMajor(), book.majorCoin(), true);
                    cost = Utilities.currencyFormat(trade.getMinor(), book.minorCoin(), true);
                }

                iconId = R.drawable.ic_trade;

                cost = context.getString(R.string.cost_format, cost);

                String feesCurrency = trade.getFeesCurrency().toUpperCase();
                String fee = Utilities.currencyFormat(trade.getFeesAmount(), feesCurrency, true);
                fee = context.getString(R.string.fee_format, fee);

                String priceCurrency = book.minorCoin();
                String price = Utilities.currencyFormat(trade.getPrice(), priceCurrency, true);
                price = context.getString(R.string.price_format, price);

                movementViewHolder.mInfoLine1TextView.setText(cost);
                movementViewHolder.mInfoLine1TextView.setVisibility(View.VISIBLE);

                movementViewHolder.mInfoLine2TextView.setText(fee);
                movementViewHolder.mInfoLine2TextView.setVisibility(View.VISIBLE);

                movementViewHolder.mInfoLine3TextView.setText(price);
                movementViewHolder.mInfoLine3TextView.setVisibility(View.VISIBLE);
            } else if (object instanceof Funding) {
                Funding funding = (Funding) object;

                String currency = funding.getCurrency().toUpperCase();
                total = Utilities.currencyFormat(funding.getAmount(), currency, true);

                color = Utilities.color(currency);

                iconId = R.drawable.ic_funding;

                String method = funding.getMethod();

                if ("sp".equals(method)) {
                    method = "SPEI";
                }

                method = method.toUpperCase();
                method = context.getString(R.string.method_format, method);

                String status = funding.getStatus();

                switch (status) {
                    case "pending":
                        status = context.getString(R.string.pending);
                        break;
                    case "complete":
                        status = context.getString(R.string.complete);
                        break;
                    case "cancelled":
                        status = context.getString(R.string.cancelled);
                        break;
                }

                status = context.getString(R.string.status_format, status);

                movementViewHolder.mInfoLine1TextView.setText(method);
                movementViewHolder.mInfoLine1TextView.setVisibility(View.VISIBLE);

                movementViewHolder.mInfoLine2TextView.setText(status);
                movementViewHolder.mInfoLine2TextView.setVisibility(View.VISIBLE);
            } else {
                Withdrawal withdrawal = (Withdrawal) object;

                String currency = withdrawal.getCurrency().toUpperCase();
                total = Utilities.currencyFormat(withdrawal.getAmount(), currency, true);

                color = Utilities.color(currency);

                iconId = R.drawable.ic_withdrawal;

                String method = withdrawal.getMethod();

                if ("sp".equals(method)) {
                    method = "SPEI";
                }

                method = method.toUpperCase();
                method = context.getString(R.string.method_format, method);

                String status = withdrawal.getStatus();

                switch (status) {
                    case "pending":
                        status = context.getString(R.string.pending);
                        break;
                    case "complete":
                        status = context.getString(R.string.complete);
                        break;
                    case "cancelled":
                        status = context.getString(R.string.cancelled);
                        break;
                }

                status = context.getString(R.string.status_format, status);

                movementViewHolder.mInfoLine1TextView.setText(method);
                movementViewHolder.mInfoLine1TextView.setVisibility(View.VISIBLE);

                movementViewHolder.mInfoLine2TextView.setText(status);
                movementViewHolder.mInfoLine2TextView.setVisibility(View.VISIBLE);
            }

            movementViewHolder.mIconImageView.setImageResource(iconId);
            movementViewHolder.mIconImageView.setColorFilter(context.getResources().getColor(color));
            movementViewHolder.mTotalTextView.setText(total);
            movementViewHolder.mTotalTextView.setTextColor(context.getResources().getColor(color));
            movementViewHolder.mHourTextView.setText(hour);
        }
    }

    @Override
    public int getItemCount() {
        return mMovementList.size();
    }
}
