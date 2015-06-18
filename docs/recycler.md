## 集成方法

```groovy
    // v1.0.9 新增
    // recycler EndlessRecyclerView, 格式:aar
    compile 'com.mcxiaoke.next:recycler:1.+'
```

## EndlessRecyclerView

- **EndlessRecyclerView** 支持滚动到列表底部自动加载更多的RecyclerView
- **RecyclerArrayAdapter** 适用于RecyclerView的ArrayAdapter，接口同ArrayAdapter
- **HeaderFooterRecyclerAdapter** 支持添加Header和Footer的RecyclerView.Adapter
- **HeaderFooterRecyclerArrayAdapter** 支持添加Header和Footer的ArrayAdapter

```java

        mRecyclerAdapter = new StringRecyclerAdapter(this, new ArrayList<String>());
        mRecyclerAdapter.addAll(buildData());
        // 设置RecyclerView.Adapter
        mEndlessRecyclerView.setAdapter(mRecyclerAdapter);
        // 设置自动加载更多的回调
        mEndlessRecyclerView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(final EndlessRecyclerView view) {
                doLoadMore();
            }
        });
        // 是否启用滚动到底部自动加载更多功能
        mEndlessRecyclerView.enable(true);
        //底部Footer显示正在加载中
        mEndlessRecyclerView.showProgress();
        // 底部Footer显示为空
        mEndlessRecyclerView.showEmpty();
        // 底部Footer显示文本
        mEndlessRecyclerView.showText("no more data");
        // 里列表底部多少个就开始自动加载更多
        mEndlessRecyclerView.setLoadMoreThreshold(3);
        // 加载更多数据完成时调用此方法
        mEndlessRecyclerView.onComplete();

```
