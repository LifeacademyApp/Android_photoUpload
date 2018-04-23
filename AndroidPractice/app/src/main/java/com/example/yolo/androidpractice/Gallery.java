package com.example.yolo.androidpractice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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
    public void getSelected(View v){
        ArrayList<String> photos = adapter.getSelectedList();
        for(int i=0; i< photos.size(); i++)
        {
            Log.v("###Selected", photos.get(i));
        }
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

