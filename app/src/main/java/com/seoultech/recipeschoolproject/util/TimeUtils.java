package com.seoultech.recipeschoolproject.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public String convertTimeFormat(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        long currentDate = new Date(System.currentTimeMillis()).getTime();
        long diff = (currentDate - date.getTime()) / 1000;

        if (diff >= 0 && diff < 10) {
            return "지금 막";
        } else if (diff >= 10 && diff < 60) {
            return diff + "초 전";
        } else if (diff >= 60 && diff < 60 * 60) {
            return diff / 60 + "분 전";
        } else if (diff >= 60 * 60 && diff < 60 * 60 * 24) {
            return diff / (60 * 60) + "시간 전";
        } else if (diff >= 60 * 60 * 24 && diff < 60 * 60 * 48) {
            return "어제";
        } else if (diff >= 60 * 60 * 48 && diff < 60 * 60 * 72) {
            return "그저께";
        } else if (diff >= 60 * 60 * 72 && diff < 60 * 60 * 24 * 7) {
            return diff / (60 * 60 * 24) + "일 전";
        } else {
            return dateFormat.format(date);
        }
    }
}
