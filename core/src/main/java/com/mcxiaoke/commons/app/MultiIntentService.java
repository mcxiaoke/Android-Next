package com.mcxiaoke.commons.app;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import com.mcxiaoke.commons.utils.LogUtils;
import com.mcxiaoke.commons.utils.ThreadUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 类似于IntentService，但是多个异步任务可以并行执行
 * Service每隔30秒自动检查，如果任务数目为0则自动结束
 * User: mcxiaoke
 * Date: 14-4-22
 * Time: 14:04
 */
public abstract class MultiIntentService extends Service {
    private static final String BASE_TAG = MultiIntentService.class.getSimpleName();

    public static final long CHECK_STOP_DELAY = 30 * 1000L;

    private final Object mLock = new Object();

    private ExecutorService mExecutor;
    private Handler mHandler;

    private volatile Map<Long, Future<?>> mReferenceTasks;
    private volatile AtomicInteger mReferenceCount;

    public MultiIntentService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.v(BASE_TAG, "onCreate()");
        mReferenceCount = new AtomicInteger(0);
        mReferenceTasks = new ConcurrentHashMap<Long, Future<?>>();
        ensureHandler();
        ensureExecutor();
    }

    @Override
    public final int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            dispatchIntent(intent);
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(BASE_TAG, "onDestroy() mReferenceCount=" + mReferenceCount.get());
        LogUtils.v(BASE_TAG, "onDestroy() mReferenceTasks.size()=" + mReferenceTasks.size());
        cancelCheckStopService();
        destroyHandler();
        destroyExecutor();
    }

    private void dispatchIntent(final Intent intent) {
        final long id = System.currentTimeMillis();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                LogUtils.v(BASE_TAG, "before handleIntent() thread=" + Thread.currentThread());
                LogUtils.v(BASE_TAG, "before handleIntent() id=" + id);
                onHandleIntent(intent, id);
                LogUtils.v(BASE_TAG, "after handleIntent() id=" + id);
                decrementReferenceCount(id);
            }
        };
        Future<?> future = submit(runnable);
        incrementReferenceCount(id, future);
    }

    private void incrementReferenceCount(final long id, final Future<?> future) {
        LogUtils.v(BASE_TAG, "incrementReferenceCount() id=" + id);
        mReferenceCount.incrementAndGet();
        mReferenceTasks.put(id, future);
    }

    private void decrementReferenceCount(final long id) {
        LogUtils.v(BASE_TAG, "decrementReferenceCount() id=" + id);
        mReferenceCount.decrementAndGet();
        mReferenceTasks.remove(id);
        scheduleCheckStopService();
    }

    private final Runnable mCheckStopRunnable = new Runnable() {
        @Override
        public void run() {
            checkStopService();
        }
    };

    private void scheduleCheckStopService() {
        ensureHandler();
        mHandler.postDelayed(mCheckStopRunnable, CHECK_STOP_DELAY);
    }

    private void cancelCheckStopService() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mCheckStopRunnable);
        }
    }

    private void checkStopService() {
        LogUtils.v(BASE_TAG, "checkStopService() mReferenceCount=" + mReferenceCount.get());
        LogUtils.v(BASE_TAG, "checkStopService() mReferenceTasks.size()=" + mReferenceTasks.size());
        if (mReferenceCount.get() <= 0) {
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

    protected final void cancel(long id) {
        Future<?> future = mReferenceTasks.get(id);
        if (future != null) {
            future.cancel(true);
            decrementReferenceCount(id);
        }
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
     * @param id     ID，可以用于取消任务
     */
    protected abstract void onHandleIntent(Intent intent, long id);

}
