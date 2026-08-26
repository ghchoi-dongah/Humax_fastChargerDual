package com.dongah.fastcharger.utils;

import android.annotation.SuppressLint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeConvert {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeConvert.class);

    @SuppressLint("SimpleDateFormat")
    public String dateTimeCovertY2ToY4(String value) {
        DateFormat formatter2y, formatter4y;
        Date date = null;
        formatter2y = new SimpleDateFormat("yyMMdd");
        try {
            date = formatter2y.parse(value);
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        formatter4y = new SimpleDateFormat("yyyyMMdd");
        return formatter4y.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public String dateTimeCovertY4ToY2(String value) {
        DateFormat formatter2y, formatter4y;
        Date date = null;
        formatter4y = new SimpleDateFormat("yyyyMMdd");
        try {
            date = formatter4y.parse(value);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            return value.substring(2);
        }
        formatter2y = new SimpleDateFormat("yyMMdd");
        return formatter2y.format(date);
    }
}
