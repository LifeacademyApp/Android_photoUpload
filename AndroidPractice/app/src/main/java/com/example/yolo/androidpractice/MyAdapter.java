package com.example.yolo.androidpractice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Yolo on 2018/4/1.
 */

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

//        private ArrayList<CreateList> galleryList;
        private ArrayList<String> photoNameList;
        private Context context;
        private String photoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/PhotoUploader";
//        public MyAdapter(Context context, ArrayList<CreateList> galleryList) {
//            this.galleryList = galleryList;
//            this.context = context;
//        }
        public MyAdapter(Context context, ArrayList<String> photoNameList) {
            this.photoNameList = photoNameList;
            this.context = context;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cell_layout, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int i) {
            viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Get the dimensions of the View
            int targetW = viewHolder.img.getWidth();
            int targetH = viewHolder.img.getHeight();

            Uri imgUri = Uri.parse(photoDir + "/" + photoNameList.get(i));

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imgUri.getPath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
//            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
            int scaleFactor = 2;
            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(imgUri.getPath(), bmOptions);
            viewHolder.img.setImageBitmap(bitmap);

        }
//        public void onBindViewHolder(MyAdapter.ViewHolder viewHolder, int i) {
////            viewHolder.title.setText(galleryList.get(i).getImage_title());
//            viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            viewHolder.img.setImageResource((photoNameList.get(i).getImage_ID()));
//        }

        @Override
        public int getItemCount() {
            return photoNameList.size();
        }
//        public int getItemCount() {
//            return galleryList.size();
//        }

        public class ViewHolder extends RecyclerView.ViewHolder{
//            private TextView title;
            private ImageView img;
            public ViewHolder(View view) {
                super(view);

//                title = (TextView)view.findViewById(R.id.title);
                img = (ImageView) view.findViewById(R.id.img);
            }
        }
}
