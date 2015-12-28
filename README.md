# Android-Next 公共组件库

这个库是我在日常开发过程中积累下来的一些可复用组件，大部分都在我的工作项目和个人项目中有使用。

最新版本: [![Maven Central](http://img.shields.io/badge/2015.12.28-com.mcxiaoke.next:core:1.2.1-brightgreen.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.mcxiaoke.next%22)

## Gradle集成

```groovy
    // core 核心库, 格式:jar和aar
    compile 'com.mcxiaoke.next:core:1.2.+'
    // task 异步任务库，格式:jar和aar
    compile 'com.mcxiaoke.next:task:1.2.+'
    // http HTTP组件, 格式:jar和aar
    compile 'com.mcxiaoke.next:http:1.2.+'
    // 异步网络和文件IO组件，替代Volley
    compile 'com.mcxiaoke.next:ioasync:1.2.+'
    // 函数操作组件
    compile 'com.mcxiaoke.next:functions:1.2.+'
    // ui UI组件, 格式:aar
    compile 'com.mcxiaoke.next:ui:1.2.+'
    // recycler EndlessRecyclerView, 格式:aar
    compile 'com.mcxiaoke.next:recycler:1.2.+'
    // extra-abc 依赖support-v7 AppCompat 格式:aar
    compile 'com.mcxiaoke.next:extras-abc:1.2.+'
    
```

## 使用指南（2015.08.24更新）

**使用前请阅读对应模块的文档和示例，如果有不清楚的地方，可以看源码，或者向我提问。**

### Core 

[`基类和工具类`](docs/core.md) 

MultiIntentService, NextMessage, Charsets, StringUtils, AndroidUtils, IOUtils, LogUtils。包含基础Activity和Service，还有一些工具类，功能包括：文件路径处理，Toast显示，屏幕方向，组件启用禁用，获取App签名信息；常用的文件复制/字符串/数组/列表/数据流读写，常用的字符串合并/分割/比较/转换/判断等操作；网络类型和状态获取，代理设置；Package相关的工具类，App是否安装，是否运行，启用和禁用组件等；Bitmap缩放，旋转，圆角，阴影，裁剪等；加密算法相关的工具方法，支持MD5/SHA1/SHA256/AES/HEX等。

### HttpRequest 

[`网络请求管理`](docs/http.md)

NextClient, NextRequest, NextResponse, ProgressListener, RequestInterceptor。包含一个经过简单封装的HTTP操作模块，用于简化常用的网络请求操作：

- **NextClient** 网络组件的核心类，封装全局的配置参数
- **NextParams** HTTP参数封装和处理
- **NextRequest** HTTP 请求封装
- **NextResponse** HTTP 响应数据结构

### TaskQueue 

[`异步任务队列`](docs/task.md)

TaskQueue, Async, TaskBuilder, TaskFuture, TaskCallback

包含异步任务执行模块相关的类，详细的使用见后面的说明

* TaskQueue 对外接口，支持单例使用
* TaskFuture 表示单个异步任务对象
* TaskBuilder 对外接口，链式调用
* TaskCallback 任务回调接口

### IOAsync 

[`异步IO组件`](docs/ioasync.md)

IOAsync, AsyncCallback, ResponseCallback, StringCallback, GsonCallback, FileCallback

主要是结合 `http`模块和`task`模块，提供方便的异步网络操作，本模块主要的方法都是异步执行，通过回调接口反馈结果，内部使用 `TaskQeue` 执行异步任务管理，使用 `NextClient` 发送和解析HTTP网络请求，通过回调接口返回数据，网络请求在异步线程执行，回调方法在主线程调用，可用于替代Google的`Volley`库，能极大的减轻应用开发中异步请求数据然后主线程更新UI这一过程的工作量。

### Function 

[`函数式操作符`](docs/func.md) 

函数模块对外只有一个接口类： `com.mcxiaoke.next.func.Fn`，主要包含常见的函数式数据操作符：`map/flatMap/reduce/concat/filter/all/any` 等

### Cache 

[`磁盘和内存缓存`](docs/cache.md) 

包含磁盘缓存 `DiscCache` 和内存缓存 `MemoryCache`，内部封装了HashMap和LruCache两种类型的缓存，可根据需要选用。

### RecyclerView 

[`无限加载列表`](docs/recycler.md) 

封装 `RecyclerView` ，用于支持滚动到底部时自动加载数据和显示正在加载，主要有这几个类：

- **EndlessRecyclerView** 支持滚动到列表底部自动加载更多的RecyclerView
- **RecyclerArrayAdapter** 适用于RecyclerView的ArrayAdapter，接口同ArrayAdapter
- **HeaderFooterRecyclerAdapter** 支持添加Header和Footer的RecyclerView.Adapter
- **HeaderFooterRecyclerArrayAdapter** 支持添加Header和Footer的ArrayAdapter

### UI Widgets 

[`常用UI控件`](docs/ui.md)

一些常用的UI控件，可简化日常开发，包括 AlertDialogFragment, ProgressDialogFragment, AspectRatioImageView, ArrayAdapterCompat等。

### ShareProvider 

[`高级分享组件`](docs/share.md) 

封装的一个 `ActionProvider` ，比系统自带的 `SharedActionProvider` 提供大得多的灵活度，可自定义出现在列表里的项目，主要包括 `AdvancedShareActionProvider` 和 `ShareTarget` 两个类。


## 更新记录
- **1.2.1**  2015.12.28
	* http: 调整NextClient的参数处理方式，调整NextRequest的初始化，增加配置接口
	* task: 调整TaskQueue的接口，增加创建队列的工厂方法，支持并发和顺序两种模式
- **1.2.0**  2015.08.24
	 * func: 新增函数式操作符模块，通过`Fn`类支持常用的 `map/reduce/filter/zip/all/any/concat` 函数，更方便的操作数据序列
	 * ioasync: 新增异步IO模块，能有效的简化App中请求数据更新UI这一通用逻辑
	 * 补充文档，给几个模块的文档添加了详细的API说明和示例
- **1.1.13** 2015.08.05
    * task: 增加Async类，添加最简单的异步执行方法 Async.run(task)
- **1.1.12** 2015.08.04
    * http: 微调NextRequest，默认Multipart类型改为"multipart/form-data"
- **1.1.11** 2015.07.13
    * http: 添加OkClientInterceptor，支持对内部的OkClient进行定制
- **1.1.10** 2015.07.09
    * recycler: 重写EndlessRecyclerAdapter，修复与HeaderFooterRecyclerAdapter的兼容问题
- **1.1.9** 2015.07.08
    * extras: 更新extras里的AdvancedShareActionProvider，与ui同步
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
    * core: 优化TaskQueue，去掉对support-v4的依赖
    * core: 微调LogUtils，其它细节调整
    * ui: 微调EndlessListView，新增ListViewExtend
- **1.0.6** 2015.03.20
    * 细节调整
- **1.0.5** 2014.12.22
    * 补充完整的文档
- **1.0.4** 2014.09.15
    * 发布到github


### 其它问题

发现任何问题可以提issue

------

## 关于作者

#### 联系方式

* Blog: <http://blog.mcxiaoke.com>
* Github: <https://github.com/mcxiaoke>
* Email: [github@mcxiaoke.com](mailto:github@mcxiaoke.com)

#### 开源项目

* Next公共组件库: <https://github.com/mcxiaoke/Android-Next>
* PackerNg极速打包: <https://github.com/mcxiaoke/packer-ng-plugin>
* Gradle渠道打包: <https://github.com/mcxiaoke/gradle-packer-plugin>
* EventBus实现xBus: <https://github.com/mcxiaoke/xBus>
* Rx文档中文翻译: <https://github.com/mcxiaoke/RxDocs>
* MQTT协议中文版: <https://github.com/mcxiaoke/mqtt>
* 蘑菇饭App: <https://github.com/mcxiaoke/minicat>
* 饭否客户端: <https://github.com/mcxiaoke/fanfouapp-opensource>
* Volley镜像: <https://github.com/mcxiaoke/android-volley>

------

## License

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





