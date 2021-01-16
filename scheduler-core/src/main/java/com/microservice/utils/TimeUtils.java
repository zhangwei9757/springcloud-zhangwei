package com.microservice.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * @author zhangwei
 * @date 2019-12-10
 * <p>
 * Time 工具类
 */
public class TimeUtils {

    public static String format1 = "yyyy-MM-dd HH:mm:ss";
    public static String format2 = "yyyy-MM-dd HH:mm";
    public static String format3 = "yyyy-MM-dd";
    public static String format4 = "yyyy年MM月dd日";
    public static String format5 = "yyyy年MM月";
    public static String format6 = "yyyy年MM月dd日 HH时";
    public static String format7 = "HH:mm";
    public static String format8 = "yyMMddHHmmss";
    public static String format9 = "yyyy-MM-dd HH";
    public static String format10 = "yyyy-MM";
    public static String format11 = "yyyy";
    public static String format12 = "HH";
    public static String format13 = "dd";
    public static String format14 = "MM";
    public static String format15 = "yyyyMMdd";
    public static String format16 = "yyyyMM";
    public static String format17 = "HH:mm:ss";

    /**
     * 获取当前时间 20190907
     *
     * @return
     */
    public static int getCurrentDay() {
        LocalDate ldt = LocalDate.now();
        return ldt.getYear() * 10000 + ldt.getMonthValue() * 100 + ldt.getDayOfMonth();
    }

    /**
     * 今天零晨
     *
     * @return
     */
    public static String getCurrentZero() {
        long current = System.currentTimeMillis();
        long zeroT = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(zeroT);
    }

    /**
     * 明天零晨
     *
     * @return
     */
    public static String getCurrentEnd() {
        long current = System.currentTimeMillis();
        long zeroT = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        long endT = zeroT + 24 * 60 * 60 * 1000 - 1;
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endT);
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date getCurrDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    /**
     * 获取当前unixtime时间
     *
     * @return
     */
    private static SimpleDateFormat sdfLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static long getCurrDateUnixtime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime().getTime();
    }

    public static long getUnixtimeByMinute(int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, min);
        return calendar.getTime().getTime();
    }

    public static long getUnixtimeByHour(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hour);
        return calendar.getTime().getTime();
    }

    public static long getUnixtimeByDay(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, day);
        return calendar.getTime().getTime();
    }

    public static Date millisecondToDate(long millisecond) {
        return new Date(millisecond);
    }


    /**
     * 获取当前时间前七天的时间
     *
     * @return
     */
    public static Date getBefore7Day() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -6);
        return c.getTime();
    }

    /**
     * 判断当前时间相差的天数
     *
     * @param beginTime
     * @return
     */
    public static long belongCalendar(Date beginTime) {
        LocalDate beginLocalDate = dateToLocalDate(beginTime);
        LocalDate now = LocalDate.now();
        return Duration.between(beginLocalDate, now).toDays();
    }

    /**
     * 获取当前时间的凌晨
     *
     * @return
     */
    public static Date getDayFirst(Date c) {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(c);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return currentDate.getTime();
    }

    /**
     * 获取当天时间的凌晨
     *
     * @return
     */
    public static Date getcurrentDayFirst(Date c) {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(c);
        int day = currentDate.get(Calendar.DATE);
        currentDate.set(Calendar.DATE, day);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        return currentDate.getTime();

    }

    /**
     * 获取当天时间的最后时间，即23:59:59
     *
     * @return
     */
    public static LocalDateTime getcurrentDayLast(Date c) {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(c);
        int day = currentDate.get(Calendar.DATE);
        currentDate.set(Calendar.DATE, day);
        currentDate.set(Calendar.HOUR_OF_DAY, 23);
        currentDate.set(Calendar.MINUTE, 59);
        currentDate.set(Calendar.SECOND, 59);
        return dateToLocalDateTime(currentDate.getTime());

    }

    /**
     * 获取传入时间时间的前一小时的时间
     */
    public static String getLastHour(Date searchTime) {
        Calendar curr = Calendar.getInstance();
        curr.setTime(searchTime);
        curr.add(Calendar.HOUR_OF_DAY, -1);
        String date = getDateToStr(curr.getTime(), format9);
        return date;
    }

    /**
     * 取当月的第一天
     *
     * @return
     */
    public static Date getMonthFirst(Date c) {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(c);
        currentDate.set(Calendar.DAY_OF_MONTH, 1);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        Date date = currentDate.getTime();
        return date;
    }

    /**
     * 取当年的第一个月
     *
     * @return
     */
    public static Date getYearFirst(Date c) {
        Calendar currentDate = Calendar.getInstance();
        currentDate.setTime(c);
        currentDate.set(Calendar.MONTH, 0);
        currentDate.set(Calendar.DAY_OF_MONTH, 1);
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);

        return currentDate.getTime();
    }

    /**
     * 获取当前时间的毫秒数
     *
     * @return
     */
    public static long getTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    /**
     * 获取当前时间的后一天时间
     *
     * @param cl
     * @return
     */
    public static Date getAfterDay(Date cl) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cl);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取当前时间的后一月时间
     *
     * @param cl
     * @return
     */
    public static Date getAfterMonth(Date cl) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cl);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取当前时间的后一天时间
     *
     * @param cl
     * @return
     */
    public static Date getAfterYear(Date cl) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(cl);
        calendar.set(Calendar.MONTH, 12);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    /**
     * 获取月份起始日期
     *
     * @param date
     * @return
     */
    public static String getMinMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

        return getDateToStr(calendar.getTime(), format3);
    }

    /**
     * 获取月份最后日期
     *
     * @param date
     * @return
     */
    public static String getMaxMonthDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));

        return getDateToStr(calendar.getTime(), format3);
    }

    /**
     * 日期转换为字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String getDateToStr(Date date, String format) {
        if (StringUtils.isBlank(format)) {
            format = format1;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * LocalDateTime  To  String
     *
     * @param dateTime
     * @param format
     * @return
     */
    public static String localDateTimeToStr(LocalDateTime dateTime, String format) {
        if (StringUtils.isBlank(format)) {
            format = format1;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(dateTimeFormatter);
    }

    /**
     * timestamp  To  LocalDateTime
     *
     * @param timeStamp
     * @return
     */
    public static LocalDateTime timeStampToLocalDateTime(long timeStamp) {
        return Instant.ofEpochMilli(timeStamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    public static LocalDateTime timeStampToLocalDateTime(String timeStamp) {
        long parseLong = Long.parseLong(timeStamp);
        return Instant.ofEpochMilli(parseLong).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * LocalDateTime To timeStamp
     *
     * @param dateTime
     * @return
     */
    public static long LocalDateTimeTotimeStamp(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }


    public static List<String> getMonthList(Date beginDate, Date endDate) {
        List<String> list = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        long tempTime = beginDate.getTime();
        long endTime = endDate.getTime();
        while (tempTime <= endTime) {
            calendar.setTimeInMillis(tempTime);
            list.add(getDateToStr(calendar.getTime(), format14));

            calendar.add(Calendar.MONTH, 1);
            tempTime = calendar.getTimeInMillis();
        }

        return list;
    }

    /**
     * 获取时间段内的年份
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static List<String> getYearList(Date beginDate, Date endDate) {
        List<String> list = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        long tempTime = beginDate.getTime();
        long endTime = endDate.getTime();
        while (tempTime <= endTime) {
            calendar.setTimeInMillis(tempTime);
            list.add(getDateToStr(calendar.getTime(), format11));

            calendar.add(Calendar.YEAR, 1);
            tempTime = calendar.getTimeInMillis();
        }

        return list;
    }

    public static List<String> getHourList(Date beginDate, Date endDate) {
        List<String> list = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        long tempTime = beginDate.getTime();
        long endTime = endDate.getTime();
        while (tempTime <= endTime) {
            calendar.setTimeInMillis(tempTime);
            list.add(getDateToStr(calendar.getTime(), format12));

            calendar.add(Calendar.HOUR_OF_DAY, 1);
            tempTime = calendar.getTimeInMillis();
        }

        return list;
    }

    public static List<String> getDayList(Date beginDate, Date endDate) {
        List<String> list = new ArrayList<String>();

        Calendar calendar = Calendar.getInstance();
        long tempTime = beginDate.getTime();
        long endTime = endDate.getTime();
        while (tempTime <= endTime) {
            calendar.setTimeInMillis(tempTime);
            list.add(getDateToStr(calendar.getTime(), format13));

            calendar.add(Calendar.DATE, 1);
            tempTime = calendar.getTimeInMillis();
        }

        return list;
    }

    /**
     * 获取系统当前时间的前一天时间
     */
    public static String getNextDay() {
        Date dNow = new Date();   //当前时间
        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(dNow); //把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        String date = getDateToStr(calendar.getTime(), format3);

        return date;
    }

    /**
     * 获取所传时间的前一天时间
     */
    public static String getLastDay(Date searchTime) {
        Calendar calendar = Calendar.getInstance();  //得到日历
        calendar.setTime(searchTime); //把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        String date = getDateToStr(calendar.getTime(), format3);

        return date;
    }

    /**
     * 获取传入时间的上一个月时间
     */
    public static String getLastMonth(Date searchTime) {
        Calendar c = Calendar.getInstance();
        c.setTime(searchTime);
        c.add(Calendar.MONTH, -1);
        String date = getDateToStr(c.getTime(), format10);
        return date;
    }

    /**
     * 获取传入时间时间的前一年时间
     */
    public static String getLastYear(Date searchTime) {
        Calendar curr = Calendar.getInstance();
        curr.setTime(searchTime);
        curr.add(Calendar.YEAR, -1);
        String date = getDateToStr(curr.getTime(), format3);
        return date;
    }


    /**
     * 字符串日期转Date类型
     */
    public static Date getDateTime(String dateTime, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * 获取两个时间中的最大时间
     *
     * @param d1
     * @param d2
     * @return
     */
    public static Date getMaxDate(Date d1, Date d2) {
        return (d1.getTime() > d2.getTime()) ? d1 : d2;
    }

    /**
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date getDate(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }

    /**
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param second
     * @return
     */
    public static Date getMo5Date(int year, int month, int day, int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute - minute % 5);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }

    /**
     * 判断两个时间相差几个月
     *
     * @param str1
     * @return
     */
    public static int getMonth(String str1) {
        SimpleDateFormat sdf = new SimpleDateFormat(format10);
//        String str1 = "2012-02";
        String str2 = getDateToStr(new Date(), format10);
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        try {
            bef.setTime(sdf.parse(str1));
            aft.setTime(sdf.parse(str2));
        } catch (Exception e) {
            e.getStackTrace();
        }
        int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
        int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
        return Math.abs(month + result);
    }

    /**
     * 6      * 时间戳转换成日期格式字符串
     * 7      * @param seconds 精确到秒的字符串
     * 8      * @param formatStr
     * 9      * @return
     * 10
     */
    public static Date timeStamp2Date(String seconds, String format) {
        if (format == null || format.isEmpty()) {
            format = TimeUtils.format2;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return new Date(Long.valueOf(seconds));
    }

    /**
     * 时间戳到日期字符串
     *
     * @param ts
     * @param formatter
     * @return
     */
    public static String longToStr(long ts, String formatter) {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(ts, 0, ZoneOffset.ofHours(8));
        return ldt.format(DateTimeFormatter.ofPattern(formatter));
    }

    /**
     * 时间戳到日期类
     *
     * @param ts
     * @return
     */
    public static Date longToDate(long ts) {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(ts, 0, ZoneOffset.ofHours(8));
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = ldt.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * 时间字符串转时间戳
     *
     * @param ldt
     * @return
     */
    public static long StrToLong(String ldt) {

        return LocalDateTime.parse(ldt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    /**
     * 当前时间的字符串
     *
     * @return
     */
    public static String nowString() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());
    }

    public static int getToday() {
        LocalDate ldt = LocalDate.now();
        return ldt.getYear() * 10000 + ldt.getMonthValue() * 100 + ldt.getDayOfMonth();
    }

    public static String getTodayOfYesr() {
        return String.valueOf(getToday() / 10000);
    }

    /**
     * 用年和周组成一个唯一的星期时间，一个星期内都是这个标识时间
     *
     * @return
     */
    public static int getWeekDay() {
        Calendar c = new GregorianCalendar(Locale.CHINA);
        return c.getWeekYear() * 100 + c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 返回星期几; 周一是1，周日是7
     *
     * @return
     */
    public static int WeekDay() {
        LocalDate ld = LocalDate.now();
        return ld.getDayOfWeek().getValue();
    }

    public static int getNextWeekDay() {
        LocalDate ld = LocalDate.now();
        int day = (8 - ld.getDayOfWeek().getValue());
        LocalDate nd = ld.plusDays(day);
        return (nd.getYear() * 1000 + nd.getDayOfYear());
    }

    /**
     * 将一个Date转换为20171201类似的数字
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(date.getTime() / 1000, 0, ZoneOffset.ofHours(8));
        return ldt.getYear() * 10000 + ldt.getMonthValue() * 100 + ldt.getDayOfMonth();
    }

    /**
     * 反转上述操作，将一个20171201的数字，转换为Date
     *
     * @param day
     * @return
     */
    public static LocalDate fromDay(int day) {
        int year = day / 10000;
        int tmp = day % 10000;
        int month = tmp / 100;
        int d = tmp % 100;

        return LocalDate.of(year, month, d);
    }


    public static int getDay(LocalDate ld) {
        return ld.getYear() * 10000 + ld.getMonthValue() * 100 + ld.getDayOfMonth();
    }


    /**
     * 得到到进入为止过去的日期, 今天到今天是0天
     *
     * @param date
     * @return
     */
    public static int pastDays(Date date) {
        Instant instance = Instant.ofEpochMilli(date.getTime());
        LocalDate ld = LocalDateTime.ofInstant(instance, ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();
        if (ld.isAfter(today)) {
            return -1;
        }

        return (int) (today.toEpochDay() - ld.toEpochDay());
    }

    /**
     * 计算二个指定日期的相差天数
     *
     * @param start 开始日期 格式：20181008
     * @param end   结束日期 格式：20181008
     * @return 相差天数
     */
    public static int fromDuration(int start, int end) {
        return (int) ChronoUnit.DAYS.between(fromDay(start), fromDay(end));
    }

    /**
     * 返回当前时间 指定格式
     *
     * @return
     */
    public static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 返回当前时间戳 long
     *
     * @return
     */
    public static synchronized long currentTime() {
        return LocalDateTime.now().toEpochSecond(ZoneOffset.ofHours(8));
    }

    /**
     * 比较时间大小，传入时间与当前时间比较
     *
     * @param locateDateTime
     * @return
     */
    public static boolean compareTime(LocalDateTime locateDateTime) {
        if (!Optional.ofNullable(locateDateTime).isPresent()) {
            return false;
        }
        long time = locateDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        return time - currentTime() > 0;
    }

    /**
     * 比较时间差
     *
     * @param sourceTime 开始
     * @param targetTime 结束
     * @return
     */
    public static long compareTime(LocalDateTime sourceTime, LocalDateTime targetTime) {
        if (null == sourceTime) {
            sourceTime = LocalDateTime.now();
        }
        if (null == targetTime) {
            targetTime = LocalDateTime.now();
        }
        long source = sourceTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        long target = targetTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        return target - source;
    }


    /**
     * 比较时间差额，传入时间与当前时间比较
     *
     * @param locateDateTime
     * @return
     */
    public static long countCompareTime(LocalDateTime locateDateTime) {
        Duration between = Duration.between(locateDateTime, LocalDateTime.now());
        return between.toHours();
    }

    /**
     * 比较两个时间的差额，时间戳之差(分钟数)
     *
     * @param sourceTime
     * @param targetTime
     * @return
     */
    public static long countCompareTimeMin(LocalDateTime sourceTime, LocalDateTime targetTime) {
        if (null == targetTime) {
            targetTime = LocalDateTime.now();
        }
        Duration between = Duration.between(sourceTime, targetTime);
        return between.toMinutes();
    }


    /**
     * 比较两个时间的差额，时间戳之差(秒数)
     *
     * @param sourceTime
     * @param targetTime
     * @return
     */
    public static long countCompareTimeSecond(LocalDateTime sourceTime, LocalDateTime targetTime) {
        if (null == targetTime) {
            targetTime = LocalDateTime.now();
        }
        Duration between = Duration.between(sourceTime, targetTime);
        return between.toMillis();
    }

    /**
     * 获取当前时间字符串
     *
     * @return
     */
    public static int localDateTimeToInt(LocalDateTime ldt) {
        return ldt.getYear() * 10000 + ldt.getMonthValue() * 100 + ldt.getDayOfMonth();
    }

    /**
     * 获取指定格式的日期字符串
     *
     * @param format
     * @return
     */
    public static String currentFullTime(String format) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * long 转 LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * LocalDateTime -> Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            localDateTime = LocalDateTime.now();
        }
        return Date.from(localDateTime.atZone(ZoneOffset.ofHours(8)).toInstant());
    }

    /**
     * String -> LocalDateTime
     *
     * @param dateString
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String dateString) throws Exception {
        Date date = null;
        if (StringUtils.isEmpty(dateString)) {
            date = new Date();
        } else {
            date = new SimpleDateFormat(format3).parse(dateString);
        }
        return date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * 精确到时分秒
     * @param dateString
     * @return
     * @throws Exception
     */
    public static LocalDateTime strToLocalDateTime(String dateString) throws Exception {
        Date date = null;
        if (StringUtils.isEmpty(dateString)) {
            date = new Date();
        } else {
            date = new SimpleDateFormat(format1).parse(dateString);
        }
        return date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * Date -> LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (null == date) {
            date = new Date();
        }
        return date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * Date -> LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        if (null == date) {
            date = new Date();
        }
        return date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDate();
    }

    /**
     * 获取指定秒，经过的时分秒   59L -> 59秒
     *
     * @param duration
     * @return
     */
    public static String durationToStr(long duration) {
        StringBuilder desc = new StringBuilder();
        if (duration < 0) {
            duration = Math.abs(duration);
        }
        long hour = duration >= 3600 ? duration / 3600 : 0;
        long minute = duration >= 60 ? duration / 60 : 0;

        if (hour > 0) {
            desc.append(hour);
            desc.append("小时");
            minute = duration % 3600 / 60;
            desc.append(minute);
            desc.append("分钟");
            if (minute > 1) {
                long second = duration % 3600 % 60;
                desc.append(second);
                desc.append("秒");
            } else {
                desc.append(duration % 3600 % 60);
                desc.append("秒");
            }
        } else if (minute > 0) {
            long second = duration % 60;
            desc.append(minute);
            desc.append("分钟");
            desc.append(second);
            desc.append("秒");
        } else {
            desc.append(duration);
            desc.append("秒");
        }
        return desc.toString();
    }

    /**
     * LocalDateTime  -> 秒
     *
     * @param dateTime
     * @return
     */
    public static long toEpochSecond(LocalDateTime dateTime) {
        if (null == dateTime) {
            throw new RuntimeException("LocalDateTime->秒, 参数为空!");
        }
        return dateTime.toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * 校验时间段区间，在指定秒值是否生效
     *
     * @param startTime   区间开始时间
     * @param endTime     区间结束时间
     * @param currentTime 当前时间
     * @return
     */
    public static boolean durationAllow(LocalDateTime startTime, LocalDateTime endTime, LocalDateTime currentTime) {
        if (null == startTime || null == endTime) {
            throw new RuntimeException("校验时间段区间，在指定秒值是否生效, 参数存在空值!");
        }

        long toEpochSecondStart = TimeUtils.toEpochSecond(startTime);
        long toEpochSecondEnd = TimeUtils.toEpochSecond(endTime);
        long currentScanEpochSecond = TimeUtils.toEpochSecond(currentTime);

        boolean allowStart = currentScanEpochSecond >= toEpochSecondStart;
        boolean allowEnd = currentScanEpochSecond < toEpochSecondEnd;

        // 当前时间已生效，且未到失效时间
        return allowStart && allowEnd;
    }

    /**
     * 目标时间秒 是否大于 待比较时间秒
     *
     * @param targetTime  目标时间秒
     * @param compareTime 待比较时间秒
     * @return
     */
    public static boolean compareEpochSecond(LocalDateTime targetTime, LocalDateTime compareTime) {
        if (null == targetTime || null == compareTime) {
            throw new RuntimeException("LocalDateTime->秒, 参数为空!");
        }
        return TimeUtils.toEpochSecond(targetTime) >= TimeUtils.toEpochSecond(compareTime);
    }

    /**
     * 根据开始时间和结束时间拆分其中的日期
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> splitDate(LocalDateTime startTime, LocalDateTime endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(format3);
        Date start = localDateTimeToDate(startTime);
        Date end = localDateTimeToDate(endTime);
        //存放拆分的日期
        List<String> allDate = new ArrayList();
        //放入当前日期
        allDate.add(sdf.format(start));
        Calendar calBegin = Calendar.getInstance();
        calBegin.setTime(start);
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(end);
        //当是同一天时不需要走后面的逻辑
        boolean sameDay = DateUtils.isSameDay(calBegin, calEnd);
        if (sameDay) {
            return allDate;
        }
        while (end.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            allDate.add(sdf.format(calBegin.getTime()));
        }
        return allDate;
    }

    //基于当日加减
    public static String dayAdd(int days) {
        SimpleDateFormat form = new SimpleDateFormat(format3);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, day + days);
        Date date = calendar.getTime();
        return form.format(date);
    }

    /**
     * 校验字符串的日期格式是否符合规范
     * @param str
     * @return
     */
    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        SimpleDateFormat format = new SimpleDateFormat(format3);
        try {
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            convertSuccess=false;
        }
        return convertSuccess;
    }
}
