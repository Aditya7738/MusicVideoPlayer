package com.example.adimusic;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chibde.visualizer.BarVisualizer;

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

    String songtitle;
    ArrayList<AudioModel> songData;
    AudioModel currentSong;

    SeekBar seekBar;

    BarVisualizer barVisualizer;

    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

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

        barVisualizer = findViewById(R.id.barvisualizer);





        songData = (ArrayList<AudioModel>) getIntent().getSerializableExtra("songData");

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

    void setResourceWithMusic(){
        currentSong = songData.get(MyMediaPlayer.currentIndex);
        songtv.setText(currentSong.getTitle());
        songtv.setSelected(true);

        barVisualizer.setColor(ContextCompat.getColor(this, R.color.metallic_gold));
        barVisualizer.setDensity(70);
        barVisualizer.setPlayer(mediaPlayer.getAudioSessionId());

        totalTime.setText(convertTOMMSS(currentSong.getDuration()));

        playMusic();
        pause.setOnClickListener(view -> pauseMusic());
        next.setOnClickListener(view -> playNextMusic());
        previous.setOnClickListener(view -> playPrevousMusic());

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void pauseMusic(){
        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
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