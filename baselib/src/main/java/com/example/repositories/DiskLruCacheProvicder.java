package com.example.repositories;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.example.utils.FileHelper;
import com.example.utils.StringUtil;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by fan-gk on 2017/4/24.
 */


public class DiskLruCacheProvicder extends CacheStoreProvider {

    public class DiskLruCacheStore extends CacheStore {
        private static final int APP_VERSION = 2;
        private static final int INDEX_VALUE = 0;
        private static final int INDEX_TIME = 1;
        public final DiskLruCache diskLruCache;

        public DiskLruCacheStore(String directory, int maxSize) throws IOException {
            File file = new File(FileHelper.getCacheDir(), directory);
            if (!file.exists()) {
                file.mkdirs();
            }
            diskLruCache = DiskLruCache.open(file, APP_VERSION, 1, maxSize);
        }

        @Override
        protected void realSet(String key, CacheData data) {
            try {
                DiskLruCache.Editor editor = diskLruCache.edit(key);
                editor.set(INDEX_VALUE, data.value == null ? StringUtil.EMPTY : data.value);
                editor.set(INDEX_TIME, data.millisTime == null ? StringUtil.EMPTY : String.valueOf(data.millisTime.longValue()));
                editor.commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected CacheData realGet(String key) {
            try {
                DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
                if (snapshot != null) {
                    String timeString = snapshot.getString(INDEX_TIME);
                    Long time = StringUtil.isNullOrEmpty(timeString) ? null : Long.parseLong(timeString);
                    String value = snapshot.getString(INDEX_VALUE);
                    return new CacheData(time, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void realDel(String key) {
            try {
                diskLruCache.remove(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void realSaveBitmap(@NonNull String key, @NonNull Bitmap bitmap) {
            DiskLruCache.Editor editor = null;
            try {
                editor = diskLruCache.edit(key);
                if (editor != null) {
                    OutputStream out = editor.newOutputStream(0);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    editor.commit();
                    diskLruCache.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Bitmap realGetBitmap(String key) {
            DiskLruCache.Snapshot snapshot = null;
            FileInputStream fileInputStream = null;
            FileDescriptor fileDescriptor = null;
            Bitmap bitmap = null;
            try {
                snapshot = diskLruCache.get(key);
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
    }

    private final String directory;
    private final int maxSize;
    private DiskLruCacheStore store = null;

    public DiskLruCacheProvicder(String directory, int maxSize) {
        this.directory = directory;
        this.maxSize = maxSize;
    }

    @Override
    public synchronized CacheStore getCacheStore() {
        if (store == null) {
            try {
                store = new DiskLruCacheStore(directory, maxSize);
            } catch (Exception e) {
                e.printStackTrace();
                return CacheStore.None;
            }
        }
        return store;
    }
}

