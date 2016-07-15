package com.xycode.xylibrary.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by XY on 2016/7/12.
 */
public class DateUtils {

    public static long getNow() {
        return System.currentTimeMillis();
    }

    public static Calendar getCalendar(long dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTime);
        return calendar;
    }

    public static String formatSimpleDateTime(Date date) {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(date);
    }

    public static String formatSimpleDateTime(long date) {
        return DateFormat.getDateInstance(DateFormat.MEDIUM).format(longToDate(date));
    }

    public static int longToDays(long date) {
        Calendar calendar = getCalendar(date);
        int days = calendar.get(Calendar.YEAR) * 10000 + calendar.get(Calendar.MONTH) * 100 + calendar.get(Calendar.DAY_OF_MONTH);
        return days;
    }


    public static long daysToLong(int days) {

        Calendar calendar = getCalendar(getNow());
        int y = (int) Math.floor(days / 10000.0);
        int m = (int) Math.floor((days - y * 10000) / 100.0);
        int d = (int) Math.floor((days - y * 10000 - m * 100));
        calendar.set(y, m, d, 0, 0, 0);

        return calendar.getTimeInMillis();
    }


    public static long dayBeginFromLong(long date) {
        Calendar calendar = getCalendar(date);
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(y, m, d, 0, 0, 0);
        return calendar.getTimeInMillis();
    }

    public static Date longToDate(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return calendar.getTime();
    }

    public static String formatDateTime(String dateTimeFormat, Date date) {
        return new SimpleDateFormat(dateTimeFormat).format(date);
    }

    public static String formatDateTime(String dateTimeFormat, long date) {
        return new SimpleDateFormat(dateTimeFormat).format(longToDate(date));
    }

    public static Date formatDateTime(String dateTimeFormat, String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateTimeFormat);
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date formatDate(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     *
     * @return date
     */
    public static Date getCustomDate(int y, int m, int d) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(y, m, d);
        return calendar.getTime();
    }

    /**
     * @param dateNow
     * @param datePassed
     * @return
     */
    public static int yearsBetweenDates(Date dateNow, Date datePassed) {
        int years;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(dateNow);
        c2.setTime(datePassed);
        years = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        if (c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH) == 0) {
            if (c1.get(Calendar.DAY_OF_MONTH) < c2.get(Calendar.DAY_OF_MONTH)) {
                years--;
            }
        } else if (c1.get(Calendar.MONTH) < c2.get(Calendar.MONTH)) {
            years--;
        }
        return years;
    }

    /**
     * @param dateNow
     * @param datePassed
     * @return
     */
    public static int yearsBetweenDates(long dateNow, long datePassed) {
        int years;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(dateNow);
        c2.setTimeInMillis(datePassed);
        years = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        if (c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH) == 0) {
            if (c1.get(Calendar.DAY_OF_MONTH) < c2.get(Calendar.DAY_OF_MONTH)) {
                years--;
            }
        } else if (c1.get(Calendar.MONTH) < c2.get(Calendar.MONTH)) {
            years--;
        }
        return years;
    }

    /**
     * @param dateNow
     * @param datePassed
     * @return
     */
    public static int monthsBetweenDates(long dateNow, long datePassed) {
        // must less than 1 year in 1 month, use -X
        int months, days;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(dateNow);
        c2.setTimeInMillis(datePassed);
        months = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        days = c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
        L.e("months" + "(" + c1.get(Calendar.MONTH) + ")" + c2.get(Calendar.MONTH));
        if (months < 0) {
            months = 12 + months;
        }
        if (days < 0) {
            months--;
        }
        if (months == -1) {
            months = 12 + months;
        } else if (months == 0) {
            months = -days;
        }
        return months;
    }

}
