Android公共组件库
===========================================
####Android Common Components: Views, Widgets, Utils

已经部署到Maven Central，可以直接使用


###项目结构

分为以下几个模块：

* core - 核心库，包括异步任务/网络/工具类
* ui - UI组件，包括Dialog/ListView等
* extra-abc - 依赖AppCompat的组件
* extra-abs - 依赖ActionBarSherlock的组件
* samples - 使用示例



###使用说明

  Gradle使用方法：

```
    // core 核心库, 格式:jar和aar
    compile 'com.mcxiaoke.next:core:1.+'
    
    // ui UI组件, 格式:aar
    compile 'com.mcxiaoke.next:ui:1.+'
    
    // extra-abc 依赖support-v7 AppCompat 格式:aar
    compile 'com.mcxiaoke.next:extra-abc:1.+'
    
    // extra-abs 依赖ActionBarSherlock 格式:aar
    compile 'com.mcxiaoke.next:extra-abs:1.+'
    
```


###已有组件



###其它说明

发现任何问题可以提issue



