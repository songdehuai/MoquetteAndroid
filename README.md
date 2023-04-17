# 这是一个运行在Android上的MqttBroker

如果你在找一个可以在Android上运行的MqttBroker,那么你找到了

本项目基于
[moquette-io/moquette](https://github.com/moquette-io/moquette)

## 注意!!!

如果您要在项目是使用,请在build.gradle android { } 中添加以下代码

```
 packagingOptions {
        exclude 'META-INF/INDEX.LIST'
        exclude 'META-INF/io.netty.versions.properties'
 }
```

感谢

[moquette-io/moquette](https://github.com/moquette-io/moquette)

[Blankj/AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)
