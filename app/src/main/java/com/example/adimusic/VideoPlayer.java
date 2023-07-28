package com.example.adimusic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.concurrent.TimeUnit;

public class VideoPlayer extends AppCompatActivity {

    TextView vtitletv, ctimetv, etimetv;
    SeekBar vseekbar;
    ImageButton replay10ib, for10ib, pause;

    //ImageView backBtn;
    String videoname, videopath;

    int vduration;

    VideoView videoView;
    RelativeLayout videocontrols, videoRL;
    boolean isOpen = true; //to check custom controls are visible or not
    private int videoprogress;
    private String currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        vtitletv = (TextView) findViewById(R.id.videotitle);
        ctimetv = (TextView) findViewById(R.id.ctime);
        etimetv = (TextView) findViewById(R.id.etime);

        replay10ib = (ImageButton) findViewById(R.id.replay10);
        pause = (ImageButton) findViewById(R.id.pausev);
        for10ib = (ImageButton) findViewById(R.id.forward10);
        //backBtn = (ImageView) findViewById(R.id.backBtn);

        vseekbar = (SeekBar) findViewById(R.id.vseekbar);

        videoView = (VideoView) findViewById(R.id.videoview);

        videocontrols = (RelativeLayout) findViewById(R.id.videocontrols);
        videoRL = (RelativeLayout) findViewById(R.id.rlvideo);


        Intent intent = getIntent();
        videoname = intent.getStringExtra("videoName");
        videopath = intent.getStringExtra("videopath");

        vduration = videoView.getDuration();

        videoView.setVideoURI(Uri.parse(videopath));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                vseekbar.setMax(videoView.getDuration());
                videoView.start();
            }
        });


        vtitletv.setText(videoname + ".mp4");

        Log.d("Vidoendtime", "" + convertTime(videoView.getDuration()));

        etimetv.setText("" + convertTime(videoView.getDuration()));

        //etimetv.setText(convertTOMMSS(videoView.getDuration() + ""));

        int vcrntpos = videoView.getCurrentPosition();

        replay10ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.seekTo(videoView.getCurrentPosition() - 10000);
            }
        });

        for10ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoView.seekTo(videoView.getCurrentPosition() + 10000);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(videoView.isPlaying()){
                    videoView.pause();

                    pause.setImageDrawable(getResources().getDrawable(R.drawable.baseline_play_circle_outline_24));
                }else{
                    videoView.start();
                    pause.setImageDrawable(getResources().getDrawable(R.drawable.baseline_pause_circle_outline_24));
                }
            }
        });

//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(VideoPlayer.this, VideoActivity.class));
//            }
//        });

        videoRL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    hideControls();
                    isOpen = false;
                }else {
                    showControls();
                    isOpen = true;
                }
            }
        });

        sethandler();

        vseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(vseekbar.getId() == R.id.vseekbar){
                    if(fromUser){
                        videoprogress = progress;
                        videoView.seekTo(progress);
                        videoView.start();
                        //int currentPosition = videoView.getCurrentPosition();
                        //ctimetv.setText("" + convertTime(videoView.getDuration() - currentPosition));
                        ctimetv.setText("" + convertTime(videoView.getCurrentPosition()));
                        currentPosition = convertTime(videoView.getCurrentPosition());

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //initializeseekbar();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seekprogress", videoprogress);
        outState.putString("seekcurrentpos", currentPosition);
        //outState.putString("taskpriority", taskpriority);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        videoView.seekTo(savedInstanceState.getInt("seekprogress"));
        ctimetv.setText(savedInstanceState.getString("seekcurrentpos"));
    }

    private void sethandler(){
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(videoView.getDuration() > 0){
                    int curntpos = videoView.getCurrentPosition();
                    vseekbar.setProgress(curntpos);
                    ctimetv.setText("" + convertTime(videoView.getCurrentPosition()));
                    etimetv.setText("" + convertTime(videoView.getDuration()));
                }

                handler.postDelayed(this, 0);

            }
        };

        handler.postDelayed(runnable, 500);
    }

    private void showControls() {
        videocontrols.setVisibility(View.VISIBLE);

        final Window window = this.getWindow();
        if(window == null){
            return;
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View decorview = window.getDecorView();
        if(decorview != null){
            int uiOption = decorview.getSystemUiVisibility();
            if(Build.VERSION.SDK_INT >= 14){
                uiOption &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            if(Build.VERSION.SDK_INT >= 16){
                uiOption &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if(Build.VERSION.SDK_INT >= 19){
                uiOption &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            decorview.setSystemUiVisibility(uiOption);

        }
    }

    private void hideControls() {
        videocontrols.setVisibility(View.GONE);

        final Window window = this.getWindow();
        if(window == null){
            return;
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        View decorview = window.getDecorView();
        if(decorview != null){
            int uiOption = decorview.getSystemUiVisibility();
            if(Build.VERSION.SDK_INT >= 14){
                uiOption |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
            }
            if(Build.VERSION.SDK_INT >= 16){
                uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }
            if(Build.VERSION.SDK_INT >= 19){
                uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            }

            decorview.setSystemUiVisibility(uiOption);

        }
    }

    private void initializeseekbar(){
        vseekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(vseekbar.getId() == R.id.vseekbar){
                    if(fromUser){
                        videoView.seekTo(progress);
                        videoView.start();
                        ctimetv.setText("" + convertTime(videoView.getDuration() - videoView.getCurrentPosition()));
                    }
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

    private String convertTime(int ms){
        String time;
        int x, seconds, mins, hours;
        x = ms/1000;
        seconds = x % 60;
        x /= 60;
        mins = x % 60;
        x /= 60;
        hours = x % 24;
        if(hours != 0){
            time = String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", seconds); //showing in hours:mins:sec format
        }else{
            time = String.format("%02d", mins) + ":" + String.format("%02d", seconds); //showing in min:sec format
        }
        return time;
    }

    private String convertTOMMSS(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.DAYS.toHours(1), //converted to hours
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1), //converted to minutes
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)); //converted to seconds
    }
}