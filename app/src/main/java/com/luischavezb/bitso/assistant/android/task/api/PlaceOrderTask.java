package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Order;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.OrderType;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.math.BigDecimal;

/**
 * Created by luischavez on 14/03/18.
 */

public class PlaceOrderTask extends BitsoTask<String> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.PLACE_ORDER_TASK";

    private final OrderType mOrderType;
    private final Bitso.Book mBook;
    private final BigDecimal mAmount;
    private final BigDecimal mPrice;
    private final boolean mMinor;

    public PlaceOrderTask(OrderType orderType, Bitso.Book book, BigDecimal amount, BigDecimal price, boolean minor,
                          boolean enableDialog, int... targets) {
        super(TAG, enableDialog, true, targets);
        mOrderType = orderType;
        mBook = book;
        mAmount = amount;
        mPrice = price;
        mMinor = minor;
    }

    @Override
    protected void onSuccess(String oid) {
        Bitso bitso = getValue();

        if (isSync()) {
            synchronized (bitso) {
                bitso.waitAvailableCall();
                ApiResponse<Order> order = bitso.order(oid);

                if (order.success()) {
                    DbHelper.getInstance().storeOrder(order.object());
                }
            }
        } else {
            bitso.waitAvailableCall();
            ApiResponse<Order> order = bitso.order(oid);

            if (order.success()) {
                DbHelper.getInstance().storeOrder(order.object());
            }
        }
    }

    @Override
    protected ApiResponse<String> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_place_order));

        bitso.waitAvailableCall();

        if (OrderType.BUY.equals(mOrderType)) {
            return bitso.buy(mBook, mAmount, mPrice, mMinor);
        }

        return bitso.sell(mBook, mAmount, mPrice, mMinor);
    }
}
