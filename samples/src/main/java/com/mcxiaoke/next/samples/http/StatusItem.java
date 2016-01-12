package com.mcxiaoke.next.samples.http;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * User: mcxiaoke
 * Date: 16/1/12
 * Time: 15:56
 */
public class StatusItem {


    /**
     * reshared_count : 69
     * text : Sean Penn在丛林里七小时秘密采访挖地洞越狱的那个墨西哥毒枭，内容马上会在Rolling Stone杂志上放出。毒枭又被抓可能是因为他准备拍一部关于自己的电影的时候暴露了行踪。这本身就是电影啊，Sean Penn可以直接演。
     * like_count : 60
     * id : 1782582971
     * can_reply : 1
     * media : [{"width":460,"href":"https://dou.bz/475ktk","thumb":"https://img1.doubanio.com/view/status/median/public/1c989a4421ea22b.jpg","image":"https://img1.doubanio.com/view/status/raw/public/1c989a4421ea22b.jpg","is_animated":false,"type":"image","height":298}]
     * source : 豆瓣App iOS版
     * created_at : 2016-01-10 11:59:37
     * author : {"name":"阿北","is_suicide":false,"avatar":"https://img3.doubanio.com/icon/u1000001-30.jpg","uid":"ahbei","alt":"http://www.douban.com/people/ahbei/","type":"user","id":"1000001","large_avatar":"https://img3.doubanio.com/icon/up1000001-30.jpg"}
     * comments_count : 11
     * activity : 说：
     */

    @SerializedName("reshared_count")
    public int resharedCount;
    @SerializedName("text")
    public String text;
    @SerializedName("like_count")
    public int likeCount;
    @SerializedName("id")
    public String id;
    @SerializedName("can_reply")
    public int canReply;
    @SerializedName("source")
    public String source;
    @SerializedName("created_at")
    public String createdAt;
    /**
     * name : 阿北
     * is_suicide : false
     * avatar : https://img3.doubanio.com/icon/u1000001-30.jpg
     * uid : ahbei
     * alt : http://www.douban.com/people/ahbei/
     * type : user
     * id : 1000001
     * large_avatar : https://img3.doubanio.com/icon/up1000001-30.jpg
     */

    @SerializedName("author")
    public AuthorEntity author;
    @SerializedName("comments_count")
    public int commentsCount;
    @SerializedName("activity")
    public String activity;
    /**
     * width : 460
     * href : https://dou.bz/475ktk
     * thumb : https://img1.doubanio.com/view/status/median/public/1c989a4421ea22b.jpg
     * image : https://img1.doubanio.com/view/status/raw/public/1c989a4421ea22b.jpg
     * is_animated : false
     * type : image
     * height : 298
     */

    @SerializedName("media")
    public List<MediaEntity> media;

    public static class AuthorEntity {
        @SerializedName("name")
        public String name;
        @SerializedName("is_suicide")
        public boolean isSuicide;
        @SerializedName("avatar")
        public String avatar;
        @SerializedName("uid")
        public String uid;
        @SerializedName("alt")
        public String alt;
        @SerializedName("type")
        public String type;
        @SerializedName("id")
        public String id;
        @SerializedName("large_avatar")
        public String largeAvatar;
    }

    public static class MediaEntity {
        @SerializedName("width")
        public int width;
        @SerializedName("href")
        public String href;
        @SerializedName("thumb")
        public String thumb;
        @SerializedName("image")
        public String image;
        @SerializedName("is_animated")
        public boolean isAnimated;
        @SerializedName("type")
        public String type;
        @SerializedName("height")
        public int height;
    }

    @Override
    public String toString() {
        return "StatusItem{" +
                "id='" + id + '\'' +
                ", source='" + source + '\'' +
                ", text='" + text + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", canReply=" + canReply +
                ", likeCount=" + likeCount +
                '}';
    }
}
