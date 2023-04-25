
com.snail.dubbo.common.extension包
    dubbo SPI 扩展包
    @SPI  dubbo扩展接口。1. 声明了SPI注解，该注解只能应用在接口上.  一个接口如果被SPI注解修饰，则表示该接口是一个扩展接口
    @SPI("key")  默认使用指定key得扩展实现
