package com.tim1.daimler.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Dater {

    public static String toDate(long longTime) {
        Date date = new Date(longTime);
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }

    public static String toTime(long longTime) {
        Date date = new Date(longTime);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(date);
    }
}
