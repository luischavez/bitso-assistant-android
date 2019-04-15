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

import com.geometrycloud.bitso.assistant.library.Profile;
import com.luischavezb.bitso.assistant.android.adapter.ProfileAdapter;

/**
 * Created by luischavez on 08/03/18.
 */

public class ServiceFragment extends Fragment implements View.OnClickListener {

    public interface ServiceEvents {

        void onServiceRequestNewProfile();

        void onServiceRequestEditProfile(Profile profile);

        void onServiceRequestDeleteProfile(Profile profile);
    }

    private ServiceEvents mServiceEvents;

    private RecyclerView mProfileRecyclerView;
    private RecyclerView.LayoutManager mProfileLayoutManager;
    private ProfileAdapter mProfileAdapter;

    private FloatingActionButton mAddProfileFloatingActionButton;

    @MainThread
    public void reloadProfiles() {
        mProfileAdapter.notifyDataSetChanged();
        mProfileRecyclerView.invalidate();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_service, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProfileRecyclerView = view.findViewById(R.id.profile_recycler_view);
        mAddProfileFloatingActionButton = view.findViewById(R.id.new_profile_button);

        mProfileLayoutManager = new LinearLayoutManager(getActivity());
        mProfileAdapter = new ProfileAdapter(new ProfileAdapter.ProfileAdapterEvents() {
            @Override
            public void onProfileAdapterItemSelected(final Profile profile) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(getString(R.string.profile));
                builder.setItems(getResources().getStringArray(R.array.profile_menu), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (null != mServiceEvents) {
                                    mServiceEvents.onServiceRequestEditProfile(profile);
                                }
                                break;
                            case 1:
                                if (null != mServiceEvents) {
                                    mServiceEvents.onServiceRequestDeleteProfile(profile);
                                }
                                break;
                        }
                    }
                });
                builder.show();
            }
        });

        mProfileRecyclerView.setHasFixedSize(true);

        mProfileRecyclerView.setLayoutManager(mProfileLayoutManager);
        mProfileRecyclerView.setAdapter(mProfileAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        mAddProfileFloatingActionButton.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        mAddProfileFloatingActionButton.setOnClickListener(null);

        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ServiceEvents) {
            mServiceEvents = (ServiceEvents) context;
        }
    }

    @Override
    public void onDetach() {
        mServiceEvents = null;

        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        if (null != mServiceEvents) {
            mServiceEvents.onServiceRequestNewProfile();
        }
    }
}
