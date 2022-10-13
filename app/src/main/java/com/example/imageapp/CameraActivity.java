package com.example.imageapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CameraActivity extends AppCompatActivity {

    ImageButton imageButtonTakePicture;
    ImageView imageViewCamera;

    private static int REQUEST_CODE_CAMERA = 1;
    private static int REQUEST_CODE_SAVE_IMAGE = 100;

    OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageButtonTakePicture = (ImageButton) findViewById(R.id.imageButtonTakePicture);
        imageViewCamera = (ImageView) findViewById(R.id.imageViewCamera);

        imageButtonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open camera using intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, REQUEST_CODE_CAMERA);
                activityResultLauncher.launch(intent);
            }
        });
    }

    //Replace for startActivityForResult
    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Bitmap bitmap = (Bitmap) intent.getExtras().get("data"); //data: default key
                        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                            saveImage(bitmap);
                        }else {
                            askPermission();
                        }
                        imageViewCamera.setImageBitmap(bitmap);
                    }
                }
            });

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        //resultCode: OK OR NOT WHEN PICTURE IS TAKEN
//        //data: the taken picture
//        if(requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK && data != null) {
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data"); //data: default key
//            imageViewCamera.setImageBitmap(bitmap);
//            if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//                            saveImage(bitmap);
//                        }else {
//                            askPermission();
//                        }
//                        imageViewCamera.setImageBitmap(bitmap);
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void askPermission() {

        ActivityCompat.requestPermissions(CameraActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_SAVE_IMAGE);

    }

    private void saveImage(Bitmap bitmap) {
        File dir = new File(Environment.getExternalStorageDirectory(),"SaveImage");
        if (!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir,System.currentTimeMillis()+".jpeg");
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("File: ", String.valueOf(file));
        if(bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            Toast.makeText(CameraActivity.this,"Save image successfully",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(CameraActivity.this,"Fail to save image",Toast.LENGTH_LONG).show();
        }
        try {
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Display geographical place
        try {
            ExifInterface exif = new ExifInterface("/storage/emulated/0/SaveImage/1665307456424.jpeg");
            float[] latLong = new float[2];
            boolean hasLatLong = exif.getLatLong(latLong);
            if (hasLatLong) {
                Log.d("Latitude: ", String.valueOf(latLong[0]));
                Log.d("Longitude: ", String.valueOf(latLong[1]));
                Toast.makeText(CameraActivity.this,"Latitude: " + String.valueOf(latLong[0]),Toast.LENGTH_LONG).show();
            }
            Log.d("Latitude: ", String.valueOf(latLong[0]));
            Log.d("Longitude: ", String.valueOf(latLong[1]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //
    }
}