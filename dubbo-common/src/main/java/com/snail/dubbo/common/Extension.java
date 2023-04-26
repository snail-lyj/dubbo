package com.snail.dubbo.common;


import java.lang.annotation.*;


/**
 * 作用：指定扩展类实现类的别名
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Extension {

    String value() default "";
}
