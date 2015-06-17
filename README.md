Android Next 公共组件库
======
Tasks, Cache, Views, Widgets, Http, Utils
------

## 项目介绍


这个库是我在日常开发过程中积累下来的一些可复用组件，有一些是原创的，有一些是修改开源项目的，有一些则完全是从Android源码/Apache-Commons源码中复制过来的，大部分都在我的工作项目和个人项目中有实际使用案例，具体就不一一说明了


## 最新版本

* [![Maven Central](http://img.shields.io/badge/2015.06.16-com.mcxiaoke.next:core:1.0.9-brightgreen.svg)](http://search.maven.org/#artifactdetails%7Ccom.mcxiaoke.next%7Ccore%7C1.0.9%7Cjar)
* [![Maven Central](http://img.shields.io/badge/2015.06.16-com.mcxiaoke.next:http:1.0.9-brightgreen.svg)](http://search.maven.org/#artifactdetails%7Ccom.mcxiaoke.next%7Chttp%7C1.0.9%7Cjar)
* [![Maven Central](http://img.shields.io/badge/2015.06.16-com.mcxiaoke.next:ui:1.0.9-brightgreen.svg)](http://search.maven.org/#artifactdetails%7Ccom.mcxiaoke.next%7Cui%7C1.0.9%7Cjar)

已经部署到Maven Central，可以直接使用

- **1.0.9** 2015.06.16
    * core: 优化TaskQueue，调整接口，添加新的辅助类Task，支持链式调用
    * core: 重构MemoryCache，精简结构，缓存对象支持设置过期时间
    * core: 添加一些工具类，如PackageUtils和TrafficUtils
    * recycler: 增加EndlessRecyclerView，支持底部自动加载更多
    * 细节调整，更新示例和说明文档

- **1.0.8** 2015.05.18
    * ui: 移除所有的ic_launcher.png，修复appt报错问题

- **1.0.7** 2015.03.24
    * core: 优化TaskQueue，去掉对support-v4的依赖
    * core: 微调LogUtils，其它细节调整
    * ui: 微调EndlessListView，新增ListViewExtend
    
- **1.0.6** 2015.03.20
    * 细节调整
    
- **1.0.5** 2014.12.22
    * 补充完整的文档
    
- **1.0.4** 2014.09.15
    * 发布到github

------

## 开始使用

Gradle集成方法：

```groovy

    // core 核心库, 格式:jar和aar
    compile 'com.mcxiaoke.next:core:1.0.+'
    
    // http HTTP组件, 格式:jar和aar
    compile 'com.mcxiaoke.next:http:1.0.+'
    
    // ui UI组件, 格式:aar
    compile 'com.mcxiaoke.next:ui:1.0.+'

    // 1.0.9版新增
    // recycler EndlessRecyclerView, 格式:aar
    compile 'com.mcxiaoke.next:recycler:1.0.+'
    
    // extra-abc 依赖support-v7 AppCompat 格式:aar
    compile 'com.mcxiaoke.next:extras-abc:1.0.+'
    
    
```

------

## 使用说明

#### TaskQueue/TaskBuilder
[异步任务](task.md)
#### AdvancedShareActionProvider
[高级分享](share.md)
#### NextClient/NextRequest
[HTTP组件](http.md)
#### MemoryCache/DisCache
[缓存组件](cache.md)
#### Utils/Miscs
[常用工具类](core.md)
#### EndlessRecyclerView
[无限加载](recydler.md)
#### Views/Widgets
[常用UI控件](ui.md)

------


##其它问题

发现任何问题可以提issue


------


##License


    Copyright 2013 - 2015 Xiaoke Zhang

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.





