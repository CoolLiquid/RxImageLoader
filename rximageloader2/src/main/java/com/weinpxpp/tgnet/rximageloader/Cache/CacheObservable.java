package com.weinpxpp.tgnet.rximageloader.Cache;

import com.weinpxpp.tgnet.rximageloader.Bean.ImageBean;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tgnet on 2017/11/29.
 */

public abstract class CacheObservable {

    public Observable<ImageBean> getImage(final String url) {
        return Observable.create(new ObservableOnSubscribe<ImageBean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<ImageBean> emit) throws Exception {
                if (!emit.isDisposed()) {
                    ImageBean imageBean = getDataFromCache(url);
                    emit.onNext(imageBean);
                    emit.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public abstract ImageBean getDataFromCache(String url);

    public abstract void putDataToCahce(ImageBean imagebean);
}
