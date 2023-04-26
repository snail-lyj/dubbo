package com.snail.dubbo.common.extension;

/**
 * 
 * @author liuyajie
 * @date 2023/04/26/4:36 下午
 */
public class DubboLoadingStrategy implements LoadingStrategy {
    @Override
    public String directory() {
        return "META-INF/dubbo";
    }
}
