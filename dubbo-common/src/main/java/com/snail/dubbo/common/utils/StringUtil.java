package com.snail.dubbo.common.utils;

/**
 * @author liuyajie
 * @date 2023/04/26/11:51 上午
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     * 空的定义： str == null 或者 str的长度为0
     * @param str 字符串
     * @return true, false
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
