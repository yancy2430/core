package com.tdeado.core.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeConvertUtils {
    public final static String EXPRESSION_DAY = "yyyy-MM-dd";
    public final static String EXPRESSION_MIN = "yyyy-MM-dd HH:mm";
    public final static String EXPRESSION_SEN = "yyyy-MM-dd HH:mm:ss";


    /**
     * 字符串转换LocalDate，使用默认转换格式
     * @param dateStr 日哥字符串
     * @return
     */
    public static LocalDate stringToLocalDate(String dateStr){
        return stringToLocalDate(dateStr,EXPRESSION_DAY);
    }

    /**
     * 字符串转换LocalDate
     * @param dateStr 日哥字符串
     * @param expression 格式字符串
     * @return
     */
    public static LocalDate stringToLocalDate(String dateStr,String expression){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(expression);
        return LocalDate.parse(dateStr,dateTimeFormatter);
    }

    /**
     * 字符串转换LocalDateTime，使用默认转换格式
     * @param dateStr 日哥字符串
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String dateStr){
        return stringToLocalDateTime(dateStr,EXPRESSION_SEN);
    }

    /**
     * 字符串转换LocalDateTime
     * @param dateStr 日哥字符串
     * @param expression 格式字符串
     * @return
     */
    public static LocalDateTime stringToLocalDateTime(String dateStr,String expression){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(expression);
        return LocalDateTime.parse(dateStr,dateTimeFormatter);
    }


}
