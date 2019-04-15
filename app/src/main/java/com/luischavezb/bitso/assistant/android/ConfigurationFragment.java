package com.luischavezb.bitso.assistant.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.mtramin.rxfingerprint.RxFingerprint;

/**
 * Created by luischavez on 08/03/18.
 */

public class ConfigurationFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public interface ConfigurationEvents {

        void onConfigurationUpdated(String key);
    }

    private ConfigurationEvents mConfigurationEvents;

    private SharedPreferences mSharedPreferences;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        addPreferencesFromResource(R.xml.configuration);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (RxFingerprint.isUnavailable(getActivity())) {
            getPreferenceScreen()
                    .findPreference(getString(R.string.configuration_use_fingerprint))
                    .setEnabled(false);
        }

        Preference versionPreference = findPreference(getString(R.string.configuration_version));

        try {
            versionPreference.setSummary(AssistantApplication.appVersion());
        } catch (PackageManager.NameNotFoundException e) {
            // IGNORE
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Preference preference = findPreference(getString(R.string.configuration_notifications));

            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", AssistantApplication.getContext().getPackageName());

            preference.setIntent(intent);
        }

        mSharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);

        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ConfigurationEvents) {
            mConfigurationEvents = (ConfigurationEvents) context;
        }
    }

    @Override
    public void onDetach() {
        mConfigurationEvents = null;

        super.onDetach();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (null != mConfigurationEvents) {
            mConfigurationEvents.onConfigurationUpdated(key);
        }
    }
}
