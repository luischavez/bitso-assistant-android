package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Order;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.List;

/**
 * Created by luischavez on 28/02/18.
 */

public class RequestOrdersTask extends BitsoTask<List<Order>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.REQUEST_ORDERS_TASK";

    private final Bitso.Book mBook;
    private final String[] mOids;

    public RequestOrdersTask(Bitso.Book book, String[] oids, boolean enableDialog, int... targets) {
        super(TAG, enableDialog, true, targets);

        mBook = book;
        mOids = oids;
    }

    @Override
    protected void onSuccess(List<Order> orders) {
        DbHelper.getInstance().storeOrders(mBook, orders, null == mOids);
    }

    @Override
    protected ApiResponse<List<Order>> executeBitso(Bitso bitso) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_orders));

        bitso.waitAvailableCall();

        return null == mOids ? bitso.orders(mBook) : bitso.orders(mOids);
    }
}
