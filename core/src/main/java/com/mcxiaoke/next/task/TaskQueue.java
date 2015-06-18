package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 12:02
 */
public abstract class TaskQueue implements ITaskQueue {

    static final class SingletonHolder {
        static final TaskQueue INSTANCE = TaskFactory.createQueue();
    }

    public static TaskQueue getDefault() {
        return SingletonHolder.INSTANCE;
    }

    public static TaskQueue createNew() {
        return TaskFactory.createQueue();
    }

    abstract <Result> TaskTag execute(final Task<Result> task);

    abstract void remove(final TaskTag tag);


}
