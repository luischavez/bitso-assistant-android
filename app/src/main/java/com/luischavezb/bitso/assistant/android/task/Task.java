package com.luischavezb.bitso.assistant.android.task;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.view.View;

import com.luischavezb.bitso.assistant.android.dialog.LoadingDialog;

/**
 * Created by luischavez on 23/02/18.
 */

public abstract class Task<V, R> extends AsyncTask<Object, String, R> {

    public interface OnTaskResult {

        void onTaskResult(Task task, Object result, boolean success, boolean cancelled);
    }

    private final String mTag;

    private Context mContext;

    private V mValue;
    private int[] mTargets;

    private boolean mSync;

    private Dialog mDialog;
    private boolean mEnableDialog;

    private OnTaskResult mOnTaskResult;

    private boolean mStarted = false;

    public Task(String tag, V value, boolean enableDialog, boolean sync, int... targets) {
        mTag = tag;
        mValue = value;
        mEnableDialog = enableDialog;
        mSync = sync;
        mTargets = targets;
    }

    public Task(String tag, V value) {
        this(tag, value, false, false, null);
    }

    public String getTag() {
        return mTag;
    }

    protected Context getContext() {
        return mContext;
    }

    protected <C extends Context> C getContext(Class<C> contextClass) {
        if (null == mContext) return null;

        if (contextClass.isAssignableFrom(mContext.getClass())) {
            return contextClass.cast(mContext);
        }

        return null;
    }

    public V getValue() {
        return mValue;
    }

    public boolean isSync() {
        return mSync;
    }

    @MainThread
    protected Dialog onCreateDialog(Context context) {
        return new LoadingDialog(context);
    }

    @MainThread
    protected void onUpdateProgressDialog(Dialog dialog, String string) {
        if (dialog instanceof LoadingDialog) {
            ((LoadingDialog) dialog).setStatus(string);
        }
    }

    @CallSuper
    @MainThread
    protected void openDialog() {
        if (null != mDialog && mEnableDialog) {
            mDialog.show();
        }
    }

    @CallSuper
    @MainThread
    protected void closeDialog() {
        if (null != mDialog) {
            mDialog.dismiss();
        }
    }

    @CallSuper
    @MainThread
    protected void updateTargets(boolean enabled) {
        if (null != mTargets) {
            for (int target : mTargets) {
                if (null == mContext) continue;

                if (mContext instanceof Activity) {
                    final View view = getContext(Activity.class).findViewById(target);

                    if (null != view) {
                        view.setEnabled(enabled);
                    }
                }
            }
        }
    }

    @CallSuper
    @MainThread
    protected void enableTargets() {
        updateTargets(true);
    }

    @CallSuper
    @MainThread
    protected void disableTargets() {
        updateTargets(false);
    }

    @CallSuper
    @MainThread
    protected void onAttach(Context context) {
        if (context instanceof OnTaskResult) {
            mOnTaskResult = (OnTaskResult) context;
        }

        if (isCancelled() || Status.FINISHED.equals(getStatus())) return;

        if (mEnableDialog && null == mDialog) {
            mDialog = onCreateDialog(context);
        }

        if (mStarted) {
            openDialog();
        }

        disableTargets();
    }

    protected void onDetach() {
        mOnTaskResult = null;

        closeDialog();
        enableTargets();

        mDialog = null;
    }

    @CallSuper
    @MainThread
    public void attach(Context context) {
        mContext = context;

        onAttach(mContext);
    }

    @CallSuper
    @MainThread
    public void detach() {
        onDetach();

        mContext = null;
    }

    protected void onTaskResult(R result, boolean success, boolean cancelled) {
        if (null != mOnTaskResult) {
            mOnTaskResult.onTaskResult(this, result, success, cancelled);
        }
    }

    @WorkerThread
    protected abstract R execute(V value);

    @Override
    protected void onPreExecute() {
        if (isCancelled()) return;
    }

    @Override
    protected void onProgressUpdate(String... strings) {
        if (null != mDialog) {
            for (String string : strings) {
                onUpdateProgressDialog(mDialog, string);
            }
        }
    }

    @Override
    protected R doInBackground(Object... voids) {
        if (isCancelled()) return null;

        if (mSync) {
            synchronized (mValue) {
                mStarted = true;

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        openDialog();
                    }
                });

                return execute(mValue);
            }
        } else {
            mStarted = true;

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    openDialog();
                }
            });

            return execute(mValue);
        }
    }

    @Override
    protected void onPostExecute(R result) {
        closeDialog();
        enableTargets();

        mValue = null;

        boolean success = null != result;
        boolean cancelled = isCancelled();

        onTaskResult(result, success, cancelled);

        mContext = null;
        mOnTaskResult = null;
    }

    @Override
    protected void onCancelled() {
        closeDialog();
        enableTargets();

        mValue = null;

        R result = null;

        boolean success = null != result;
        boolean cancelled = isCancelled();

        onTaskResult(result, success, cancelled);

        mContext = null;
        mOnTaskResult = null;
    }
}
