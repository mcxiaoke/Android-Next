package com.mcxiaoke.next.task;

import android.os.Bundle;

import java.util.concurrent.Callable;

/**
 * User: mcxiaoke
 * Date: 15/6/16
 * Time: 11:50
 */
public class Task<Result> {

    public interface Success<Result> {
        void onSuccess(final Result result, final Bundle extras);
    }

    public interface Failure {
        void onFailure(Throwable throwable, final Bundle extras);
    }

    private Object mCaller;
    private TaskCallback<Result> mCallback;
    private Success<Result> mSuccess;
    private Failure mFailure;
    private Callable<Result> mCallable;
    private boolean mSerially;

    public static <Result> Task<Result> create(Callable<Result> callable) {
        return new Task<Result>().call(callable);
    }

    Task() {
    }

    public String start() {
        if (mCaller == null) {
            throw new NullPointerException("caller can not be null.");
        }
        if (mCallable == null) {
            throw new NullPointerException("callable can not be null.");
        }
        if (mCallback == null) {
            mCallback = new TaskCallback<Result>() {
                @Override
                public void onTaskStarted(final String tag, final Bundle extras) {
                }

                @Override
                public void onTaskFinished(final Result result, final Bundle extras) {
                }

                @Override
                public void onTaskSuccess(final Result result, final Bundle extras) {
                    if (mSuccess != null) {
                        mSuccess.onSuccess(result, extras);
                    }
                }

                @Override
                public void onTaskFailure(final Throwable ex, final Bundle extras) {
                    if (mFailure != null) {
                        mFailure.onFailure(ex, extras);
                    }
                }
            };
        }
        return TaskQueue.getDefault().execute(mCallable, mCallback, mCaller, mSerially);
    }

    public <Caller> Task<Result> with(final Caller caller) {
        if (mCaller != null) {
            throw new IllegalStateException("caller is already set.");
        }
        mCaller = caller;
        return this;
    }

    public Task<Result> call(final Callable<Result> callable) {
        if (mCallable != null) {
            throw new IllegalStateException("callable is already set.");
        }
        mCallable = callable;
        return this;
    }

    public Task<Result> success(final Success<Result> success) {
        if (mSuccess != null) {
            throw new IllegalStateException("success is already set.");
        }
        mSuccess = success;
        return this;
    }

    public Task<Result> failure(final Failure failure) {
        if (mFailure != null) {
            throw new IllegalStateException("failure is already set.");
        }
        mFailure = failure;
        return this;
    }

    public Task<Result> callback(final TaskCallback<Result> callback) {
        if (mCallback != null) {
            throw new IllegalStateException("callback is already set.");
        }
        mCallback = callback;
        return this;
    }

    public Task<Result> serial(final boolean serially) {
        mSerially = serially;
        return this;
    }

}
