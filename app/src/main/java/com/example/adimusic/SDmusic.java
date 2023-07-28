package com.example.adimusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
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
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class SDmusic extends AppCompatActivity {

    ListView sdlist;

    ArrayList<String> sdmusic;

    TextView sdtv;

    AudioModel audioModel1;
    AudioModel audioModel2;

    ArrayList<AudioModel> songData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdmusic);
        sdlist = (ListView) findViewById(R.id.SDsongList);
        sdtv = (TextView) findViewById(R.id.sdtext);

        getSupportActionBar().setTitle("SD card's Musics");

        sdmusic = new ArrayList<>();

        songData = new ArrayList<>();

        sdlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = position;


                startActivity(new Intent(getApplicationContext(), MusicPlayer.class)
                        .putExtra("songData", songData)
                );
            }
        });
        // android version => R

        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);

        List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();

        StorageVolume storageVolume = storageVolumeList.get(1);

        Uri externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

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


                    audioModel1 = new AudioModel(path,
                            f.getName().replace(".mp3", ""),
                            duration, externalContentUri);

                    if (new File(audioModel1.getPath()).exists()) {
                        songData.add(audioModel1);
                        sdmusic.add(audioModel1.getTitle());
                }
            }

            ArrayAdapter sdadapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, sdmusic);
            sdlist.setAdapter(sdadapter);
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

                                audioModel2 = new AudioModel(path,
                                        file.getName().replace(".mp3", ""),
                                        duration, externalContentUri);

                                if (new File(audioModel2.getPath()).exists()) {
                                    songData.add(audioModel2);
                                    sdmusic.add(audioModel2.getTitle());
                                }
                            }
                        }
                    }
                    ArrayAdapter sdadapter2 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, sdmusic);
                    sdlist.setAdapter(sdadapter2);
                }
            }
        }
    }

        /////////////////////////
        /*Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        String[] proj = {MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST};

        Cursor audioCursor = getContentResolver().query(uri, proj, selection, null, null);
        if (audioCursor != null) {
            while (audioCursor.moveToNext()) {
                //uri = audioCursor.getString(audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                audioModel = new AudioModel(audioCursor.getString(1),
                        audioCursor.getString(0),
                        audioCursor.getString(2));

                if (new File(audioModel.getPath()).exists()) {
                    songData.add(audioModel);
                    sdmusic.add(audioModel.getTitle());
                }

            }*/
            ///////////////////////////////working///////////////////
       /* if(Environment.getExternalStorageState() == Environment.MEDIA_REMOVED){
            sdtv.setText("SD card not inserted");
            Toast.makeText(getApplicationContext(), "SD card not found", Toast.LENGTH_LONG);
        }else{*/

            /////////////////////////////

            //if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            //error below
            /*File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
            if(dir.exists()){
                File[] list = dir.listFiles();
                for(int i =  0; i< list.length; i++){
                    myList.add(list[i].getName());
                }

                ArrayAdapter sdadapter =
                        new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, myList);
                sdlist.setAdapter(sdadapter);
            }*/


            /////////////working/////////////////////
           /* File directory = new File(String.valueOf(Environment.getExternalStoragePublicDirectory("Music")));
                    File[] musicfiles = directory.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return file.getName().endsWith(".mp3");
                        }
                    });

            for (File f: musicfiles){
                sdmusic.add(f.getName());
            }*/

            /*ArrayAdapter<String> sdarrayAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, sdmusic);
            sdlist.setAdapter(sdarrayAdapter);*/
}

        ///////////////////////////////////////////



