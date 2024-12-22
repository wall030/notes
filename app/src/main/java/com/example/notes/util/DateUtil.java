package com.example.notes.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    public static String getCurrentTimestamp() {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault());
        return formatter.format(now);
    }

    public static String formatTimestamp(String timestamp) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());

        try {
            Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
