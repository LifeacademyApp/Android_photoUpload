package com.example.yolo.androidpractice;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    Uri imgUri;
    ImageView imv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //imv = (ImageView) findViewById(R.id.imageView);
    }

    public void onGet(View v){
        dispatchTakePictureIntent();


      //  Log.v("yooooooo","tesettest");
//        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//        String fname = "p" + System.currentTimeMillis() + ".jpg";
//        imgUri = Uri.parse("file://" + dir + "/" + fname);
//
//
//        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        it.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
//        startActivityForResult(it, REQUEST_TAKE_PHOTO);
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    //? 當一個 activity return 後，便會來到此
    //? 相機程式大概也是個 activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_TAKE_PHOTO){
           // showImg();
            galleryAddPic();                // 通知系統有新的照片
            dispatchTakePictureIntent();
        }
        else{
            Toast.makeText(this, "取消拍照", Toast.LENGTH_LONG).show();
        }
    }
    void showImg(){
        int iw, ih, vw, vh;

        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, option);
        iw = option.outWidth;
        ih = option.outHeight;

        vw = imv.getWidth();
        vh = imv.getHeight();

        int scaleFactor = Math.min(iw/vw, ih/vh);

        option.inJustDecodeBounds = false;
        option.inSampleSize = scaleFactor;

        Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath, option);
        imv.setImageBitmap(bmp);
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) ;
        // Log.d("MainActivity", "Environment.DIRECTORY_PICTURES");
        // 建出暫存檔
        //File.
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );


        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            //File photoFile = null;
            String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            String fname = "p" + System.currentTimeMillis() + ".jpg";
            mCurrentPhotoPath = dir + "/" + fname;
//            File photoFile = null;
            File photoFile = new File(mCurrentPhotoPath);

//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);


                //Uri photoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                // 確認一下 在哪行程式碼創建了檔案的，並試試看原本非 provider 的
                // 方法是否也會創建空的檔案
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

}
