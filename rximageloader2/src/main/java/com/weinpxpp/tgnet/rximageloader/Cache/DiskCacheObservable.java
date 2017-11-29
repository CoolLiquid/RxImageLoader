package com.weinpxpp.tgnet.rximageloader.Cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.jakewharton.disklrucache.DiskLruCache;
import com.weinpxpp.tgnet.rximageloader.Bean.ImageBean;
import com.weinpxpp.tgnet.rximageloader.Utils.DiskCacheUtils;
import com.weinpxpp.tgnet.rximageloader.Utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tgnet on 2017/11/29.
 */

public class DiskCacheObservable extends CacheObservable {

    private Context mContext;
    private DiskLruCache mDiskLruCache;
    private final int maxSize = 10 * 1024 * 1024;

    public DiskCacheObservable(Context mContext) {
        this.mContext = mContext;
        initDiskLruCache();
    }

    private void initDiskLruCache() {
        File cacheDir = DiskCacheUtils.getDiskCacheDir(mContext, "our_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        int versionCode = DiskCacheUtils.getAppVersion(mContext);
        try {
            //这里需要注意参数二：缓存版本号，只要不同版本号，缓存都会被清除，重新使用新的
            mDiskLruCache = DiskLruCache.open(cacheDir, versionCode, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ImageBean getDataFromCache(String url) {
        LogUtils.log("DiskCacheObservable--getDataFromCache");
        Bitmap bitmap = getDataFromDiskLruCache(url);
        return new ImageBean(bitmap, url);
    }


    @Override
    public void putDataToCahce(final ImageBean imagebean) {
        LogUtils.log("DiskCacheObservable--putDataToCahce");
        Observable.concat(new Observable<ObservableSource<?>>() {
            @Override
            protected void subscribeActual(Observer<? super ObservableSource<?>> observer) {
                putDataToDiskLruCache(imagebean);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private Bitmap getDataFromDiskLruCache(String url) {
        Bitmap bitmap = null;
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        try {
            final String key = DiskCacheUtils.getMD5String(url);
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot != null) {
                fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
            }
            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeStream(fileInputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


    private void putDataToDiskLruCache(ImageBean imageBean) {
        try {
            String key = DiskCacheUtils.getMD5String(imageBean.getUrl());
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                OutputStream out = editor.newOutputStream(0);
                Bitmap bitmap = imageBean.getBitmap();
                boolean isSucess = false;
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    isSucess = true;
                } else {
                    isSucess = downLoadUrlToStream(imageBean.getUrl(), out);
                }
                if (isSucess) {
                    editor.commit();
                } else {
                    editor.abort();
                }
            }
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean downLoadUrlToStream(String url, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL new_url = new URL(url);
            urlConnection = (HttpURLConnection) new_url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
