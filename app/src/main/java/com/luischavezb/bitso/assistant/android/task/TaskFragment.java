package com.luischavezb.bitso.assistant.android.task;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by luischavez on 23/02/18.
 */

public class TaskFragment extends Fragment {

    private Task mTask;

    public Task getTask() {
        return mTask;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (mTask.isSync()) {
            mTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } else {
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTask.attach(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mTask.attach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mTask.detach();
    }

    public static TaskFragment getInstance(Task task) {
        TaskFragment taskFragment = new TaskFragment();
        taskFragment.mTask = task;

        return taskFragment;
    }
}
