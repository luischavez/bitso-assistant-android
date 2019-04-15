package com.luischavezb.bitso.assistant.android;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;

/**
 * Created by luischavez on 17/03/18.
 */

public class CurrencyValueFormatter extends DefaultValueFormatter {

    public CurrencyValueFormatter(int digits) {
        super(digits);
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        String formattedValue = super.getFormattedValue(value, entry, dataSetIndex, viewPortHandler);
        String currency = entry.getData().toString().toUpperCase();

        formattedValue = formattedValue.replace(",", "");

        return Utilities.currencyFormat(new BigDecimal(formattedValue), currency) + " " + currency;
    }
}
