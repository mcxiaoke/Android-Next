Android公共UI组件库
===========================================
####Android Common UI Components: Views, Widgets, Utils

###版本更新

* **1.0.0** 20131025

  1. 创建公共UI组件库项目
  2. 添加AdvancedShareActionProvider
  3. 添加AlertDialogFragment
  4. 添加EndlessListView
  5. 完善Maven和Gradle构建
  6. 添加UI组件使用示例


###Maven和Gradle使用

  注意：maven的artifactId为library-apklib，gradle为library

* **Maven使用方法**

  ```
<dependency>
  <groupId>com.douban.ui</groupId>
  <artifactId>library-apklib</artifactId>
  <version>1.0.0</version>
  <type>apklib</type>
</dependency>
```
* **Gradle使用方法**

  ```
dependencies {
    compile('com.douban.ui:library:1.0.+')
}
```


###已有组件
* **AlertDialogFragment** 一个自定义的DialogFragment，接口和功能基本等同于系统的AlertDialog [20131025]

* **AdvancedShareActionProvider** 高级版的ShareActionProvider，支持自定义优先显示的分享目标 [20131025]

* **EndlessListView** 对ListView做了一层轻封装，支持底部点击加载更多和滚动到底部自动加载等模式，支持ListFooter的自定义文本显示，使用方法和正常的ListView基本一样 [20131025]



###其它说明

发现任何问题可以提issue，大家可以把各自发现的/写的对项目开发有帮助的一些UI小模块共享出来



