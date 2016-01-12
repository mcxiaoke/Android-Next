package com.mcxiaoke.next.samples.http;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.gson.reflect.TypeToken;
import com.mcxiaoke.next.http.HttpMethod;
import com.mcxiaoke.next.http.HttpQueue;
import com.mcxiaoke.next.http.NextRequest;
import com.mcxiaoke.next.http.callback.GsonCallback;
import com.mcxiaoke.next.http.callback.StringCallback;
import com.mcxiaoke.next.samples.BaseActivity;
import com.mcxiaoke.next.samples.BuildConfig;
import com.mcxiaoke.next.samples.R;
import com.mcxiaoke.next.samples.SampleUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * User: mcxiaoke
 * Date: 14-5-30
 * Time: 17:19
 */
public class NextClientSamples extends BaseActivity {
    public static final String TAG = NextClientSamples.class.getSimpleName();
    private static final boolean DEBUG = true;
    private HttpQueue mHttpQueue;

    private TextView mLogView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_text);
        mLogView = (TextView) findViewById(R.id.log_view);
        mHttpQueue = new HttpQueue();
        mHttpQueue.setDebug(true);
        testPostForm();
        testGet();
        testGetList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHttpQueue.cancelAll(this);
    }

    private void testGet() {
        final String url = "https://api.douban.com/v2/user/1000001";
        final NextRequest request = new NextRequest(HttpMethod.GET, url)
                .debug(true)
                .query("platform", "Android")
                .query("udid", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .form("douban", "yes no")
                .query("version", "6")
                .form("form-key", "form-value")
                .form("hello", "world")
                .query("app_version", "1.5.2");

        mHttpQueue.add(request, new GsonCallback<User>(User.class) {

            @Override
            public boolean handleException(final Throwable error) {
                mLogView.append(TAG + " testGet http response error: " + error);
                return true;
            }

            @Override
            public void handleResponse(final User response) {
                mLogView.append(TAG + " testGet http response content: "
                        + response + "\n");
            }
        }, this);

    }

    private void testGetList() {
        final String url = "https://api.douban.com/v2/lifestream/user_timeline/1000001";
        final NextRequest request = new NextRequest(HttpMethod.GET, url)
                .debug(true)
                .query("platform", "Android")
                .query("udid", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .form("douban", "yes no")
                .query("version", "6")
                .form("form-key", "form-value")
                .form("hello", "world")
                .query("app_version", "1.5.2");

        final Type type = new TypeToken<List<StatusItem>>() {
        }.getType();
        mHttpQueue.add(request, new GsonCallback<List<StatusItem>>(type) {
            @Override
            public boolean handleException(final Throwable error) {
                mLogView.append(TAG + " testGetList http response error: " + error);
                return true;
            }

            @Override
            public void handleResponse(final List<StatusItem> response) {
                mLogView.append(TAG + " testGetList http response content: "
                        + response + "\n");
            }
        }, this);
    }

    private void testPostForm() {
        final String url = "https://moment.douban.com/api/post/114309/like";
        final NextRequest request = new NextRequest(HttpMethod.POST, url)
                .debug(true)
                .header("X-UDID", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .query("platform", "Android")
                .query("udid", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .query("version", "6")
                .form("form-key", "form value")
                .form("hello", "world")
                .query("app_version", "1.2.3");
        mHttpQueue.add(request, new StringCallback() {
            @Override
            public boolean handleException(final Throwable error) {
                mLogView.append(TAG + " testPostForm http response error: " + error);
                return true;
            }

            @Override
            public void handleResponse(final String response) {
                mLogView.append(TAG + " testPostForm http response content: "
                        + SampleUtils.prettyPrintJson(response) + "\n");
            }
        }, this);
    }

    private void testPostJson() throws JSONException {
        final String url = "https://api.github.com/gists";
        final NextRequest request = new NextRequest(HttpMethod.POST, url)
                .debug(true)
                .header("X-UDID", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .query("platform", "Android")
                .query("udid", "a0b609c99ca4bfdcef3d03a234d78d253d25e924")
                .query("version", "6")
                .form("form-key", "form value")
                .form("hello", "world")
                .query("app_version", "1.2.3");
        request.userAgent("Samples test " + BuildConfig.APPLICATION_ID
                + "/" + BuildConfig.VERSION_NAME);
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
        request.body(json.toString().getBytes());
        mHttpQueue.add(request, new StringCallback() {
            @Override
            public boolean handleException(final Throwable error) {
                mLogView.append(TAG + " testPostJson http response error: " + error);
                return true;
            }

            @Override
            public void handleResponse(final String response) {
                mLogView.append(TAG + " testPostJson http response content: "
                        + SampleUtils.prettyPrintJson(response) + "\n");
            }
        }, this);

    }
}
