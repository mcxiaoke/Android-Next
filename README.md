Android公共组件库
===========================================
####Android Common Components: Views, Widgets, Utils

已经部署到Maven Central，Maven和Gradle可以直接使用


###项目结构

分为两个模块：
* library 公用模块
* samples 使用示例


###版本更新

* 0.9.0 20131025
  1. 创建公共UI组件库项目
  2. 添加AdvancedShareActionProvider
  3. 添加AlertDialogFragment
  4. 添加EndlessListView
  5. 完善Maven和Gradle构建
  6. 添加UI组件使用示例


* 0.9.5 20131028
  1. 修改AdvancedShareActionProvider接口
  2. 添加ProgressDialogFragment
  3. 添加AspectRatioImageView
  4. 添加SquaredImageView
  5. 添加NoPressStateLinearLayout
  6. 添加并修改TwoDirectionListView
  7. 添加CheckableLinearLayout系列组件
  
* 0.9.9 20131224
  1. 给ShareActionProvider添加高级自定义选项
  2. 更新ShareActionProvider的例子
  
* 1.0.0 20140116
  1. 添加各种Utils，位于com.mcxiaoke.commons.utils包
  2. 添加异步任务管理类TaskExecutor
  3. 整理代码，更改包名为com.mcxiaoke.commons
  

###Maven和Gradle使用

  注意：使用Maven的项目可以直接复制使用，Gradle使用方法：

```
compile('com.mcxiaoke.commons:library:1.0.+')
```


###已有组件

* **AlertDialogFragment** 一个自定义的DialogFragment，接口和功能基本等同于系统的AlertDialog

* **AdvancedShareActionProvider** 高级版的ShareActionProvider，支持自定义分享目标

* **EndlessListView** 对ListView做了一层轻封装，支持底部点击加载更多和滚动到底部自动加载等模式，支持ListFooter的自定义文本显示，使用方法和正常的ListView基本一样

* **AspectRatioImageView** 显示时保持图片长宽比的ImageView

* **SquaredImageView** 显示时保持长宽一致的正方形ImageView

* **NoPressStateLinearLayout** 阻止PRESS和FOCUS状态传递到子View的LinearLayout

* **TwoDirectionListView** 支持横向和竖向两种方向的ListView

* **CheckableLinearLayout** 一系列实现了Checkable接口的Layout

* **TaskExecutor** 异步任务执行管理，支持Caller检测

* **ArrayAdapterCompat** 扩展ArrayAdapter，添加了一些数据删除/添加方法

* **AlertDialogFragment** Fragment版本的AlertDialog，接口基本一致

* **ProgressDialogFragment** Fragment版本的ProgressDialog，接口基本一致

* **Utils** 各种工具类，StringUtils/MimeUtils/BitmapUtils/CryptoUtils等


###其它说明

发现任何问题可以提issue



