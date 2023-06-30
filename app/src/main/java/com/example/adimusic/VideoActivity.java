package com.example.adimusic;

import static com.example.adimusic.R.id.progressbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class VideoActivity extends AppCompatActivity implements VideoAdapter.VideoClickInterface{
    RecyclerView videoRV;
    ArrayList<VideoModel> videoData;

    VideoAdapter videoAdapter;

    LinearLayout permissionDeniedLayout;

    Button vgrantPermissionBtn;

    private int currentprogress;


    @SuppressLint({"NotifyDataSetChanged", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoRV = (RecyclerView) findViewById(R.id.videorev);

//        @SuppressLint("InflateParams")
//        View view = LayoutInflater.from(this).inflate(R.layout.custom_progress_dialog, null);
//        progressBar = view.findViewById(progressbar);



        getSupportActionBar().setTitle("Video Player");

        permissionDeniedLayout = (LinearLayout) findViewById(R.id.vpermissionDeniedLayout);

        vgrantPermissionBtn = (Button) findViewById(R.id.vgrantPermissionBtn);

        permissionDeniedLayout.setVisibility(View.GONE);




        if(getIntent().getBooleanExtra("readvideopermission", false)){
            permissionDeniedLayout.setVisibility(View.VISIBLE);

        }else {

            Dialog dialog = new Dialog(this);
            dialog.setTitle("Please wait..");
            dialog.setContentView(R.layout.custom_progress_dialog);
            dialog.setCancelable(false);
            dialog.show();
//
//
//        final CountDownTimer countDownTimer = new CountDownTimer(40000, 10000) {
//            @Override
//            public void onTick(long l) {
//                currentprogress = currentprogress + 4;
//                progressBar.setProgress(currentprogress);
//                progressBar.setMax(100);
//            }
//
//            @Override
//            public void onFinish() {
//                dialog.cancel();
//                Toast.makeText(VideoActivity.this, "Scroll down to see videos", Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        countDownTimer.start();


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.cancel();
                    Toast.makeText(VideoActivity.this, "Scroll down to see videos", Toast.LENGTH_SHORT).show();
                }
            }, 40000);

        }

//        vgrantPermissionBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(VideoActivity.this, "hi", Toast.LENGTH_SHORT).show();
//                new AlertDialog.Builder(VideoActivity.this)
//                        .setTitle("Required Permission")
//                        .setMessage("To fetch your songs and videos from internal storage, please give this permission manually. Click on Settings. \n " +
//                                "App permissions -> Click on Files and media -> select Allow")
//                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                Intent intent = new Intent();
//                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                Uri uri = Uri.fromParts("package", getPackageName(), null);
//                                intent.setData(uri);
//                                startActivity(intent);
//                                dialogInterface.dismiss();
//                            }
//                        }).show();
//            }
//        });

        Handler uiHandler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent getList = getIntent();
                videoData = (ArrayList<VideoModel>) getList.getSerializableExtra("vlist");

                ConvertData(videoData);

                //videoData = new ArrayList<>();
                //uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                videoAdapter = new VideoAdapter(videoData, VideoActivity.this, VideoActivity.this, null);

                //RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                videoRV.setLayoutManager(new GridLayoutManager(VideoActivity.this, 2));

                videoRV.setAdapter(videoAdapter);
                //progressBar.setVisibility(View.GONE);

                videoAdapter.notifyDataSetChanged();
                //progressBar.setVisibility(View.GONE);

            }
        };
        uiHandler.post(runnable);

        /* working
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Intent getList = getIntent();
                videoData = (ArrayList<VideoModel>) getList.getSerializableExtra("vlist");

                ConvertData(videoData);

                videoAdapter = new VideoAdapter(videoData, VideoActivity.this, VideoActivity.this, null);

                //RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
                videoRV.setLayoutManager(new GridLayoutManager(VideoActivity.this, 2));
                dispPb.setVisibility(View.GONE);
                videoRV.setAdapter(videoAdapter);

                videoAdapter.notifyDataSetChanged();
            }
        });*/


        //uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        ///////////////////////////////////

        /*Cursor videoCursor = getContentResolver().query(uri, null, null, null, null);
        while (videoCursor != null && videoCursor.moveToNext()){ //try moveToFirst

            @SuppressLint("Range") String videoTitle = videoCursor
                    .getString(videoCursor.getColumnIndex(MediaStore.Video.Media.TITLE));

            @SuppressLint("Range") String videoPath = videoCursor
                    .getString(videoCursor.getColumnIndex(MediaStore.Video.Media.DATA));

            Bitmap videoThumb= null;
            ////////////////////
            /*try {
                videoThumb = retriveVideoFrameFromVideo(videoPath); //error
            } catch (Throwable e) {
                throw new RuntimeException(e); //error
            }
            /*try {

                videoThumb = getContentResolver().loadThumbnail(Uri.parse(videoPath),new Size(64,64),null);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }*/
            //////
            //videoThumb = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
            //////
            //Bitmap videoThumb =MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),
            // videoPath,
            // MediaStore.Images.Thumbnails.MINI_KIND,
            // (BitmapFactory.Options) null);

            ////////
            //VideoModel videoModel = new VideoModel(videoTitle, videoPath, videoThumb, uri);
            ///videoData.add(videoModel);
        //}
        //videoAdapter = new VideoAdapter(videoData, getApplicationContext(), this );
        //////////
        //videoAdapter.notifyDataSetChanged();
        /////////
        //videoRV.setAdapter(videoAdapter);

    }

//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable("videolist", videoData);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//
//        videoData = (ArrayList<VideoModel>) savedInstanceState.getSerializable("videolist");
//    }


    private void ConvertData(ArrayList<VideoModel> videoData) {

        Handler uihandler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for(VideoModel model : videoData)
                {
                    Uri urii = Uri.parse(model.getUri());

                    Bitmap videoThumb = ThumbnailUtils.
                            createVideoThumbnail(model.getVideopath(), MediaStore.Images.Thumbnails.MINI_KIND);

                    model.setUriURI(urii);
                    model.setThumbnail(videoThumb);
                }
            }
        };

        uihandler.post(runnable);
//        for(VideoModel model : videoData)
//        {
//            Uri urii = Uri.parse(model.getUri());
//
//            Bitmap videoThumb = ThumbnailUtils.
//               createVideoThumbnail(model.getVideopath(), MediaStore.Images.Thumbnails.MINI_KIND);
//
//            model.setUriURI(urii);
//            model.setThumbnail(videoThumb);
//        }
    }

    /*private Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e){
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo" + e.getMessage()); //error
        } finally {
            if (mediaMetadataRetriever != null){
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }*/

    @Override
    public void onVideoClick(int position) {
        startActivity(new Intent(getApplicationContext(), VideoPlayer.class)
                .putExtra("videoName", videoData.get(position).getVideoName())
                .putExtra("videopath", videoData.get(position).getVideopath()));
    }

    public void grantPermission(View view) {
        Log.d("VideoActivity", "step 1");
        Toast.makeText(VideoActivity.this, "hi", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
//        new AlertDialog.Builder(VideoActivity.this)
//                .setTitle("Required Permission")
//                .setMessage("To fetch your songs and videos from internal storage, please give this permission manually. Click on Settings. \n " +
//                        "App permissions -> Click on Files and media -> select Allow")
//                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent();
//                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getPackageName(), null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                        dialogInterface.dismiss();
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                }).show();
    }

    /*public void getvideos(){
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query()
    }*/
}