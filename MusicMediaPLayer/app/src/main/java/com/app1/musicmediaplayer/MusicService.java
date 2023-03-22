package com.app1.musicmediaplayer;

import static com.app1.musicmediaplayer.PlayerActivity.listSongs;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.lang.reflect.Method;
import java.security.Provider;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener {
    IBinder mBinder=new MyBinder();
    Uri uri ;
    int position=-1;
    MediaPlayer mediaPlayer;
    ArrayList<MusicFiles>musicFiles=new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Bind", "Method");
        return mBinder;
    }



    public  class MyBinder extends Binder
    {
        MusicService getService()
        {
            return MusicService.this;
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition=intent.getIntExtra("servicePosition",-1);
        if(myPosition!=-1)
        {
            playMedia(myPosition);
        }

        return START_STICKY;
    }

    private void playMedia(int Startposition) {
        musicFiles=listSongs;
        position=Startposition;
        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
            if(musicFiles!=null)
            {
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }
        else
        {
            createMediaPlayer(position);
            mediaPlayer.start();
        }

    }

    void start()
    {
        mediaPlayer.start();
    }
    boolean isPlaying()
    {
        return mediaPlayer.isPlaying();
    }
    void stop()
    {
        mediaPlayer.stop();
    }
    void release()
    {
        mediaPlayer.release();
    }
    int getDuration()
    {
        return mediaPlayer.getDuration();
    }
    void seekTo(int position)
    {
        mediaPlayer.seekTo(position);
    }
    void createMediaPlayer(int position)
    {
        uri=Uri.parse(musicFiles.get(position).getPath());
        mediaPlayer=MediaPlayer.create(getBaseContext(),uri);
    }
    int getCurrentPosition()
    {
        return mediaPlayer.getCurrentPosition();
    }
    void pause()
    {
        mediaPlayer.pause();
    }
    void OnComplete()
    {
        mediaPlayer.setOnCompletionListener(this);
    }
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }




}
