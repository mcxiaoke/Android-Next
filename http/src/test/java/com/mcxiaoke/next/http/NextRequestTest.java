package com.mcxiaoke.next.http;

import okhttp3.HttpUrl;
import org.junit.Test;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 15/7/6
 * Time: 15:30
 */
public class NextRequestTest extends BaseTest {
    private static final String TEST_URL = "https://api.douban.com/v2/user/1000001";


    @Test
    public void create1() {
        new NextRequest(HttpMethod.HEAD, TEST_URL);
        new NextRequest(HttpMethod.GET, TEST_URL);
        new NextRequest(HttpMethod.DELETE, TEST_URL);
        new NextRequest(HttpMethod.PUT, TEST_URL);
        new NextRequest(HttpMethod.POST, TEST_URL);
    }

    @Test
    public void create2() {
        NextRequest r1 = new NextRequest(HttpMethod.GET, TEST_URL);
        NextRequest r2 = NextRequest.get(TEST_URL);
        notNull(r1);
        isEquals(HttpMethod.GET, r1.method());
        isEquals(TEST_URL, r1.url().toString());
        isEquals(r1.method(), r2.method());
        isEquals(r1.url(), r2.url());
    }


    @Test(expected = IllegalArgumentException.class)
    public void createNull1() {
        new NextRequest(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull2() {
        new NextRequest(null, TEST_URL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull3() {
        new NextRequest(HttpMethod.GET, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull4() {
        new NextRequest(HttpMethod.GET, TEST_URL, null);
    }

    @Test
    public void testUrl() {
        HttpUrl url = HttpUrl.parse("hxxp://www.douban.com");
        isNull(url);
        url = HttpUrl.parse("www.douban.com");
        isNull(url);
        url = HttpUrl.parse("sms://18600000000");
        isNull(url);

        url = HttpUrl.parse("http://123.456");
        notNull(url);
        url = HttpUrl.parse("http://8.8.8.8");
        notNull(url);
        url = HttpUrl.parse("http://123456789");
        notNull(url);
        url = HttpUrl.parse("http://www.douban.com");
        notNull(url);
        url = HttpUrl.parse("https://www.douban.com");
        notNull(url);

        url = HttpUrl.parse(TEST_URL);
        notNull(url);
        isEquals(TEST_URL, url.toString());
    }


    @Test(expected = IllegalArgumentException.class)
    public void invalidUrl1() {
        new NextRequest(HttpMethod.GET, "hxxp://www.douban.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidUrl2() {
        new NextRequest(HttpMethod.GET, "www.douban.com");
    }

    @Test
    public void testBase() {
        final NextRequest r = NextRequest.get(TEST_URL);

        notNull(r);
        isEquals(HttpMethod.GET, r.method());
        isEquals(TEST_URL, r.url().toString());

        r.authorization("auth").userAgent("ua");
        isEquals("auth", r.getHeader(HttpConsts.AUTHORIZATION));
        isEquals("ua", r.getHeader(HttpConsts.USER_AGENT));
    }

    @Test
    public void testHeaders() {
        final NextRequest r = NextRequest.get(TEST_URL);
        r.header("q1", "value1");
        r.header("q2", "value2");
        isEquals("value1", r.getHeader("q1"));
        isEquals("value2", r.getHeader("q2"));
        isFalse(r.hasHeader("x1"));
        isFalse(r.hasHeader("x2"));
        isEquals(2, r.headersSize());

        r.header("q3", null);
        r.header("q4", "");
        // null value not allowed
        isFalse(r.hasHeader("q3"));
        isTrue(r.hasHeader("q4"));
        isEquals(3, r.headersSize());
    }

    @Test
    public void testQueries() {
        final NextRequest r = NextRequest.get(TEST_URL);
        r.query("q1", "value1");
        r.query("q2", "value2");
        isEquals("value1", r.getQuery("q1"));
        isEquals("value2", r.getQuery("q2"));
        isFalse(r.hasQuery("x1"));
        isFalse(r.hasQuery("x2"));
        isEquals(2, r.queriesSize());

        r.query("q3", null);
        r.query("q4", "");
        // null value not allowed
        isFalse(r.hasQuery("q3"));
        isTrue(r.hasQuery("q4"));
        isEquals(3, r.queriesSize());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam1() {
        final NextRequest r = new NextRequest(HttpMethod.DELETE, TEST_URL);
        r.query(null, "value");
    }

    @Test
    public void testNullParam2() {
        final NextRequest r = new NextRequest(HttpMethod.DELETE, TEST_URL);
        r.form(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullParam3() {
        final NextRequest r = new NextRequest(HttpMethod.DELETE, TEST_URL);
        r.header(null, "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyParam1() {
        final NextRequest r = new NextRequest(HttpMethod.DELETE, TEST_URL);
        r.query("", "value");
    }

    @Test
    public void testEmptyParam2() {
        final NextRequest r = new NextRequest(HttpMethod.DELETE, TEST_URL);
        r.form("", "value");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyParam3() {
        final NextRequest r = new NextRequest(HttpMethod.DELETE, TEST_URL);
        r.header("", "value");
    }


    @Test
    public void testForms() {
        final NextRequest r = NextRequest.post(TEST_URL);
        r.form("q1", "value1");
        r.form("q2", "value2");
        isEquals("value1", r.getForm("q1"));
        isEquals("value2", r.getForm("q2"));
        isFalse(r.hasForm("x1"));
        isFalse(r.hasForm("x2"));
        isEquals(2, r.formsSize());

        r.form("q3", null);
        r.form("q4", "");
        // null value not allowed
        isFalse(r.hasForm("q3"));
        isTrue(r.hasForm("q4"));
        isEquals(3, r.formsSize());
    }

    @Test
    public void testBody() {
        final NextRequest r0 = NextRequest.head(TEST_URL).query("q", "v");
        final NextRequest r1 = NextRequest.get(TEST_URL).query("q", "v");

        final NextRequest r2 = NextRequest.delete(TEST_URL).query("q", "v").form("k", "v2");
        final NextRequest r3 = NextRequest.post(TEST_URL).query("q", "v").form("k", "v3");
        final NextRequest r4 = NextRequest.put(TEST_URL).query("q", "v").form("k", "v4");
        final NextRequest r5 = NextRequest.post(TEST_URL).query("q", "v");
        final NextRequest r6 = NextRequest.put(TEST_URL).query("q", "v");
        final NextRequest r7 = NextRequest.post(TEST_URL).query("q", "v").body("hello".getBytes());
        final NextRequest r8 = NextRequest.put(TEST_URL).query("q", "v").body("hello".getBytes());
        try {
            isNull(r0.getRequestBody());
            isNull(r1.getRequestBody());
            isNull(r2.getRequestBody());
            notNull(r3.getRequestBody());
            notNull(r4.getRequestBody());
            notNull(r5.getRequestBody());
            notNull(r6.getRequestBody());
            notNull(r7.getRequestBody());
            isEquals("hello".getBytes().length, r7.getRequestBody().contentLength());
            notNull(r8.getRequestBody());
            isEquals("hello".getBytes().length, r8.getRequestBody().contentLength());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
