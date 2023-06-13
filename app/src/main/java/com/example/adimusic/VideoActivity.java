package com.example.adimusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.widget.AdapterView;
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

    ProgressBar dispPb;

    Uri uri;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoRV = (RecyclerView) findViewById(R.id.videorev);
        dispPb = (ProgressBar) findViewById(R.id.displayingVideo);

        getSupportActionBar().setTitle("Video Player");

        dispPb.setVisibility(View.VISIBLE);

        Toast.makeText(this, "Scroll up slowly to get videos", Toast.LENGTH_LONG).show();

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
                dispPb.setVisibility(View.GONE);
                videoRV.setAdapter(videoAdapter);

                videoAdapter.notifyDataSetChanged();
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

    /*public void getvideos(){
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = contentResolver.query()
    }*/
}