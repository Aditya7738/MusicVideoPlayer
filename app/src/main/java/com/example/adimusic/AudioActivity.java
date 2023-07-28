package com.example.adimusic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chibde.visualizer.SquareBarVisualizer;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class AudioActivity extends AppCompatActivity {
    ListView listView;
    TextView homesongtv, denyTxt;

    ArrayList<AudioModel> songData;
    ArrayList<String> songs;

    ArrayAdapter<String> adapter;

    RelativeLayout homeaudioWrapper;

    ImageView homepreviousiv, homepauseiv, homeskipiv;
    RelativeLayout permissionDeniedLayout;

    Button grantPermissionBtn;

    ExoPlayer exoPlayer;

    ///musicplayersview
    TextView songtitle, currentTime, totalTime;
    ImageView next, previous, pause, repeativ, playlistiv;

    int repeatModeNo = 3;

    SeekBar seekBar;

    SquareBarVisualizer squareBarVisualizer;

    LinearLayout suggestionLayout;

    RelativeLayout musicPlayerLayout;

    Button permissionBtn;
    ImageView backBtn;

    ImageView closeIv;

    TextView suggestionTv;

    AudioModel audioModel;
    private int currentIndex;

    Menu menu;
    MenuItem item;

    SearchView searchView;
    private boolean listViewClicked = false;

    boolean noFileFound;

    boolean permissionDenied;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        listView = findViewById(R.id.songList);

        homesongtv = findViewById(R.id.homeSongName);

        homepreviousiv = findViewById(R.id.previousiv);
        homepauseiv = findViewById(R.id.pauseiv);
        homeskipiv = findViewById(R.id.skipiv);


        homeaudioWrapper = findViewById(R.id.homemusicview);

        permissionDeniedLayout = (RelativeLayout) findViewById(R.id.permissionDeniedLayout);

        musicPlayerLayout = findViewById(R.id.musicplayerView);

        grantPermissionBtn = (Button) findViewById(R.id.grantPermissionBtn);
        denyTxt = (TextView) findViewById(R.id.denyTxt);

        permissionDeniedLayout.setVisibility(View.GONE);

        exoPlayer = new ExoPlayer.Builder(AudioActivity.this).build();

        /////////////musicplayer view ids
        backBtn = (ImageView) findViewById(R.id.backBtn);
        songtitle = (TextView) findViewById(R.id.songname);
        next = (ImageView) findViewById(R.id.next);
        pause = (ImageView) findViewById(R.id.pause);
        previous = (ImageView) findViewById(R.id.previous);
        currentTime = (TextView) findViewById(R.id.currentTv);
        totalTime = (TextView) findViewById(R.id.totalTimeTV);
        seekBar = (SeekBar) findViewById(R.id.musicseekbar);
        repeativ = (ImageView) findViewById(R.id.repeatmode);
        playlistiv = (ImageView) findViewById(R.id.playlist);

        suggestionLayout = (LinearLayout) findViewById(R.id.suggestionLayout);
        permissionBtn = (Button) findViewById(R.id.permissionBtn);
        closeIv = (ImageView) findViewById(R.id.closeSuggestion);
        suggestionTv = (TextView) findViewById(R.id.suggestionTv);

        squareBarVisualizer = findViewById(R.id.squarebarvisualizer);


//        if(getIntent().getBooleanExtra("readaudiopermission", false)){
//            permissionDeniedLayout.setVisibility(View.VISIBLE);
//            homeaudioWrapper.setVisibility(View.GONE);
//            grantPermissionBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(AudioActivity.this, "hi", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent();
//                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                    Uri uri = Uri.fromParts("package", getPackageName(), null);
//                    intent.setData(uri);
//                    startActivity(intent);
////                    new AlertDialog.Builder(AudioActivity.this)
////                            .setTitle("Required Permission")
////                            .setMessage("To fetch your songs and videos from internal storage, please give this permission manually. Click on Settings. \n " +
////                                    "App permissions -> Click on Files and media -> select Allow")
////                            .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
////                                @Override
////                                public void onClick(DialogInterface dialogInterface, int i) {
////                                    Intent intent = new Intent();
////                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
////                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
////                                    intent.setData(uri);
////                                    startActivity(intent);
////                                    dialogInterface.dismiss();
////                                }
////                            }).show();
//                }
//            });
//        }

        getSupportActionBar().setTitle("Music Player");

        permissionDenied = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
        if(permissionDenied){
            permissionDeniedLayout.setVisibility(View.VISIBLE);
            grantPermissionBtn.setOnClickListener(view -> giveStoragePermission());
            homeaudioWrapper.setVisibility(View.GONE);
            //searchView.setEnabled(false);
        }else {


            songData = new ArrayList<>();
            songs = new ArrayList<>();

            String[] proj = {

                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DURATION};

            //String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";

            Uri externalContentUri;

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
//            externalContentUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
//        }else{
//            externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//        }
            externalContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

            //String sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";

            Cursor audioCursor = getContentResolver()
                    .query(externalContentUri, proj, null, null, null);

            //int idColumn = audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

            while (audioCursor.moveToNext()) {

                Log.d("Inside Curor", "****************************");

                //long id = audioCursor.getLong(audioCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                //Uri uri = Uri.parse(audioModel.getPath());


                AudioModel audioModel = new AudioModel(audioCursor.getString(1),
                        audioCursor.getString(0),
                        audioCursor.getString(2),
                        externalContentUri);


                if (new File(audioModel.getPath()).exists()) {
                    Log.d("AudioLog", audioModel.getTitle());
                    songData.add(audioModel);
                    songs.add(audioModel.getTitle());

                }

            }

            adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, songs);
            listView.setAdapter(adapter);

            noFileFound = songData.size() == 0;
            if (noFileFound) {
                Toast.makeText(this, "There are no songs", Toast.LENGTH_LONG).show();
            }
        }



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                listViewClicked = true;
                showMusicPlayerView();
//                MyMediaPlayer.getInstance().reset();
//                MyMediaPlayer.currentIndex = position;
//                if(exoPlayer == null){
//                    Toast.makeText(AudioActivity.this, "exoplayer is null", Toast.LENGTH_SHORT).show();
//                    Log.d("AUDIOACTIVITY","exoplayer is null");
//                }
//

//                audioModel = songData.get(position);
//                Uri uri = Uri.parse(audioModel.getPath());


                if(!exoPlayer.isPlaying()){
                    exoPlayer.setMediaItems(getMediaItems(), position, 0);
                }else{
                    exoPlayer.pause();
                    exoPlayer.seekTo(position, 0);
                }

                exoPlayer.prepare();
                exoPlayer.play();

            }
        });

        homeaudioWrapper.setOnClickListener(view -> showMusicPlayerView());


        setResourceOfMusicPlayerView();

        setResourceOfHomeMusicPlayer();
    }

    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItemList = new ArrayList<>();

        for (AudioModel audioModel1: songData){
            MediaItem mediaItem = new MediaItem.Builder()
//                    .setUri(audioModel1.getUri())
                    .setUri(Uri.parse(audioModel1.getPath()))
                    .setMediaMetadata(getMediaMetaData(audioModel1))
                    .build();

            //MediaItem mediaItem = MediaItem.fromUri(audioModel1.getPath())

            mediaItemList.add(mediaItem);
        }
        return mediaItemList;
    }

    private MediaMetadata getMediaMetaData(AudioModel audioModel1) {
        return new MediaMetadata.Builder()
                .setTitle(audioModel1.getTitle())
                .build();
    }

    private MediaSource buildMediaSource(Uri uri) {
//        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "ExoPlayerSample");
        String userAgent = Util.getUserAgent(this, "ExoPlayerSample");
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
        MediaItem mediaItem = MediaItem.fromUri(uri);
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem);
    }

    @SuppressLint("RestrictedApi")
    private void showMusicPlayerView() {
        if(!listViewClicked){
            Toast.makeText(this, "You don't have selected song", Toast.LENGTH_LONG).show();
        } else {
            musicPlayerLayout.setVisibility(View.VISIBLE);
            //Objects.requireNonNull(getSupportActionBar()).dispatchMenuVisibilityChanged(true);
        }
    }

    private void setResourceOfHomeMusicPlayer() {

        homesongtv.setSelected(true);
        homesongtv.setEllipsize(TextUtils.TruncateAt.MARQUEE);


        homepreviousiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayer.hasPreviousMediaItem()){
                    exoPlayer.seekToPrevious();
                }
            }
        });

        homeskipiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (exoPlayer.hasNextMediaItem()){
                    exoPlayer.seekToNext();
                }
            }
        });

        homepauseiv.setOnClickListener(view -> commonPlayPauseFun());

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void commonPlayPauseFun() {
        if (exoPlayer.isPlaying()) {
            exoPlayer.pause();
            homepauseiv.setImageDrawable(getResources().getDrawable(R.drawable.baseline_play_arrow2));

            pause.setImageDrawable(getResources().getDrawable(R.drawable.baseline_play_arrow_24));
        } else {
            exoPlayer.play();
            homepauseiv.setImageDrawable(getResources().getDrawable(R.drawable.baseline_pause2));

            pause.setImageDrawable(getResources().getDrawable(R.drawable.baseline_pause_24));
        }
    }


    @Override
    public void onBackPressed() {
        if(musicPlayerLayout.getVisibility() == View.VISIBLE){
            navigateToSongList();
        }else {
            super.onBackPressed();
        }
    }

    void setResourceOfMusicPlayerView(){

        backBtn.setOnClickListener(view -> navigateToSongList());

        songtitle.setSelected(true);
        songtitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);


        suggestionLayout.setVisibility(View.GONE);

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            squareBarVisualizer.setEnabled(false);

            suggestionLayout.setVisibility(View.VISIBLE);

            suggestionTv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            suggestionTv.setSelected(true);

            permissionBtn.setOnClickListener(view -> givePermission());
            closeIv.setOnClickListener(view -> closeSuggestion());
        }else {

                squareBarVisualizer.setEnabled(true);
                suggestionLayout.setVisibility(View.GONE);
                squareBarVisualizer.setColor(ContextCompat.getColor(this, R.color.metallic_gold));
                squareBarVisualizer.setDensity(65);

                squareBarVisualizer.setGap(2);
                squareBarVisualizer.setPlayer(exoPlayer.getAudioSessionId());
            }

        //playMusic();
        pause.setOnClickListener(view -> commonPlayPauseFun());
        next.setOnClickListener(view -> playNextMusic());
        previous.setOnClickListener(view -> playPrevousMusic());
        playlistiv.setOnClickListener(view -> navigateToSongList());
        repeativ.setOnClickListener(view -> repeatMode());

        exoPlayer.addListener(new Player.Listener() {

            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                assert mediaItem != null;
                songtitle.setText(mediaItem.mediaMetadata.title);
                homesongtv.setText(mediaItem.mediaMetadata.title);

                currentTime.setText(convertTOMMSS(exoPlayer.getCurrentPosition()));
                seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                seekBar.setMax((int) exoPlayer.getDuration());
                totalTime.setText(convertTOMMSS(exoPlayer.getDuration()));

                updateSeekbarProgress();

                //set audio visualizer

//                if(!exoPlayer.isPlaying()){
//                    exoPlayer.play();
//                }
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if(playbackState == ExoPlayer.STATE_READY){
                    songtitle.setText(Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).mediaMetadata.title);
                    homesongtv.setText(Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).mediaMetadata.title);

                    currentTime.setText(convertTOMMSS(exoPlayer.getCurrentPosition()));
                    totalTime.setText(convertTOMMSS(exoPlayer.getDuration()));
                    seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                    seekBar.setMax((int) exoPlayer.getDuration());

                    updateSeekbarProgress();

                    //set audio visualizer
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if(exoPlayer != null && fromUser){
//                    exoPlayer.seekTo(progress);
//                    progressValue = seekBar.getProgress();
//                }
                progressValue = seekBar.getProgress();

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //can comment
                if(exoPlayer.getPlaybackState() == ExoPlayer.STATE_READY){
                    seekBar.setProgress(progressValue);
                    currentTime.setText(convertTOMMSS((long) progressValue));
                    exoPlayer.seekTo((long) progressValue);
                }
            }
        });

    }

    private void updateSeekbarProgress() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(exoPlayer.isPlaying()){
                    currentTime.setText(convertTOMMSS(exoPlayer.getCurrentPosition()));
                    seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                }
                updateSeekbarProgress();
            }
        }, 1000);
    }


    private void closeSuggestion() {
        suggestionLayout.setVisibility(View.GONE);
    }

    private void givePermission() {
//        Intent intent = new Intent();
//        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        Uri uri = Uri.fromParts("package", getPackageName(), null);
//        intent.setData(uri);
//        startActivity(intent);
        Log.d("AUDIOACTIVITY", "CLICKED");
        new AlertDialog.Builder(this)
                .setTitle("Required Permission")
                .setMessage("To experience audio visualizer with music, please give record audio permission manually. Click on Settings. \n " +
                        "App permissions -> Click on Microphone -> select Allow \n" + "Close app and start again to get effect" )
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

                    }
                }).show();

    }

    private void giveStoragePermission() {

        new AlertDialog.Builder(this)
                .setTitle("Required Permission")
                .setMessage("To fetch your songs and videos from internal storage, please give this permission manually. Click on Settings.\n " +
                        "App permissions -> Click on Files and media -> select Allow \n" + "Close the app and start again to get effect" )
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

                    }
                }).show();

    }







    private void repeatMode() {
        if(repeatModeNo == 1){
            exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
            repeatModeNo = 2;
            repeativ.setImageResource(R.drawable.baseline_repeat_one_24);

        } else if (repeatModeNo == 2) {
            exoPlayer.setShuffleModeEnabled(true);
            exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
            repeatModeNo = 3;
            repeativ.setImageResource(R.drawable.baseline_shuffle_24);

        } else if (repeatModeNo == 3) {
            exoPlayer.setShuffleModeEnabled(false);
            exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
            repeatModeNo = 1;
            repeativ.setImageResource(R.drawable.baseline_repeat_24);
        }
    }

    private void playNextMusic(){

        if (exoPlayer.hasNextMediaItem()){
            exoPlayer.seekToNext();
        }

        ///check this if make effect otherwise make common method for both musicplayer and home player skip button
        setResourceOfMusicPlayerView();
    }

    private void playPrevousMusic(){

        if (exoPlayer.hasPreviousMediaItem()){
            exoPlayer.seekToPrevious();
        }
        setResourceOfMusicPlayerView();
    }

    private void navigateToSongList() {
        musicPlayerLayout.setVisibility(View.GONE);
    }


    @SuppressLint("DefaultLocale")
    private String convertTOMMSS(long duration) {
        //Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1), //converted to minutes
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)); //converted to seconds
    }

//    private void showOptionMenu(Menu menu, MenuItem item){
//        onCreateOptionsMenu(menu);
//        onOptionsItemSelected(item);
//    }

    @SuppressLint("ResourceAsColor")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuoptions, menu);

        MenuItem menuItem = menu.findItem(R.id.searchbar);
        searchView = (SearchView) menuItem.getActionView();

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(noFileFound){

                }
            }
        });

        searchView.setQueryHint("Search for music");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if(permissionDenied){
                    Toast.makeText(AudioActivity.this, "You have denied permission! So, cannot access your files to search", Toast.LENGTH_LONG).show();
                } else if (noFileFound) {
                    Toast.makeText(AudioActivity.this, "No music files found to search", Toast.LENGTH_LONG).show();
                } else {
                    adapter.getFilter().filter(newText);
                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(exoPlayer.isPlaying()){
            exoPlayer.stop();
        }
        exoPlayer.release();
    }
}