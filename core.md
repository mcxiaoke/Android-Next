
## core 核心组件

包含异步任务组件，缓存组件，基础Activity和Service，还有一些工具类，按Java包介绍如下：

- **com.mcxiaoke.next.app** 基础类，包含:
    * NextBaseActivity 基础Activity，添加了一些ActionBar相关的封装方法
    * NextBaseFragment 基础Fragment，添加了ActionBar和Activity相关的一些封装方法
    * MultiIntentService 类似于IntentService，但是可以多个异步任务并发执行，可以在所有任务执行完成后自动stopSelf()，具体请看源码


- **com.mcxiaoke.next.cache** 简单缓存类，包含内存缓存MemoryCache和磁盘缓存DiscCache，使用非常简单，可定制

- **com.mcxiaoke.next.collection** 几个常用的集合类，包含：NoDuplicatesArrayList, NoDuplicatesCopyOnWriteArrayList, NoDuplicatesLinkedList和WeakFastHashMap

- **com.mcxiaoke.next.common** 包含NextMessage，类似于Android系统的Message类，但是使用更方便，能支持更多数据类型

- **com.mcxiaoke.next.db** 包含两个简单的数据库相关的工具类

- **com.mcxiaoke.next.geo**  包含LastLocationFinder，用于快速获取上次定位位置

- **com.mcxiaoke.next.io** 包含CountingInputStream, CountingOutputStream,
StringBuilderWriter, BoundedInputStream等IO数据流相关的封装类，方便使用

- **com.mcxiaoke.next.utils** 包含了很多使用简单但又非常有用的小工具类:
    * AndroidUtils Android系统相关的一些工具类，包括文件路径处理，Toast显示，屏幕方向，组件启用禁用，获取App签名信息等
    * AssertUtils Assert类，Null检查，对象检查，数组检查等
    * BitmapUtils Bitmap缩放，旋转，圆角，阴影，裁剪等方法
    * CryptoUtils 加密算法相关的工具方法，支持MD5/SHA1/SHA256/AES/HEX等
    * IOUtils IO操作工具类，包含常用的文件复制/字符串/数组/列表/数据流读写方法
    * MimeUtils MIME工具类，支持根据文件扩展名获取MIME类型
    * NetworkUtils 网络工具类，支持获取网络类型，设置代理等
    * ReflectionUtils Java反射相关的工具类
    * StringUtils 字符串工具类，支持常用的字符串合并/分割/比较/转换/判断等操作
    * ViewUtils View相关的几个工具方法，例如getScreenRawSize/getActionBarHeightInDp/getStatusBarHeightInDp/getResourceValue等
    * ZipUtils 支持ZIP文件压缩/解压缩
    * PackageUtils Package相关的工具类，App是否安装，是否运行，启用和禁用组件等
    * TrafficUtils App流量使用统计工具类
