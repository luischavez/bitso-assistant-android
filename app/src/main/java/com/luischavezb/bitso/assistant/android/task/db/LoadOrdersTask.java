package com.luischavezb.bitso.assistant.android.task.db;

import android.content.Context;

import com.geometrycloud.bitso.assistant.library.Bitso;
import com.geometrycloud.bitso.assistant.library.Order;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.List;

/**
 * Created by luischavez on 01/03/18.
 */

public class LoadOrdersTask extends DbTask<List<Order>> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.LOAD_ORDERS_TASK";

    private final Bitso.Book mBook;

    public LoadOrdersTask(Bitso.Book book, boolean enableDialog, int... targets) {
        super(TAG, enableDialog, false, targets);

        mBook = book;
    }

    @Override
    protected List<Order> execute(DbHelper dbHelper) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_get_orders));

        return dbHelper.readOrders(mBook);
    }
}
