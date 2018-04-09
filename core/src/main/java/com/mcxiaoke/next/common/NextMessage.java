package com.mcxiaoke.next.common;

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
public class NextMessage implements Parcelable {
    public static Creator<NextMessage> CREATOR = new Creator<NextMessage>() {
        public NextMessage createFromParcel(Parcel source) {
            return new NextMessage(source);
        }

        public NextMessage[] newArray(int size) {
            return new NextMessage[size];
        }
    };
    public int type;
    public int arg1;
    public long arg2;
    public boolean flag;
    public String text;
    public Object obj;
    private Bundle data;

    public NextMessage() {
    }

    public NextMessage(int type) {
        this.type = type;
    }

    public NextMessage(int type, int arg1) {
        this.type = type;
        this.arg1 = arg1;
    }

    public NextMessage(int type, int arg1, long arg2) {
        this(type, arg1, arg2, false, null);
    }


    public NextMessage(int type, int arg1, long arg2, boolean flag) {
        this(type, arg1, arg2, flag, null);
    }

    public NextMessage(int type, int arg1, long arg2, boolean flag, String text) {
        this.type = type;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.flag = flag;
        this.text = text;
    }

    private NextMessage(Parcel in) {
        this.type = in.readInt();
        this.arg1 = in.readInt();
        this.arg2 = in.readLong();
        this.flag = in.readByte() != 0;
        this.text = in.readString();
        data = in.readBundle(getClass().getClassLoader());
    }

    public static NextMessage create(int type) {
        return new NextMessage(type);
    }

    public static NextMessage create(int type, int arg1) {
        return new NextMessage(type, arg1);
    }

    public static NextMessage create(int type, int arg1, long arg2) {
        return new NextMessage(type, arg1, arg2);
    }

    public static NextMessage create(int type, int arg1, long arg2, boolean flag) {
        return new NextMessage(type, arg1, arg2, flag);
    }

    public static NextMessage create(int type, int arg1, long arg2, boolean flag, String text) {
        return new NextMessage(type, arg1, arg2, flag, text);
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
        sb.append(", text=").append(text);
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
        dest.writeByte(this.flag ? (byte) 1 : (byte) 0);
        dest.writeString(this.text);
        dest.writeBundle(this.data);
    }
}
