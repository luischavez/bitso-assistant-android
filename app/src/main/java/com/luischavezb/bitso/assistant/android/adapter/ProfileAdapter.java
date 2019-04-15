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
import com.geometrycloud.bitso.assistant.library.Profile;
import com.luischavezb.bitso.assistant.android.AndroidAssistant;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.Utilities;

/**
 * Created by luischavez on 27/01/18.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

    public interface ProfileAdapterEvents {

        void onProfileAdapterItemSelected(Profile profile);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private Profile mProfile;

        private LinearLayout mItemLinearLayout;

        private ImageView mIconImageView;
        private TextView mBookTextView;
        private TextView mTypeTextView;
        private TextView mAmountTextView;
        private TextView mPriceTextView;

        public ViewHolder(View v) {
            super(v);

            mItemLinearLayout = v.findViewById(R.id.item_linear_layout);
            mIconImageView = v.findViewById(R.id.icon_image_view);
            mTypeTextView = v.findViewById(R.id.type_text_view);
            mBookTextView = v.findViewById(R.id.book_text_view);
            mAmountTextView = v.findViewById(R.id.amount_text_view);
            mPriceTextView = v.findViewById(R.id.price_text_view);

            mItemLinearLayout.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            mProfileAdapterEvents.onProfileAdapterItemSelected(mProfile);

            return true;
        }
    }

    private ProfileAdapterEvents mProfileAdapterEvents;

    public ProfileAdapter(ProfileAdapterEvents profileAdapterEvents) {
        mProfileAdapterEvents = profileAdapterEvents;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Context context = AssistantApplication.getContext();

        Profile profile = AndroidAssistant.getInstance().getData().getProfiles().get(position);

        Bitso.Book book = profile.getBook();

        int icon = Utilities.icon(book.majorCoin());
        int color = Utilities.color(book);

        holder.mProfile = profile;

        holder.mItemLinearLayout.setAlpha(profile.isEnabled() ? 1f : 0.5f);

        holder.mIconImageView.setImageResource(icon);
        holder.mIconImageView.setColorFilter(context.getResources().getColor(color));

        String type = "";

        switch (profile.getType()) {
            case BUY:
                type = context.getString(R.string.buy);
                break;
            case SELL:
                type = context.getString(R.string.sell);
                break;
        }

        holder.mTypeTextView.setText(type);

        holder.mBookTextView.setText(profile.getBook().name());
        holder.mBookTextView.setTextColor(context.getResources().getColor(color));

        String amount = Utilities.currencyFormat(
                profile.getMaxAmount(),
                Profile.Type.SELL.equals(profile.getType()) ? book.majorCoin() : book.minorCoin(),
                true);
        String price = Utilities.currencyFormat(
                profile.getPrice(),
                book.minorCoin(),
                true);

        holder.mAmountTextView.setText(amount);
        holder.mPriceTextView.setText(price);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile, parent, false);

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public int getItemCount() {
        return AndroidAssistant.getInstance().getData().getProfiles().size();
    }
}
