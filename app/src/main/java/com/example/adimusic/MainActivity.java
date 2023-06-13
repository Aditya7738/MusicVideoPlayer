package com.example.adimusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final int REQUESTCODE = 100;

    ImageButton AudioBtn;
    ImageButton VideoBtn;

    ArrayList<VideoModel> videoData;

    VideoModel videoModel = null;

    ProgressBar lvprobar;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoData = new ArrayList<>();

        AudioBtn = (ImageButton) findViewById(R.id.Audiobtn);
        VideoBtn = (ImageButton) findViewById(R.id.Videobtn);
        lvprobar = (ProgressBar) findViewById(R.id.loadvideo);

        lvprobar.setVisibility(View.GONE);
        runtimePermission();


        AudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AudioActivity.class));
            }
        });

        VideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lvprobar.setVisibility(View.VISIBLE);
                Log.d("MainActivity", "step 1");
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                Log.d("MainActivity", "step 2");
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {

                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lvprobar.setVisibility(View.VISIBLE);
                            }
                        });*/
                        //as doInBackground() in AsyncTask
                        Log.d("MainActivity", "step 3");
                        ContentResolver contentResolver = getContentResolver();
                        Log.d("MainActivity", "step 4");
                        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        Cursor cursor = contentResolver
                                .query(uri, null, null, null, null);

                        if(cursor != null && cursor.moveToFirst()){
                            Log.d("MainActivity", "step 5");
                            do {
                                 String videoTitle = cursor
                                        .getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                                 String videoPath = cursor
                                        .getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

                              //  Bitmap videoThumb = ThumbnailUtils.
                                     //   createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MINI_KIND);

                                videoModel = new VideoModel(videoTitle, videoPath, null, uri.toString());
                                Log.d("MainActivity", "step 6");
                                videoData.add(videoModel);


                            }while(cursor.moveToNext());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lvprobar.setVisibility(View.GONE);
                            }
                        });

                        Log.d("MainActivity", "step 7");
                        Intent listIntent = new Intent(MainActivity.this, VideoActivity.class);
                       // listIntent.putExtra("vlist", videoData);
                        listIntent.putExtra("vlist",videoData);

                        Log.d("MainActivity", "step 8");
                        //Bundle bundle = new Bundle();
                        Log.d("MainActivity", "step 9");

                        //bundle.putSerializable("VideoList", videoData);
                        Log.d("MainActivity", "step 10");
                        //listIntent.putExtra("listBundle", bundle);
                        Log.d("MainActivity", "step 11");
                        startActivity(listIntent);
                        Log.d("MainActivity", "step 12");
                    }
                });

            }
        });

    }

    public void runtimePermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        requestPermission();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                                   PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();


    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(getApplicationContext(),
                    "Please give permission to access media files from settings", Toast.LENGTH_LONG).show();
        }

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
                MainActivity.REQUESTCODE);
    }




}