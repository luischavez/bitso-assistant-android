package com.luischavezb.bitso.assistant.android.task.db;

import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.luischavezb.bitso.assistant.android.task.Task;

/**
 * Created by luischavez on 01/03/18.
 */

public abstract class DbTask<R> extends Task<DbHelper, R> {

    public DbTask(String tag, boolean enableDialog, boolean sync, int... targets) {
        super(tag, DbHelper.getInstance(), enableDialog, sync, targets);
    }
}
