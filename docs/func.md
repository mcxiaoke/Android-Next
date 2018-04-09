# 函数操作符模块

## 集成方法

```groovy
compile 'com.mcxiaoke.next:functions:1.5.1'
```

## 使用介绍

函数模块对外只有一个接口类： `com.mcxiaoke.next.func.Fn`，主要包含下面常见的函数式数据操作符：

### 合并多个数据集 (concat/merge)

```java
public static <T> List<T> concat(List<List<T>> lists)
public static <T> List<T> concat(List<T>... lists)
public static <T> Collection<T> concat(Collection<T>... collections)
```

### 取最大最小值 (max/min)

```java
public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> collection)
public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> collection)
public static <T> T max(Comparator<? super T> cmp, Collection<? extends T> collection)
public static <T> T min(Comparator<? super T> cmp, Collection<? extends T> collection)
```

### 数据项去重 (distinct/unique)

```java
public static <T> Collection<T> distinct(Collection<? extends T> collection)
public static <T> List<T> distinct(List<? extends T> list)
```

### 过滤掉不符合条件的 (filter)

```java
public static <T> Collection<T> filter(Predicate<? super T> p,
                                           Iterable<? extends T> iterable)
public static <T> List<T> filter(Predicate<? super T> p,
                                     List<? extends T> list)
```

### 是否所有项都满足条件 (all)

```java
public static <T> boolean all(Predicate<? super T> p, Iterable<T> iterable)
```

### 是否存在一项满足条件 (any)

```java
public static <T> boolean any(Predicate<? super T> p, Iterable<T> iterable)
```


### 遍历数据集 (foreach)

```java
public static <T, R> void forEach(Action1<? super T> func, Iterable<T> iterable)
```

###  压缩数据集 (zip)

```java
public static <T> List<List<T>> zip(List<T>... lists)
```

### 折叠操作 (reduce/fold)

```java
public static <T, R> R reduce(final Func2<R, ? super T, R> func2,
                                  final Iterable<? extends T> iterable,
                                  final R initializer)
public static <T, R> R reduce(final Func2<R, ? super T, R> func2,
                                  final Iterable<? extends T> iterable)
```

### 普通映射操作 (map)

```java
public static <T, R> void map(Func1<? super T, ? extends R> func, Collection<T> from, Collection<R> to)
public static <T, R> Iterable<R> map(Func1<? super T, ? extends R> func, Iterable<T> iterable)
public static <T, R> Collection<R> map(Func1<? super T, ? extends R> func, Collection<T> collection)
public static <T, R> List<R> map(Func1<? super T, ? extends R> func, List<T> list)
public static <T, R> Set<R> map(Func1<? super T, ? extends R> func, Set<T> set)

```

### 平坦映射操作 (flatMap/flatten)

```java
public static <T, R> List<R> flatMap(Func1<? super T, ? extends R> func, List<List<T>> lists)
public static <T, R> List<R> flatMap(Func1<? super T, ? extends R> func, List<T>... lists)
public static <T, R> Collection<R> flatMap(Func1<? super T, ? extends R> func, Collection<T>... collections)
```

## 接口定义

说明：定义这些接口是因为Java中函数不是一等公民，不能直接用函数作为参数，所以只能定义一些包装接口来传递函数。

```java
// 零个参数，一个返回值的Function
public interface Func0<R> extends Function {
    R call();
}

// 一个参数，一个返回值的Function
public interface Func1<T, R> extends Function {
    R call(T t);
}

// 两个参数，一个返回值的Function
public interface Func2<T1, T2, R> extends Function {
    R call(T1 t1, T2 t2);
}

// Action就是没有返回值的Function

// 零个参数的Action
public interface Action0 extends Action {
    void call();
}

// 一个参数的Action
public interface Action1<T> extends Action {
    void call(T t);
}

// 两个参数的Action
public interface Action2<T1, T2> extends Action {
    void call(T1 t1, T2 t2);
}

// 条件判断，满足返回True，用于 filter/all/any等操作
public interface Predicate<T> {

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    boolean accept(T t);
}
```
