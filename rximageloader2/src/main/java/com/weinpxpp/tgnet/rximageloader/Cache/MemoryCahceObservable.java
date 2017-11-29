package com.weinpxpp.tgnet.rximageloader.Cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.weinpxpp.tgnet.rximageloader.Bean.ImageBean;

/**
 * Created by tgnet on 2017/11/29.
 */

public class MemoryCahceObservable extends CacheObservable {
    private int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private int cacheSize = maxMemory / 4;
    private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
        }
    };

    @Override
    public ImageBean getDataFromCache(String url) {
        Bitmap bitmap = mLruCache.get(url);
        return new ImageBean(bitmap, url);
    }

    @Override
    public void putDataToCahce(ImageBean imagebean) {
        mLruCache.put(imagebean.getUrl(), imagebean.getBitmap());
    }
}
