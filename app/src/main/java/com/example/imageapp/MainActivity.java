package com.example.imageapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewImage;
    Button buttonBackward, buttonForward, buttonAddLink;
    EditText editTextImageURL;

    Handler handler = new Handler();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewImage = (ImageView) findViewById(R.id.imageViewImage);

        editTextImageURL = (EditText) findViewById(R.id.editTextImageURL);

        buttonBackward = (Button) findViewById(R.id.buttonBackward);
        buttonForward = (Button) findViewById(R.id.buttonForward);
        buttonAddLink = (Button) findViewById(R.id.buttonAddLink);

        buttonBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Get URL from input
                String imageURL = editTextImageURL.getText().toString();
                new FetchImage(imageURL).start();
            }
        });

    }

    class FetchImage extends Thread {

        String URL;
        Bitmap bitmap;

        FetchImage(String URL) {
            this.URL = URL;
        }

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //Show progress dialog box
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Getting picture from Internet...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });
            InputStream inputStream = null;
            try {
                inputStream = new URL(URL).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    imageViewImage.setImageBitmap(bitmap);
                }
            });
        }
    }
}