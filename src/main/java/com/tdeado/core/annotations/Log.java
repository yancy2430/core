package com.tdeado.core.annotations;


import java.lang.annotation.*;

/**
 * 自定义操作日志记录注解
 *
 * @author ruoyi
 *
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log
{
    /**
     * 模块
     */
    public String title() default "";

    /**
     * 业务ID
     */
    public String busId() default "";


    /**
     * 是否保存返回的数据
     */
    public boolean isSaveReturnData() default true;
}
