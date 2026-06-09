package com.github.tvbox.osc.util;

import com.github.catvod.crawler.SpiderDebug;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.util.parser.SuperParse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Proxy {

    public static Object[] proxy(Map<String, String> params) {
        try {
            String what = params.get("go");
            if (what == null) return null;
            if (what.equals("live")) {
                return itv(params);
            } else if (what.equals("bom")) {
                return removeBOMFromM3U8(params);
            } else if (what.equals("SuperParse")) {
                return SuperParse.loadHtml(params.get("flag"), params.get("url"));
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static Object[] itv(Map<String, String> params) throws Exception {
        try {
            Object[] result = new Object[3];
            String url = params.get("url");
            String type = params.get("type");
            url = URLDecoder.decode(url, "UTF-8");

            OkHttpClient client = OkGoHelper.ItvClient;
            if (client == null) client = OkGoHelper.getDefaultClient();
            if (type == null) throw new IllegalArgumentException("type is null");
            if (type.equals("m3u8")) {
                String redirectUrl = getRedirectedUrl(url);
                Request request = new Request.Builder().url(redirectUrl).build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String respContent = response.body().string();
                        String m3u8Content = processM3u8Content(respContent, redirectUrl);
                        result[0] = 200;
                        result[1] = "application/vnd.apple.mpegurl";
                        result[2] = new ByteArrayInputStream(m3u8Content.getBytes());
                    } else {
                        throw new IOException("M3U8 Request failed with code: " + response.code());
                    }
                }
            } else if (type.equals("ts")) {
                Request request = new Request.Builder().url(url).build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        result[0] = 200;
                        result[1] = "video/mp2t";
                        result[2] = new ByteArrayInputStream(response.body().bytes());
                    } else {
                        throw new IOException("TS Request failed with code: " + response.code());
                    }
                }
            } else {
                throw new IllegalArgumentException("Invalid type: " + type);
            }
            return result;
        } catch (Exception e) {
            SpiderDebug.log(e);
            return null;
        }
    }

    public static Object[] removeBOMFromM3U8(Map<String, String> params) throws Exception {
        try {
            Object[] result = new Object[3];
            String url = params.get("url");
            url = URLDecoder.decode(url, "UTF-8");

            OkHttpClient client = OkGoHelper.ItvClient;
            if (client == null) client = OkGoHelper.getDefaultClient();
            String redirectUrl = getRedirectedUrl(url);

            Request request = new Request.Builder().url(redirectUrl).build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String m3u8Content = response.body().string();
                    if (m3u8Content.startsWith("\ufeff")) {
                        m3u8Content = m3u8Content.substring(1);
                    }
                    result[0] = 200;
                    result[1] = "application/vnd.apple.mpegurl";
                    result[2] = new ByteArrayInputStream(m3u8Content.getBytes());
                } else {
                    throw new IOException("M3U8 Request failed with code: " + response.code());
                }
            }
            return result;
        } catch (Exception e) {
            SpiderDebug.log(e);
            return null;
        }
    }

    private static String processM3u8Content(String m3u8Content, String m3u8Url) {
        String[] m3u8Lines = m3u8Content.trim().split("\n");
        StringBuilder processedM3u8 = new StringBuilder();

        for (String line : m3u8Lines) {
            if (line.startsWith("#")) {
                processedM3u8.append(line).append("\n");
            } else {
                processedM3u8.append(joinUrl(m3u8Url, line)).append("\n");
            }
        }
        return processedM3u8.toString().replace("\\n\\n", "\n");
    }

    private static String joinUrl(String base, String url) {
        if (base == null) base = "";
        if (url == null) url = "";
        try {
            URI baseUri = new URI(base.trim());
            url = url.trim();
            URI urlUri = new URI(url);
            String proxyUrl = ControlManager.get().getAddress(true) + "proxy?go=live&type=ts&url=";
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return proxyUrl + URLEncoder.encode(urlUri.toString(), "UTF-8");
            } else if (url.startsWith("://")) {
                return proxyUrl + URLEncoder.encode(new URI(baseUri.getScheme() + url).toString(), "UTF-8");
            } else if (url.startsWith("//")) {
                return proxyUrl + URLEncoder.encode(new URI(baseUri.getScheme() + ":" + url).toString(), "UTF-8");
            } else {
                URI resolvedUri = baseUri.resolve(url);
                return proxyUrl + URLEncoder.encode(resolvedUri.toString(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getRedirectedUrl(String url) throws IOException {
        OkHttpClient client = OkGoHelper.getNoRedirectClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isRedirect()) {
                return response.header("Location");
            }
            return url;
        }
    }
}
