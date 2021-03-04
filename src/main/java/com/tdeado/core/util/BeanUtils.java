package com.tdeado.core.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author 雷超
 * @version 1.0.0
 * @date 2020/7/3 16:55
 **/
@Slf4j
public class BeanUtils {

    public static void populate(Object bean, Map<String, Object> param){
        Class<?> clazz = bean.getClass();
        for (String key : param.keySet()) {
            try{
                Field field = clazz.getDeclaredField(key);
                field.setAccessible(true);
                Class<?> type = field.getType();
                String value = param.get(key).toString();

                if(type.equals(String.class)){
                    field.set(bean,value);
                }else if(type.equals(Integer.class)){
                    field.set(bean,Integer.parseInt(value));
                }else if(type.equals(Double.class)) {
                    field.set(bean,Double.parseDouble(value));
                }else if(type.equals(Long.class)){
                    field.set(bean,Long.parseLong(value));
                }else if(type.equals(BigDecimal.class)){
                    field.set(bean,new BigDecimal(Double.parseDouble(value)));
                }else if(type.equals(LocalDate.class)){
                    field.set(bean,TimeConvertUtils.stringToLocalDate(value));
                }else if(type.equals(LocalDateTime.class)){
                    field.set(bean,TimeConvertUtils.stringToLocalDateTime(value));
                }
            }catch (NoSuchFieldException e){
                log.error("No Such Field,FieldName:{}",key);
            } catch (IllegalAccessException e) {
                log.error("类型转换错误,FieldName:{}",key);
            }
        }
    }


}
