package com.mcxiaoke.next.task;

import com.mcxiaoke.next.utils.AndroidUtils;

/**
 * User: mcxiaoke
 * Date: 15/6/17
 * Time: 11:09
 */
class TaskHelper {

    public static final String SEPARATOR = "::";
    private static volatile int sSequence = 0;

    /**
     * 根据Caller生成对应的TAG，hashcode+类名+timestamp+seq
     *
     * @param caller 调用对象
     * @return 任务的TAG
     */
    static String buildTag(final Object caller) {
        // caller的key是hashcode
        // tag的组成 className::hashcode::timestamp::seq
        final int hashCode = System.identityHashCode(caller);
        final int sequence = incSequence();
        final String className = caller.getClass().getSimpleName();
        final long timestamp = System.currentTimeMillis();
        final StringBuilder builder = new StringBuilder();
        builder.append(className).append(SEPARATOR);
        builder.append(hashCode).append(SEPARATOR);
        builder.append(timestamp).append(SEPARATOR);
        builder.append(sequence);
        return builder.toString();
    }

    static int incSequence() {
        return ++sSequence;
    }

    static boolean validCaller(final TaskInfo task) {
        return task.caller != null && (!task.check || AndroidUtils.isActive(task.caller));
    }
}
