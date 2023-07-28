package com.example.adimusic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chibde.visualizer.SquareBarVisualizer;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MusicPlayer extends AppCompatActivity {

    TextView songtv, currentTime, totalTime;
    ImageView next, previous, pause, repeativ, playlistiv;

    int repeatModeNo = 3;

    ArrayList<AudioModel> songData;


    SeekBar seekBar;

    SquareBarVisualizer squareBarVisualizer;

    ExoPlayer exoPlayer;

    LinearLayout suggestionLayout;

    Button permissionBtn;

    ImageView closeIv;

    TextView suggestionTv;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayer);

        getSupportActionBar().setTitle("Music Player");

        exoPlayer = new ExoPlayer.Builder(this).build();

        songtv = (TextView) findViewById(R.id.songname);
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

        songData = (ArrayList<AudioModel>) getIntent().getParcelableExtra("songData");
        //exoPlayer = getIntent().getParcelableExtra("exoplayer");
        //exoPlayer = AudioActivity.getExoPlayer();

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        setResourceWithMusic();

        MusicPlayer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(exoPlayer != null){
                    seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                    currentTime.setText(convertTOMMSS(exoPlayer.getCurrentPosition()));
                }
                new Handler().postDelayed(this, 100); //to run seekbar smooth
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if(exoPlayer != null && fromUser){
                    exoPlayer.seekTo(progress);
                    progressValue = seekBar.getProgress();
                }
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

    @Override
    public void supportNavigateUpTo(@NonNull Intent upIntent) {
        super.supportNavigateUpTo(upIntent);
        finish();
    }

    void setResourceWithMusic(){
        //audioModel = songData.get(MyMediaPlayer.currentIndex);
        //songname = audioModel.getTitle();

        if(!exoPlayer.isPlaying()){
            exoPlayer.setMediaItems(getMediaItems());
        }
        exoPlayer.prepare();
        exoPlayer.play();

        suggestionLayout.setVisibility(View.GONE);
        if(getIntent().getBooleanExtra("recordAudioPermDenied", true)){
            squareBarVisualizer.setEnabled(false);

            suggestionLayout.setVisibility(View.VISIBLE);

            suggestionTv.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            suggestionTv.setSelected(true);

            permissionBtn.setOnClickListener(view -> givePermission());
            closeIv.setOnClickListener(view -> closeSuggestion());

        }else {

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {

                squareBarVisualizer.setEnabled(true);
                suggestionLayout.setVisibility(View.GONE);
                squareBarVisualizer.setColor(ContextCompat.getColor(this, R.color.metallic_gold));
                squareBarVisualizer.setDensity(65);

                squareBarVisualizer.setGap(2);
                squareBarVisualizer.setPlayer(exoPlayer.getAudioSessionId());
            }

        }

        //playMusic();
        pause.setOnClickListener(view -> pauseMusic());
        next.setOnClickListener(view -> playNextMusic());
        previous.setOnClickListener(view -> playPrevousMusic());
        playlistiv.setOnClickListener(view -> navigateToSongList());
        repeativ.setOnClickListener(view -> repeatMode());

        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                assert mediaItem != null;
                songtv.setText(mediaItem.mediaMetadata.title);
                songtv.setSelected(true);
                songtv.setEllipsize(TextUtils.TruncateAt.MARQUEE);

                currentTime.setText(convertTOMMSS(exoPlayer.getCurrentPosition()));
                seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                seekBar.setMax((int) exoPlayer.getDuration());
                totalTime.setText(convertTOMMSS(exoPlayer.getDuration()));

                if(!exoPlayer.isPlaying()){

                    exoPlayer.play();
                }
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                if(playbackState == ExoPlayer.STATE_READY){
                    songtv.setText(Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).mediaMetadata.title);
                    currentTime.setText(convertTOMMSS(exoPlayer.getCurrentPosition()));
                    totalTime.setText(convertTOMMSS(exoPlayer.getDuration()));
                    seekBar.setProgress((int) exoPlayer.getCurrentPosition());
                    seekBar.setMax((int) exoPlayer.getDuration());
                }
            }


        });

    }

    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItemList = new ArrayList<MediaItem>();

        for (AudioModel audioModel1: songData){
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(audioModel1.getUri())
                    .setMediaMetadata(getMediaMetaData(audioModel1))
                    .build();

            mediaItemList.add(mediaItem);
        }
        return mediaItemList;
    }

    private MediaMetadata getMediaMetaData(AudioModel audioModel1) {
        return new MediaMetadata.Builder()
                .setTitle(audioModel1.getTitle())
                .build();
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

    private void navigateToSongList() {
        startActivity(new Intent(this, AudioActivity.class));
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


    @SuppressLint("UseCompatLoadingForDrawables")
    private void pauseMusic(){
        if(exoPlayer.isPlaying()) {
            exoPlayer.pause();
            pause.setImageDrawable(null);
            pause.setImageDrawable(getResources().getDrawable(R.drawable.baseline_play_arrow_24));

        }else {
            exoPlayer.play();
            pause.setImageDrawable(getResources().getDrawable(R.drawable.baseline_pause_24));
        }
    }

    private void playNextMusic(){
//        if(MyMediaPlayer.currentIndex == songData.size()-1){
//            return;
//        }
//        MyMediaPlayer.currentIndex += 1;
        if (exoPlayer.hasNextMediaItem()){
            exoPlayer.seekToNext();
        }
        setResourceWithMusic();
    }

    private void playPrevousMusic(){
//        if(MyMediaPlayer.currentIndex == 0){
//            return;
//        }
//        MyMediaPlayer.currentIndex -= 1;
//        exoPlayer.reset();
        if (exoPlayer.hasPreviousMediaItem()){
            exoPlayer.seekToPrevious();
        }
        setResourceWithMusic();
    }

//    private void playMusic(){
//        mediaPlayer.reset();
//        try {
//            mediaPlayer.setDataSource(currentSong.getPath());
//            exoPlayer.prepare();
//            mediaPlayer.start();
//            seekBar.setProgress(0);
//            seekBar.setMax((int) exoPlayer.getDuration());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private String convertTOMMSS(long duration) {
        //Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1), //converted to minutes
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)); //converted to seconds
    }


}