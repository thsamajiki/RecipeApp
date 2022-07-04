package com.seoultech.recipeschoolproject.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    private static TimeUtils instance;

    private TimeUtils() {
    }

    public static TimeUtils getInstance() {
        if(instance == null) {
            instance = new TimeUtils();
        }
        return instance;
    }

    public String convertTimeFormat(long timeStamp, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        date.setTime(timeStamp);
        return dateFormat.format(date);
    }

    public String convertTimeFormat(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
