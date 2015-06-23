package com.mcxiaoke.next.task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Task Tag
 * User: mcxiaoke
 * Date: 15/6/18
 * Time: 11:19
 */
final class TaskTag {

    private static final String SEP = "|";
    private static final DateFormat DF_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static final DateFormat DF_NAME = new SimpleDateFormat("HHmmssSSS", Locale.US);
    private static volatile int sSequence = 0;

    private static int incSequence() {
        return ++sSequence;
    }

    public static String getGroup(final Object caller) {
        final String hashcodeHex = Integer.toHexString(System.identityHashCode(caller));
        return caller.getClass().getSimpleName() + SEP + hashcodeHex;
    }

    private final String group;
    private final long createdAt;
    private final int sequence;
    private final String fullTag;

    public TaskTag(final Object caller) {
        // group=className|hashcode-hex
        // fullTag=group|timestamp-hex|seq
        final Date now = new Date();
        this.group = getGroup(caller);
        this.createdAt = now.getTime();
        this.sequence = incSequence();
        this.fullTag = group + SEP + DF_NAME.format(now) + SEP + sequence;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return fullTag;
    }

    public int getSequence() {
        return sequence;
    }

    public String getCreatedAt() {
        return DF_DATE.format(new Date(createdAt));
    }

    @Override
    public String toString() {
        return fullTag;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TaskTag taskTag = (TaskTag) o;

        return fullTag.equals(taskTag.fullTag);

    }

    @Override
    public int hashCode() {
        return fullTag.hashCode();
    }
}
