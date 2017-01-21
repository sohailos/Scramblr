package com.example.osamasohail.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageOptions extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_options);
        Bundle bundle = getIntent().getExtras();
        Uri uri = bundle.getParcelable("uri");
        Log.e("URI", uri.toString());
        Bitmap bm = null;
        try {
            bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bm);
    }
}
