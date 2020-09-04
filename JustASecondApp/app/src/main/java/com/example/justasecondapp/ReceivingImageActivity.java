package com.example.justasecondapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class ReceivingImageActivity extends AppCompatActivity {

    private TextView textView1;
    private TextView textView2;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiving_image);

        textView1 = (TextView) findViewById(R.id.startImage);
        textView2 = (TextView) findViewById(R.id.typeOfImage);
        imageView = (ImageView) findViewById(R.id.someRandomImage);

        textView1.setText("This is second app: " + getApplicationContext().getPackageName() + "\n In ReceivingImageActivity \n Below is the information received.");

        Intent intent = getIntent();
        Uri gottenUri = intent.getData();

        if (gottenUri != null) {
            textView2.setText("We were shared an image Uri \n " + gottenUri.toString());
            imageView.setImageURI(gottenUri);

        }else{
            String base64StringOfImage = intent.getStringExtra("EncodedImage");
            if(base64StringOfImage != null){
                textView2.setText("Received a Base64 String of an image byte array! ");

                byte[] decodedImageByteArray = Base64.decode(base64StringOfImage, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImageByteArray, 0, decodedImageByteArray.length);
                imageView.setImageBitmap(bitmap);

            }else{
                byte[] imageByteArray = intent.getByteArrayExtra("ImageBytes");
                if(imageByteArray != null){
                    textView2.setText("Received a byte array of an image. ");

                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                    imageView.setImageBitmap(bitmap);

                }else{
                    Bitmap imageBitmap = intent.getParcelableExtra("ImageBitmap");
                    if(imageBitmap != null){
                        textView2.setText("Received a bitmap object! ");
                        imageView.setImageBitmap(imageBitmap);
                    }else{
                        textView2.setText("No image data received! ");
                    }
                }
            }
        }
    }
}
