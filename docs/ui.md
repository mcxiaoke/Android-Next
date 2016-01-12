## 集成方法

```groovy
    // ui UI组件, 格式:aar
    compile 'com.mcxiaoke.next:ui:1.3.+'
```

## UI组件

一些常用的UI控件，可简化日常开发，包含：

- **AlertDialogFragment和ProgressDialogFragment** 封装好的DialogFragment，接口简单，同时有4.0版本和使用support-v4的版本

- **EndlessListView** 封装的ListView，添加了支持加载更多数据的接口和FooterView展示

- **CheckableFrameLayout** Checkable布局系列，包含几种常用布局的Checkable封装，附带一个很有用的CheckableImageView

- **NoPressStateFrameLayout** NoPress布局系统，包含几种常用布局的NoPressState封装

- **SquaredFrameLayout** Squared布局系列，包含几个常用布局的Squared封装，正方形布局

- **typeface** typeface包里包含一些支持字体设置的常用控件，如Button/TextView/CheckBox等

- **AspectRatioImageView** 定制的ImageView，缩放时会保持长宽比

- **CircularImageView** 圆形ImageView，一般用作头像等显示，不建议使用，github有更好的项目

- **FixedRatioImageView** 强制高宽比的ImageView，可选以水平或垂直方向为基准，另外还有一个强制保持正方形的SquaredImageView

- **AdvancedShareActionProvider** 高级版的ShareActionProvider，支持自定义分享菜单列表项，自定图标和分享内容等，包含4.0的版本和使用AppCompat的版本，具体可以看源码和示例

- **ArrayAdapterCompat** 增强版的ArrayAdapter，支持2.3以上版本，增加很多实用方法
