package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 14:50
 */
final class TaskFactory {

    static TaskQueue createQueue(int maxThreads) {
        return new TaskQueueImpl(maxThreads);
    }

    static <Result> Task<Result> createTask(final TaskBuilder<Result> builder) {
        return new TaskImpl<Result>(builder);
    }

    static <Result> ITaskRunnable createRunnable(final Task<Result> task) {
        return new TaskRunnable<Result>(task, task.getName());
    }
}
