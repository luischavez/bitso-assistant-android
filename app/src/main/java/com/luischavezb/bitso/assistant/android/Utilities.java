package com.luischavezb.bitso.assistant.android;

import com.geometrycloud.bitso.assistant.library.Bitso;

import java.math.BigDecimal;
import java.security.Key;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by luischavez on 28/02/18.
 */

public class Utilities {

    private static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm:ss");
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");

    public static String encode(String key, String value) {
        if (4 > key.length() || 4 < key.length()) return null;

        key = String.format("<BITSO%sBITSO>", key);

        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return new String(encrypted);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String decode(String key, String encrypted) {
        if (4 > key.length() || 4 < key.length()) return null;

        key = String.format("<BITSO%sBITSO>", key);

        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);

            return new String(cipher.doFinal(encrypted.getBytes()));
        } catch (Exception ex) {
            return null;
        }
    }

    public static String currencyFormat(Object value, String currency, boolean append) {
        if (!"MXN".equals(currency.toUpperCase())) {
            if (value instanceof BigDecimal) {
                BigDecimal bigDecimal = (BigDecimal) value;

                if (bigDecimal.intValue() < bigDecimal.floatValue()) {
                    if (append) {
                        return bigDecimal.toPlainString() + " " + currency;
                    }

                    return bigDecimal.toPlainString();
                }
            }

            if (append) {
                return NumberFormat.getNumberInstance().format(value) + " " + currency;
            }

            return NumberFormat.getNumberInstance().format(value);
        }

        if (append) {
            return NumberFormat.getCurrencyInstance().format(value) + " " + currency;
        }

        return NumberFormat.getCurrencyInstance().format(value);
    }

    public static String currencyFormat(Object value, String currency) {
        return currencyFormat(value, currency, false);
    }

    public static int color(Bitso.Book book) {
        int color = R.color.bitcoin;

        switch (book) {
            case BTC_MXN:
                color = R.color.bitcoin;
                break;
            case ETH_BTC:
            case ETH_MXN:
                color = R.color.ethereum;
                break;
            case XRP_BTC:
            case XRP_MXN:
                color = R.color.ripple;
                break;
            case LTC_BTC:
            case LTC_MXN:
                color = R.color.litecoin;
                break;
            case BCH_BTC:
                color = R.color.bitcoin_cash;
                break;
        }

        return color;
    }

    public static int color(String currency) {
        int color = R.color.bitcoin;

        switch (currency.toLowerCase()) {
            case "mxn":
                color = R.color.peso;
                break;
            case "btc":
                color = R.color.bitcoin;
                break;
            case "eth":
                color = R.color.ethereum;
                break;
            case "xrp":
                color = R.color.ripple;
                break;
            case "ltc":
                color = R.color.litecoin;
                break;
            case "bch":
                color = R.color.bitcoin_cash;
                break;
        }

        return color;
    }

    public static int icon(String currency) {
        int iconId = R.drawable.ic_bitcoin;

        switch (currency.toLowerCase()) {
            case "btc":
                iconId = R.drawable.ic_bitcoin;
                break;
            case "eth":
                iconId = R.drawable.ic_ethereum;
                break;
            case "xrp":
                iconId = R.drawable.ic_ripple;
                break;
            case "ltc":
                iconId = R.drawable.ic_litecoin;
                break;
            case "bch":
                iconId = R.drawable.ic_bitcoin;
                break;
        }

        return iconId;
    }

    public static String formatDateTime(Date date) {
        DATE_TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("GMT-12:00"));
        return DATE_TIME_FORMATTER.format(date);
    }

    public static String formatTime(Date date) {
        return TIME_FORMATTER.format(date);
    }

    public static String formatDate(Date date) {
        return DATE_FORMATTER.format(date);
    }
}
