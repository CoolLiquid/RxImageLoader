package com.weinpxpp.tgnet.rximageloader.Creator;

import android.content.Context;
import android.graphics.Bitmap;

import com.weinpxpp.tgnet.rximageloader.Bean.ImageBean;
import com.weinpxpp.tgnet.rximageloader.Cache.CacheObservable;
import com.weinpxpp.tgnet.rximageloader.Cache.DiskCacheObservable;
import com.weinpxpp.tgnet.rximageloader.Cache.MemoryCahceObservable;
import com.weinpxpp.tgnet.rximageloader.Cache.NetworkCacheObservable;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

/**
 * Created by tgnet on 2017/11/29.
 */

public class RequestCreator {

    public CacheObservable memoryCahceObservable;
    public CacheObservable networkCacheObservable;
    public CacheObservable diskCacheObservable;

    public RequestCreator(Context context) {
        memoryCahceObservable = new MemoryCahceObservable();
        networkCacheObservable = new NetworkCacheObservable();
        diskCacheObservable = new DiskCacheObservable(context);
    }

    public Observable<ImageBean> getImageFromMemory(String url) {
        return memoryCahceObservable
                .getImage(url)
                .filter(new Predicate<ImageBean>() {
                    @Override
                    public boolean test(@NonNull ImageBean imageBean) throws Exception {
                        Bitmap bitmap = imageBean.getBitmap();
                        return bitmap != null;
                    }
                });
    }

    public Observable<ImageBean> getImageFromDisk(String url) {
        return diskCacheObservable
                .getImage(url)
                .filter(new Predicate<ImageBean>() {
                    @Override
                    public boolean test(@NonNull ImageBean imageBean) throws Exception {
                        Bitmap bitmap = imageBean.getBitmap();
                        return bitmap != null;
                    }
                }).doOnNext(new Consumer<ImageBean>() {
                    @Override
                    public void accept(@NonNull ImageBean imageBean) throws Exception {
                        memoryCahceObservable.putDataToCahce(imageBean);
                    }
                });
    }

    public Observable<ImageBean> getImageFromNetwork(String url) {
        return networkCacheObservable
                .getImage(url)
                .filter(new Predicate<ImageBean>() {
                    @Override
                    public boolean test(@NonNull ImageBean imageBean) throws Exception {
                        Bitmap bitmap = imageBean.getBitmap();
                        return bitmap != null;
                    }
                }).doOnNext(new Consumer<ImageBean>() {
                    @Override
                    public void accept(@NonNull ImageBean imageBean) throws Exception {
                        diskCacheObservable.putDataToCahce(imageBean);
                        memoryCahceObservable.putDataToCahce(imageBean);
                    }
                });
    }

    public Observable<ImageBean> getImage(String url) {
        Observable<ImageBean> observable = Observable.concat(
                getImageFromMemory(url),
                getImageFromDisk(url),
                getImageFromNetwork(url)
        ).first(new ImageBean(null, url))
                .toObservable();
        return observable;
    }


}
