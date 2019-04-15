package com.luischavezb.bitso.assistant.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.luischavezb.bitso.assistant.android.task.Task;
import com.luischavezb.bitso.assistant.android.task.TaskManager;
import com.luischavezb.bitso.assistant.android.task.api.CreateAccountTask;
import com.luischavezb.bitso.assistant.android.task.api.ValidateApiTask;
import com.shashank.sony.fancytoastlib.FancyToast;

/**
 * Created by luischavez on 12/02/18.
 */

public class IntroActivity extends AppIntro2 implements IntroApiFragment.OnValidate, Task.OnTaskResult {

    private final TaskManager mTaskManager = new TaskManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showStatusBar(false);
        showSkipButton(false);

        addSlide(new IntroApiFragment());

        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.intro_success_title), getString(R.string.intro_success_description),
                R.drawable.success,
                getResources().getColor(R.color.intro_success)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        mTaskManager.attach(this);
    }

    @Override
    protected void onPause() {
        mTaskManager.detach();

        super.onPause();
    }

    @Override
    public boolean onCanRequestNextPage() {
        return false;
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        startActivity(MainActivity.intent(this));
    }

    @Override
    public void validate(String key, String secret, String nip, boolean useFingerprint) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit()
                .putBoolean(getString(R.string.configuration_use_fingerprint), useFingerprint)
                .commit();

        if (AssistantApplication.sDebug && (null == nip || nip.isEmpty())) {
            nip = "1234";
        }

        mTaskManager.execute(new ValidateApiTask(key, secret, nip, true));
    }

    @Override
    public void onTaskResult(Task task, Object result, boolean success, boolean cancelled) {
        switch (task.getTag()) {
            case ValidateApiTask.TAG:
                if (null != result) {
                    ValidateApiTask.ValidationResult validationResult = (ValidateApiTask.ValidationResult) result;

                    boolean valid = validationResult.isValid();
                    String key = validationResult.getKey();
                    String secret = validationResult.getSecret();
                    String nip = validationResult.getNip();

                    if (valid) {
                        FancyToast.makeText(this,
                                getString(R.string.valid_api_key_secret),
                                FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false)
                                .show();

                        mTaskManager.execute(new CreateAccountTask(key, secret, nip, true, true));
                        return;
                    }
                }

                FancyToast.makeText(this,
                        getString(R.string.invalid_api_key_secret),
                        FancyToast.LENGTH_SHORT, FancyToast.ERROR, false)
                        .show();
                break;
            case CreateAccountTask.TAG:
                if (null != result && (Boolean) result) {
                    getPager().goToNextSlide();
                }
                break;
        }
    }

    public static Intent intent(Context context) {
        Intent intent = new Intent(context, IntroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        return intent;
    }
}
