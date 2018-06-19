# Android-Next 公共组件库

这个库是我在日常开发过程中积累下来的一些可复用组件，大部分都在我的工作项目和个人项目中有使用。

最新版本: [![Maven Central](http://img.shields.io/badge/2018.06.18-com.mcxiaoke.next:core:1.5.3-brightgreen.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.mcxiaoke.next%22)

## Gradle集成

```groovy
    // core 核心库, 格式:jar和aar
    compile 'com.mcxiaoke.next:core:1.5.3'
    // task 异步任务库，格式:jar和aar
    compile 'com.mcxiaoke.next:task:1.5.3'
    // http HTTP组件, 格式:jar和aar
    compile 'com.mcxiaoke.next:http:1.5.3'
    // 函数操作组件
    compile 'com.mcxiaoke.next:functions:1.5.3'
    // ui UI组件, 格式:aar
    compile 'com.mcxiaoke.next:ui:1.5.3'
    // recycler EndlessRecyclerView, 格式:aar
    compile 'com.mcxiaoke.next:recycler:1.5.3'
    // extra-abc 依赖support-v7 AppCompat 格式:aar
    compile 'com.mcxiaoke.next:extras-abc:1.5.3'

```

## 使用指南（2016.04.21更新）

**使用前请阅读对应模块的文档和示例，如果有不清楚的地方，可以看源码，或者向我提问。**

### Core

[`基类和工具类`](docs/core.md)

MultiIntentService, NextMessage, Charsets, StringUtils, AndroidUtils, IOUtils, LogUtils。包含基础Activity和Service，还有一些工具类，功能包括：文件路径处理，Toast显示，屏幕方向，组件启用禁用，获取App签名信息；常用的文件复制/字符串/数组/列表/数据流读写，常用的字符串合并/分割/比较/转换/判断等操作；网络类型和状态获取，代理设置；Package相关的工具类，App是否安装，是否运行，启用和禁用组件等；Bitmap缩放，旋转，圆角，阴影，裁剪等；加密算法相关的工具方法，支持MD5/SHA1/SHA256/AES/HEX等。


### TaskQueue

[`异步任务队列`](docs/task.md)

TaskQueue, Async, TaskBuilder, TaskFuture, TaskCallback

包含异步任务执行模块相关的类，详细的使用见后面的说明

* TaskQueue 对外接口，支持单例使用
* TaskFuture 表示单个异步任务对象
* TaskBuilder 对外接口，链式调用
* TaskCallback 任务回调接口


### HttpQueue

**注意：1.4.0及之后的版本依赖okhttp3**

[`同步和异步HTTP请求`](docs/http.md)

#### 同步接口

NextClient, NextRequest, NextResponse, ProgressListener, RequestInterceptor。包含一个经过简单封装的HTTP操作模块，用于简化常用的网络请求操作

包含一个经过简单封装的HTTP操作模块，简化常用的网络请求操作

 - **NextClient** 网络组件的核心类，封装全局的配置参数
 - **NextParams** HTTP参数封装和处理
 - **NextRequest** HTTP 请求封装
 - **NextResponse** HTTP 响应数据结构
 - **ProgressListener** HTTP请求数据传输进度回调接口

#### 异步接口

主要是结合 `http`模块和`task`模块，提供方便的异步网络操作，本模块主要的方法都是异步执行，通过回调接口反馈结果，内部使用 `TaskQeue` 执行异步任务管理，使用 `NextClient` 发送和解析HTTP网络请求，通过回调接口返回数据，网络请求在异步线程执行，回调方法在主线程调用，可用于替代Google的`Volley`库，能极大的减轻应用开发中异步请求数据然后主线程更新UI这一过程的工作量。

- ***HttpAsync** 异步HTTP操作辅助类，支持直接的异步HEAD/GET/DELETE/POST/PUT请求
- **HttpQueue** 异步HTTP任务队列，支持添加和取消HTTP异步任务，支持多种形式的Callback和Transformer
- **HttpJob** HTTP任务对象，封装了Request/Callback/Transformer等
- **HttpJobBuilder** 生成HttpJob对象的Builder
- **HttpCallback** 异步HTTP请求回调接口，调用者可以获知HTTP请求的结果是成功还是失败，获取数据和异常对象
- **HttpTransformer** 异步HTTP请求数据类型转换接口，支持Response/String/Gson/File等类型，支持自定义数据类型
- **ResponseProcessor** 异步HTTP请求返回数据的处理器，支持多个Processor

### Function

[`函数式操作符`](docs/func.md)

函数模块对外只有一个接口类： `com.mcxiaoke.next.func.Fn`，主要包含常见的函数式数据操作符：`map/flatMap/reduce/concat/filter/all/any` 等

### Cache

[`磁盘和内存缓存`](docs/cache.md)

包含磁盘缓存 `DiscCache` 和内存缓存 `MemoryCache`，内部封装了HashMap和LruCache两种类型的缓存，可根据需要选用。

### RecyclerView

[`无限加载列表`](docs/recycler.md)

封装 `RecyclerView` ，用于支持滚动到底部时自动加载数据和显示正在加载，主要有这几个类：

- **AdvancedRecyclerView** 支持列表顶部和底部加载更多的RecyclerView
- **AdvancedRecyclerArrayAdapter** 适用于RecyclerView的ArrayAdapter，接口同ArrayAdapter
- **HeaderFooterRecyclerAdapter** 支持添加Header和Footer的RecyclerView.Adapter

### UI Widgets

[`常用UI控件`](docs/ui.md)

一些常用的UI控件，可简化日常开发，包括 AlertDialogFragment, ProgressDialogFragment, AspectRatioImageView, ArrayAdapterCompat等。

### ShareProvider

[`高级分享组件`](docs/share.md)

封装的一个 `ActionProvider` ，比系统自带的 `SharedActionProvider` 提供大得多的灵活度，可自定义出现在列表里的项目，主要包括 `AdvancedShareActionProvider` 和 `ShareTarget` 两个类。


## 更新记录
- **1.4.2** 2016.10.25
	* http: 微调发送请求的逻辑，修复上传文件进度回调处理
- **1.4.1** 2016.04.21
    * 去掉http模块对OkHttp2的依赖，更新其它一些依赖的版本
- **1.4.0** 2016.03.30
    * http: **不兼容更新**，更新内部实现使用OkHttp3，不兼容OkHttp2的代码，所以不兼容1.3.0版本
- **1.3.0** 2016.01.12
	* http: 合并原来的ioasync模块，增加异步HTTP队列相关的类：HttpQueue/HttpAsync/HttpJob/HttpProcessor/HttpTransformer/HttpCallback等，调整日志记录，更新文档
	* task: 调整创建TaskQueue的方式，支持设置最大并发线程数，调试信息中增加当前线程的名字
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





