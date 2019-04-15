package com.luischavezb.bitso.assistant.android.task;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by luischavez on 23/02/18.
 */

public class TaskManager {

    private Context mContext;

    private FragmentManager mFragmentManager;

    public static <F extends Fragment> F getFragmentOrCreate(FragmentManager fragmentManager, Class<F> fragmentClass) {
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentClass.getName());

        if (null == fragment) {
            try {
                fragment = fragmentClass.newInstance();
            } catch (IllegalAccessException e) {
                // IGNORE
            } catch (InstantiationException e) {
                // IGNORE
            }
        }

        return (F) fragment;
    }

    public void execute(Task task, boolean ifNotExist) {
        if (null == mFragmentManager) return;

        String tag = task.getTag();

        if (ifNotExist) {
            Fragment fragment = mFragmentManager.findFragmentByTag(tag);

            if (fragment instanceof TaskFragment) {
                TaskFragment taskFragment = (TaskFragment) fragment;

                if (AsyncTask.Status.FINISHED.equals(taskFragment.getTask().getStatus())) {
                    remove(tag);
                } else {
                    return;
                }
            }
        }

        TaskFragment taskFragment = TaskFragment.getInstance(task);

        mFragmentManager.beginTransaction().add(taskFragment, tag).commit();
    }

    public void execute(Task task) {
        execute(task, true);
    }

    public void cancel(String tag) {
        if (null == mFragmentManager) return;

        Fragment fragment = mFragmentManager.findFragmentByTag(tag);

        if (null == fragment || !(fragment instanceof TaskFragment)) return;

        TaskFragment taskFragment = (TaskFragment) fragment;

        taskFragment.getTask().cancel(true);

        mFragmentManager.beginTransaction().remove(fragment).commit();
    }

    public void remove(String tag) {
        if (null == mFragmentManager) return;

        Fragment fragment = mFragmentManager.findFragmentByTag(tag);

        if (null == fragment || !(fragment instanceof TaskFragment)) return;

        mFragmentManager.beginTransaction().remove(fragment).commit();
    }

    public void attach(Context context) {
        mContext = context;

        if (context instanceof Activity) {
            mFragmentManager = ((Activity) context).getFragmentManager();
        }
    }

    public void detach() {
        mContext = null;
        mFragmentManager = null;
    }
}
