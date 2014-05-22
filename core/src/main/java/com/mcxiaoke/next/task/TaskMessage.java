package com.mcxiaoke.next.task;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 简单的消息载体
 * 注意：Object不能通过Parcelable传递
 * User: mcxiaoke
 * Date: 14-5-15
 * Time: 18:21
 */
public class TaskMessage implements Parcelable {
    public int type;
    public int arg1;
    public long arg2;
    public boolean flag;
    public Object obj;
    private Bundle data;

    public static TaskMessage create(int type) {
        return new TaskMessage(type);
    }

    public static TaskMessage create(int type, int arg1) {
        return new TaskMessage(type, arg1);
    }

    public static TaskMessage create(int type, int arg1, long arg2) {
        return new TaskMessage(type, arg1, arg2);
    }

    public static TaskMessage create(int type, int arg1, long arg2, boolean flag) {
        return new TaskMessage(type, arg1, arg2, flag);
    }


    public TaskMessage() {
    }

    public TaskMessage(int type) {
        this.type = type;
    }

    public TaskMessage(int type, int arg1) {
        this.type = type;
        this.arg1 = arg1;
    }

    public TaskMessage(int type, int arg1, long arg2) {
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public TaskMessage(int type, int arg1, long arg2, boolean flag) {
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.flag = flag;
    }

    private TaskMessage(Parcel in) {
        this.type = in.readInt();
        this.arg1 = in.readInt();
        this.arg2 = in.readLong();
        this.flag = in.readByte() != 0;
        data = in.readBundle();
    }

    public Bundle getData() {
        if (data == null) {
            data = new Bundle();
        }
        return data;
    }

    public void setData(final Bundle data) {
        this.data = data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NextMessage{");
        sb.append("type=").append(type);
        sb.append(", arg1=").append(arg1);
        sb.append(", arg2=").append(arg2);
        sb.append(", flag=").append(flag);
        sb.append(", obj=").append(obj);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeInt(this.arg1);
        dest.writeLong(this.arg2);
        dest.writeByte(flag ? (byte) 1 : (byte) 0);
        dest.writeBundle(data);
    }


    public static Creator<TaskMessage> CREATOR = new Creator<TaskMessage>() {
        public TaskMessage createFromParcel(Parcel source) {
            return new TaskMessage(source);
        }

        public TaskMessage[] newArray(int size) {
            return new TaskMessage[size];
        }
    };
}
