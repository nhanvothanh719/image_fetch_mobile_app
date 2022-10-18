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
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

public class CameraActivity extends AppCompatActivity implements LocationListener {
    ImageButton imageButtonTakePicture;
    ImageView imageViewCamera;

    private static int REQUEST_CODE_CAMERA = 1;
    private static int REQUEST_CODE_SAVE_IMAGE = 100;
    private final int REQUEST_CODE_GET_LOCATION = 123;

    OutputStream outputStream;

    LocationManager locationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        imageButtonTakePicture = (ImageButton) findViewById(R.id.imageButtonTakePicture);
        imageViewCamera = (ImageView) findViewById(R.id.imageViewCamera);

        if (ContextCompat.checkSelfPermission(CameraActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CameraActivity.this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE_GET_LOCATION);
        }

        imageButtonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open camera using intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, REQUEST_CODE_CAMERA);
                activityResultLauncher.launch(intent);
            }
        });
        getCurrentLocation();
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
        getCurrentLocation();
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, CameraActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(this, "Latitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        //LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        //LocationListener.super.onProviderDisabled(provider);
    }
}