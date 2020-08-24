package com.china.fortune.restfulHttpServer.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AddFilter {
    Class<?>[] lsFilter() default {Object.class};
}
