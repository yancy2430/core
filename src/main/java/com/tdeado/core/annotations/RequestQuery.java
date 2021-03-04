package com.tdeado.core.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD,ElementType.PARAMETER})
public @interface RequestQuery {
    Class<?> dto() default Void.class;
    String[] exclude() default {};
}
