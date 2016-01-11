package com.mcxiaoke.next.http.async;

import android.test.suitebuilder.annotation.SmallTest;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.mcxiaoke.next.http.HttpAsync;
import com.mcxiaoke.next.http.HttpQueue;
import com.mcxiaoke.next.http.NextResponse;
import com.mcxiaoke.next.http.callback.JsonCallback;
import com.mcxiaoke.next.http.transformer.HttpTransformer;
import com.mcxiaoke.next.task.TaskQueue;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 15/8/21
 * Time: 14:52
 */
@SmallTest
public class HttpQueueTest {
    private static final String TEST_URL = "https://api.douban.com/v2/user/1000001";
    private static final String TEST_URL2 = "https://api.douban.com/v2/lifestream/user_timeline/1000001";

    private HttpQueue httpQueue;

    @Before
    public void setup() {
        TaskQueue queue = TaskQueue.pool();
        queue.setExecutor(new TestExecutor());
        httpQueue = new HttpQueue();
        httpQueue.setQueue(queue);

    }

    @Test
    public void testGsonCallback1() {
        HttpAsync.get(TEST_URL, new JsonCallback<User>(User.class) {
            @Override
            public void onSuccess(final User user) {
                Assert.assertNotNull(user);
                Assert.assertEquals("1000001", user.id);
            }
        }, this);
    }

    @Test
    public void testGsonCallback2() {
        Type type = new TypeToken<List<Status>>() {
        }.getType();
        HttpAsync.get(TEST_URL2, new JsonCallback<List<Status>>(type) {
            @Override
            public void onSuccess(final List<Status> statuses) {
                System.err.println(statuses.get(1));
                Assert.assertNotNull(statuses);
                Assert.assertNotNull(statuses.get(1));
                Assert.assertNotNull(statuses.get(1).id);
            }
        }, this);
    }

    @After
    public void tearDown() {
    }


    static class User {
        public String id;
        public String name;
        @SerializedName("created")
        public String createdAt;
        public String avatar;
        @SerializedName("large_avatar")
        public String largeAvatar;
        public String type;
        public String desc;
        @SerializedName("is_banned")
        public boolean isBanned;

    }

    static class Status {
        public String id;
        public String type;
        public String title;
        @Expose
        public String text;
        @Expose
        @SerializedName("created_at")
        public String createdAt;
        @Expose
        @SerializedName("can_reply")
        public int canReply;
        @Expose
        @SerializedName("liked")
        public boolean isLiked;
        @Expose
        @SerializedName("like_count")
        public int likeCount;
        @Expose
        @SerializedName("comments_count")
        public int commentsCount;

        @Override
        public String toString() {
            return "Status{" +
                    "id='" + id + '\'' +
                    '}';
        }
    }

    static class GsonTransformer<T> implements HttpTransformer<T> {
        private Gson gson;
        private Type type;

        public GsonTransformer(final Gson gson, final Type type) {
            this.gson = gson;
            this.type = type;
        }

        @Override
        public T transform(final NextResponse response) throws IOException {
            return gson.fromJson(response.string(), type);
        }
    }

}
