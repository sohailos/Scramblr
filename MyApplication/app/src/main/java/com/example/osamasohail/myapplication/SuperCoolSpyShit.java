package com.example.osamasohail.myapplication;

import android.Manifest;

import android.content.SharedPreferences;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by osamasohail on 2017-01-22.
 */

public class SuperCoolSpyShit extends AppCompatActivity {

    private int requestcode;
    private String key;
    String initVector = "RandomInitVector"; // 16 bytes IV
    int[] teststuff;

    public void encryptPicture(String passCode/*, Uri uri*/) {

        key = passCode;
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 23);
        Log.d("myTag", "start");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    }
    public void decryptPicture(String passCode/*, Uri uri*/) {
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 23);
        //showAlert("testing");
        Log.d("myTag", "clicked");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("requestcode", Integer.toString(requestcode));
        if (requestcode == 23 && resultCode == Activity.RESULT_OK) {
            //Encrypt
            if (data == null) {
                return;
            }

            final Uri uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }

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

            byte[] array = byteBuffer.array();
            byte[] encryptedBytes = encrypt(key, initVector, array);

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
                // Log.d("alpha","red: " + red + " green: " + green +" blue: " + blue+" alpha: " + alpha);
                encryptedints[x] = Color.argb(alpha, blue, green, red);
                x++;
            }
            Log.d("encryptedImage",Arrays.toString(encryptedints));
            Log.d("pixelstringLength",Integer.toString(pixels.length));
            Log.d("encryptedImageLength",Integer.toString(encryptedints.length));
            saveBitmap("encryptedbitmap.png",bitmap.getWidth(),bitmap.getHeight(),encryptedints);
            teststuff = encryptedints;
            SharedPreferences prefs = getApplicationContext().getSharedPreferences("teststuff", 0);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("teststuff" +"_size", teststuff.length);

            for(int i=0;i<teststuff.length;i++)
                editor.putInt("teststuff" + "_" + i, teststuff[i]);

            editor.commit();
            Log.d("teststuff",Arrays.toString(teststuff));



            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
        else if (requestcode == 33 && resultCode == Activity.RESULT_OK) {
            //Decrypts
            Log.d("amihere","Am I here");
            if (data == null) {
                return;
            }

            final Uri uri = data.getData();
            Bitmap encryptedbitmap =Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            //encryptedbitmap.setPremultiplied(false);
            encryptedbitmap.setHasAlpha(true);
            try {
                encryptedbitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //encryptedbitmap.setPremultiplied(true);
            int[] encryptedpixels  = new int[encryptedbitmap.getHeight()*encryptedbitmap.getWidth()];

            encryptedbitmap.getPixels(encryptedpixels, 0, encryptedbitmap.getWidth(), 0, 0, encryptedbitmap.getWidth(), encryptedbitmap.getHeight());

            Bitmap newbitmap = Bitmap.createBitmap(encryptedbitmap.getWidth(), encryptedbitmap.getHeight(), Bitmap.Config.ARGB_8888);
            //newbitmap.setPremultiplied(true);
            newbitmap.setPixels(encryptedpixels, 0, encryptedbitmap.getWidth(), 0, 0, encryptedbitmap.getWidth(), encryptedbitmap.getHeight());

            encryptedpixels  = new int[newbitmap.getHeight()*newbitmap.getWidth()];
            newbitmap.getPixels(encryptedpixels, 0, newbitmap.getWidth(), 0, 0, newbitmap.getWidth(), newbitmap.getHeight());

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
                byte[] toAdd = intToByteArray(encryptedpixels[i]);
                for (byte byteToAdd : toAdd)
                {
                    decarray[y]=byteToAdd;
                    y++;
                }
            }

            Log.d("decArrayLength",Integer.toString(decarray.length));
            Log.d("decArray", Arrays.toString(decarray));

            byte[] decryptedarray = decrypt(key, initVector, decarray);
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
            saveBitmap("bitmap.png",encryptedbitmap.getWidth(),encryptedbitmap.getHeight(),decryptedints);
        }
    }
    public static byte[] encrypt(String key, String initVector, byte[] value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value);
            System.out.println("encrypted string: "
                    + Arrays.toString(encrypted));

            return encrypted;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;

    }

    public static byte[] decrypt(String key, String initVector, byte[] encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            Log.d("test","reaaaached");
            byte[] original = cipher.doFinal(encrypted);
            Log.d("test","reaaaached3");
            return original;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public void saveBitmap(String filename, int width, int height, int[] pixels)
    {
        pixels = Arrays.copyOf(pixels, pixels.length-4);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 23);
        Bitmap newbitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //newbitmap.setHasAlpha(true);
        //newbitmap.setPremultiplied(false);
        Log.d("passedpixels", Arrays.toString(pixels));
        Log.d("passedpixelssize", Integer.toString(pixels.length));

        newbitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        //newbitmap.copyPixelsFromBuffer(IntBuffer.wrap(pixels));
        newbitmap.setPixel(0,0,Color.argb(Color.alpha(pixels[0]), Color.red(pixels[0]), Color.green(pixels[0]), Color.blue(pixels[0])));
        int[] temppixels  = new int[height*width];
        newbitmap.getPixels(temppixels, 0, width, 0, 0, width, height);

        Log.d("temppixels", Arrays.toString(temppixels));
        Log.d("temppixelssize", Integer.toString(temppixels.length));
        //showAlert("reached input");
        FileOutputStream out = null;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+filename);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            out = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+filename);
            Log.d("myTag", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/bitmap.png");
            newbitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            //sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))));
            Log.d("tag","almost wrote succesfully");
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    Log.d("tag","wrote succesfully");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static final byte[] intToByteArray(int a)
    {
        byte[] ret = new byte[4];
        ret[0] = (byte) ((a >> 24) & 0xFF);
        ret[1] = (byte) (a & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[3] = (byte) ((a >> 16) & 0xFF);

        return ret;
    }
}
