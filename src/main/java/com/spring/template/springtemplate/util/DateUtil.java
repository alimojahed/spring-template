package com.spring.template.springtemplate.util;

import com.ghasemkiani.util.PersianCalendarHelper;
import com.ghasemkiani.util.icu.PersianCalendar;
import com.spring.template.springtemplate.exception.ExceptionStatus;
import com.spring.template.springtemplate.exception.ProjectException;
import eu.medsea.mimeutil.MimeUtil;
import net.fortuna.ical4j.model.DateTime;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Ali Mojahed on 6/4/2021
 * @project spring-template
 **/
public class DateUtil {
    public static final int PERSIAN_CALENDAR_CODE = 1;
    public static final int GREGORIAN_CALENDAR_CODE = 2;
    public static final String GREGORIAN_CALENDAR_NAME = "gregorian";
    public static final String MILLI_DELIMITER = ".";
    static final Logger logger = LogManager.getLogger(DateUtil.class.getName());
    private static final String DATE_TIME_DELIMITER = " ";
    private static final String DATE_DELIMITER = "/";
    private static final String DATE_DELIMITER_REGEX = "[/-]";  // accept 1398/02/12 or 1398-02-12
    private static final String TIME_DELIMITER = ":";
    private static final Map<String, SimpleDateFormat> DATE_TIME_FORMAT_MAP = new HashMap<>();
    private static final Map<String, SimpleDateFormat> DATE_FORMAT_MAP = new HashMap<>();
    private static final Map<String, SimpleDateFormat> TIMESTAMP_FORMAT_MAP = new HashMap<>();

    private static final long DAYS_MILLIS = 1000L * 3600L * 24L;
    private static final long HOURS_MILLIS = 1000L * 60 * 60;
    private static final long MINUTES_MILLIS = 1000L * 60;

    private static final com.ibm.icu.util.TimeZone DEFAULT_TIMEZONE =
            com.ibm.icu.util.TimeZone.getTimeZone("Asia/Tehran");

    static {
        MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    }

    private DateUtil() {
    }

    public static String formatDateTime(PersianCalendar persianCalendar) throws ProjectException {
        try {
            int year = persianCalendar.get(Calendar.YEAR);
            int month = persianCalendar.get(Calendar.MONTH);
            int day = persianCalendar.get(Calendar.DAY_OF_MONTH);

            int hour = persianCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = persianCalendar.get(Calendar.MINUTE);
            int second = persianCalendar.get(Calendar.SECOND);

            return formatLength(String.valueOf(year), 4, "0", true) + DATE_DELIMITER +
                    formatLength(String.valueOf(month + 1), 2, "0", true) + DATE_DELIMITER +
                    formatLength(String.valueOf(day), 2, "0", true) + DATE_TIME_DELIMITER +
                    formatLength(String.valueOf(hour), 2, "0", true) + TIME_DELIMITER +
                    formatLength(String.valueOf(minute), 2, "0", true) + TIME_DELIMITER +
                    formatLength(String.valueOf(second), 2, "0", true);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            logger.warn("An exception occurred", e);
            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);
        }
    }

    public static String formatDate(PersianCalendar persianCalendar) throws ProjectException {
        try {
            int year = persianCalendar.get(Calendar.YEAR);
            int month = persianCalendar.get(Calendar.MONTH);
            int day = persianCalendar.get(Calendar.DAY_OF_MONTH);

            return formatLength(String.valueOf(year), 4, "0", true) + DATE_DELIMITER +
                    formatLength(String.valueOf(month + 1), 2, "0", true) + DATE_DELIMITER +
                    formatLength(String.valueOf(day), 2, "0", true);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            logger.warn("An exception occurred", e);
            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);
        }
    }

    public static String formatDate(String date) throws ProjectException {
        try {
            String[] split = date.split(DATE_DELIMITER_REGEX);
            int year = Integer.parseInt(split[0], 10);
            int month = Integer.parseInt(split[1], 10) - 1;
            int day = Integer.parseInt(split[2], 10);

            return formatLength(String.valueOf(year), 4, "0", true) + DATE_DELIMITER +
                    formatLength(String.valueOf(month + 1), 2, "0", true) + DATE_DELIMITER +
                    formatLength(String.valueOf(day), 2, "0", true);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            logger.warn("An exception occurred", e);
            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);
        }
    }

    private static String formatLength(String str, int length, String fillWith, boolean toLeft) {
        StringBuilder strBuilder = new StringBuilder(str);
        while (strBuilder.length() < length) {
            if (toLeft) {
                strBuilder.insert(0, fillWith);
            } else {
                strBuilder.append(fillWith);
            }
        }
        str = strBuilder.toString();

        return str;
    }

    private static SimpleDateFormat getDateTimeFormat(String timezoneId) {
        SimpleDateFormat format = DATE_TIME_FORMAT_MAP.get(timezoneId);
        if (format == null) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone(timezoneId));
            DATE_TIME_FORMAT_MAP.put(timezoneId, format);
        }
        return format;
    }

    private static SimpleDateFormat getDateFormat(String timezoneId) {
        SimpleDateFormat format = DATE_FORMAT_MAP.get(timezoneId);
        if (format == null) {
            format = new SimpleDateFormat("yyyy/MM/dd");
            format.setTimeZone(TimeZone.getTimeZone(timezoneId));
            DATE_FORMAT_MAP.put(timezoneId, format);
        }
        return format;
    }

    private static SimpleDateFormat getTimestampFormat(String timezoneId) {
        SimpleDateFormat format = TIMESTAMP_FORMAT_MAP.get(timezoneId);
        if (format == null) {
            format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
            format.setTimeZone(TimeZone.getTimeZone(timezoneId));
            TIMESTAMP_FORMAT_MAP.put(timezoneId, format);
        }
        return format;
    }

    public static String getTodayPersianDate() {
        String now = getPersianNowStr();
        return now.substring(0, now.indexOf(" "));
    }

    public static Date getTodayDate() {
        Date date = null;
        try {
            date = convertPersianDateToDate(getTodayPersianDate());
        } catch (ProjectException ignore) {
        }
        return date;
    }

    public static String getPersianNowStr() {
        String dateTime;
        try {
            PersianCalendar persianCalendar = new PersianCalendar();
            persianCalendar.setTimeZone(DEFAULT_TIMEZONE);
            dateTime = formatDateTime(persianCalendar);
        } catch (ProjectException ignore) {
            dateTime = "";
        }
        return dateTime;
    }

    public static String getPersianNowDateStr() {
        String dateTime;
        try {
            PersianCalendar persianCalendar = new PersianCalendar();
            persianCalendar.setTimeZone(DEFAULT_TIMEZONE);
            dateTime = formatDate(persianCalendar);
        } catch (ProjectException ignore) {
            dateTime = "";
        }
        return dateTime;
    }

    public static String getGregorianNowStr() {
        return getGregorianNowStr(DEFAULT_TIMEZONE.getID());
    }

    public static String getGregorianNowStr(String timezoneId) {
        return getDateTimeFormat(timezoneId).format(new Date());
    }

    public static String getGregorianTimestampStr(Date date) {
        return getGregorianTimestampStr(date, DEFAULT_TIMEZONE.getID());
    }

    public static String getGregorianTimestampStr(Date date, String timezoneId) {
        return getTimestampFormat(timezoneId).format(date);
    }

    public static String getGregorianTimestampStr(Date date, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(date);
    }

    public static String getGregorianDateStr(Date date) {
        return getGregorianDateStr(date, DEFAULT_TIMEZONE.getID());
    }

    public static String getGregorianDateStr(Date date, String timezoneId) {
        return getDateFormat(timezoneId).format(date);
    }

    public static Date getNowDate() {
        return getNowCalendar().getTime();
    }

    public static Calendar getNowCalendar() {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"));
    }

    public static String convertGregorianToPersian(Date date) {
        String dateTime;
        try {
            PersianCalendar persianCalendar = new PersianCalendar(date);
            persianCalendar.setTimeZone(DEFAULT_TIMEZONE);
            dateTime = formatDateTime(persianCalendar);
        } catch (ProjectException e) {
            dateTime = "";
        }
        return dateTime;
    }

    public static String convertGregorianToPersianDate(Date date) {
        String dateTime;

        try {
            PersianCalendar persianCalendar = new PersianCalendar(date);
            persianCalendar.setTimeZone(DEFAULT_TIMEZONE);
            dateTime = formatDate(persianCalendar);
        } catch (ProjectException e) {
            dateTime = "";
        }
        return dateTime;
    }

    public static String convertGregorianToPersianWithoutTime(Date date) {
        String dateTime = convertGregorianToPersian(date);
        return dateTime.split("\\s+")[0];
    }

    public static String convertGregorianToPersian(Calendar calendar) {
        return convertGregorianToPersian(calendar.getTime());
    }

    private static void validateDate(int year, int month, int day) throws ProjectException {
        if (month >= 0 && month < 6) {
            if (day < 1 || day > 31) {
                throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "مقدار روز اشتباه است.");
            }
        } else if (month < 11 && month > 5) {
            if (day < 1 || day > 30) {
                throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "مقدار روز اشتباه است.");
            }
        } else if (month == 11) {
            if (!PersianCalendarHelper.isLeapYear(year)) {
                if (day < 1 || day > 29) {
                    throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "مقدار روز اشتباه است.");
                }
            } else {
                if (day < 1 || day > 30) {
                    throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "مقدار روز اشتباه است.");
                }
            }
        } else {
            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "مقدار ماه اشتباه است.");
        }
    }

    private static void validateTime(int hour, int minute, int second) throws ProjectException {
        if (hour < 0 || hour > 23) {
            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "مقدار ساعت اشتباه است.");
        }
        if (minute < 0 || minute > 59) {
            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "مقدار دقیقه اشتباه است.");
        }
        if (second < 0 || second > 59) {
            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "مقدار ثانیه اشتباه است.");
        }
    }

    public static Date convertPersianDateToDate(String date) throws ProjectException {
        if (date.contains(DATE_TIME_DELIMITER)) {
            return convertPersianDateTimeToDate(date);
        } else {
            return convertPersianDateWithoutTimeToDate(date);
        }
    }

    public static Date convertPersianDateToDateEndOfDay(String date) throws ProjectException {
        if (date.contains(DATE_TIME_DELIMITER)) {
            return convertPersianDateTimeToDate(date);
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(convertPersianDateWithoutTimeToDate(date));
            calendar.add(Calendar.DATE, 1);
            return calendar.getTime();
        }
    }

    public static Date convertPersianDateWithoutTimeToDate(String date) throws ProjectException {
        return convertPersianDateWithoutTimeToDate(date, DEFAULT_TIMEZONE);
    }

    public static Date convertPersianDateWithoutTimeToDate(String date,
                                                           com.ibm.icu.util.TimeZone timeZone) throws ProjectException {
        try {
            String[] split = date.split(DATE_DELIMITER_REGEX);
            int year = Integer.parseInt(split[0], 10);
            int month = Integer.parseInt(split[1], 10) - 1;
            int day = Integer.parseInt(split[2], 10);

            validateDate(year, month, day);

            PersianCalendar pc = new PersianCalendar(timeZone);
            pc.set(year, month, day, 0, 0, 0);
            pc.set(PersianCalendar.MILLISECOND, 0);
            return pc.getTime();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            logger.warn("An exception occurred", e);
            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "فرمت تاریخ وارد شده اشتباه است.");
        }
    }

    public static Date convertPersianDateTimeToDate(String dateTime) throws ProjectException {
        return convertPersianDateTimeToDate(dateTime, DEFAULT_TIMEZONE);
    }

    public static boolean isDateInFormat(String date, String format) {
        boolean result = true;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            dateFormat.parse(date);
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    public static String getDefaultTimeZoneId() {
        return com.ibm.icu.util.TimeZone.getDefault().getID();
    }

    public static Date convertPersianDateTimeToDate(String dateTime,
                                                    com.ibm.icu.util.TimeZone timeZone) throws ProjectException {
        try {
            String[] date_time = dateTime.split("[" + DATE_TIME_DELIMITER + "]");
            String[] dateSplit = date_time[0].split(DATE_DELIMITER_REGEX);
            String[] timeSplit = date_time[1].split("[" + TIME_DELIMITER + "]");

            int year = Integer.parseInt(dateSplit[0], 10);
            int month = Integer.parseInt(dateSplit[1], 10) - 1;
            int day = Integer.parseInt(dateSplit[2], 10);

            int hour = Integer.parseInt(timeSplit[0], 10);
            int minute = Integer.parseInt(timeSplit[1], 10);
            int second = 0;
            if (timeSplit.length > 2) {
                second = Integer.parseInt(timeSplit[2], 10);
            }

            validateDate(year, month, day);
            validateTime(hour, minute, second);

            PersianCalendar pc = new PersianCalendar(timeZone);
            pc.set(year, month, day, hour, minute, second);
            pc.set(PersianCalendar.MILLISECOND, 0);
            return pc.getTime();
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            logger.warn("An exception occurred", e);

            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "فرمت تاریخ داده شده اشتباه است");
        }
    }

    public static int compareDates(Date date1, Date date2) {
        return convertGregorianToPersian(date1)
                .substring(0, 10)
                .compareTo(convertGregorianToPersian(date2).substring(0, 10));
    }

    public static int compareDates(Date date1, String date2) {
        return convertGregorianToPersian(date1).substring(0, 10).compareTo(date2);
    }

    public static int compareDates(String date1, Date date2) {
        return date1.compareTo(convertGregorianToPersian(date2).substring(0, 10));
    }

    public static Date addMillies(Date date, long millies) {
        return new Date(date.getTime() + millies);
    }

    public static Date addDays(Date date, int days) {
        return new Date(date.getTime() + ((long) days) * 1000L * 60L * 60L * 24L);
    }

    public static String addDaysPersianDate(String date, int days) throws ProjectException {
        return convertGregorianToPersian(addDays(convertPersianDateToDate(date), days));
    }

    public static String addDaysPersianDateTime(String date, int days) throws ProjectException {
        return convertGregorianToPersian(addDays(convertPersianDateTimeToDate(date), days));
    }

    public static int getYearsDiff(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        return getYearsDiff(calendar1, calendar2);
    }

    public static int getYearsDiff(Calendar date1, Calendar date2) {
        int diff;
        if (date1.compareTo(date2) > 0) {
            diff = -getYearsDiff(date2, date1);
        } else {
            int year1 = getYear(date1);
            int year2 = getYear(date2);
            diff = year2 - year1;
            Calendar tempDate = (Calendar) date2.clone();
            tempDate.set(Calendar.YEAR, year1);
            if (tempDate.compareTo(date1) < 0) {
                diff--;
            }
        }
        return diff;
    }

    public static int getMonthsDiff(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        return getMonthsDiff(calendar1, calendar2);
    }

    public static int getMonthsDiff(Calendar date1, Calendar date2) {
        int diff;
        if (date1.compareTo(date2) > 0) {
            diff = -getMonthsDiff(date2, date1);
        } else {
            int year1 = getYear(date1);
            int month1 = getMonth(date1);
            int month2 = getMonth(date2);
            diff = month2 - month1;
            Calendar tempDate = (Calendar) date2.clone();
            tempDate.set(Calendar.YEAR, year1);
            tempDate.set(Calendar.MONTH, month1 - 1);
            if (tempDate.compareTo(date1) < 0) {
                diff--;
            }
            if (diff < 0) {
                diff += 12;
            }
        }
        return diff + (getYearsDiff(date1, date2) * 12);
    }

    public static int getDaysDiff(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        return getDaysDiff(calendar1, calendar2);
    }

    public static int getDaysDiff(Calendar date1, Calendar date2) {
        long l = date2.getTime().getTime() - date1.getTime().getTime();
        return (int) (l / DAYS_MILLIS);
    }

    public static long getHoursDiff(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        long diff = calendar2.getTime().getTime() - calendar1.getTime().getTime();
        return diff / (HOURS_MILLIS);
    }

    public static long getMinutesDiff(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);

        long diff = calendar2.getTime().getTime() - calendar1.getTime().getTime();
        return diff / (MINUTES_MILLIS);
    }

    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getYear(calendar);
    }

    public static int getYear(Calendar calendar) {
        return calendar.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getMonth(calendar);
    }

    public static int getMonth(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public static int getWeekOfYear(Calendar calendar) {
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getDay(calendar);
    }

    public static int getDay(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getHour(calendar);
    }

    public static int getHour(Calendar calendar) {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getMinute(calendar);
    }

    public static int getMinute(Calendar calendar) {
        return calendar.get(Calendar.MINUTE);
    }

    public static Date nthYearsAfter(Date date, int years) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, getYear(calendar) + years);

        return calendar.getTime();
    }

    public static Date nthMonthsAfter(Date date, int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int monthNumber = getMonth(calendar) + months;
        int years = monthNumber / 12;
        int month = monthNumber - (years * 12);
        calendar.set(Calendar.YEAR, getYear(calendar) + years);
        if (month > 0) {
            calendar.set(Calendar.MONTH, month - 1);
        }

        return calendar.getTime();
    }

    public static Date nthDaysAfter(Date date, int days) {
        return new Date(date.getTime() + (days * DAYS_MILLIS));
    }

    public static Date convertTimeStringToDate(String time) throws ProjectException {
        Date date;
        SimpleDateFormat twentyFourFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat amPmFormat = new SimpleDateFormat("hh:mm a");
        try {
            date = amPmFormat.parse(time);
        } catch (ParseException e) {
            logger.info(String.format("time: %s is not in '%s' format, we try '%s'",
                    time, amPmFormat.toPattern(), twentyFourFormat.toPattern()));
            try {
                date = twentyFourFormat.parse(time);
            } catch (ParseException e1) {
                logger.error(String.format("Cannot parse time %s", time), e);
                throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT, "فرمت تاریخ داده شده اشتباه است");
            }
        }
        return date;
    }

    public static String convertDateToTimeString(Date time) {
        return formatDate(time, "HH:mm");
    }

    public static String formatDate(Date date, String formatPattern) {
        String dateTime;
        DateFormat dateFormat = new SimpleDateFormat(formatPattern);
        dateTime = dateFormat.format(date);
        return dateTime;
    }

    public static Date increase(Date date, int increaseValue, DateType dateType) {
        Calendar cal = JalaliCalendar.getInstance();
        cal.setTime(date);
        Date newDate = (Date) date.clone();
        if (dateType.equals(DateType.YEAR)) {
            /*cal.add(Calendar.YEAR, increaseValue);
            newDate = cal.getTime();*/
            newDate = DateUtils.addYears(date, increaseValue);
        } else if (dateType.equals(DateType.MONTH)) {
            /*cal.add(Calendar.MONTH, increaseValue);
            newDate = cal.getTime();*/
            newDate = DateUtils.addMonths(date, increaseValue);
        } else if (dateType.equals(DateType.WEEK)) {
            /*cal.add(Calendar.WEEK_OF_MONTH, increaseValue);
            newDate = cal.getTime();*/
            newDate = DateUtils.addWeeks(date, increaseValue);
        } else if (dateType.equals(DateType.DAY)) {
            /*cal.add(Calendar.DAY_OF_WEEK, increaseValue);
            newDate = cal.getTime();*/
            newDate = DateUtils.addDays(date, increaseValue);
        } else if (dateType.equals(DateType.HOUR)) {
            //cal.add(Calendar.HOUR_OF_DAY, increaseValue);
            newDate = DateUtils.addHours(date, increaseValue);
        } else if (dateType.equals(DateType.MINUTE)) {
            //cal.add(Calendar.MINUTE, increaseValue);
            newDate = DateUtils.addMinutes(date, increaseValue);
        } else if (dateType.equals(DateType.SECOND)) {
            //cal.add(Calendar.SECOND, increaseValue);
            newDate = DateUtils.addSeconds(date, increaseValue);
        }
        return newDate;
    }

    public static boolean beforeEqual(Date src, Date dest) {
        return src.compareTo(dest) <= 0;
    }

    public static Timestamp convertDateToTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    public static Date extractICalTimeFromDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss'Z'");
        try {
            return sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Date convertPersianDateToDate(String date, String timeZone) throws ProjectException {
        if (date.contains(DATE_TIME_DELIMITER)) {
            return convertPersianDateTimeToDate(date, com.ibm.icu.util.TimeZone.getTimeZone(timeZone));
        } else {
            return convertPersianDateWithoutTimeToDate(date, com.ibm.icu.util.TimeZone.getTimeZone(timeZone));
        }
    }

    public static Date removeTime(Date date) {
        MyDateTime myDateTime = getDateTime(date);
        myDateTime.setHour(0);
        myDateTime.setMinute(0);
        myDateTime.setSecond(0);
        myDateTime.setMilliSecond(0);
        return myDateTime.toDate();
    }

    public static Date setTime(Date date, int hour, int minute, int second, int milliSecond) {
        MyDateTime myDateTime = getDateTime(date);
        myDateTime.setHour(hour);
        myDateTime.setMinute(minute);
        myDateTime.setSecond(second);
        myDateTime.setMilliSecond(milliSecond);
        return myDateTime.toDate();
    }

    public static MyDateTime getDateTime(Date date) {
        Calendar cal = JalaliCalendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        return new MyDateTime(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                cal.get(Calendar.SECOND),
                cal.get(Calendar.MILLISECOND));
    }

    public static Long diff(Date start, Date end, DateUtil.DateType type) {
        long diff = end.getTime() - start.getTime();

        switch (type) {
            case SECOND:
                return TimeUnit.MILLISECONDS.toSeconds(diff);
            case MINUTE:
                return TimeUnit.MILLISECONDS.toMinutes(diff);
            case HOUR:
                return TimeUnit.MILLISECONDS.toHours(diff);
            case DAY:
                return TimeUnit.MILLISECONDS.toDays(diff);
            case YEAR:
                return TimeUnit.MILLISECONDS.toDays(diff) / 365;
            default:
                return diff;
        }
    }

    public static boolean before(Date src, Date dest) {
        return src.compareTo(dest) < 0;
    }

    public static boolean afterEqual(Date src, Date dest) {
        return src.compareTo(dest) >= 0;
    }

    public static boolean after(Date src, Date dest) {
        return src.compareTo(dest) > 0;
    }

    public static String getPersianYear(Date date) {
        String persianDate = convertGregorianToPersian(date);
        return persianDate.length() >= 4 ? persianDate.substring(0, 4) : "";
    }

    public static String getPersianMonth(Date date) {
        String persianDate = convertGregorianToPersian(date);
        return persianDate.length() >= 7 ? persianDate.substring(5, 7) : "";
    }

    public static String getPersianDay(Date date) {
        String persianDate = convertGregorianToPersian(date);
        return persianDate.length() >= 10 ? persianDate.substring(8, 10) : "";
    }

    public static Date setTimeForDate(Date time, Date date) {
        Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
        calendar.setTime(time);   // assigns calendar to given date
        int hour = calendar.get(Calendar.HOUR_OF_DAY);// gets hour in 24h format
        //calendar.get(java.util.Calendar.HOUR);        // gets hour in 12h format
        int minute = calendar.get(Calendar.MINUTE);
        date = setTime(date, hour, minute, 0, 0);
        return date;
    }

    public static Calendar convertCalendar(final Calendar calendar, final TimeZone timeZone) {
        Calendar ret = new GregorianCalendar(timeZone);
        ret.setTimeInMillis(calendar.getTimeInMillis() +
                timeZone.getOffset(calendar.getTimeInMillis()) -
                TimeZone.getDefault().getOffset(calendar.getTimeInMillis()));
        ret.getTime();
        return ret;
    }

    public static DateTime dateTime(Long time, net.fortuna.ical4j.model.TimeZone tz) {
        DateTime dateTime = new DateTime(time);
        dateTime.setTimeZone(tz);
        return dateTime;
    }

    public static Date convertDateStrToDate(String dateStr, String format) throws ProjectException {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.parse(dateStr);
        } catch (Exception e) {
            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);
        }
    }

    /**
     * parsing date from string yyyy-MM-dd
     *
     * @param dateStr
     * @return
     * @throws ProjectException
     */
    public static Date parseExpirationDate(String dateStr) throws ProjectException {
        Date expDate;

        if (StringUtils.isNotBlank(dateStr)) {

            if (dateStr.length() == "yyyy-MM-dd".length()) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Calendar calendar = Calendar.getInstance();

                    calendar.setTime(dateFormat.parse(dateStr));
                    calendar.add(Calendar.HOUR_OF_DAY, 23);
                    calendar.add(Calendar.MINUTE, 59);
                    calendar.add(Calendar.SECOND, 59);

                    expDate = calendar.getTime();
                    if (expDate.before(getNowDate())) {
                        throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);
                    }

                } catch (ParseException e) {
                    throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);
                }
            } else if (dateStr.length() == "yyyy-MM-dd HH:mm:ss".length()) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                    Calendar calendar = Calendar.getInstance();

                    calendar.setTime(dateFormat.parse(dateStr));

                    expDate = calendar.getTime();

                    if (expDate.before(DateUtil.getNowDate())) {
                        throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);
                    }

                } catch (ParseException e) {
                    throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);
                }
            } else {
                throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);

            }

        } else {
            throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);

        }

        return expDate;
    }

//    public static net.fortuna.ical4j.model.TimeZone getTimeZone(String timeZoneId) {
//        return new net.fortuna.ical4j.model.TimeZone(getVTimeZone(timeZoneId));
//    }
//
//    public static VTimeZone getVTimeZone(String timeZoneId) {
//        net.fortuna.ical4j.model.TimeZone fortuneTZ;
//        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
//        fortuneTZ = registry.getTimeZone(timeZoneId);
//        return fortuneTZ.getVTimeZone();
//    }

    private com.ibm.icu.util.TimeZone convert(TimeZone timeZone) {
        return com.ibm.icu.util.TimeZone.getTimeZone(timeZone.getID());
    }

    private TimeZone convert(com.ibm.icu.util.TimeZone timeZone) {
        return TimeZone.getTimeZone(timeZone.getID());
    }

    public Calendar convertToTimezone(com.ibm.icu.util.TimeZone timeZone, Calendar calendar) {
        int offset = timeZone.getOffset(calendar.getTime().getTime());
        calendar.add(Calendar.MILLISECOND, offset);
        return calendar;
    }


    public enum DateType {
        HOUR("1", "HOUR"),
        MINUTE("3", "MINUTE"),
        SECOND("2", "SECOND"),
        DAY("4", "DAY"),
        MONTH("5", "MONTH"),
        YEAR("6", "YEAR"),
        WEEK("7", "WEEK");

        private final String code;
        private final String value;

        DateType(String code, String value) {
            this.code = code;
            this.value = value;

        }

        public String getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

    }
}
