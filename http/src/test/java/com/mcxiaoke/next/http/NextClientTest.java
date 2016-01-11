package com.mcxiaoke.next.http;

import android.test.suitebuilder.annotation.SmallTest;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.mcxiaoke.next.http.transformer.ResponseTransformer;
import com.mcxiaoke.next.http.transformer.StringTransformer;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * User: mcxiaoke
 * Date: 15/7/6
 * Time: 17:10
 */
@SmallTest
public class NextClientTest extends BaseTest {
    private static final String TEST_URL = "https://api.douban.com/v2/user/1000001";
    private static final String TEST_URL2 = "https://api.douban.com/v2/lifestream/user_timeline/1000001";

    @Test
    public void createClient() {
        new NextClient();
    }

    @Test
    public void testClientConfig() throws IOException {
        final NextClient client = new NextClient();
        final OkHttpClient okClient=client.getClient();
        okClient.setConnectTimeout(15, TimeUnit.SECONDS);
        okClient.setReadTimeout(20, TimeUnit.SECONDS);
        okClient.setWriteTimeout(25, TimeUnit.SECONDS);
        client.setUserAgent("client-ua");
        client.setAuthorization("client-auth");
        client.setReferer("www.douban.com");
        isEquals(15000, okClient.getConnectTimeout());
        isEquals(20000, okClient.getReadTimeout());
        isEquals(25000, okClient.getWriteTimeout());
        isEquals("client-ua", client.getUserAgent());
        isEquals("client-auth", client.getAuthorization());
        isEquals("www.douban.com", client.getRefer());
    }

    @Test
    public void testClientParams() throws IOException {
        final NextClient client = new NextClient();
        client.addParam("k1", "v1");
        client.addParam("k2", "v2");
        client.addParam("k3", null);
        client.addParam("k4", "");
        isEquals(2, client.getParamsSize());
        notNull(client.getParam("k1"));
        notNull(client.getParam("k2"));
        isNull(client.getParam("k3"));
        isNull(client.getParam("k4"));
        final Map<String, String> cp = new HashMap<String, String>();
        cp.put("k5", "v5");
        cp.put("k6", null);
        cp.put("k7", "");
        isEquals(3, cp.size());
        isTrue(cp.containsKey("k5"));
        isTrue(cp.containsKey("k6"));
        isTrue(cp.containsKey("k7"));
        client.addParams(cp);
        isEquals(3, client.getParamsSize());
        notNull(client.getParam("k5"));
        isNull(client.getParam("k6"));
        isNull(client.getParam("k7"));
    }

    @Test
    public void testClientHeaders() throws IOException {
        final NextClient client = new NextClient();
        client.addHeader("k1", "v1");
        client.addHeader("k2", "v2");
        client.addHeader("k3", null);
        client.addHeader("k4", "");
        isEquals(2, client.getHeadersSize());
        notNull(client.getHeader("k1"));
        notNull(client.getHeader("k2"));
        isNull(client.getHeader("k3"));
        isNull(client.getHeader("k4"));
        final Map<String, String> hp = new HashMap<String, String>();
        hp.put("k5", "v5");
        hp.put("k6", null);
        hp.put("k7", "");
        isEquals(3, hp.size());
        isTrue(hp.containsKey("k5"));
        isTrue(hp.containsKey("k6"));
        isTrue(hp.containsKey("k7"));
        client.addHeaders(hp);
        isEquals(3, client.getHeadersSize());
        notNull(client.getHeader("k5"));
        isNull(client.getHeader("k6"));
        isNull(client.getHeader("k7"));
    }


    @Test
    public void testGetRequest() throws IOException {
        final NextClient client = new NextClient();
        client.addParam("k1", "v1");
        client.addParam("k2", "v2");
        client.addParam("k3", null);
        client.addParam("k4", "");

        client.addHeader("ha", "ha1");
        client.addHeader("hb", "hb1");
        client.addParam("hc", null);
        client.addParam("hd", "");

        final Map<String, String> queries = new HashMap<String, String>();
        queries.put("q1", "v1");
        final Map<String, String> forms = new HashMap<String, String>();
        forms.put("f1", "v1");
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("h1", "v1");

        final NextRequest request = client.createRequest(HttpMethod.GET, TEST_URL,
                queries, forms, headers);

        notNull(request.getQuery("k1"));
        notNull(request.getQuery("k2"));
        isNull(request.getQuery("k3"));
        isNull(request.getQuery("k4"));

        notNull(request.getHeader("ha"));
        notNull(request.getHeader("hb"));
        isNull(request.getHeader("hc"));
        isNull(request.getHeader("hd"));

        notNull(request.getQuery("q1"));
        isNull(request.getForm("f1"));
        notNull(request.getHeader("h1"));

        isNull(request.getRequestBody());
    }

    @Test
    public void testPostRequest() throws IOException {
        final NextClient client = new NextClient();
        client.addParam("k1", "v1");
        client.addParam("k2", "v2");
        client.addParam("k3", null);
        client.addParam("k4", "");

        client.addHeader("ha", "ha1");
        client.addHeader("hb", "hb1");
        client.addParam("hc", null);
        client.addParam("hd", "");

        final Map<String, String> queries = new HashMap<String, String>();
        queries.put("q1", "v1");
        final Map<String, String> forms = new HashMap<String, String>();
        forms.put("f1", "v1");
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("h1", "v1");

        final NextRequest request = client.createRequest(HttpMethod.POST, TEST_URL,
                queries, forms, headers);

        notNull(request.getForm("k1"));
        notNull(request.getForm("k2"));
        isNull(request.getForm("k3"));
        isNull(request.getForm("k4"));

        notNull(request.getHeader("ha"));
        notNull(request.getHeader("hb"));
        isNull(request.getHeader("hc"));
        isNull(request.getHeader("hd"));

        notNull(request.getQuery("q1"));
        notNull(request.getForm("f1"));
        notNull(request.getHeader("h1"));

        notNull(request.getRequestBody());
    }

    @Test
    public void testHeadersOverride() throws IOException {
        final NextClient client = new NextClient();
        client.addHeader("ha", "ha1");
        client.addHeader("hb", "hb1");
        client.addHeader("hc", null);
        client.addHeader("hd", "");
        client.addHeader("he", "he1");

        final Map<String, String> queries = new HashMap<String, String>();
        queries.put("q1", "v1");
        final Map<String, String> forms = new HashMap<String, String>();
        forms.put("f1", "v1");
        final Map<String, String> headers = new HashMap<String, String>();
        headers.put("h1", "v1");
        headers.put("ha", "new-ha");
        headers.put("hb", "new-hb");
        headers.put("hc", "new-hc");
        headers.put("hh", "hh");

        final NextRequest r = client.createRequest(HttpMethod.GET, TEST_URL,
                queries, forms, headers);
        isEquals("new-ha", r.getHeader("ha"));
        isEquals("new-hb", r.getHeader("hb"));
        isEquals("new-hc", r.getHeader("hc"));
        isNull(r.getHeader("hd"));
        isEquals("he1", r.getHeader("he"));
        isEquals("hh", r.getHeader("hh"));
        isEquals("v1", r.getHeader("h1"));
    }

    @Test
    public void testQueriesOverride() throws IOException {
        final NextClient client = new NextClient();
        client.addParam("ha", "ha1");
        client.addParam("hb", "hb1");
        client.addParam("hc", null);
        client.addParam("hd", "");
        client.addParam("he", "he1");

        final Map<String, String> queries = new HashMap<String, String>();
        queries.put("q1", "v1");
        queries.put("ha", "new-ha");
        queries.put("hb", "new-hb");
        queries.put("hc", "new-hc");
        queries.put("hh", "hh");
        final Map<String, String> forms = new HashMap<String, String>();
        forms.put("f1", "v1");
        final Map<String, String> headers = new HashMap<String, String>();

        final NextRequest r = client.createRequest(HttpMethod.GET, TEST_URL,
                queries, forms, headers);
        isEquals("new-ha", r.getQuery("ha"));
        isEquals("new-hb", r.getQuery("hb"));
        isEquals("new-hc", r.getQuery("hc"));
        isNull(r.getQuery("hd"));
        isEquals("he1", r.getQuery("he"));
        isEquals("hh", r.getQuery("hh"));
        isEquals("v1", r.getQuery("q1"));
    }

    @Test
    public void testFormsOverride() throws IOException {
        final NextClient client = new NextClient();
        client.addParam("ha", "ha1");
        client.addParam("hb", "hb1");
        client.addParam("hc", null);
        client.addParam("hd", "");
        client.addParam("he", "he1");

        final Map<String, String> queries = new HashMap<String, String>();
        queries.put("q1", "v1");
        final Map<String, String> forms = new HashMap<String, String>();
        forms.put("f1", "v1");
        forms.put("ha", "new-ha");
        forms.put("hb", "new-hb");
        forms.put("hc", "new-hc");
        forms.put("hh", "hh");
        final Map<String, String> headers = new HashMap<String, String>();

        final NextRequest r = client.createRequest(HttpMethod.POST, TEST_URL,
                queries, forms, headers);
        isEquals("new-ha", r.getForm("ha"));
        isEquals("new-hb", r.getForm("hb"));
        isEquals("new-hc", r.getForm("hc"));
        isNull(r.getForm("hd"));
        isEquals("he1", r.getForm("he"));
        isEquals("hh", r.getForm("hh"));
        isEquals("v1", r.getForm("f1"));
    }

    @Test
    public void testUrlQueries() throws IOException {
        final NextClient client = new NextClient();
        client.addParam("k1", "v1");
        client.addParam("中文", "编码测试");
        final Map<String, String> queries = new HashMap<String, String>();
        queries.put("q1", "v1");
        queries.put("q2", "v2");
        queries.put("q3", null);
        final Map<String, String> forms = new HashMap<String, String>();
        forms.put("f1", "v1");
        final NextRequest request = client.createRequest(HttpMethod.GET, TEST_URL,
                queries, forms, null);
        notNull(request.getQuery("k1"));
        isEquals("编码测试", request.getQuery("中文"));
        notNull(request.getQuery("q1"));
        notNull(request.getQuery("q2"));
        isNull(request.getQuery("q3"));
        isNull(request.getQuery("f1"));

        final HttpUrl url = request.url();
        isEquals("v1", url.queryParameter("k1"));
        isEquals("编码测试", url.queryParameter("中文"));
        isEquals("v1", url.queryParameter("q1"));
        isEquals("v2", url.queryParameter("q2"));
        isNull(url.queryParameter("q3"));
        isNull(url.queryParameter("f1"));

    }

    @Test
    public void testExecute() throws IOException {
        final Map<String, String> headers = new HashMap<String, String>();
        final Map<String, String> queries = new HashMap<String, String>();
        final Map<String, String> forms = new HashMap<String, String>();
        final NextClient client = new NextClient();
        client.setUserAgent("client-ua");
        client.setAuthorization("client-auth");
        client.setReferer("www.douban.com");
        client.get(TEST_URL, queries, headers);
    }

    @Test
    public void testStringConverter() throws IOException {
        final Map<String, String> headers = new HashMap<String, String>();
        final Map<String, String> queries = new HashMap<String, String>();
        final NextRequest request = new NextRequest(HttpMethod.GET, TEST_URL);
        request.queries(queries).headers(headers);

        final NextClient client = new NextClient();
        client.setUserAgent("client-ua");
        client.setAuthorization("client-auth");
        client.setReferer("www.douban.com");
        StringTransformer converter = new StringTransformer();
        final String content = client.execute(request, converter);
        notNull(content);
        isTrue(content.contains("1000001"));
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

    static class GsonTransformer<T> implements ResponseTransformer<T> {
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

    @Test
    public void testGsonConverter1() throws IOException {
        final Map<String, String> headers = new HashMap<String, String>();
        final Map<String, String> queries = new HashMap<String, String>();
        final NextRequest request = new NextRequest(HttpMethod.GET, TEST_URL);
        request.queries(queries).headers(headers);

        final NextClient client = new NextClient();
        client.setUserAgent("client-ua");
        client.setAuthorization("client-auth");
        client.setReferer("www.douban.com");
        GsonTransformer<User> converter = new GsonTransformer<User>(new Gson(), User.class);
        final User user = client.execute(request, converter);
        notNull(user);
        notNull(user.id);
        notNull(user.name);
        isEquals("1000001", user.id);
    }

    @Test
    public void testGsonConverter2() throws IOException {
        final Map<String, String> headers = new HashMap<String, String>();
        final Map<String, String> queries = new HashMap<String, String>();
        final NextRequest request = new NextRequest(HttpMethod.GET, TEST_URL2);
        request.queries(queries).headers(headers);

        final NextClient client = new NextClient();
        client.setUserAgent("client-ua");
        client.setAuthorization("client-auth");
        client.setReferer("www.douban.com");
        Type type = new TypeToken<List<Status>>() {
        }.getType();
        Gson gson = new Gson();
        GsonTransformer<List<Status>> converter = new GsonTransformer<List<Status>>(gson, type);
        List<Status> timeline = client.execute(request, converter);
        System.err.println("timeline:" + timeline);
        notNull(timeline);
        notNull(timeline.get(1));
        notNull(timeline.get(1).id);
        notNull(timeline.get(1).createdAt);
        notNull(timeline.get(1).text);
    }
}
