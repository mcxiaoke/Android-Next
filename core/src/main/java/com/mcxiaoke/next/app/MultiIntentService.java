package com.mcxiaoke.next.app;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import com.mcxiaoke.next.utils.LogUtils;
import com.mcxiaoke.next.utils.ThreadUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类似于IntentService，但是多个异步任务可以并行执行
 * Service每隔300秒自动检查，如果活跃任务目为0则自动结束
 * 自动结束时间可设置，是否启用自动结束功能可设置
 * User: mcxiaoke
 * Date: 14-4-22 14-05-22
 * Time: 14:04
 */
public abstract class MultiIntentService extends Service {
    // 默认空闲5分钟后自动stopSelf()
    public static final long AUTO_CLOSE_DEFAULT_TIME = 300 * 1000L;
    private static final String BASE_TAG = MultiIntentService.class.getSimpleName();
    private static final String SEPARATOR = "::";

    private static volatile long sSequence = 0L;
    private final Object mLock = new Object();
    private final Runnable mAutoCloseRunnable = new Runnable() {
        @Override
        public void run() {
            autoClose();
        }
    };
    private ExecutorService mExecutor;
    private Handler mHandler;

    private volatile Map<String, Future<?>> mFutures;
    private volatile AtomicInteger mRetainCount;

    private boolean mAutoCloseEnable;
    private long mAutoCloseTime;

    public MultiIntentService() {
        super();
    }

    static long incSequence() {
        return ++sSequence;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.v(BASE_TAG, "onCreate()");
        mRetainCount = new AtomicInteger(0);
        mFutures = new ConcurrentHashMap<String, Future<?>>();
        mAutoCloseEnable = true;
        mAutoCloseTime = AUTO_CLOSE_DEFAULT_TIME;
        ensureHandler();
        ensureExecutor();
        checkAutoClose();
    }

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            dispatchIntent(intent);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(BASE_TAG, "onDestroy() mRetainCount=" + mRetainCount.get());
        LogUtils.v(BASE_TAG, "onDestroy() mFutures.size()=" + mFutures.size());
        cancelAutoClose();
        destroyHandler();
        destroyExecutor();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void setAutoCloseEnable(boolean enable) {
        mAutoCloseEnable = enable;
        checkAutoClose();
    }

    protected void setAutoCloseTime(long milliseconds) {
        mAutoCloseTime = milliseconds;
        checkAutoClose();
    }

    protected boolean isIdle() {
        return mRetainCount.get() <= 0;
    }

    protected final void cancel(final String tag) {
        Future<?> future;
        synchronized (mLock) {
            future = mFutures.get(tag);
        }
        if (future != null) {
            future.cancel(true);
            release(tag);
        }
    }

    private void dispatchIntent(final Intent intent) {
        final String tag = buildTag(intent);
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LogUtils.v(BASE_TAG, "dispatchIntent thread=" + Thread.currentThread());
                LogUtils.v(BASE_TAG, "dispatchIntent start tag=" + tag);
                onHandleIntent(intent, tag);
                LogUtils.v(BASE_TAG, "dispatchIntent end tag=" + tag);
                release(tag);
            }
        };
        Future<?> future = submit(runnable);
        retain(tag, future);
    }

    protected void retain(final String tag, final Future<?> future) {
        LogUtils.v(BASE_TAG, "retain() tag=" + tag);
        mRetainCount.incrementAndGet();
        mFutures.put(tag, future);
    }

    protected void release(final String tag) {
        LogUtils.v(BASE_TAG, "release() tag=" + tag);
        mRetainCount.decrementAndGet();
        synchronized (mLock) {
            mFutures.remove(tag);
        }
        checkAutoClose();
    }

    private void checkAutoClose() {
        if (mAutoCloseEnable) {
            scheduleAutoClose();
        } else {
            cancelAutoClose();
        }
    }

    private void scheduleAutoClose() {
        if (mAutoCloseTime > 0) {
            LogUtils.v(BASE_TAG, "scheduleAutoClose()");
            if (mHandler != null) {
                mHandler.postDelayed(mAutoCloseRunnable, mAutoCloseTime);
            }
        }
    }

    private void cancelAutoClose() {
        LogUtils.v(BASE_TAG, "cancelAutoClose()");
        if (mHandler != null) {
            mHandler.removeCallbacks(mAutoCloseRunnable);
        }
    }

    private void autoClose() {
        LogUtils.v(BASE_TAG, "autoClose() mRetainCount=" + mRetainCount.get());
        LogUtils.v(BASE_TAG, "autoClose() mFutures.size()=" + mFutures.size());
        if (isIdle()) {
            stopSelf();
        }
    }

    private void ensureHandler() {
        if (mHandler == null) {
            mHandler = new Handler();
        }
    }

    private void destroyHandler() {
        synchronized (mLock) {
            if (mHandler != null) {
                mHandler.removeCallbacksAndMessages(null);
                mHandler = null;
            }
        }
    }

    private ExecutorService ensureExecutor() {
        if (mExecutor == null || mExecutor.isShutdown()) {
            mExecutor = createExecutor();
        }
        return mExecutor;
    }

    private void destroyExecutor() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
            mExecutor = null;
        }
    }

    private String buildTag(final Intent intent) {
        final long hashCode = System.identityHashCode(intent);
        final long sequence = incSequence();
        final long timestamp = SystemClock.elapsedRealtime();
        StringBuilder builder = new StringBuilder();
        builder.append(hashCode).append(SEPARATOR);
        builder.append(timestamp).append(SEPARATOR);
        builder.append(sequence);
        return builder.toString();
    }

    private Future<?> submit(Runnable runnable) {
        ensureExecutor();
        return mExecutor.submit(runnable);
    }

    protected ExecutorService createExecutor() {
        return ThreadUtils.newCachedThreadPool(BASE_TAG);
    }

    /**
     * 此方法在非UI线程执行
     *
     * @param intent Intent
     * @param tag    TAG，可以用于取消任务
     */
    protected abstract void onHandleIntent(final Intent intent, final String tag);

}
