package com.snail.dubbo.common.extension;

/**
 * @author liuyajie
 * @date 2023/04/26/4:31 下午
 */
public class ServicesLoadingStrategy implements LoadingStrategy {
    @Override
    public String directory() {
        return "META-INF/services/";
    }
}
