package com.snail.dubbo.common.extension;

import com.snail.dubbo.common.utils.ClassUtil;
import com.snail.dubbo.common.utils.Holder;
import com.snail.dubbo.common.utils.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 框架设计： 经常会使用到有状态的对象
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

    private Class<T> type;

    private final Map<String, Holder<T>> cachedInstances = new HashMap<>(128);

    private final Holder<Map<String, Class<T>>> cachedClasses = new Holder<>();


    private final LoadingStrategy[] loadingStrategies = new LoadingStrategy[];

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
     * 获取扩展接口的指定实例
     * @param name 实例名称
     * @return
     */
    public T getExtension(String name) {
        // 1、参数校验
        if (StringUtil.isEmpty(name)) {
            throw new IllegalArgumentException("extension name == null");
        }
        // 2、 从实例缓存中获取实例
        Holder<T> holder = getAndCreateHolder(name);
        T instance = holder.get();
        if (instance == null) {
            // 3、实例创建,放入缓存中。 cachedInstances是一个有状态资源，需要锁机制控制.
            /**
             * cachedInstances是一个有状态资源 存储所有name的所有实例。
             * 1、如果采用cachedInstances当作锁对象，锁力度太粗，导致并发度低
             * 2、如果instance实例当作锁对象， 并发度高，但是instance可能为空，如果为空，则不能当作锁
             * 3、使用Holder包装instance， 使用holder来充当锁对象，既能包装并发度
             */
            synchronized (holder) {
                // DCL
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return instance;
    }

    private Holder getAndCreateHolder(String name) {
        Holder<T> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        return holder;
    }

    private T createExtension(String name) {
        // 1、获取扩展接口的所有实现类
        Map<String, Class<T>> extensionClasses =  getExtensionClasses();
        // 2、获取指定name的实现类
        Class<?> clazz = extensionClasses.get(name);
        // 判断
        if (clazz == null) {
            // 找不到指定的扩展实现类
            throw new IllegalArgumentException("not find name = " + name + "ExtensionImplClass");
        }
        // 3、实例化实现类
        return getExtensionClassInstance(clazz);
    }

    /**
     * 获取扩展接口的所有实现类
     * @return 所有实现类
     */
    private Map<String, Class<T>> getExtensionClasses() {
        // 1、 先从缓存获取所有实现类
        Map<String, Class<T>> classess = cachedClasses.get();

        // 2、缓存没有则加锁创建。 锁选用
        /**
         * 功能是获取所有的实现类， 所以锁力度肯定会粗点，整个cachedExtensionClass 缓存级别
         * 1、如果cachedExtensionClass当作锁对象， 可能会空，
         * 2、使用Holder包装cachedExtensionClass， holder充当锁对象
         */
        if (classess == null) {
            synchronized (cachedClasses) {
                classess = cachedClasses.get();
                if (classess == null) {
                    classess = loadExtensionClasses();
                    cachedClasses.set(classess);
                }
            }
        }
        return classess;
    }

    private Map<String, Class<T>> loadExtensionClasses() {

        Map<String, Class<T>> resultMap = new HashMap<>();
        for (LoadingStrategy loadingStrategy : loadingStrategies) {
            this.loadDirectory(resultMap, loadingStrategy, type.getName());
        }
        return resultMap;
    }

    /**
     * 加载资源使用的是 classLoader.getResources(fileName)
     * @param loadingStrategy
     * @param type
     */
    private void loadDirectory(Map<String, Class<T>> extensionClasses, LoadingStrategy loadingStrategy, String type) {
        String fileName = loadingStrategy.directory() + type;

        //1、 获取类加载器
        ClassLoader classLoader = findClassLoader();

        // 2、加载资源
        Enumeration<URL> urls = classLoader.getResources(fileName);

        // 3、 URL转为Class
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            this.loadResource(extensionClasses, url);
        }
    }

    /**
     * resourceURL 资源中数据的格式， key = value; key表示扩展接口实现类的别名， value表示 扩展接口实现类的全程
     * 可以写# 号， #号表示注释， 只支持单行注释。
     * 也会存在空格的情况。
     *
     * 实现方式：
     * 按行读取文件中的数据，取value的值，
     * 然后调用Class.forName(clazz, true, classLoader) 生成Class文件。
     * @param extensionClasses
     * @param resourceURL
     */
    private void loadResource(Map<String, Class<T>> extensionClasses, URL resourceURL) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8));
            String line = null;
            String clazz = null;
            while ((line = bufferedReader.readLine()) != null) {
                // 35表示： #
                int ci = line.indexOf(35);
                if (ci >= 0) {
                    // 找到#号前面的字符串
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (line.length() > 0) {
                    // 获取类名
                    String name = null;
                    // 61表示 =号
                    int i = line.indexOf(61);
                    if (i > 0) {
                        name = line.substring(0, i).trim();
                        clazz = line.substring(i + 1).trim();
                    } else {
                        clazz = line;
                    }
                    if (!StringUtil.isEmpty(clazz)) {
                        System.out.println(clazz);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClassLoader findClassLoader() {
        // 获取类加载器， 打破双亲委派机制
        return ClassUtil.getClassLoader(ExtensionLoader.class);
    }

}
