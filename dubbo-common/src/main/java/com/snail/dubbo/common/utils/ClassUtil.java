package com.snail.dubbo.common.utils;

/**
 * 
 * @author liuyajie
 * @date 2023/04/26/7:02 下午
 */
public class ClassUtil {

    /**
     * 打破双亲委派方式，获取类加载器
     * @param clazz
     * @return
     */
    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ignored) {

        }

        if (cl == null) {
            cl = clazz.getClassLoader();
            if (cl == null) {
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ignored) {

                }
            }
        }
        return cl;
    }
}
