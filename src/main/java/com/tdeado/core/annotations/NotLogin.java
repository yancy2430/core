package com.tdeado.core.annotations;

import java.lang.annotation.*;

/**
 * 不判断登录
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface NotLogin {

}
