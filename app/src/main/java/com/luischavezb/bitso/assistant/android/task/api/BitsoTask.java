package com.luischavezb.bitso.assistant.android.task.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.widget.Toast;

import com.geometrycloud.bitso.assistant.library.ApiResponse;
import com.geometrycloud.bitso.assistant.library.Bitso;
import com.luischavezb.bitso.assistant.android.AndroidAssistant;
import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.db.DbContract;
import com.luischavezb.bitso.assistant.android.db.DbHelper;
import com.luischavezb.bitso.assistant.android.task.Task;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by luischavez on 28/02/18.
 */

public abstract class BitsoTask<A> extends Task<Bitso, A> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitsoTask.class);

    public BitsoTask(String tag, Bitso bitso, boolean enableDialog, boolean sync, int... targets) {
        super(tag, bitso, enableDialog, sync, targets);
    }

    public BitsoTask(String tag, boolean enableDialog, boolean sync, int... targets) {
        super(tag, AndroidAssistant.getInstance().bitso(), enableDialog, sync, targets);
    }

    public BitsoTask(String tag) {
        super(tag, AndroidAssistant.getInstance().bitso());
    }

    @WorkerThread
    protected void onError(final String errorCode, final String errorMessage) {
        LOGGER.error("BITSO ERROR {}: {}", errorCode, errorMessage);

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Context context = AssistantApplication.getContext();

                String message = Bitso.INVALID_NAME_OR_INVALID_CREDENTIALS.equals(errorCode)
                        ? context.getString(R.string.api_error_invalid_credentials) : errorMessage;

                FancyToast.makeText(
                        context, message,
                        Toast.LENGTH_SHORT, FancyToast.ERROR, false)
                        .show();
            }
        });

        if (Bitso.INVALID_NAME_OR_INVALID_CREDENTIALS.equals(errorCode)) {
            DbHelper.getInstance().storeConfigurationField(DbContract.ConfigurationEntry.COLUMN_NAME_API_VALID, false);
        }
    }

    @WorkerThread
    protected void onException(Exception ex) {
        LOGGER.error("BITSO EXCEPTION: {}", ex.getMessage());

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Context context = AssistantApplication.getContext();

                String message = context.getString(R.string.api_error);

                FancyToast.makeText(
                        context, message,
                        Toast.LENGTH_SHORT, FancyToast.ERROR, false)
                        .show();
            }
        });
    }

    @WorkerThread
    protected void onSuccess(A result) {
        DbHelper.getInstance().storeConfigurationField(DbContract.ConfigurationEntry.COLUMN_NAME_API_VALID, true);
    }

    @WorkerThread
    protected abstract ApiResponse<A> executeBitso(Bitso bitso);

    @Override
    protected A execute(Bitso bitso) {
        ApiResponse<A> response = executeBitso(bitso);

        if (!response.success()) {
            if (null != response.exception()) {
                onException(response.exception());
            } else {
                onError(response.errorCode(), response.errorMessage());
            }
        } else {
            onSuccess(response.object());
        }

        return response.object();
    }
}
