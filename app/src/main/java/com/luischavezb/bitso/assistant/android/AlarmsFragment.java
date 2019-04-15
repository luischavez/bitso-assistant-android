package com.luischavezb.bitso.assistant.android;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.luischavezb.bitso.assistant.android.adapter.AlarmAdapter;
import com.luischavezb.bitso.assistant.android.alarm.Alarm;
import com.luischavezb.bitso.assistant.android.db.DbHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luischavez on 27/03/18.
 */

public class AlarmsFragment extends Fragment implements View.OnClickListener {

    public interface AlarmsEvents {

        void onAlarmsRequestNewAlarm();

        void onAlarmsRequestEditProfile(Alarm alarm);

        void onAlarmsRequestDeleteProfile(Alarm alarm);
    }

    private AlarmsEvents mAlarmsEvents;

    private RecyclerView mAlarmRecyclerView;
    private RecyclerView.LayoutManager mAlarmLayoutManager;
    private AlarmAdapter mAlarmAdapter;

    private FloatingActionButton mAddAlarmFloatingActionButton;

    @MainThread
    public void reloadAlarms() {
        List<Alarm> alarms = DbHelper.getInstance().readAlarms();
        if (null == alarms) {
            alarms = new ArrayList<>();
        }

        mAlarmAdapter.setAlarms(alarms);
        mAlarmAdapter.notifyDataSetChanged();
        mAlarmRecyclerView.invalidate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarms, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAlarmRecyclerView = view.findViewById(R.id.alarm_recycler_view);
        mAddAlarmFloatingActionButton = view.findViewById(R.id.new_alarm_button);

        mAlarmLayoutManager = new LinearLayoutManager(getActivity());

        List<Alarm> alarms = DbHelper.getInstance().readAlarms();
        if (null == alarms) {
            alarms = new ArrayList<>();
        }
        mAlarmAdapter = new AlarmAdapter(alarms, new AlarmAdapter.AlarmAdapterEvents() {
            @Override
            public void onAlarmAdapterItemSelected(final Alarm alarm) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.alarm));
                builder.setItems(getResources().getStringArray(R.array.alarm_menu), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (null != mAlarmsEvents) {
                                    mAlarmsEvents.onAlarmsRequestEditProfile(alarm);
                                }
                                break;
                            case 1:
                                if (null != mAlarmsEvents) {
                                    mAlarmsEvents.onAlarmsRequestDeleteProfile(alarm);
                                }
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        mAlarmRecyclerView.setHasFixedSize(true);

        mAlarmRecyclerView.setLayoutManager(mAlarmLayoutManager);
        mAlarmRecyclerView.setAdapter(mAlarmAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        mAddAlarmFloatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        mAddAlarmFloatingActionButton.setOnClickListener(null);

        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AlarmsEvents) {
            mAlarmsEvents = (AlarmsEvents) context;
        }
    }

    @Override
    public void onDetach() {
        mAlarmsEvents = null;

        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if (null != mAlarmsEvents) {
            mAlarmsEvents.onAlarmsRequestNewAlarm();
        }
    }
}
