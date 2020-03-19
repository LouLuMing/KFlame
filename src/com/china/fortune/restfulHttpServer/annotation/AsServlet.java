package com.china.fortune.restfulHttpServer.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsServlet {
    boolean ipAllow() default false;
    boolean ipFrequent() default false;
}
