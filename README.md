Android-Next 公共组件库
----------
### Task, Cache, Views, Widgets, Http, Utils

### 项目介绍

这个库是我在日常开发过程中积累下来的一些可复用组件，大部分都在我的工作项目和个人项目中有使用。

### 最新版本

[![Maven Central](http://img.shields.io/badge/2015.07.07-com.mcxiaoke.next:1.1.8-brightgreen.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.mcxiaoke.next%22)
- **1.1.8** 2015.07.07
    * http: 紧急修复部分情况下URL参数没有添加的问题，添加对应的测试
- **1.1.7** 2015.07.07
    * http: 修复一些细节问题，添加几个类的单元测试
- **1.1.6** 2015.07.03
    * http: 完善NextClient和NextRequest，增加实用方法，减少重复代码
    
- **1.1.5** 2015.07.02
    * http: 模块内部使用OkHttp处理Http请求，简化并重构NextRequest和NextClient接口
    
- **1.1.4** 2015.07.01
    * http: 修复Client默认参数没有添加到HTTP请求中的问题
    
- **1.1.3** 2015.07.01
    * recycler: 修复当数据较少不足一屏时的事件处理
    
- **1.1.2** 2015.06.24
    * recycler: 添加设置Footer字体大小和颜色方法
    * 后续版本只有aar发布到Maven Central，不再发布jar文件
    
- **1.1.1** 2015.06.19
    * task: 重构，减少不必要的接口，增加TaskFuture，清理代码
    * docs: 修改和完善task模块的文档和示例
    
- **1.1.0** 2015.06.18
    * task: 完全重构TaskQueue组件，单独部署
    * core: 移除未使用的类，移除task相关的类
    * samples: 添加新的Task使用示例
    * docs: 移动所有文档到docs子目录
    
- **1.0.9** 2015.06.16
    * core: 优化TaskQueue，调整接口，添加新的辅助类Task，支持链式调用
    * core: 重构MemoryCache，精简结构，缓存对象支持设置过期时间
    * core: 添加一些工具类，如PackageUtils和TrafficUtils
    * recycler: 增加EndlessRecyclerView，支持底部自动加载更多
    * 细节调整，更新示例和说明文档
    
- **1.0.8** 2015.05.18
    * ui: 移除所有的ic_launcher.png，修复appt报错问题
- **1.0.7** 2015.03.24
- 
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

### Gradle集成

```groovy
    // core 核心库, 格式:jar和aar
    compile 'com.mcxiaoke.next:core:1.1.+'
    // v1.1.0 新增
    // task 异步任务库，格式:jar和aar
    compile 'com.mcxiaoke.next:task:1.1.+'
    // http HTTP组件, 格式:jar和aar
    compile 'com.mcxiaoke.next:http:1.1.+'
    // ui UI组件, 格式:aar
    compile 'com.mcxiaoke.next:ui:1.1.+'
    // v1.0.9 新增
    // recycler EndlessRecyclerView, 格式:aar
    compile 'com.mcxiaoke.next:recycler:1.1.+'
    // extra-abc 依赖support-v7 AppCompat 格式:aar
    compile 'com.mcxiaoke.next:extras-abc:1.1.+'
    
```
------

### 使用说明

#### 提示

**使用前请阅读对应模块的文档和示例，如果有不清楚的地方，可以看源码，或者向我提问。**

* [常用工具类 Utils/Misc](docs/core.md)
* [异步任务 TaskQueue/TaskBuilder](docs/task.md)
* [高级分享 AdvancedShareActionProvider](docs/share.md)
* [HTTP组件 NextClient/NextRequest](docs/http.md)
* [缓存组件 MemoryCache/DisCache](docs/cache.md)
* [无限加载 EndlessRecyclerView](docs/recycler.md)
* [常用UI控件 Views/Widgets](docs/ui.md)

------

### 其它问题

发现任何问题可以提issue

------

### License

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





