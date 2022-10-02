package com.example.imageapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewImage;
    Button buttonBackward, buttonForward, buttonAddImageURL;
    EditText editTextImageURL;

    DatabaseHelper databaseHelper;

    ArrayList<String> imageId, imageAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewImage = (ImageView) findViewById(R.id.imageViewImage);

        editTextImageURL = (EditText) findViewById(R.id.editTextImageURL);

        buttonBackward = (Button) findViewById(R.id.buttonBackward);
        buttonForward = (Button) findViewById(R.id.buttonForward);
        buttonAddImageURL = (Button) findViewById(R.id.buttonAddImageURL);

        databaseHelper = new DatabaseHelper(MainActivity.this);
        imageId = new ArrayList<>();
        imageAddress = new ArrayList<>();

        Cursor cursor = databaseHelper.getAllImages();
        if(cursor.getCount() != 0) {
            cursor.moveToLast();
            Glide.with(getApplicationContext())
                    .load(cursor.getString(1))
                    .placeholder(R.drawable.ic_image_search)
                    .error(R.drawable.ic_broken_image)
                    .centerCrop()
                    .override(320, 320)
                    .into(imageViewImage);
            checkFirstImg(cursor);
            checkLastImg(cursor);
        }

        buttonBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPrevious();
                Glide.with(getApplicationContext())
                        .load(cursor.getString(1))
                        .placeholder(R.drawable.ic_image_search)
                        .error(R.drawable.ic_broken_image)
                        .centerCrop()
                        .override(320, 320)
                        .into(imageViewImage);
                checkFirstImg(cursor);
                checkLastImg(cursor);
            }
        });

        buttonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToNext();
                Glide.with(getApplicationContext())
                        .load(cursor.getString(1))
                        .placeholder(R.drawable.ic_image_search)
                        .error(R.drawable.ic_broken_image)
                        .centerCrop()
                        .override(320, 320)
                        .into(imageViewImage);
                checkLastImg(cursor);
                checkFirstImg(cursor);
            }
        });

        buttonAddImageURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);

                String inputImageURL = editTextImageURL.getText().toString().trim();

                if(checkImageURLExist(inputImageURL) == false) {
                    Toast.makeText(MainActivity.this, "Fail to add image URL", Toast.LENGTH_LONG).show();
                } else {
                    databaseHelper.addImage(inputImageURL);
                    editTextImageURL.setText("");
                    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(MainActivity.this, "Add image URL successfully", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean checkImageURLExist(String inputImageURL) {
        return URLUtil.isValidUrl(inputImageURL);
    }

    private void checkFirstImg(Cursor cursor) {
        if(cursor.moveToPrevious() == false) {
            buttonBackward.setVisibility(View.GONE);
        } else {
            buttonBackward.setVisibility(View.VISIBLE);
        }
    }

    private void checkLastImg(Cursor cursor) {
        if(cursor.moveToNext() == false) {
            buttonForward.setVisibility(View.GONE);
        } else {
            buttonForward.setVisibility(View.VISIBLE);
        }
    }
}