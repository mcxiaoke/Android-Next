package com.mcxiaoke.next.task;

/**
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 14:50
 */
final class TaskFactory {

    public static TaskQueue createQueue() {
        return new TaskQueueImpl();
    }

    public static <Result> Task<Result> createTask(final TaskBuilder<Result> builder) {
        return new TaskImpl<Result>(builder);
    }
}
