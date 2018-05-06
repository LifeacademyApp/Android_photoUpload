package com.example.yolo.androidpractice;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    static final int TAKE_PHOTO = 1;
    static final int REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    String mCurrentPhotoPath;
    ImageView imv;

    String dirPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).toString() + "/PhotoUploader";
    File photoDir;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        photoDir = new File(dirPath);
        if (!photoDir.isDirectory()) {
            photoDir.mkdirs();
        }

        // check if the permission is granted
        permissionCheckAsk();
    }

    public void onGet(View v){
        dispatchTakePictureIntent();
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

        if(resultCode == Activity.RESULT_OK ){
            if(requestCode == TAKE_PHOTO){
                // showImg();
                galleryAddPic();                // 通知系統有新的照片
                dispatchTakePictureIntent();
            }
            else{
                Toast.makeText(this, "RESULT_OK", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(this, "結束拍照", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }
//    void showImg(){
//        int iw, ih, vw, vh;
//
//        BitmapFactory.Options option = new BitmapFactory.Options();
//        option.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, option);
//        iw = option.outWidth;
//        ih = option.outHeight;
//
//        vw = imv.getWidth();
//        vh = imv.getHeight();
//
//        int scaleFactor = Math.min(iw/vw, ih/vh);
//
//        option.inJustDecodeBounds = false;
//        option.inSampleSize = scaleFactor;
//
//        Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath, option);
//        imv.setImageBitmap(bmp);
//    }
//    private File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
//
//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) ;
//
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        return image;
//    }

    private void dispatchTakePictureIntent() {
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED )
            return;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            //File photoFile = null;
            dirPath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).toString() + "/PhotoUploader";
            //? String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            Log.d("####Path",Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).toString());
            String fname = "p" + System.currentTimeMillis() + ".jpg";
            mCurrentPhotoPath = dirPath + "/" + fname;
            File photoFile = new File(mCurrentPhotoPath);

            // Continue only if the File was successfully created
            if (photoFile != null) {
                // also need to add provider in AndroidMainifest
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }

    }
    public void browsePic(View v) {

        // permission check
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED )
            return;

        ArrayList<String> photosNames = new ArrayList<>();

        File[] files = photoDir.listFiles();

        // !!!! remember ask android permission about storage

        for (int i = 0; i < files.length; i++)
        {
            photosNames.add(files[i].getName());
            //? Log.d("Files", "FileName:" + files[i].getName());
        }
        Intent toGallery = new Intent(this, Gallery.class);
        toGallery.putExtra("photoNames", photosNames);
        startActivity(toGallery);
    }
    private void permissionCheckAsk(){
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }
    public void Browse2(View v)
    {
        Intent browseIt = new Intent(Intent.ACTION_PICK);
        //Intent browseIt = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        browseIt.setType("image/*");
        startActivityForResult(browseIt, 101);
    }
    public void Browse1(View v)
    {
        Intent browseIt = new Intent(Intent.ACTION_GET_CONTENT);
        browseIt.setType("image/*");
        startActivityForResult(browseIt, 101);
    }
    //? 分享
    public void send(View v)
    {
        Intent sendIt = new Intent(Intent.ACTION_SEND);
        sendIt.setType("image/*");
        startActivity(sendIt);
    }




    //? Gallery activity
    public void browsePicxxx(View v) {
        startActivity(new Intent(this, Gallery.class));
    }

    //? 印圖
    public void Browsexxx(View v) {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img1);
//        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }
    //

}
