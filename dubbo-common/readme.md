
com.snail.dubbo.common.extension包
    dubbo SPI 扩展包
    @SPI  dubbo扩展接口。1. 声明了SPI注解，该注解只能应用在接口上.  一个接口如果被SPI注解修饰，则表示该接口是一个扩展接口
    @SPI("key")  默认使用指定key得扩展实现

# dubbo 扩展点的使用
## @SPI注解
使用： 使用在接口上表示该接口是一个dubbo扩展接口
可以指定一个value值，表示该扩展接口实例 具体使用实现类的别名
存储设计：
@SPI注解修饰的扩展接口--》 使用Class<T> type 来承接
@SPI注解中的value值--》 使用 String cachedDefaultName 来承接

## 扩展接口适配器
规则： 一个扩展点只能有一个接口适配器。
可以使用@Adaptive注解，指定具体实现类为 接口适配器。
没有用户没有使用@Adaptive注解，自己指定，系统会自动生成一个接口适配器。

一个扩展点的实现类上不能被 大于一个@Adaptive注解修饰。
@Adaptive注解
    使用：该注解可以使用在扩展点实现类上，表明该实现类是扩展接口的适配器

存储设计： 一个扩展接口只能有一个适配器
适配器类--》 Class<T> cachedAdaptiveClass 来承接
适配器类的实例--》Holder<Object> cachedAdaptiveInstance 来承接


## 扩展接口实现类别名
1、可以在接口的文件中定义。 格式为 key = value。 key为别名，如果有多个别名，可以在key中使用逗号分隔，逗号前后可以有空格。
2、在接口文件中定义。 格式为 value。  默认生成别名为 value - type.getName() 剩下的字符串
3、可以使用@Extension注解。 为扩展接口实现类 标明别名
    @Extension注解使用在扩展接口实现类上

## LoadingStrategy接口
该接口表明扩展接口资源 加载的位置。 默认支持的有 META-INF/services、META-INF/dubbo、META-INF/dubbo/internal三个路径

## @Active注解

