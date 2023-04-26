package com.snail.dubbo.common.utils;

/**
 * @author liuyajie
 * @date 2023/04/26/3:06 下午
 */
public class Holder<T> {

    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
