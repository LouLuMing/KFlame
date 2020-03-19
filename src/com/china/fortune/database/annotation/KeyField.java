package com.china.fortune.database.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface KeyField {
    int size() default 255;
    boolean isPK() default false;
    boolean isIndex() default false;
    boolean ignore() default false;
}
