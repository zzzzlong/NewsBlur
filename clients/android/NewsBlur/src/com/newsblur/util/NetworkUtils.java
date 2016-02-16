package com.newsblur.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import okio.BufferedSink;
import okio.Okio;

public class NetworkUtils {

    private static OkHttpClient ImageFetchHttpClient;

    static {
        ImageFetchHttpClient = new OkHttpClient.Builder()
                               .connectTimeout(AppConstants.IMAGE_PREFETCH_CONN_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                               .readTimeout(AppConstants.IMAGE_PREFETCH_READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                               .followSslRedirects(true)
                               .build();
    }

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return (netInfo != null && netInfo.isConnected());
	}

    public static long loadURL(URL url, File file) throws IOException {
        long bytesRead = 0;
        try {
            Request.Builder requestBuilder = new Request.Builder().url(url);
            Response response = ImageFetchHttpClient.newCall(requestBuilder.build()).execute();
            if (response.isSuccessful()) {
                BufferedSink sink = Okio.buffer(Okio.sink(file));
                bytesRead = sink.writeAll(response.body().source());
                sink.close();
            }
        } catch (Throwable t) {
            // a huge number of things could go wrong fetching and storing an image. don't spam logs with them
        }
        return bytesRead;
    }
}
