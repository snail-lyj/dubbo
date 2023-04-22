package com.snail.dubbo.common.extension;


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * ExtensionLoader设计
 *
 * 1、一个扩展点对应一个ExtensionLoader。多个扩展点对应多个ExtensionLoader。 该类是共用
 *
 * 2、扩展点
 *
 * 3、
 *
 *
 *
 *
 *
 */
public class ExtensionLoader<T> {

    private static final ConcurrentMap<Class<?>, Object> EXTENSION_LOADERS = new ConcurrentHashMap<>(64);

    private final Class<T> type;

    ExtensionLoader(Class<T> type) {
        this.type = type;
    }

    /**
     * 扩展类加载器只能通过该方法构件，不能通过构造方法，因为构造方法是私有的
     *
     * 该方法的作用
     * 1、接受一个类
     * 判断这个类是否是dubbo扩展类，如果是才会返回扩展类加载器，才会进行dubbo后续的扩展特性
     * @param type
     * @return
     */
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("extension type is null");
        }

        if (!type.isInterface()) {
            throw new IllegalArgumentException("extension type (" + type + ") is not interface");
        }

        if (!withExtensionAnnotation(type)) {
            throw new IllegalArgumentException("extension type (" + type + ") is not extension, because without @" + SPI.class.getSimpleName() + "Annotation");
        }

        // 如果这个地方直接new,垃圾回收得时候就会被回收掉，影响性能。 改用缓存，需要考虑线程安全
        // return new ExtensionLoader(type);
        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    /**
     * type类上是否直接有@SPI注解
     * @param type 指定类
     * @return true type上直接有@SPI注解， 否则返回false
     *
     */
    private static <T> boolean withExtensionAnnotation(Class<T> type) {
        return type.isAnnotationPresent(SPI.class);
    }


    /**
     * 实例方法：先获取扩展类加载器，表明该接口是dubbo扩展接口。
     * 然后用扩展类加载器获取扩展点
     * 获取扩展点
     * @return
     */
    public T getExtension() {

        // 1、判断 type是否是扩展接口


        // 1、 获取所有的扩展类

        // 2、 实例化指定的扩展点
        return null;
    }

    /**
     * 获取指定扩展点实例
     * @param name
     * @return
     */
    public T getExtension(String name) {
        return null;
    }
}
