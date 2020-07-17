package com.px.tool.infrastructure.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

public final class DateTimeUtils {
    private static final SimpleDateFormat fm_date_time = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * now without time (beginning of the day).
     */
    public static Instant now() {
        return LocalDate
                .now()
                .atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    public static long nowAsMilliSec() {
        return now().toEpochMilli();
    }

    public static Date nowAsDate() {
        return Date.from(now());
    }

    public static Date toDate(Long fromDate) {
        return new Date(fromDate);
    }

    public static String toString(Long time) {
        if (Objects.isNull(time)) {
            return "";
        }
        return toString(new Date(time));
    }

    // Serve for Statistical
    public static String dateLongToString(Long time) {
        if (Objects.isNull(time) || time < 1) {
            return "";
        }
        return fm_date_time.format(new Date(time));
    }

    public static String nowAsString() {
        return toString(new Date());
    }

    public static String toString(Date date) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Saigon"));
        cal.setTime(date);
        return String.format("Ngày %s Tháng %s Năm %s", cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR));
    }

}