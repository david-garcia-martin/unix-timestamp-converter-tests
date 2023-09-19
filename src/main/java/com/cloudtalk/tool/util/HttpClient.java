package com.cloudtalk.tool.util;

import com.cloudtalk.tool.model.ConverterRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static javax.xml.transform.OutputKeys.MEDIA_TYPE;

@Slf4j
public class HttpClient {
    private static OkHttpClient client;
    private static final int TIMEOUT = 30;

    public static Response doGetRequest(String url, ConverterRequest converterRequest) {
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(url))
                .newBuilder()
                .addQueryParameter("cached", converterRequest.getCached())
                .addQueryParameter("s", converterRequest.getS())
                .build();
        Request request = new Request.Builder().url(httpUrl).build();
        return doRequest(request);
    }

    @SneakyThrows
    public static Response doGetRequestWithoutParameters(String url) {
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(url))
                .newBuilder()
                .build();
        Request request = new Request.Builder().url(httpUrl).build();
        return doRequest(request);
    }

    @SneakyThrows
    public static Response doRequest(String url, String method, String body) {
        RequestBody requestBody = RequestBody.create(body, MediaType.parse(MEDIA_TYPE));
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(url))
                .newBuilder()
                .build();
        Request request = new Request.Builder().url(httpUrl).method(method, requestBody).build();
        return doRequest(request);
    }

    @SneakyThrows
    private static Response doRequest(Request request) {
        log.debug("Sending request with \nmethod type: '{}'\nheaders: {} \nendpoint: {}",
                request.method(), request.headers(), request.url());
        log.trace("------- Request start -------");
        try {
            return getOrCreateClient().newCall(request).execute();
        } catch (IOException e) {
            log.error("An error occurred while executing HTTP request ", e);
            throw new Exception(e);
        } finally {
            log.trace("------- Request end -------");
        }
    }

    private static OkHttpClient getOrCreateClient() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
                    .build();
        }

        return client;
    }
}
