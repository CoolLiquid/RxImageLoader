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
                .load("http://reactivex.io/documentation/operators/images/concat.png")
                .into(imageView);

    }
}
