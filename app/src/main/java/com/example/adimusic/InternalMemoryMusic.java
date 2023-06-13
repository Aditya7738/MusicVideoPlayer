package com.example.adimusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InternalMemoryMusic extends AppCompatActivity {

    ListView imListView;

    AudioModel audioModel3;
    AudioModel audioModel4;
    ArrayList<AudioModel> imsongData = new ArrayList<>();
    ArrayList<String> imsongs = new ArrayList<>();

    ArrayAdapter<String> imAdapter;
    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_memory_music);

        getSupportActionBar().setTitle("Internal memory's Musics");

        imListView = (ListView) findViewById(R.id.IMsongList);

        imListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = position;
                startActivity(new Intent(getApplicationContext(), MusicPlayer.class)
                        .putExtra("songData", imsongData));
            }
        });

        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);

        List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();

        StorageVolume storageVolume = storageVolumeList.get(0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {

            //File directory = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("MyMusic")));
            //File sdfile = new File(storageVolume.getDirectory().getPath() + "/My Music/English");
            File sdfile = new File(storageVolume.getDirectory().getPath() + "/Music");
            //File sdfile = new File(storageVolume.toString() + "/Music");
            Log.d("SDfile", sdfile.getName());
            //File sdfile = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("MyMusic")));

            if (sdfile.exists()) {
                File[] list = sdfile.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.getName().endsWith(".mp3");
                    }
                });
                for (File f : list) {

                    String path = f.getPath();
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(path);
                    String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                    audioModel3 = new AudioModel(path,
                            f.getName().replace(".mp3", ""),
                            duration);

                    if (new File(audioModel3.getPath()).exists()) {
                        imsongData.add(audioModel3);
                        imsongs.add(audioModel3.getTitle());
                    }
                }

                ArrayAdapter sdadapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, imsongs);
                imListView.setAdapter(sdadapter);
            }

            ///////////////////////////

        }else{
            String state = Environment.getExternalStorageState();
            if(Environment.MEDIA_MOUNTED.equals(state)){
                File musicDir = new File(Environment.getExternalStorageDirectory().getPath() + "/Music/");
                if(musicDir.exists()){
                    File[] files = musicDir.listFiles();
                    if (files != null) {
                        for(File file : files){
                            if(file.getName().endsWith(".mp3")){

                                String path = file.getPath();
                                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                                mmr.setDataSource(path);
                                String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

                                audioModel4 = new AudioModel(path,
                                        file.getName().replace(".mp3", ""),
                                        duration);

                                if (new File(audioModel4.getPath()).exists()) {
                                    imsongData.add(audioModel4);
                                    imsongs.add(audioModel4.getTitle());
                                }
                            }
                        }
                    }
                    ArrayAdapter imadapter2 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, imsongs);
                    imListView.setAdapter(imadapter2);
                }
            }
        }
    }

        ////////////working//////////////////////////////////////////////////////////
//
//        String[] proj = {MediaStore.Audio.Media.TITLE,
//                MediaStore.Audio.Media.DATA,
//                MediaStore.Audio.Media.DURATION,
//                MediaStore.Audio.Media.DISPLAY_NAME};
//
//        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
//
//        Cursor audioCursor = getContentResolver()
//                .query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, proj, selection,null,null);
//
//        while(audioCursor.moveToNext()) {
//            AudioModel audioModel = new AudioModel(audioCursor.getString(1),
//                    audioCursor.getString(0),
//                    audioCursor.getString(2));
//
//            if (new File(audioModel.getPath()).exists()) {
//                imsongData.add(audioModel);
//                imsongs.add(audioModel.getTitle());
//
//            }
//        }
//
//
//        ArrayAdapter<String> imadapter = new ArrayAdapter<>(this,
//                android.R.layout.simple_list_item_1, imsongs);
//        imListView.setAdapter(imadapter);

        ////////////////////////////////////////////////////////////////////////////////////
        //same code

        /*ArrayList<String> audioList = new ArrayList<>();

        String[] proj = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME};

        Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null,null,null);

        if(audioCursor != null) {
            if (audioCursor.moveToFirst()) {
                do {
                    int audioIndex = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);

                    audioList.add(audioCursor.getString(audioIndex));

                } while (audioCursor.moveToNext());
            }
        }
        if (audioCursor != null) {
            audioCursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, audioList);
        IMList.setAdapter(adapter);*/
    }