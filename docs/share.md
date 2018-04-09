## 集成方法

```groovy
    // ui UI组件, 格式:aar
    compile 'com.mcxiaoke.next:ui:1.5.1'
```

## AdvancedShareActionProvider

```java

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem share = menu.findItem(R.id.menu_share);
        final AdvancedShareActionProvider provider = (AdvancedShareActionProvider) share.getActionProvider();
        final MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Log.v(TAG, "Share Target, onMenuItemClicked");
                return true;
            }
        };
        ShareTarget target = new ShareTarget("ShareTarget",
                getResources().getDrawable(android.R.drawable.ic_menu_share), listener);
        provider.addShareTarget(target);
        final String pkg = getPackageName();
        provider.addCustomPackage("com.twitter.android");
        provider.addCustomPackage(pkg);
        provider.addCustomPackage("com.twitter.android");
        provider.removePackage("com.google.android.apps.plus");
        provider.setDefaultLength(3);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "I am some text for sharing!");
        provider.setShareIntent(intent);
        return true;
    }


```
