package com.example.adimusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

    TextView songtv;




    ArrayList<AudioModel> songData;
    ArrayList<String> songs;

    ArrayAdapter<String> adapter;

    RelativeLayout homeaudioWrapper;

    LinearLayout permissionDeniedLayout;

    Button grantPermissionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        listView = (ListView) findViewById(R.id.songList);

        homeaudioWrapper = (RelativeLayout) findViewById(R.id.homemusicview);

        permissionDeniedLayout = (LinearLayout) findViewById(R.id.permissionDeniedLayout);

        grantPermissionBtn = (Button) findViewById(R.id.grantPermissionBtn);

        permissionDeniedLayout.setVisibility(View.GONE);





        if(getIntent().getBooleanExtra("readaudiopermission", false)){
            permissionDeniedLayout.setVisibility(View.VISIBLE);
            homeaudioWrapper.setVisibility(View.GONE);
            grantPermissionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(AudioActivity.this, "hi", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
//                    new AlertDialog.Builder(AudioActivity.this)
//                            .setTitle("Required Permission")
//                            .setMessage("To fetch your songs and videos from internal storage, please give this permission manually. Click on Settings. \n " +
//                                    "App permissions -> Click on Files and media -> select Allow")
//                            .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    Intent intent = new Intent();
//                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
//                                    intent.setData(uri);
//                                    startActivity(intent);
//                                    dialogInterface.dismiss();
//                                }
//                            }).show();
                }
            });
        }



        getSupportActionBar().setTitle("Music Player");

        homeaudioWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AudioActivity.this, MusicPlayer.class));

//                startActivity(new Intent(getApplicationContext(), MusicPlayer.class)
//                        .putExtra("songData", songData)
//                        .putExtra("recordAudioPermDenied", getIntent().getBooleanExtra("recordaudiopermissionDenied", true)));
            }
        });

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

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, songs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = position;


                startActivity(new Intent(getApplicationContext(), MusicPlayer.class)
                        .putExtra("songData", songData)
                        .putExtra("recordAudioPermDenied", getIntent().getBooleanExtra("recordaudiopermissionDenied", true))
                );
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuoptions, menu);

        MenuItem menuItem = menu.findItem(R.id.searchbar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search for music");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return false;
            }
        });

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