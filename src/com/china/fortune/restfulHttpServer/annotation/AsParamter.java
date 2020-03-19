package com.china.fortune.restfulHttpServer.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsParamter {
    boolean isNecessary() default true;
}
