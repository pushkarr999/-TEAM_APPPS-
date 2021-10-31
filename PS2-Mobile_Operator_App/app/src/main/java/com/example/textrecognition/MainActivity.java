package com.example.textrecognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Button captureImageBtn, detectTextBtn,submit;
    private ImageView imageView;
    public EditText textView;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap imageBitmap;
    public double lat,lon,latedited,lonedited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captureImageBtn = findViewById(R.id.capture_image);
        detectTextBtn = findViewById(R.id.detect_text_image);
        imageView = findViewById(R.id.image_view);
        textView = findViewById(R.id.text_display);
        submit = findViewById(R.id.submit);

        captureImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
                textView.setText("");
            }
        });
        detectTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTextFromImage();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFinal();
            }
        });
    }

    private void checkFinal() {
        String editAddress = textView.getText().toString();
        Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
        String result2 = null;
        try{
            List addressList2 = geocoder.getFromLocationName(editAddress,1);
            if(addressList2 != null && addressList2.size()>0){
                Address address2 = (Address) addressList2.get(0);
                StringBuilder stringBuilder2 = new StringBuilder();
                latedited = address2.getLatitude();
                lonedited = address2.getLongitude();
                stringBuilder2.append(latedited).append("\n");
                stringBuilder2.append(lonedited).append("\n");
                stringBuilder2.append(editAddress);
                result2 = stringBuilder2.toString();
                //textView.setText(editAddress);
                float[] resultsd = new float[1];
                Location.distanceBetween(latedited,lonedited,lat,lon,resultsd);
                float distance = resultsd[0];
                if(distance>100) {
                    Toast.makeText(this, "Invalid Address : Edited location should be within 100 mtr", Toast.LENGTH_SHORT).show();
                    final AlertDialog dialog = new AlertDialog.Builder(this)
                            .setTitle("Invalid")
                            .setMessage("Edited location should be within 100 mtr")
                            .setPositiveButton("Ok", null)
                            .setNegativeButton("Cancel", null)
                            .show();

                    Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
                else
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Invalid Address", Toast.LENGTH_SHORT).show();
            }
        }catch (IOException e){
            e.printStackTrace();}
        double dist = distance(latedited,latedited,lat,lon);
        if(dist<0.2)
            Toast.makeText(this, "Invalid Address", Toast.LENGTH_SHORT).show();
        //textView.setText(editAddress);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
    private void detectTextFromImage() {
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextDetector firebaseVisionTextDetector = FirebaseVision.getInstance().getVisionTextDetector();
        firebaseVisionTextDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displayTextFromImage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Error"+ e.getMessage(),Toast.LENGTH_SHORT).show();
                Log.d("Error :",e.getMessage());
            }
        });
    }

    private void displayTextFromImage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blockList = firebaseVisionText.getBlocks();
        if(blockList.size()==0){
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show();
        }
        else{
            for (FirebaseVisionText.Block block : firebaseVisionText.getBlocks()){
                String text = block.getText();
                Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.getDefault());
                String result = null;
                try{
                    List addressList = geocoder.getFromLocationName(text,1);
                    if(addressList != null && addressList.size()>0){
                        Address address = (Address) addressList.get(0);
                        StringBuilder stringBuilder = new StringBuilder();
                        lat = address.getLatitude();
                        lon = address.getLongitude();
                        stringBuilder.append(lat).append("\n");
                        stringBuilder.append(lon).append("\n");
                        stringBuilder.append(text);
                        result = stringBuilder.toString();
                        textView.setText(text);
                    }
                    else {
                        Toast.makeText(this, "Invalid Address", Toast.LENGTH_SHORT).show();
                    }
                }catch (IOException e){
                    e.printStackTrace();}
                //textView.setText();
            }
        }
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}