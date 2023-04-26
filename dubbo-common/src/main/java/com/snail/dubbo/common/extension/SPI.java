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
 * 用法：
 * 用在接口上， 如果一个接口被 @SPI注解修饰，表示该接口是扩展接口
 * value： 表示该扩展接口的默认实例
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {
    String value();
}
