package com.example.imageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewImage, imageViewNoImage;
    Button buttonBackward, buttonForward, buttonAddImageURL;
    EditText editTextImageURL;
    ImageButton imageButtonCamera;

    DatabaseHelper databaseHelper;

    ArrayList<String> imageId, imageAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewImage = (ImageView) findViewById(R.id.imageViewImage);
        imageViewNoImage = (ImageView) findViewById(R.id.imageViewNoImage);

        editTextImageURL = (EditText) findViewById(R.id.editTextImageURL);

        buttonBackward = (Button) findViewById(R.id.buttonBackward);
        buttonForward = (Button) findViewById(R.id.buttonForward);
        buttonAddImageURL = (Button) findViewById(R.id.buttonAddImageURL);

        imageButtonCamera = (ImageButton) findViewById(R.id.imageButtonCamera);

        databaseHelper = new DatabaseHelper(MainActivity.this);
        imageId = new ArrayList<>();
        imageAddress = new ArrayList<>();

        Cursor cursor = databaseHelper.getAllImages();
        if(cursor != null && cursor.moveToFirst()) {
            cursor.moveToLast();
            if(cursor.getCount() == 1) {
                buttonBackward.setVisibility(View.GONE);
                buttonForward.setVisibility(View.GONE);
            }
            if(cursor.getPosition() == cursor.getCount() - 1) {
                buttonForward.setVisibility(View.GONE);
            }
            Glide.with(getApplicationContext())
                    .load(cursor.getString(1))
                    .placeholder(R.drawable.ic_image_search)
                    .error(R.drawable.ic_broken_image)
                    .centerCrop()
                    .override(320, 320)
                    .into(imageViewImage);
        } else {
            buttonBackward.setVisibility(View.GONE);
            buttonForward.setVisibility(View.GONE);
            imageViewImage.setVisibility(View.GONE);
            imageViewNoImage.setVisibility(View.VISIBLE);
        }

        buttonBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPrevious();
                int columnIndex = cursor.getColumnIndex("address");
                if(columnIndex > 0) {
                    Glide.with(getApplicationContext())
                            .load(cursor.getString(columnIndex))
                            .placeholder(R.drawable.ic_image_search)
                            .error(R.drawable.ic_broken_image)
                            .centerCrop()
                            .override(320, 320)
                            .into(imageViewImage);
                }
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

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkImageURLExist(String inputImageURL) {
        return URLUtil.isValidUrl(inputImageURL);
    }

    private void checkFirstImg(Cursor cursor) {
        if(!cursor.moveToPrevious()) {
            buttonBackward.setVisibility(View.GONE);
        } else {
            buttonBackward.setVisibility(View.VISIBLE);
        }
    }

    private void checkLastImg(Cursor cursor) {
        if(!cursor.moveToNext()) {
            buttonForward.setVisibility(View.GONE);
        } else {
            buttonForward.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater(); //Inflate menu layout
        inflater.inflate(R.menu.custom_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.deleteAll) {
            displayConfirmDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    void displayConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete all uploaded images");
        builder.setMessage("Are you sure to you want to delete all images?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseHelper myDB = new DatabaseHelper(MainActivity.this);
                databaseHelper.deleteAllImages();
                //Redirect to Main activity --> Refresh activity
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); //Close current activity and get to Main activity
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }
}