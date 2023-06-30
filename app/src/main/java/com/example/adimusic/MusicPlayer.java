package com.example.adimusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chibde.visualizer.BarVisualizer;
import com.chibde.visualizer.SquareBarVisualizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayer extends AppCompatActivity {

    TextView songtv;

    TextView currentTime;

    TextView totalTime;
    ImageView next;
    ImageView previous;
    ImageView pause;

    ImageView repeativ, playlistiv;

    int repeatModeNo = 3;
    String songtitle;
    ArrayList<AudioModel> songData;
    AudioModel currentSong;

    SeekBar seekBar;

    SquareBarVisualizer squareBarVisualizer;

    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    LinearLayout suggestionLayout;

    Button permissionBtn;

    ImageView closeIv;

    TextView suggestionTv;

    String songname;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayer);

        getSupportActionBar().setTitle("Music Player");

        songtv = (TextView) findViewById(R.id.songname);
        next = (ImageView) findViewById(R.id.next);
        pause = (ImageView) findViewById(R.id.pause);
        previous = (ImageView) findViewById(R.id.previous);
        currentTime = (TextView) findViewById(R.id.cttv);
        totalTime = (TextView) findViewById(R.id.totalTimeTV);
        seekBar = (SeekBar) findViewById(R.id.musicseekbar);
        repeativ = (ImageView) findViewById(R.id.repeatmode);
        playlistiv = (ImageView) findViewById(R.id.playlist);

        suggestionLayout = (LinearLayout) findViewById(R.id.suggestionLayout);
        permissionBtn = (Button) findViewById(R.id.permissionBtn);
        closeIv = (ImageView) findViewById(R.id.closeSuggestion);
        suggestionTv = (TextView) findViewById(R.id.suggestionTv);

        squareBarVisualizer = findViewById(R.id.squarebarvisualizer);
        
        songData = (ArrayList<AudioModel>) getIntent().getSerializableExtra("songData");

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        setResourceWithMusic();

        MusicPlayer.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    currentTime.setText(convertTOMMSS(mediaPlayer.getCurrentPosition() + "" ));
                }
                new Handler().postDelayed(this, 100); //to run seekbar smooth
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void supportNavigateUpTo(@NonNull Intent upIntent) {
        super.supportNavigateUpTo(upIntent);
        finish();
    }

    void setResourceWithMusic(){
        currentSong = songData.get(MyMediaPlayer.currentIndex);
        songname = currentSong.getTitle();
        songtv.setText(currentSong.getTitle());
        songtv.setSelected(true);
        songtv.setEllipsize(TextUtils.TruncateAt.MARQUEE);

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
                squareBarVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
            }

        }
        totalTime.setText(convertTOMMSS(currentSong.getDuration()));

        playMusic();
        pause.setOnClickListener(view -> pauseMusic());
        next.setOnClickListener(view -> playNextMusic());
        previous.setOnClickListener(view -> playPrevousMusic());
        playlistiv.setOnClickListener(view -> navigateToSongList());
        repeativ.setOnClickListener(view -> repeatMode());

    }

    private void repeatMode() {
        if(repeatModeNo == 1){

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
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            pause.setImageDrawable(null);
            pause.setImageDrawable(getResources().getDrawable(R.drawable.baseline_play_arrow_24));

        }else {
            mediaPlayer.start();
            pause.setImageDrawable(getResources().getDrawable(R.drawable.baseline_pause_24));
        }
    }

    private void playNextMusic(){
        if(MyMediaPlayer.currentIndex == songData.size()-1){
            return;
        }
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResourceWithMusic();
    }

    private void playPrevousMusic(){
        if(MyMediaPlayer.currentIndex == 0){
            return;
        }
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResourceWithMusic();
    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }

    private String convertTOMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1), //converted to minutes
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)); //converted to seconds
    }
}