package com.snail.dubbo.common.extension;

import java.lang.annotation.*;

/**
 *
 * // @Retention(RetentionPolicy.RUNTIME) 注解得生命周期
 *  SOURCE： 编译阶段存在
 *  CLASS： 类加载阶段
 *  RUNTIME： 运行时阶段
 * ElementType.TYPE 注解只能用在类上
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {
    String value();
}
