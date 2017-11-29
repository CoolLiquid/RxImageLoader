package com.weinpx.tgnet.rximageloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.weinpxpp.tgnet.rximageloader.Loader.RxImageLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        RxImageLoader.with(this)
                .load("http://img3.imgtn.bdimg.com/it/u=1524823793,944673388&fm=11&gp=0.jpg")
                .into(imageView);

    }
}
