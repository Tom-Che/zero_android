package com.che.acommon.web;


import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.che.acommon.AsyncTaskImpl;
import com.che.acommon.util.FileUtil;
import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by soap on 16/5/31.
 */
public class OkWebRequest {
    private static final String TAG = "common.OkWebRequest";

    private static OkWebRequest self;

    public static OkWebRequest self() {
        if (self == null) {
            self = new OkWebRequest();
        }
        return self;
    }

    private OkHttpClient okHttpClient;
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final MediaType FILE = MediaType.parse("application/octet-stream");
    private Gson gson;

    private Handler handler;

    //private String cookie;

    private OkWebRequest() {

    }

    public void init() {
        okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(30, TimeUnit.SECONDS);

        okHttpClient.setCookieHandler(new CookieManager(new PersistentCookieStore(), CookiePolicy.ACCEPT_ALL));

        gson = new Gson();

        handler = new Handler();

        downloadFileTask = new ConcurrentHashMap<>();
    }

    public <T> void postJson(@NonNull String url, final Object param, final WebResponse<T> webResponse) {
        request(url, RequestBody.create(JSON, gson.toJson(param)), webResponse);
    }

    public <T> void postForm(String url, Map<String, Object> params, @NonNull final WebResponse<T> webResponse) {
        MultipartBuilder builder = new MultipartBuilder();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value instanceof File) {
                builder.addFormDataPart(key, key, RequestBody.create(FILE, (File) value));
            } else {
                builder.addFormDataPart(key, value + "");
            }
        }
        request(url, builder.build(), webResponse);
    }

    private <T> void request(final String url, RequestBody requestBody, @NonNull final WebResponse<T> webResponse) {
        //取消这个URL之前的请求
        okHttpClient.cancel(url);

        Request.Builder builder = new Request.Builder().url(url);
//        if (!Strings.isNullOrEmpty(cookie)) {
//            builder.addHeader("Cookie", cookie);
//        }
        builder.post(requestBody).tag(url);

        try {
            Call call = okHttpClient.newCall(builder.build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.d(TAG, "Failure");
                    if (webResponse != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                webResponse.response(null);
                            }
                        });
                    }
                    e.printStackTrace();
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    String c = response.header("Set-Cookie");
//                    if(!Strings.isNullOrEmpty(c)) {
//                        cookie = response.header("Set-Cookie");
//                    }

                    if (webResponse != null) {
                        final String sr = response.body().string();
                        Log.d(TAG, url + " Success:" + sr);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                T r = null;
                                try {
                                    Type type = ((ParameterizedType) webResponse.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                                    r = gson.fromJson(sr, type);
                                } catch (Exception e) {
                                    Log.e(TAG, "Gson转换错误:" + url + " 返回内容:" + sr);
                                    e.printStackTrace();
                                }
                                webResponse.response(r);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void get(final String url, final WebResponse<T> webResponse) {
        okHttpClient.cancel(url);
        Request.Builder builder = new Request.Builder().url(url);
        builder.get().tag(url);
        try {
            Call call = okHttpClient.newCall(builder.build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.d(TAG, "Failure");
                    if (webResponse != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                webResponse.response(null);
                            }
                        });
                    }
                    e.printStackTrace();
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    if (webResponse != null) {
                        final String sr = response.body().string();
                        Log.d(TAG, url + " Success:" + sr);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                T r = null;
                                try {
                                    Type type = ((ParameterizedType) webResponse.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
                                    r = gson.fromJson(sr, type);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                webResponse.response(r);
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, AsyncTaskImpl> downloadFileTask;

    /**
     * 下载文件
     *
     * @param url
     * @param target
     * @param progressListener
     */
    public void downloadFile(final String url, final File target, final FileDownloadListener progressListener) {
        okHttpClient.cancel(url);
        Request.Builder builder = new Request.Builder().url(url);
        builder.get().tag(url);
        final Call call = okHttpClient.newCall(builder.build());
        AsyncTaskImpl task = new AsyncTaskImpl() {
            @Override
            protected void doInBackground() {
                InputStream is = null;
                FileOutputStream fos = null;
                try {
                    Response response = call.execute();
                    byte[] buf = new byte[2048];
                    int len = 0;
                    is = response.body().byteStream();
                    final long total = response.body().contentLength();
                    int sum = 0;

                    FileUtil.mkdirs(target.getParentFile());
                    if (target.exists()) {
                        target.delete();
                    }
                    target.createNewFile();
                    fos = new FileOutputStream(target);
                    while ((len = is.read(buf)) != -1) {
                        sum += len;
                        fos.write(buf, 0, len);

                        progressListener.onProgress(sum, (int) total);
                    }
                    fos.flush();
                    progressListener.onFinish(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isCancelled()) {
                        progressListener.onFinish(false);
                    }
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        };
        task.execute();
        downloadFileTask.put(url, task);
    }

    /**
     * 取消文件下载
     *
     * @param url
     */
    public void cancelDownloadFile(String url) {
        okHttpClient.cancel(url);
        AsyncTaskImpl task = downloadFileTask.get(url);
        if (task != null) {
            task.cancel();
        }
    }

    public void cancel(String url) {
        okHttpClient.cancel(url);
    }

    public static abstract class WebResponse<T> {
        public abstract void response(T res);
    }

}
