package com.spring.template.springtemplate.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Ali Mojahed on 6/4/2021
 * @project spring-template
 **/
public class MyDateTime {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int milliSecond;

    public MyDateTime(int year, int month, int day, int hour, int minute, int second, int milliSecond) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.milliSecond = milliSecond;
    }

    public SpecificDate getDate() {
        return new SpecificDate(year, month, day);
    }

    public Timestamp toTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, milliSecond);
        return new Timestamp(calendar.getTime().getTime());
    }

    public Date toDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MILLISECOND, milliSecond);
        return calendar.getTime();
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getRight() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getMilliSecond() {
        return milliSecond;
    }

    public void setMilliSecond(int milliSecond) {
        this.milliSecond = milliSecond;
    }

    @Override
    public String toString() {
        return year + "/" + month + "/" + day + "-" + hour + ":" + minute + ":" + second + ":" + milliSecond;
    }

    public class SpecificDate {
        private int year;
        private int month;
        private int day;

        public SpecificDate(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SpecificDate fanapDate = (SpecificDate) o;

            return year == fanapDate.year && month == fanapDate.month && day == fanapDate.day;

        }

        @Override
        public int hashCode() {
            int result = year;
            result = 31 * result + month;
            result = 31 * result + day;
            return result;
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }
    }
}
