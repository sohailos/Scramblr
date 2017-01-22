package com.example.osamasohail.myapplication;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.support.v7.app.*;
import android.widget.EditText;
import android.text.InputType;
import android.content.DialogInterface;

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

    public void scrambleThis(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Password");
        createDialog(builder);
    }

    public void unscrambleThis(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");
        createDialog(builder);
    }

    public void createDialog(AlertDialog.Builder builder) {
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setCancelable(false);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
