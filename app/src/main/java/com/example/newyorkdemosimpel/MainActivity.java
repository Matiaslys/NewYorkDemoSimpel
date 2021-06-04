package com.example.newyorkdemosimpel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ImageView imageViewAR, imageViewWindow;
    private Button useCamera, useGallery;
    private SeekBar deph;
    private TextView dephText;
    private Uri imageURI;
    int downX, downY = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageViewAR = findViewById(R.id.imageViewAR);
        imageViewWindow = findViewById(R.id.imageViewWindow);
        useCamera = findViewById(R.id.takePic);
        useGallery = findViewById(R.id.fromGallery);
        deph = findViewById(R.id.deph);
        dephText = findViewById(R.id.dephText);

        imageViewWindow.setImageResource(R.drawable.wall_after_paint_and_remove_background);
        imageViewAR.setImageResource(R.drawable.empty_room);

        deph.setProgress((int) imageViewWindow.getScaleX() * 100);

        deph.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                float size = (float) progress / 100;
                imageViewWindow.setScaleX(size);
                imageViewWindow.setScaleY(size);
                //set TextView
                dephText.setText("Deph " + deph.getProgress());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        GalleryRequest();
        CameraRequest();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            // finger on view
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                break;

            // after every pixel the finger move on the view
            case MotionEvent.ACTION_MOVE:
                int movedX = (int) event.getX();
                int movedY = (int) event.getY();
                int distanceX = movedX - downX;
                int distanceY = movedY - downY;

                // calculate how much the user moved their finger
                imageViewWindow.setX(imageViewWindow.getX() + distanceX);
                imageViewWindow.setY(imageViewWindow.getY() + distanceY);

                // move the view to the position
                downX = movedX;
                downY = movedY;
                break;
        }
        return true;
    }

    private void GalleryRequest() {
        useGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            }, 1);
                } else {
                    GalleryUsage();
                }
            }
        });
    }

    private void GalleryUsage() {
        Intent gallery = new Intent();
        gallery.setType("image/*");
        gallery.setAction(Intent.ACTION_PICK);

        startActivityForResult(Intent.createChooser(gallery, "Select picture"), 1);
    }

    private void CameraRequest(){
        useCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                    Manifest.permission.CAMERA
                            },100);
                } else {
                    CameraUsage();
                }
            }
        });
    }

    private void CameraUsage(){
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // get captured image
            Bitmap capturedImage = (Bitmap) data.getExtras().get("data");

            //set captured image to imageView
            imageViewAR.setImageBitmap(capturedImage);
        }

        if (requestCode == 1 && resultCode == RESULT_OK){
            imageURI = data.getData();
            try {
                Bitmap image = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURI);
                imageViewAR.setImageBitmap(image);
                imageViewAR.setRotation(90);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 100: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CameraUsage();
                    break;
                } else{
                    Toast.makeText(this, "Permission denied ", Toast.LENGTH_SHORT).show();
                }
            }
            case 1: {
                if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    GalleryUsage();
                    break;
                } else{
                    Toast.makeText(this, "Permission denied ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}