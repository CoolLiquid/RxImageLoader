package com.weinpxpp.tgnet.rximageloader.Loader;

import android.content.Context;
import android.widget.ImageView;

import com.weinpxpp.tgnet.rximageloader.Bean.ImageBean;
import com.weinpxpp.tgnet.rximageloader.Creator.RequestCreator;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by tgnet on 2017/11/29.
 */

public class RxImageLoader {

    public static class Builder {

        private Context mContext;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public RxImageLoader build() {
            return new RxImageLoader(this);
        }
    }

    static RxImageLoader singleton;
    private String url;
    private RequestCreator requestCreator;

    private RxImageLoader(Builder builder) {
        requestCreator = new RequestCreator(builder.mContext);
    }

    public static RxImageLoader with(Context context) {
        if (singleton == null) {
            synchronized (RxImageLoader.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    public RxImageLoader load(String url) {
        this.url = url;
        return singleton;
    }

    public void into(final ImageView imageView) {
        Observable.concat(
                requestCreator.getImageFromMemory(url),
                requestCreator.getImageFromDisk(url),
                requestCreator.getImageFromNetwork(url)
        ).first(new ImageBean(null, url))
                .toObservable()
                .subscribe(new Observer<ImageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageBean imageBean) {
                        if (imageBean.getBitmap() != null) {
                            imageView.setImageBitmap(imageBean.getBitmap());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }






}
