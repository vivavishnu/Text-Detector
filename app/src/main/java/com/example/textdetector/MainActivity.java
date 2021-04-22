package com.example.textdetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    public Button opencam;
    public ImageView Image;
    public Button Detect;
    public Bitmap bitmap;
    public String text;
    TextToSpeech textToSpeech;
    FirebaseVisionImage firebaseVisionImage;
    FirebaseVisionTextDetector firebaseVisionTextDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        opencam=findViewById(R.id.button);
        Image=findViewById(R.id.image);
        Detect=findViewById(R.id.detect);
        textToSpeech=new TextToSpeech(this, this);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
        opencam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,100);
            }
        });
        Detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
                try {
                    firebaseVisionTextDetector=FirebaseVision.getInstance().getVisionTextDetector();
                    firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
                            if (blockList.size() == 0) {
                              textToSpeech.speak("No text identified in image",TextToSpeech.QUEUE_FLUSH,null);
                            } else {
                                for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()) {
                                    text = block.getText();
                                }
                              textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (NullPointerException exception){
                    Toast.makeText(getApplicationContext(),"N",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            bitmap = (Bitmap) data.getExtras().get("data");
            Image.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onInit(int status) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}