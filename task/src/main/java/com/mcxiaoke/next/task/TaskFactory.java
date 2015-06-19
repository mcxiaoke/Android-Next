package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 14:50
 */
final class TaskFactory {

    static TaskQueue createQueue() {
        return new TaskQueueImpl();
    }

    static <Result> Task<Result> createTask(final TaskBuilder<Result> builder) {
        return new TaskImpl<Result>(builder);
    }

    static <Result> ITaskRunnable createRunnable(final ITaskActions<Result> callback, boolean debug) {
        return new TaskRunnable<Result>
                (callback, debug);
    }
}
