package com.example.adimusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioActivity extends AppCompatActivity {
    ListView listView;



    ArrayList<AudioModel> songData;
    ArrayList<String> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        listView = (ListView) findViewById(R.id.songList);

        getSupportActionBar().setTitle("Music Player");

        songData = new ArrayList<>();
        songs = new ArrayList<>();
        String[] proj = {MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION};

        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

        Cursor audioCursor = getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, selection,null,null);

        while(audioCursor != null && audioCursor.moveToNext()){

            Log.d("Inside Curor","****************************");

            /*String path = audioCursor.getString(1);
            String title = audioCursor.getString(0);
            String duration = audioCursor.getString(2);
            AudioModel audioModel = new AudioModel(audioCursor.getString(1),
                    audioCursor.getString(0),
                    audioCursor.getString(2));*/

            AudioModel audioModel = new AudioModel(audioCursor.getString(1),
                    audioCursor.getString(0),
                    audioCursor.getString(2));


            if(new File(audioModel.getPath()).exists()) {
                Log.d("AudioLog",audioModel.getTitle());
                songData.add(audioModel);
                songs.add(audioModel.getTitle());

            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = position;


                startActivity(new Intent(getApplicationContext(), MusicPlayer.class)
                        .putExtra("songData", songData)
                );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuoptions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.inm:
                startActivity(new Intent(this, InternalMemoryMusic.class));
                break;
            case R.id.sdm:
                Handler handler = new Handler(Looper.getMainLooper());
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);

                        List<StorageVolume> storageVolumeList = storageManager.getStorageVolumes();

                        //StorageVolume storageVolume = null;
                        boolean lesstwo = storageVolumeList.size() == 1;

                        if (lesstwo) {
                            Toast.makeText(AudioActivity.this, "SD card is not inserted", Toast.LENGTH_LONG).show();
                            //startActivity(new Intent(AudioActivity.this, AudioActivity.class));

                        }else {
                            startActivity(new Intent(AudioActivity.this, SDmusic.class));
                        }

                    }
                };
                handler.post(runnable);


                break;
        }

        return true;
    }
}