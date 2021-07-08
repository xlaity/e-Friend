package com.tanhua.server.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RelativeDateFormat {
 
    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;
 
    private static final String ONE_SECOND_AGO = "秒前";
    private static final String ONE_MINUTE_AGO = "分钟前";
    private static final String ONE_HOUR_AGO = "小时前";
    private static final String ONE_DAY_AGO = "天前";
    private static final String ONE_MONTH_AGO = "月前";
    private static final String ONE_YEAR_AGO = "年前";
 
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Date date = format.parse("2013-11-11 18:35:35");
        System.out.println(format(new Date()));
    }
 
    public static String format(Date date) {
        long delta = new Date().getTime() - date.getTime();
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return (months <= 0 ? 1 : months) + ONE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return (years <= 0 ? 1 : years) + ONE_YEAR_AGO;
        }
    }
 
    private static long toSeconds(long date) {
        return date / 1000L;
    }
 
    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }
 
    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }
 
    private static long toDays(long date) {
        return toHours(date) / 24L;
    }
 
    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }
 
    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }


    /**
     * 毫秒数转换成天时分秒
     *
     * @param milliseconds
     */
    public static String millisecondsConvertToDHMS(long milliseconds) {
        String daysStr;
        String hoursStr;
        String minutesStr;
        String secondsStr;
        //天
        long day = (milliseconds / 1000) / (24 * 3600);
        if (day < 10) {
            daysStr = "0" + day;
        } else {
            daysStr = day + "";
        }
        //时
        long hour = ((milliseconds / 1000) % (24 * 3600)) / 3600;
        if (hour < 10) {
            hoursStr = "0" + hour;
        } else {
            hoursStr = hour + "";
        }
        //分
        long minute = ((milliseconds / 1000) % 3600) / 60;
        if (minute < 10) {
            minutesStr = "0" + minute;
        } else {
            minutesStr = minute + "";
        }
        //秒
        long second = (milliseconds / 1000) % 60;
        if (second < 10) {
            secondsStr = "0" + second;
        } else {
            secondsStr = second + "";
        }

        return daysStr+"天"+hoursStr+"时"+minutesStr+"分"+secondsStr+"秒";
    }
}