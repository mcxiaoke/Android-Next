## Core - Next核心库使用指南

### Gradle集成

```groovy
    // core 核心库, 格式:jar和aar
    compile 'com.mcxiaoke.next:core:1.3.+'
```

包含基础Activity和Service，还有一些工具类，按Java包介绍如下：

### 多种工具类

#### AndroidUtils

跟Android系统相关的一些工具类，包括文件路径处理，Toast显示，屏幕方向，组件启用禁用，获取App签名信息等，主要方法如下：

```java
// 是否是合法的文件名
public static boolean isFilenameSafe(File file)
// 获取Cache目录
public static File getCacheDir(Context context)
// 根据Uri获取真实文件路径
public static String getPath(final Context context, final Uri uri)
// SD卡存储空间判断
public static boolean noSdcard()
public static long getFreeSpace()
public static boolean isMediaMounted()
// 键盘隐藏与显示
public static void hideSoftKeyboard(Context context, EditText editText)
public static void showSoftKeyboard(Context context, EditText editText)
public static void toggleSoftInput(Context context, View view)
// 显示Toast
public static void showToast(Context context, int resId)
public static void showToast(Context context, CharSequence text)
// 检测相机
public static boolean hasCamera(Context context)
// 媒体扫描
public static void mediaScan(Context context, Uri uri)
public static void addToMediaStore(Context context, File file)
// 横竖屏设置
public static void setFullScreen(final Activity activity,final boolean fullscreen)
public static void setPortraitOrientation(final Activity activity,final boolean portrait)
public static void lockScreenOrientation(final Activity context, final boolean portrait)
// 系统版本判断
public static boolean hasKitkat()
public static boolean hasLollipop()
// 获取系统服务
public <T> T getSystemService(final Context context, final String name)
// 重启Activity
public static void restartActivity(final Activity activity)
// 获取电池信息
public static float getBatteryLevel(Intent batteryIntent)
public static String getBatteryInfo(Intent batteryIntent)
// 获取应用签名
public static String getSignature(Context context)
public static String getSignatureInfo(Context context)
```

#### IOUtils

IO操作工具类，包含常用的文件复制/字符串/数组/列表/数据流读写方法，每个方法都包含不同参数的多个重载的版本，主要方法如下：

```java
// 文件和数据流复制
public static void copy(File sourceFile, File destFile) throws IOException
public static long copyLarge(InputStream input, OutputStream output)
public static void copy(InputStream input, Writer output)
public static int copy(Reader input, Writer output) throws IOException

// 数据流读取
public static int read(Reader input, char[] buffer, int offset, int length) throws IOException
public static int read(InputStream input, byte[] buffer) throws IOException
public static void readFully(Reader input, char[] buffer) throws IOException

// 读取应用资源
public static String readStringFromAssets(Context context, String fileName)
public static String readStringFromRaw(Context context, int resId)

// 关闭流
public static void closeQuietly(Closeable closeable) 

// 读取字节
public static byte[] readBytes(File file) throws IOException
public static byte[] readBytes(InputStream input) throws IOException
public static byte[] readBytes(Reader input) throws IOException

// 读取字符，字符串
public static char[] readChars(InputStream is) throws IOException
public static char[] readChars(Reader input) throws IOException
public static String readString(InputStream input) throws IOException
public static String readString(Reader input) throws IOException

// 写入字节，字符，字符串
public static void writeBytes(byte[] data, OutputStream output)
public static void writeChars(char[] data, OutputStream output)
public static void writeString(String data, Writer output) throws IOException

// 读取列表
public static List<String> readStringList(Reader input) throws IOException
public static List<String> readStringList(InputStream input) throws IOException
public static List<String> readStringList(File file, String encoding) throws IOException

// 写入列表
public static void writeList(Collection<?> lines,
                                 String filePath) throws IOException
public static void writeList(Collection<?> lines,
                                 File file) throws IOException
public static void writeList(Collection<?> lines,
                                 Writer writer) throws IOException

// 文件和目录操作
public static String getFileNameWithoutExtension(String filePath)
public static String getFileExtension(String filePath)
public static String getFileName(String filePath)
public static boolean makeDirs(String filePath) 
public static long sizeOf(File file)

// 人类可读的文件大小
public static String byteCountToDisplaySize(long size)
```


#### StringUtils

字符串工具类，支持常用的字符串合并/分割/比较/转换/判断等操作，这里面的方法同样包含很多参数不同的重载版本，使用时根据IDE提示选取即可，主要方法如下：

```java
// 空白字符判断
public static boolean hasLength(CharSequence str)
public static boolean isEmpty(CharSequence text)
public static boolean isBlank(final CharSequence cs)
public static boolean containsWhitespace(CharSequence str) 
public static String deleteWhitespace(final String str)
public static String trimWhitespace(String str)
public static String reduceLineBreaks(String text)

// 字符串替换
public static boolean substringMatch(CharSequence str, int index,
                                         CharSequence substring)
public static String replace(String inString, String oldPattern,
                                 String newPattern)
// 字符串数组
public static String[] addStringToArray(String[] array, String str)
public static String[] concatenateStringArrays(String[] array1,
                                                   String[] array2)
// Intent/SharedPreferences/Bundle字符串展示
public static String toString(Intent intent)
public static String toString(SharedPreferences sp)
public static String toString(final Bundle bundle)

// 集合的字符串展示
public static <K, V> String toString(Map<K, V> map)
public static String toString(Collection<?> coll,
                                  String delim, String prefix, String suffix)
public static String[] toStringArray(Collection<String> collection)
public static String[] toStringArray(Enumeration<String> enumeration) 
public static String[] split(String toSplit, String delimiter)
public static Set<String> commaDelimitedListToSet(String str)

// 其它字符串方法
public static boolean nullSafeEquals(String text1, String text2)
public static String getHumanReadableByteCount(long bytes)
public static String toSafeFileName(String name)
public static String getUrlWithoutQuery(String url)
public static Map<String, String> parseQueryString(String queryString)                              
```

#### 其它的工具类
* NetworkUtils - 网络工具类，支持获取网络类型，设置代理等
* PackageUtils  - Package相关的工具类，App是否安装，是否运行，启用和禁用组件等
* AssertUtils - Assert类，Null检查，对象检查，数组检查等
* BitmapUtils - Bitmap缩放，旋转，圆角，阴影，裁剪等方法
* CryptoUtils - 加密算法相关的工具方法，支持MD5/SHA1/SHA256/AES/HEX等
* MimeUtils - MIME工具类，支持根据文件扩展名获取MIME类型
* ReflectionUtils - Java反射相关的工具类
* ViewUtils - View相关的几个工具方法，例如getScreenRawSize/getActionBarHeightInDp/getResourceValue等
* ZipUtils - 支持ZIP文件压缩/解压缩
* TrafficUtils - App流量使用统计工具类

### 列表和集合

包含几个常用的集合类：

* NoDuplicatesArrayList - 不包含重复元素的ArrayList
* NoDuplicatesCopyOnWriteArrayList - 同样不包含重复元素
* NoDuplicatesLinkedList - 不包含重复元素的LinkedList
* WeakFastHashMap - 一个高性能的WeakHashMap实现

### Acitivty和Service

#### MultiIntentService

用法类似于IntentService，但是支持多个异步任务并发执行，可以在所有任务执行完成后空闲一段时间后自动调用stopSelf()，主要包括以下增强的方法：

```java
protected void setAutoCloseEnable(boolean enable);
protected void setAutoCloseTime(long milliseconds);
protected boolean isIdle();
protected final void cancel(final String tag) ;
protected void retain(final String tag, final Future<?> future);
protected void release(final String tag);
protected ExecutorService createExecutor();

// 要执行的异步任务放在这里，创建子类重写此方法：
protected abstract void onHandleIntent(final Intent intent, final String tag);
```

#### NextBaseActivity

基础Activity，添加了一些ActionBar相关的封装方法，主要包括如下方法：

```java
    protected boolean isPaused();
    protected NextBaseActivity getActivity();
    public void showProgressIndicator();
    public void hideProgressIndicator();
    public void setActionBarTitle(CharSequence text);
    public void setActionBarTitle(int resId);
    public void setActionBarSubtitle(CharSequence text);
    public void setActionBarSubtitle(int resId);
```

#### NextBaseFragment
 
基础Fragment，添加了ActionBar和Activity相关的一些封装方法：

```java
    public final NextBaseActivity getBaseActivity();
    public final void showProgressIndicator();
    public final void hideProgressIndicator();
    public final void finishActivity();
    public final void setResult(int resultCode);
    public final void setResult(int resultCode, Intent data);
    public final void invalidateOptionsMenu();
    public final ActionBar getActionBar();
    public final void setActionBarTitle(CharSequence text);
    public final void setActionBarTitle(int resId);
    public final void setActionBarSubTitle(CharSequence text);
    public final void setActionBarSubTitle(int resId);
    protected NextBaseFragment getFragment();
```


### 其它组件

- **com.mcxiaoke.next.common** 一个简单数据容器，NextMessage，类似于Android系统的Message类，但是使用更方便，能支持更多数据类型
- **com.mcxiaoke.next.db** 包含两个简单的数据库相关的工具类
- **com.mcxiaoke.next.geo**  包含LastLocationFinder，用于快速获取上次定位位置
- **com.mcxiaoke.next.io** 包含CountingInputStream, CountingOutputStream, StringBuilderWriter, BoundedInputStream等IO数据流相关的封装类，方便使用
