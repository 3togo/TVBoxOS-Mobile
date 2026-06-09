package com.github.tvbox.osc.util;

import static okhttp3.ConnectionSpec.CLEARTEXT;
import static okhttp3.ConnectionSpec.COMPATIBLE_TLS;
import static okhttp3.ConnectionSpec.MODERN_TLS;
import static okhttp3.ConnectionSpec.RESTRICTED_TLS;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.picasso.MyOkhttpDownLoader;
import com.github.tvbox.osc.util.urlhttp.BrotliInterceptor;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.ConnectionSpec;
import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.dnsoverhttps.DnsOverHttps;
import okhttp3.internal.Util;
import okhttp3.internal.Version;
import xyz.doikki.videoplayer.exo.ExoMediaSourceHelper;

public class OkGoHelper {
    public static final long DEFAULT_MILLISECONDS = 10000;      //默认的超时时间

    // 内置doh json
    private static final String dnsConfigJson = "["
            + "{\"name\": \"腾讯\", \"url\": \"https://doh.pub/dns-query\"},"
            + "{\"name\": \"阿里\", \"url\": \"https://dns.alidns.com/dns-query\"},"
            + "{\"name\": \"360\", \"url\": \"https://doh.360.cn/dns-query\"}"
            + "]";

    static OkHttpClient ItvClient = null;

    static void initExoOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkExoPlayer");

        if (Hawk.get(HawkConfig.DEBUG_OPEN, false)) {
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            loggingInterceptor.setColorLevel(Level.INFO);
        } else {
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.NONE);
            loggingInterceptor.setColorLevel(Level.OFF);
        }
        builder.addInterceptor(loggingInterceptor);
        builder.connectionSpecs(getConnectionSpec());
        builder.addInterceptor(new BrotliInterceptor());
        builder.retryOnConnectionFailure(true);
        builder.followRedirects(true);
        builder.followSslRedirects(true);

        try {
            setOkHttpSsl(builder);
        } catch (Throwable th) {
            th.printStackTrace();
        }

        builder.dns(new CustomDns(dnsOverHttps));
        ItvClient = builder.build();
        ExoMediaSourceHelper.getInstance(App.getInstance()).setOkClient(ItvClient);
    }

    public static DnsOverHttps dnsOverHttps = null;

    public static ArrayList<String> dnsHttpsList = new ArrayList<>();

    public static Map<String, String> myHosts = null;

    public static String getDohUrl(int type) {
        String json = Hawk.get(HawkConfig.DOH_JSON, "");
        if (json.isEmpty()) json = dnsConfigJson;
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        if (type >= 1 && type < dnsHttpsList.size()) {
            JsonObject dnsConfig = jsonArray.get(type - 1).getAsJsonObject();
            return dnsConfig.get("url").getAsString();
        }
        return "";
    }

    public static void setDnsList() {
        dnsHttpsList.clear();
        String json = Hawk.get(HawkConfig.DOH_JSON, "");
        if (json.isEmpty()) json = dnsConfigJson;
        JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
        dnsHttpsList.add("关闭");
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject dnsConfig = jsonArray.get(i).getAsJsonObject();
            String name = dnsConfig.has("name") ? dnsConfig.get("name").getAsString() : "Unknown Name";
            dnsHttpsList.add(name);
        }
        if (Hawk.get(HawkConfig.DOH_URL, 0) + 1 > dnsHttpsList.size()) Hawk.put(HawkConfig.DOH_URL, 0);
    }

    private static List<InetAddress> DohIps(JsonArray ips) {
        List<InetAddress> inetAddresses = new ArrayList<>();
        if (ips != null) {
            for (int j = 0; j < ips.size(); j++) {
                try {
                    InetAddress inetAddress = InetAddress.getByName(ips.get(j).getAsString());
                    inetAddresses.add(inetAddress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return inetAddresses;
    }

    static void initDnsOverHttps() {
        Integer dohSelector = Hawk.get(HawkConfig.DOH_URL, 0);
        JsonArray ips = null;
        try {
            dnsHttpsList.add("关闭");
            String json = Hawk.get(HawkConfig.DOH_JSON, "");
            if (json.isEmpty()) json = dnsConfigJson;
            JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
            if (dohSelector + 1 > jsonArray.size()) Hawk.put(HawkConfig.DOH_URL, 0);
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject dnsConfig = jsonArray.get(i).getAsJsonObject();
                String name = dnsConfig.has("name") ? dnsConfig.get("name").getAsString() : "Unknown Name";
                dnsHttpsList.add(name);
                if (dohSelector == (i + 1)) ips = dnsConfig.has("ips") ? dnsConfig.getAsJsonArray("ips") : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkExoPlayer");
        if (Hawk.get(HawkConfig.DEBUG_OPEN, false)) {
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            loggingInterceptor.setColorLevel(Level.INFO);
        } else {
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.NONE);
            loggingInterceptor.setColorLevel(Level.OFF);
        }
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(new BrotliInterceptor());
        try {
            setOkHttpSsl(builder);
        } catch (Throwable th) {
            th.printStackTrace();
        }
        builder.connectionSpecs(getConnectionSpec());
        builder.cache(new Cache(new File(App.getInstance().getCacheDir().getAbsolutePath(), "dohcache"), 100 * 1024 * 1024));
        OkHttpClient dohClient = builder.build();
        String dohUrl = getDohUrl(Hawk.get(HawkConfig.DOH_URL, 0));
        dnsOverHttps = new DnsOverHttps.Builder().client(dohClient).url(dohUrl.isEmpty() ? null : HttpUrl.get(dohUrl)).bootstrapDnsHosts((ips != null && !dohUrl.equals("https://doh.pub/dns-query")) ? DohIps(ips) : null).build();
    }

    // 自定义 DNS 解析器
    static class CustomDns implements Dns {
        private final String excludeIps = "2409:8087:6c02:14:100::14,2409:8087:6c02:14:100::18,39.134.108.253,39.134.108.245";
        private final DnsOverHttps mDnsOverHttps;

        public CustomDns(DnsOverHttps dnsOverHttps) {
            this.mDnsOverHttps = dnsOverHttps;
        }

        @NonNull
        @Override
        public List<InetAddress> lookup(@NonNull String hostname) throws UnknownHostException {
            if (myHosts == null) {
                myHosts = ApiConfig.get().getMyHost();
            }
            if (myHosts != null && !myHosts.isEmpty() && myHosts.containsKey(hostname)) {
                hostname = myHosts.get(hostname);
            }
            if (hostname == null) throw new UnknownHostException("hostname is null");
            if (isValidIpAddress(hostname)) {
                return Collections.singletonList(InetAddress.getByName(hostname));
            } else {
                return mDnsOverHttps.lookup(hostname);
            }
        }

        private List<InetAddress> getAllByName(String host) {
            try {
                InetAddress[] allAddresses = InetAddress.getAllByName(host);
                if (excludeIps.isEmpty()) return Arrays.asList(allAddresses);
                List<InetAddress> validAddresses = new ArrayList<>();
                Set<String> excludeIpsSet = new HashSet<>();
                for (String ip : excludeIps.split(",")) {
                    excludeIpsSet.add(ip.trim());
                }
                for (InetAddress address : allAddresses) {
                    if (!excludeIpsSet.contains(address.getHostAddress())) {
                        validAddresses.add(address);
                    }
                }
                return validAddresses;
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }

        private boolean isValidIpAddress(String str) {
            if (str.indexOf('.') > 0) return isValidIPv4(str);
            return str.indexOf(':') > 0;
        }

        private boolean isValidIPv4(String str) {
            String[] parts = str.split("\\.");
            if (parts.length != 4) return false;
            for (String part : parts) {
                try {
                    Integer.parseInt(part);
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
    }

    public static List<ConnectionSpec> getConnectionSpec() {
        return Util.immutableList(RESTRICTED_TLS, MODERN_TLS, COMPATIBLE_TLS, CLEARTEXT);
    }

    static OkHttpClient defaultClient = null;
    static OkHttpClient noRedirectClient = null;

    public static OkHttpClient getDefaultClient() {
        return defaultClient;
    }

    public static OkHttpClient getNoRedirectClient() {
        return noRedirectClient;
    }

    public static void init() {
        initDnsOverHttps();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");

        if (Hawk.get(HawkConfig.DEBUG_OPEN, false)) {
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            loggingInterceptor.setColorLevel(Level.INFO);
        } else {
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.NONE);
            loggingInterceptor.setColorLevel(Level.OFF);
        }

        //builder.retryOnConnectionFailure(false);
        builder.addInterceptor(loggingInterceptor);
        builder.connectionSpecs(getConnectionSpec());
        builder.addInterceptor(new BrotliInterceptor());
        builder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .dns(dnsOverHttps);
        try {
            setOkHttpSsl(builder);
        } catch (Throwable th) {
            th.printStackTrace();
        }

        HttpHeaders.setUserAgent(Version.userAgent());

        OkHttpClient okHttpClient = builder.build();
        OkGo.getInstance().setOkHttpClient(okHttpClient);

        defaultClient = okHttpClient;

        builder.followRedirects(false);
        builder.followSslRedirects(false);
        noRedirectClient = builder.build();

        initExoOkHttpClient();
        initPicasso(okHttpClient);
    }

    static void initPicasso(OkHttpClient client) {
        client.dispatcher().setMaxRequestsPerHost(32);
        MyOkhttpDownLoader downloader = new MyOkhttpDownLoader(client);
        Picasso picasso = new Picasso.Builder(App.getInstance())
                .downloader(downloader)
                .executor(HeavyTaskUtil.getBigTaskExecutorService())
                .defaultBitmapConfig(Bitmap.Config.RGB_565)
                .build();
        Picasso.setSingletonInstance(picasso);
    }

    private static synchronized void setOkHttpSsl(OkHttpClient.Builder builder) {
        try {
            final SSLSocketFactory sslSocketFactory = new com.github.catvod.net.SSLCompat();
            builder.sslSocketFactory(sslSocketFactory, com.github.catvod.net.SSLCompat.TM);
            builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
