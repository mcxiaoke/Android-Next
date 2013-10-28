Android公共UI组件库
===========================================
####Android Common UI Components: Views, Widgets, Utils

###版本更新

* **0.9.0** 20131025

  1. 创建公共UI组件库项目
  2. 添加AdvancedShareActionProvider
  3. 添加AlertDialogFragment
  4. 添加EndlessListView
  5. 完善Maven和Gradle构建
  6. 添加UI组件使用示例

* **1.0.0** 20131028
  1. 修改添加AdvancedShareActionProvider接口
  2. 添加并完善ProgressDialogFragment
  3. 添加AspectRatioImageView
  4. 添加SquaredImageView
  5. 添加NoPressStateLinearLayout
  6. 添加并修改TwoDirectionListView
  7. 添加CheckableLinearLayout系列组件


###Maven和Gradle使用

  注意：同时维护Maven和Gradle两套构建系统比较麻烦，目前不直接支持Maven，这个项目里大部分都是独立的模块（类），使用Maven的项目可以直接复制使用
```
* **Gradle使用方法**

  ```
dependencies {
    compile('com.douban.ui:library:1.0.+')
}
```


###已有组件


* **AlertDialogFragment** 一个自定义的DialogFragment，接口和功能基本等同于系统的AlertDialog [20131025] (使用案例：广播项目中目前所有的确认对话框都是使用此控件)

* **AdvancedShareActionProvider** 高级版的ShareActionProvider，支持自定义优先显示的分享目标 [20131025] (使用案例：广播项目中所有的ActionBar分享都是使用此控件)

* **EndlessListView** 对ListView做了一层轻封装，支持底部点击加载更多和滚动到底部自动加载等模式，支持ListFooter的自定义文本显示，使用方法和正常的ListView基本一样 [20131025] (使用案例：广播项目中所有的时间线和回应列表分享都是使用此控件)

* **ProgressDialogFragment** ProgressDialog的Fragment版，接口基本一致 [20131028] (使用案例：广播项目中所有的Progress显示都是使用此控件)

* **AspectRatioImageView** 显示时保持图片长宽比的ImageView [20131028] (使用案例：广播项目中时间线大图显示使用了此控件)

* **SquaredImageView** 显示时保持长宽一致的正方形ImageView [20131028] (使用案例：广播项目中时间线横向滚动缩略图列表，相册缩略图网格，本地图片选择使用了此控件)

* **NoPressStateLinearLayout** 阻止PRESS和FOCUS状态传递到子View的LinearLayout [20131028] (使用案例：广播项目中时间线广播Item外层布局使用了此控件，用于修复小米1S点击Item会触发所有子View PRESS状态的BUG)

* **TwoDirectionListView** 支持横向和竖向两种方向的ListView [20131028] (使用案例：广播项目中时间线横向滚动缩略图列表，个人主页最近照片列表使用了此控件)

* **CheckableLinearLayout** 一系列实现了Checkable接口的Layout [20131028] (使用案例：广播项目中照片上传本地图片多选界面使用了此控件)


###其它说明

发现任何问题可以提issue，大家可以把各自发现的/写的对项目开发有帮助的一些UI小模块共享出来



