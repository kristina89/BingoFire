package com.skl.bingofire.utils;

/**
 * Created by tymaks
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;


public class ImageUtil {

    public static final int MAX_CASH_SIZE = 10485760;   //10 Mb
    private final int MAX_IMAGES_COUNT = 200;

    public static final String TAG = ImageUtil.class.getSimpleName();
    public static final int N_THREADS = 3;
    private static ImageUtil instance;
    private RequestQueue requestQueue;
    private Context context;
    private ImageLoader imageLoader;

    private ImageUtil(final Context context) {
        this.context = context;
        this.requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<>(MAX_IMAGES_COUNT);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }


                });
    }

    public static ImageUtil getInstance(Context context) {
        if (instance == null) {
            instance = new ImageUtil(context);
        }

        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            Cache cache = new DiskBasedCache(ImagesDir.getTempImagesDir(context), MAX_CASH_SIZE);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network, N_THREADS);
            requestQueue.start();
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}
