package com.example.yolo.androidpractice;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Browser;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Gallery extends AppCompatActivity {

    MyAdapter adapter;
    ArrayList<String> photoNames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        photoNames = getIntent().getStringArrayListExtra("photoNames");

        RecyclerView recyclerView = findViewById(R.id.recycleGallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(layoutManager);
//        ArrayList<CreateList> createLists = prepareData();


//        MyAdapter adapter = new MyAdapter(getApplicationContext(), createLists);
        adapter = new MyAdapter(getApplicationContext(), photoNames);
        recyclerView.setAdapter(adapter);
    }

    public void onUpload(View v){

        int targetW = 0;
        int targetH = 0;
        String resMax = null;

        Intent tIntent = this.getIntent();

        Uri myURI = tIntent.getParcelableExtra("parameterFromWeb");

        if(myURI != null){
            resMax = myURI.getQueryParameter("resMax");

            // Get addresses
            String call_url = myURI.getQueryParameter("callurl");
            final String send_url = myURI.getQueryParameter("returnurl");

            //?  checked
            Log.d("###resMax",resMax);
            Log.i("###call_url",call_url);
            Log.w("###sendpic_url",send_url);

            // get Selected List
            ArrayList<String> photos = adapter.getSelectedList();

            final ArrayList<Bitmap> photoBitmaps = new ArrayList<>();
//            for(int i=0; i< photos.size(); i++)
//            {
//                Log.v("###Selected", photos.get(i));
//            }



            // adjust photo size
            // set target width and height according to parameter

            switch (resMax){
                case "HD":
                    targetW = 1280;
                    targetH = 720;
                    break;
                case "FHD":
                    targetW = 1920;
                    targetH = 1080;
                    break;
                case "2K":
                    targetW = 2560;
                    targetH = 1440;
                    break;
                case "4K":
                    targetW = 3840;
                    targetH = 2160;
                    break;
                default:
                    break;
            }



            String dirPath = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).toString() + "/PhotoUploader";

            //?
            Log.i("###dirPath",dirPath);

            // adjust the size for each selected photo
            for(int i=0; i< photos.size(); i++)
            {


                String mCurrentPhotoPath = dirPath + "/" + photos.get(i);

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.max(photoW/targetW, photoH/targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                Bitmap tmpBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                photoBitmaps.add(tmpBitmap);

                //?
                Log.i("###in","yoyo");
            }


            final ImageView tmp = findViewById(R.id.imvTest);;
            for(int i=0; i<photoBitmaps.size(); i++){
                final Bitmap tmpB = photoBitmaps.get(i);
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tmp.setImageBitmap(tmpB);
                    }
                }, 500);
            }

            // --------upload--------
            new Thread()
            {
                @Override
                public void run()
                {
                    JSONObject jsonObject = new JSONObject();
                    try {

                        for(int i=0; i< photoBitmaps.size(); i++){
                            //将Bitmap 转成 String，其实这是一个加密过程。后面会有Common.Bitmap2String()的代码。
                            jsonObject.put("pic" + i+1, Bitmap2String(photoBitmaps.get(i)));
                        }


                        String content = String.valueOf(jsonObject);

                        //? --------混雜 不只一篇----------
                        HttpURLConnection connection = (HttpURLConnection) new URL(send_url).openConnection();
                        connection.setConnectTimeout(5000);
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setRequestProperty("User-Agent", "Fiddler");
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Charset", "UTF-8");
                        OutputStream os = connection.getOutputStream();
                        os.write(content.getBytes());
                        os.close();


                        //Get Response
                        InputStream is = connection.getInputStream();

                        //?  ----------另一篇----------

                    }catch (Exception e){

                    }
                }
            }.start();


            // -----返回呼叫頁面-----

            Intent intent_main = new Intent(Intent.ACTION_VIEW);

            // 指定使用 chrome 開啟
            intent_main.setPackage("com.android.chrome");
            intent_main.setData(Uri.parse(call_url));

            // 放入 EXTRA_APPLICATION_ID ，以重複使用同一分頁
            intent_main.putExtra(Browser.EXTRA_APPLICATION_ID, "com.android.chrome");

            // 開啟網頁
            try{
                startActivity(intent_main);
            }catch (ActivityNotFoundException ex){
                // 手機沒有 chrome 的時候，改用 default browser 開啟 => package 設為 null
                intent_main.setPackage(null);
                startActivity(intent_main);
                Toast.makeText(this,"建議下載 chrome，可避免產生多餘分頁",Toast.LENGTH_SHORT).show();
            }

        }
    }

    public static String Bitmap2String(Bitmap bitmap)
    {
        return Base64.encodeToString(Bitmap2Bytes(bitmap), Base64.DEFAULT);
    }
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public void selectAll(View v){
        adapter.selectAll();
    }
//    private ArrayList<CreateList> prepareData(){
//
//        ArrayList<CreateList> theimage = new ArrayList<>();
//        for(int i = 0; i< image_titles.length; i++){
//            CreateList createList = new CreateList();
//            createList.setImage_title(image_titles[i]);
//            createList.setImage_ID(image_ids[i]);
//            theimage.add(createList);
//        }
//        return theimage;
//    }
}

