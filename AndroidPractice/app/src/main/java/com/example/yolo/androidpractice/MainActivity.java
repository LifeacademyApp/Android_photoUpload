package com.example.yolo.androidpractice;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.provider.MediaStore;
import android.content.Intent;
import android.util.Log;
import 	android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.Uri;
import 	java.io.File;

public class MainActivity extends AppCompatActivity {

    Uri imgUri;
    ImageView imv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imv = (ImageView) findViewById(R.id.imageView);
    }
    public void onGet(View v){
        //? String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        //Log.i("9999999", "yoooo");
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        String fname = "p" + System.currentTimeMillis() + ".jpg";
        //imgUri = Uri.parse("file:///SUGAR Y12/DCIM/Camera/" + fname);
        imgUri = Uri.parse("file://" + dir + "/" + fname);


        Intent it = new Intent("android.media.action.IMAGE_CAPTURE");
        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        //startActivity(it);
        startActivityForResult(it, 100);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == 100){
            Bitmap bmp = BitmapFactory.decodeFile(imgUri.getPath());
            imv.setImageBitmap(bmp);
        }
        else{
            Toast.makeText(this, "沒有拍到照片", Toast.LENGTH_LONG).show();
        }
    }
}
