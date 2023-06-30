package com.example.adimusic;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.ImageView;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final int REQUESTCODE = 100;

    ImageView AudioBtn;
    ImageView VideoBtn;

    ArrayList<VideoModel> videoData;

    VideoModel videoModel = null;

    ActivityResultLauncher<String[]> mPermissionResultLauncher;

    boolean isReadExternalStorageGranted = false;
    boolean isRecordAudioGranted = false;

    boolean isRecordAudioPermissionDenied;

    boolean isReadExternalStoragePermissionDenied;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoData = new ArrayList<>();

        AudioBtn = (ImageView) findViewById(R.id.Audiobtn);
        VideoBtn = (ImageView) findViewById(R.id.Videobtn);

        Log.d("MainActivity", "step 1");
        mPermissionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {
                        if(result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null){
                            isReadExternalStorageGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
                        }

                        if (result.get(Manifest.permission.RECORD_AUDIO) != null){
                            isRecordAudioGranted = Boolean.TRUE.equals(result.get(Manifest.permission.RECORD_AUDIO));
                        }
                    }
                });
        Log.d("MainActivity", "step 2");
        runtimePermission();


        AudioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent permIntent = new Intent(MainActivity.this, AudioActivity.class);
                permIntent.putExtra("readaudiopermission", isReadExternalStoragePermissionDenied);
                permIntent.putExtra("recordaudiopermissionDenied", isRecordAudioPermissionDenied);
                startActivity(permIntent);
            }
        });

        VideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                ExecutorService executorService = Executors.newSingleThreadExecutor();

                executorService.execute(new Runnable() {
                    @Override
                    public void run() {


                        //as doInBackground() in AsyncTask

                        ContentResolver contentResolver = getContentResolver();

                        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        Cursor cursor = contentResolver
                                .query(uri, null, null, null, null);

                        if(cursor != null && cursor.moveToFirst()){

                            do {
                                 String videoTitle = cursor
                                        .getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                                 String videoPath = cursor
                                        .getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));

                                videoModel = new VideoModel(videoTitle, videoPath, null, uri.toString());

                                videoData.add(videoModel);


                            }while(cursor.moveToNext());
                        }

                        Log.d("MainActivity", "step 7");
                        Intent listIntent = new Intent(MainActivity.this, VideoActivity.class);
                       // listIntent.putExtra("vlist", videoData);
                        listIntent.putExtra("vlist",videoData);

                        listIntent.putExtra("readvideopermission", isReadExternalStoragePermissionDenied);

                        startActivity(listIntent);

                    }
                });

            }
        });

    }

    public void runtimePermission(){

        Log.d("MainActivity", "step 3");
        isReadExternalStorageGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        isRecordAudioGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionList = new ArrayList<String>();

        if (!isRecordAudioGranted){
            Log.d("MainActivity", "step 4");

            new AlertDialog.Builder(this)
                    .setTitle("Required Permission")
                            .setMessage("To experience audio visualizer with music, please give record audio permission manually. Click on Settings. \n " +
                                    "App permissions -> Click on Microphone -> select Allow")
                                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            intent.setData(uri);
                                            startActivity(intent);
                                            dialogInterface.dismiss();
                                        }
                                    })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    isRecordAudioPermissionDenied = true;
                                                }
                                            }).show();

            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }

        if (!isReadExternalStorageGranted){
            new AlertDialog.Builder(this)
                    .setTitle("Required Permission")
                    .setMessage("To fetch your songs and videos from internal storage, please give this permission manually. Click on Settings. \n " +
                            "App permissions -> Click on Files and media -> select Allow")
                    .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            isReadExternalStoragePermissionDenied = true;
                        }
                    }).show();

            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (!permissionList.isEmpty()){
            Log.d("MainActivity", "step 5");
            mPermissionResultLauncher.launch(permissionList.toArray(new String[0]));
        }
//        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
//                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
//                MainActivity.REQUESTCODE);
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
//                                                                   PermissionToken permissionToken) {
//                        permissionToken.continuePermissionRequest();
//                    }
//                }).check();
//
//
//    }
//
//    void requestPermission(){
//        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
//                Manifest.permission.READ_EXTERNAL_STORAGE)){
//            Toast.makeText(getApplicationContext(),
//                    "Please give permission to access media files from settings", Toast.LENGTH_LONG).show();
//        }
//
//        ActivityCompat.requestPermissions(MainActivity.this,
//                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO},
//                MainActivity.REQUESTCODE);

//        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
//                MainActivity.REQUESTCODE);
       // }
    }




}