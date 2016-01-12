package com.mcxiaoke.next.samples.http;

import com.google.gson.annotations.SerializedName;

/**
 * User: mcxiaoke
 * Date: 16/1/12
 * Time: 15:55
 */
public class User {


    /**
     * loc_id : 108288
     * name : 阿北
     * created : 2006-01-09 21:12:47
     * is_banned : false
     * is_suicide : false
     * loc_name : 北京
     * avatar : http://img3.douban.com/icon/u1000001-30.jpg
     * signature : less is more
     * uid : ahbei
     * alt : http://www.douban.com/people/ahbei/
     * desc : Less is more
     * type : user
     * id : 1000001
     * large_avatar : http://img3.douban.com/icon/up1000001-30.jpg
     */

    @SerializedName("loc_id")
    public String locId;
    @SerializedName("name")
    public String name;
    @SerializedName("created")
    public String created;
    @SerializedName("is_banned")
    public boolean isBanned;
    @SerializedName("is_suicide")
    public boolean isSuicide;
    @SerializedName("loc_name")
    public String locName;
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("signature")
    public String signature;
    @SerializedName("uid")
    public String uid;
    @SerializedName("alt")
    public String alt;
    @SerializedName("desc")
    public String desc;
    @SerializedName("type")
    public String type;
    @SerializedName("id")
    public String id;
    @SerializedName("large_avatar")
    public String largeAvatar;

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", uid='" + uid + '\'' +
                ", type='" + type + '\'' +
                ", avatar='" + avatar + '\'' +
                ", locId='" + locId + '\'' +
                ", created='" + created + '\'' +
                '}';
    }
}
