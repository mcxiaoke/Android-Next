package com.mcxiaoke.next.samples.http;

import android.os.Bundle;
import android.util.Log;
import com.mcxiaoke.next.http.HttpConsts;
import com.mcxiaoke.next.http.HttpMethod;
import com.mcxiaoke.next.http.NextClient;
import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.http.NextResponse;
import com.mcxiaoke.next.samples.BaseActivity;
import com.mcxiaoke.next.samples.SampleUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 17:19
 */
public class NextClientSamples extends BaseActivity {
    public static final String TAG = NextClientSamples.class.getSimpleName();
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread() {
            @Override
            public void run() {
                try {
                    testGet();
//                    testPost();
                    testPostJson();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    private void testGet() throws IOException {
        final String url = "https://api.douban.com/v2/user/1000001";
        final NextRequest request = new NextRequest(HttpMethod.GET, url)
                .tag(TAG).debug(true)
                .query("platform", "Android")
                .query("udid", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .form("douban", "yes")
                .query("app_version", "1.5.2");
        final NextClient client = new NextClient().setDebug(true);
//

        final NextResponse response = client.execute(request);
        // get body as string
        Log.v(TAG, "http response content: "
                + SampleUtils.prettyPrintJson(response.string()));
    }

    private void testPostForm() throws IOException {
        final String url = "https://moment.douban.com/api/post/114309/like";
        final NextRequest request = new NextRequest(HttpMethod.POST, url)
                .tag(TAG).debug(true)
                .charset(HttpConsts.CHARSET_UTF8)
                .header("X-UDID", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .query("platform", "Android")
                .query("udid", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .form("version", "6")
                .query("app_version", "1.2.3");
        final NextClient client = new NextClient();
        final NextResponse response = client.execute(request);
        // get body as string
        Log.v(TAG, "http response content: "
                + SampleUtils.prettyPrintJson(response.string()));
    }

    private void testPostJson() throws JSONException, IOException {
        final String url = "https://api.github.com/gists";
        final NextRequest request = new NextRequest(HttpMethod.POST, url)
                .tag(TAG).debug(true)
                .charset(HttpConsts.CHARSET_UTF8)
                .header("X-UDID", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .query("platform", "Android")
                .query("udid", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .form("version", "6")
                .query("app_version", "1.2.3");
        JSONObject file1 = new JSONObject();
        file1.put("content", "gsgdsgsdgsdgsdgdsg gsdgjdslgk根深蒂固送到公司的");
        JSONObject file2 = new JSONObject();
        file2.put("content", "421414gsgdsgsdgsdgsdgdsg gsfdsfsddgjdslgk根深蒂固送到公司的");
        JSONObject files = new JSONObject();
        files.put("file1.txt", file1);
        files.put("file2.md", file2);
        JSONObject json = new JSONObject();
        json.put("description", "this is a gist for http post test");
        json.put("public", true);
        json.put("files", files);
        Log.v(TAG, "json string: " + json.toString());
        request.body(json.toString());
        final NextClient client = new NextClient();
        final NextResponse response = client.execute(request);
        // get body as string
        Log.v(TAG, "http response content: "
                + SampleUtils.prettyPrintJson(response.string()));
    }
}
