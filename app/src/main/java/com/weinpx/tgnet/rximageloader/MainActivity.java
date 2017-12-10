package com.weinpx.tgnet.rximageloader;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.utils.FileHelper;
import com.weinpxpp.tgnet.rximageloader.Loader.RxImageLoader;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FileHelper.init(getApplication());//初始化FileHelper
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        RxImageLoader.with(this)
                .load("http://img.zcool.cn/community/017274582000cea84a0e282b576a32.jpg")
                .into(imageView);
    }
}
