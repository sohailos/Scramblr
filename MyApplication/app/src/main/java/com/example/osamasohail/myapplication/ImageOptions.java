package com.example.osamasohail.myapplication;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import static android.app.PendingIntent.getActivity;

public class ImageOptions extends AppCompatActivity {

    private ImageView imageView;
    Bitmap bm = null;
    int[] teststuff;
    private SuperCoolSpyShit coolShit = new SuperCoolSpyShit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_options);
        Bundle bundle = getIntent().getExtras();
        final Uri uri = bundle.getParcelable("uri");
        Log.e("URI", uri.toString());

        try {
            bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bm);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 23);

    }

    public void scrambleThis(View view) {
        //Log.v("rushB", "cyka blyat");
        Bundle bundle = getIntent().getExtras();
        Uri uri = bundle.getParcelable("uri");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set Password");
        createDialogScramble(builder);

    }

    public void unscrambleThis(View view) {
        Log.v("da", "idi nahui");
        Bundle bundle = getIntent().getExtras();
        Uri uri = bundle.getParcelable("uri");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");
        createDialogUnscramble(builder);
    }

    public void createDialogScramble(AlertDialog.Builder builder/*, final Uri uri*/) {
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setCancelable(false);

        // Set up the buttons
        builder.setPositiveButton("Scramble", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();

                Bitmap bitmap = bm;

                int[] pixels = new int[bitmap.getHeight()*bitmap.getWidth()];
                bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

                for(int i=0; i<pixels.length; i++) {
                    pixels[i] = pixels[i];
                    int red = Color.red(pixels[i]);
                    int green = Color.green(pixels[i]);
                    int blue = Color.blue(pixels[i]);
                    int alpha = Color.alpha(pixels[i]);
                    pixels[i] = Color.argb(alpha, blue, green, red);
                }

                ByteBuffer byteBuffer = ByteBuffer.allocate(pixels.length * 4);
                IntBuffer intBuffer = byteBuffer.asIntBuffer();
                intBuffer.put(pixels);
                Log.d("stuck","amitho");
                byte[] array = byteBuffer.array();
                byte[] encryptedBytes = coolShit.encrypt("Bar12345Bar12345", "RandomInitVector", array);

                ByteBuffer bb = ByteBuffer.wrap(encryptedBytes);
                int x =0;
                int[] encryptedints = new int[pixels.length+4];
                while(bb.hasRemaining())
                {
                    int currentColor=bb.getInt();
                    int red = Color.red(currentColor);
                    int green = Color.green(currentColor);
                    int blue = Color.blue(currentColor);
                    int alpha = Color.alpha(currentColor);
                    Log.d("alpha","red: " + red + " green: " + green +" blue: " + blue+" alpha: " + alpha);
                    encryptedints[x] = Color.argb(alpha, blue, green, red);
                    x++;
                }
                Log.d("encryptedImage", Arrays.toString(encryptedints));
                Log.d("pixelstringLength",Integer.toString(pixels.length));
                Log.d("encryptedImageLength",Integer.toString(encryptedints.length));
                coolShit.saveBitmap("encryptedbitmap.png",bitmap.getWidth(),bitmap.getHeight(),encryptedints);
                teststuff = encryptedints;
                //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse
                 //       ("file://"
                 //               + Environment.getExternalStorageDirectory())));
                Log.d("teststuff",Arrays.toString(teststuff));
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("teststuff", 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("teststuff" +"_size", teststuff.length);

                for(int i=0;i<teststuff.length;i++)
                    editor.putInt("teststuff" + "_" + i, teststuff[i]);

                editor.commit();
                Log.d("teststuff",Arrays.toString(teststuff));

                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
                mediaScanIntent.setData(contentUri);
                getApplicationContext().sendBroadcast(mediaScanIntent);

                Toast.makeText(getApplicationContext(), "Image Scrambld!",
                        Toast.LENGTH_LONG).show();

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

    public void createDialogUnscramble(AlertDialog.Builder builder/*, final Uri uri*/) {
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);
        builder.setCancelable(false);

        // Set up the buttons
        builder.setPositiveButton("UnScramble", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                Log.d("amihere","Am I here");

                Bitmap encryptedbitmap =bm;
                //encryptedbitmap.setPremultiplied(false);

                //encryptedbitmap.setPremultiplied(true);
                int[] encryptedpixels  = new int[encryptedbitmap.getHeight()*encryptedbitmap.getWidth()];

                encryptedbitmap.getPixels(encryptedpixels, 0, encryptedbitmap.getWidth(), 0, 0, encryptedbitmap.getWidth(), encryptedbitmap.getHeight());

                Bitmap newbitmap = Bitmap.createBitmap(encryptedbitmap.getWidth(), encryptedbitmap.getHeight(), Bitmap.Config.ARGB_8888);
                //newbitmap.setPremultiplied(true);
                newbitmap.setPixels(encryptedpixels, 0, encryptedbitmap.getWidth(), 0, 0, encryptedbitmap.getWidth(), encryptedbitmap.getHeight());

                encryptedpixels  = new int[newbitmap.getHeight()*newbitmap.getWidth()];
                newbitmap.getPixels(encryptedpixels, 0, newbitmap.getWidth(), 0, 0, newbitmap.getWidth(), newbitmap.getHeight());

                Log.d("extractedPixels", Arrays.toString(encryptedpixels));
                Log.d("extractedPixels", Arrays.toString(encryptedpixels));
                SharedPreferences prefs = getApplicationContext().getSharedPreferences("teststuff", 0);
                int size = prefs.getInt("teststuff" + "_size", 0);
                int array[] = new int[size];
                for(int i=0;i<size;i++)
                    array[i] = prefs.getInt("teststuff" + "_" + i, 0);

                encryptedpixels=array;

            /*
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap encryptedbitmap = BitmapFactory.decodeFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/bitmap.png", options);
            */
                Log.d("newBitmapPixels", Arrays.toString(encryptedpixels));
                Log.d("3ncryptedlength",Integer.toString(encryptedpixels.length));
                byte[] decarray = new byte[encryptedpixels.length*4];
                int y=0;
                for(int i=0; i<encryptedpixels.length; i++) {
                    byte[] toAdd = coolShit.intToByteArray(encryptedpixels[i]);
                    for (byte byteToAdd : toAdd)
                    {
                        decarray[y]=byteToAdd;
                        y++;
                    }
                }

                Log.d("decArrayLength",Integer.toString(decarray.length));
                Log.d("decArray", Arrays.toString(decarray));

                byte[] decryptedarray = coolShit.decrypt("Bar12345Bar12345", "RandomInitVector", decarray);
                Log.d("decryptedpixels", Arrays.toString(decryptedarray));

                ByteBuffer bbd = ByteBuffer.wrap(decryptedarray);
                int x =0;
                int[] decryptedints = new int[encryptedpixels.length];
                while(bbd.hasRemaining())
                {
                    int currentColor=bbd.getInt();
                    int red = Color.red(currentColor);
                    int green = Color.green(currentColor);
                    int blue = Color.blue(currentColor);
                    int alpha = Color.alpha(currentColor);
                    // Log.d("alpha","red: " + red + " green: " + green +" blue: " + blue+" alpha: " + alpha);
                    decryptedints[x] = Color.argb(alpha, red, green, blue);
                    x++;
                }
                Log.d("arrayoutput", Arrays.toString(decryptedints));

                //Bitmap newbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                coolShit.saveBitmap("bitmap.png",encryptedbitmap.getWidth(),encryptedbitmap.getHeight(),decryptedints);

                Toast.makeText(getApplicationContext(), "Image UnScrambld!",
                        Toast.LENGTH_LONG).show();
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
