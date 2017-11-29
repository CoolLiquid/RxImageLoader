package com.weinpxpp.tgnet.rximageloader.Bean;

import android.graphics.Bitmap;

/**
 * Created by weinp
 * on 2017/11/29.
 */

public class ImageBean {
    private String url;
    private Bitmap bitmap;

    public ImageBean(Bitmap bitmap, String url) {
        this.bitmap = bitmap;
        this.url = url;
    }

   public Bitmap getBitmap() {
        return bitmap;
    }

   public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
