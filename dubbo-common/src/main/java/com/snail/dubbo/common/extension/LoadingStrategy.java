package com.snail.dubbo.common.extension;

/**
 * 扩展接口实现类加载策略
 * @author liuyajie
 */
public interface LoadingStrategy {

    /**
     * 扩展接口实现类的查找目录
     * @return
     */
    String directory();
}
