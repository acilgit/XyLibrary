package com.xycode.xylibrary.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Administrator on 2016/9/5.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SaveState {
    int JSON_OBJECT = 1;
    int NORMAL_OBJECT = 0;
    int value() default NORMAL_OBJECT;

}
