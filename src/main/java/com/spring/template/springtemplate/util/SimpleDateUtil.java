package com.spring.template.springtemplate.util;

import com.spring.template.springtemplate.exception.ExceptionStatus;
import com.spring.template.springtemplate.exception.ProjectException;
import lombok.extern.log4j.Log4j2;
import org.joda.time.Interval;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Ali Mojahed on 6/4/2021
 * @project spring-template
 **/

@Log4j2
public class SimpleDateUtil {
    public static final String tehranFormat = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String timeFormat = "HH:mm:ss";
    private static final String arangoformat = "yyyy-MM-dd'T'HH:mm";
    private static final String utcFormat = "yyyy-MM-dd'T'HH:mm:ssZ";

    public static LocalDateTime getLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static Date getExecutionDate(String date) throws ProjectException {

        try {
            if (isValidFormat(date, utcFormat)) {
                DateFormat formatter = new SimpleDateFormat(utcFormat);
                return formatter.parse(date);

            } else if (isValidFormat(date, tehranFormat)) {
                return getTehranFormat().parse(date);

            }
        } catch (ParseException e) {

        }
        throw new ProjectException(ExceptionStatus.INVALID_DATE_FORMAT);
    }

    public static Date getCurrentDate() {
        LocalDateTime localDateTime = LocalDateTime.now();

        return Date.from(Instant.from(localDateTime.atZone(ZoneId.systemDefault())));
    }


    public static Date getDate(LocalDateTime localDateTime) {
        return Date.from(Instant.from(localDateTime.atZone(ZoneId.systemDefault())));
    }

    public static long getTimestamp(String time, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(time).getTime();
    }


    public static DateFormat getDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getDefault());

        return dateFormat;
    }

    public static DateFormat get8601Format() {
        DateFormat formatter = new SimpleDateFormat(utcFormat);
        formatter.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

        return formatter;
    }


    public static String getUtcTime(LocalDateTime localDateTime) {
        DateFormat formatter = get8601Format();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        return formatter.format(date);
    }

    public static long getTimestamp(LocalDateTime localDateTime) {
        return Timestamp.valueOf(localDateTime).getTime();
    }


    public static String getUtcTime(Date date) {
        DateFormat formatter = get8601Format();
        formatter.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));

        return formatter.format(date);
    }

    public static DateFormat getTehranFormat() {
        DateFormat dateFormat = new SimpleDateFormat(tehranFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
        return dateFormat;
    }

    public static String getTehranTime(LocalDateTime localDateTime) {
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return getTehranFormat().format(date);

    }

    public static String getTehranTime(Date date) {
        DateFormat formatter = getTehranFormat();
//        formatter.setTimeZone(TimeZone.getDefault());

        return formatter.format(date);
    }


    public static String getTime(Date date) {
        DateFormat formatter = getTimeFormat();
        return formatter.format(date);
    }

    public static DateFormat getTimeFormat() {
        DateFormat dateFormat = new SimpleDateFormat(timeFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
        return dateFormat;
    }

    public static Date getDateTime(String date) throws ParseException {
        return new SimpleDateFormat(tehranFormat).parse(date);
    }

    public static boolean isValidFormat(String date, String datePattern) {
        DateFormat dateFormat = new SimpleDateFormat(datePattern);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidTimeFormat(String time) throws DateTimeParseException {
        DateTimeFormatter strictTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                .withResolverStyle(ResolverStyle.STRICT);

        LocalTime.parse(time, strictTimeFormatter);

        return true;
    }

    public static Date addAmountToCurrentDate(Date currentDate, int field, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(field, amount);

        return c.getTime();
    }

    public static Date getDate(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
        return simpleDateFormat.parse(date);
    }


    public static LocalDateTime convertToLocalDateTim(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static boolean isExpired(long expirationTimeStamp) {
        return expirationTimeStamp != 0 && new Date().getTime() > expirationTimeStamp;
    }

    public static boolean isUnauthorizedTime(String startTime, String endTime) {
        try {

            Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"));
            String date = currentDate.get(Calendar.YEAR) + "-" +
                    (currentDate.get(Calendar.MONTH) + 1) + "-" +
                    currentDate.get(Calendar.DAY_OF_MONTH);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));

            Date fromDate = dateFormat.parse(date + " " + startTime);
            Date toDate = dateFormat.parse(date + " " + endTime);
            Date current = dateFormat.parse(dateFormat.format(new Date()));

            return current.after(fromDate) && current.before(toDate);

        } catch (ParseException e) {
            log.error(e);
        }

        return false;
    }


    public static String getCron(String minute, String hour, String dayOfMonth, String month) {
        return new StringBuilder()
                .append(minute)
                .append(" ")
                .append(hour)
                .append(" ")
                .append(dayOfMonth)
                .append(" ")
                .append(month)
                .append(" ")
                .append("?")
                .toString();
    }

    public static Long getTimeInterval(String sendTime) {
        Long delay = null;

        try {
            Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tehran"));
            String date =
                    currentDate.get(Calendar.YEAR) + "-" +
                            (currentDate.get(Calendar.MONTH) + 1) + "-" +
                            currentDate.get(Calendar.DAY_OF_MONTH);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));

            Date sendDate = dateFormat.parse(date + " " + sendTime);

            Interval interval = new Interval(currentDate.getTime().getTime(), sendDate.getTime());

            delay = interval.toDurationMillis();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return delay;

    }

}
