
## 缓存


```java

        // create memory cache, internally using map
        final IMemoryCache<String,String> memoryCache= MemoryCache.mapCache();
        // create memory cache, internally using lru cache
        // final IMemoryCache<String,String> memoryCache= MemoryCache.lruCache(100);
        memoryCache.put("strKey", "value");
        memoryCache.put("intKey", 123);
        memoryCache.put("boolKey", false);
        memoryCache.put("objKey", new TaskQueue());
        memoryCache.put("boolKey", false);
        // expires in 300 seconds
        memoryCache.put("canExpireKey", "hello, world", 300*1000L);
        final String value = memoryCache.get("strKey");

        final Context context = mockContext();
        // default disc cache, use /data/data/package-name/cache/.disc/ dir
        final DiscCache discCache = new DiscCache(context);
        // use custom /data/data/package-name/cache/json-cache/ dir
        //final DiscCache discCache=new DiscCache(context,"json-cache");
        // use custom /sdcard/Android/data/package-name/cache/json-cache/ dir
        //final DiscCache discCache=new DiscCache(context,"json-cache",DiscCache.MODE_EXTERNAL);
        discCache.setCacheDir("dirName");
        discCache.setCharset("UTF-8");
        discCache.setFileNameGenerator(nameGenerator);
        final byte[] bytes = new byte[100];
        discCache.put("bytes", bytes);
        discCache.put("stream", new ByteArrayInputStream(bytes));
        discCache.put("text", "some text for cache");
        final byte[] bytesValue = discCache.getBytes("bytes");
        final File file = discCache.getFile("stream");
        final String stringValue = discCache.get("text");
        discCache.remove("cacheKey");
        discCache.clear();
        discCache.delete(new FileFilter() {
            @Override
            public boolean accept(final File pathname) {
                return false;
            }
        });


```