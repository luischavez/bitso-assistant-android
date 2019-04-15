package com.luischavezb.bitso.assistant.android.task.download;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.luischavezb.bitso.assistant.android.AssistantApplication;
import com.luischavezb.bitso.assistant.android.R;
import com.luischavezb.bitso.assistant.android.task.Task;
import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * Created by luischavez on 06/03/18.
 */

public class DownloadGravatarTask extends Task<String, Bitmap> {

    public static final String TAG = "com.luischavezb.bitso.assistant.android.tag.DOWNLOAD_GRAVATAR_TASK";

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadGravatarTask.class);

    private final static int DEFAULT_SIZE = 200;
    private final static String GRAVATAR_BASE_URL = "http://www.gravatar.com";
    private final static String GRAVATAR_AVATAR = "/avatar/";

    public DownloadGravatarTask(String value, boolean enableDialog, int... targets) {
        super(TAG, value, enableDialog, false, targets);
    }

    private String md5Hex(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex(md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error("", ex);
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("", ex);
        }

        return null;
    }

    private String hex(byte[] array) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            buffer.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return buffer.toString();
    }

    private String formatUrlParameters() {
        ArrayList<String> params = new ArrayList<>();

        params.add("s=" + DEFAULT_SIZE);

        return "?" + TextUtils.join("&", params.toArray());
    }

    private String gravatarUrl(String email) {
        String emailHash = md5Hex(email.toLowerCase().trim());
        String params = formatUrlParameters();

        if (null == emailHash) return null;

        return GRAVATAR_BASE_URL + GRAVATAR_AVATAR + emailHash + params;
    }

    @Override
    protected Bitmap execute(String email) {
        Context context = AssistantApplication.getContext();

        publishProgress(context.getString(R.string.progress_download_gravatar));

        String gravatarUrl = gravatarUrl(email);

        if (null == gravatarUrl) {
            return null;
        }

        try {
            Picasso picasso = Picasso.with(AssistantApplication.getContext());

            return picasso.load(gravatarUrl).get();
        } catch (IOException ex) {
            LOGGER.error("", ex);
        }

        return null;
    }
}
