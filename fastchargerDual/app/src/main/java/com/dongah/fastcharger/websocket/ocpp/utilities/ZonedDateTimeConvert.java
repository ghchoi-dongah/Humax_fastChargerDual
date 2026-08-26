package com.dongah.fastcharger.websocket.ocpp.utilities;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ZonedDateTimeConvert {

    private static final Logger logger = LoggerFactory.getLogger(ZonedDateTimeConvert.class);
    final String ZONED_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    final String ZONED_DATE_TIME_FORMAT1 = "yyyy-MM-dd'T'HH:mm:ss";
    final String ZONED_DATE_TIME_FORMAT_WITH_MS = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    final String SIMPLE_DATE_TIME_FORMAT = "yyyyMMddHHmmss";
    final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSSS";

    public ZonedDateTimeConvert() {
    }


    public String doZonedDateTimeToString(String inputDateTime) {
        Date parsed = null;
        String outputDateTime = null;
        SimpleDateFormat dfInput = new SimpleDateFormat(SIMPLE_DATE_TIME_FORMAT, Locale.getDefault());
        SimpleDateFormat dfOutput = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT, Locale.getDefault());
        try {
            parsed = dfInput.parse(inputDateTime);
            outputDateTime = dfOutput.format(parsed);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return outputDateTime;
    }

    public String doZonedDateTimeToStringUTC(String inputDateTime) {
        Date parsed = null;
        String outputDateTime = null;
        SimpleDateFormat dfInput = new SimpleDateFormat(SIMPLE_DATE_TIME_FORMAT, Locale.getDefault());
        SimpleDateFormat dfOutput = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT, Locale.getDefault());
        dfOutput.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            parsed = dfInput.parse(inputDateTime);
            outputDateTime = dfOutput.format(parsed);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return outputDateTime;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String zonedString(ZonedDateTime inputDateTime) {
        return DateTimeFormatter.ofPattern(ZONED_DATE_TIME_FORMAT1).format(inputDateTime);
    }


    public String doZonedDateTimeToString(ZonedDateTime inputDateTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return DateTimeFormatter.ofPattern(ZONED_DATE_TIME_FORMAT).format(inputDateTime);
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ZonedDateTime doZonedDateTimeToDatetime(String inputDateTime) {
        Date parsed = null;
        String outputDateTime = null;
        SimpleDateFormat dfInput = new SimpleDateFormat(SIMPLE_DATE_TIME_FORMAT, Locale.getDefault());
        SimpleDateFormat dfOutput = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT, Locale.getDefault());
        try {
            parsed = dfInput.parse(inputDateTime);
            outputDateTime = dfOutput.format(parsed);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ZonedDateTime.parse(outputDateTime);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public ZonedDateTime doZonedDateTimeToDatetime1(String inputDateTime) {
        Date parsed = null;
        String outputDateTime = null;
        SimpleDateFormat dfInput = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT, Locale.getDefault());
        SimpleDateFormat dfOutput = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT, Locale.getDefault());
        try {
            parsed = dfInput.parse(inputDateTime);
            outputDateTime = dfOutput.format(parsed);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ZonedDateTime.parse(outputDateTime);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public ZonedDateTime doZonedDateTimeToDatetime() {
        try {
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_TIME_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String utcTime = sdf.format(new Date());
            return doZonedDateTimeToDatetime(utcTime);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ZonedDateTime doZonedDateTimeToDatetime1() {
        try {
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String utcTime = sdf.format(new Date());
            return doZonedDateTimeToDatetime1(utcTime);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Date doGetUtcDatetimeAsDate() {
        return doStringDateToDate(doGetUtcDatetimeAsString());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String doGetUtcDatetimeAsString() {
//        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT);

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    public Date doStringDateToDate(String strDate) {
        Date dateToReturn = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(SIMPLE_DATE_TIME_FORMAT);

        try {
            dateToReturn = (Date) dateFormat.parse(strDate);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }

        return dateToReturn;
    }

    public Date doStringDateToDate1(String strDate) {
        Date dateToReturn = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT);

        try {
            dateToReturn = (Date) dateFormat.parse(strDate);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }

        return dateToReturn;
    }


    public boolean isString(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ZonedDateTime millsToZonedDateTime(long m) {
        ZoneId zoneId = ZoneId.systemDefault();
        Instant instant = Instant.ofEpochMilli(m);
        return ZonedDateTime.ofInstant(instant, zoneId);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getStringCurrentTimeZone() {
        try {
            LocalDateTime utcTime = LocalDateTime.now(ZoneOffset.UTC);
            return utcTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(SIMPLE_DATE_TIME_FORMAT);
//            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
//            return sdf.format(new Date());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ZonedDateTime doGetCurrentTime() {
        try {
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String utcTime = sdf.format(new Date());
            //return doZonedDateTimeToDatetime(utcTime);
            return ZonedDateTime.parse(utcTime, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public String doGetCurrentTimeUTC() {
        try {
            @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            return  sdf.format(new Date());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String doGetUtcDatetimeAsStringSimple() {
        @SuppressLint("SimpleDateFormat") final SimpleDateFormat sdf = new SimpleDateFormat(ZONED_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

}
