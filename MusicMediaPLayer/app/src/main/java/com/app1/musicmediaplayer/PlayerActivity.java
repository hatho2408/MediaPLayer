package com.app1.musicmediaplayer;

import static com.app1.musicmediaplayer.AlbumDetailsAdapter.albumFiles;
import static com.app1.musicmediaplayer.MainActivity.musicFiles;
import static com.app1.musicmediaplayer.MainActivity.repeatBoolean;
import static com.app1.musicmediaplayer.MainActivity.shuffleBoolean;
import static com.app1.musicmediaplayer.MusicAdapter.mFiles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener,ActionPlaying, ServiceConnection {
    TextView song_name,artist_name,duration_play,duration_total;
    ImageView cover_art,nextBtn,preBtn,backBtn,shuffleBtn,repeatBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    static  Uri uri;
    int position=-1;
   // static MediaPlayer mediaPlayer;
    private Handler handler=new Handler();
    static ArrayList<MusicFiles>listSongs=new ArrayList<>();
    private Thread playThread,prevThread    ,nextThread;
    MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initViews();
        getIntentMethod();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(musicService!=null&& b)
                {
                    musicService.seekTo(i*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(musicService!=null)
                {
                    int mCurrentPosition=musicService.getCurrentPosition()/1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_play.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffleBoolean)
                {
                    shuffleBoolean=false;
                    shuffleBtn.setImageResource(R.drawable.baseline_shuffle);
                }else
                {
                    shuffleBoolean=true;
                    shuffleBtn.setImageResource(R.drawable.baseline_shuffle_24);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatBoolean)
                {
                    repeatBoolean=false;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat);
                }
                else {
                    repeatBoolean=true;
                    repeatBtn.setImageResource(R.drawable.baseline_repeat_24);

                }
            }
        });
    }

    @Override
    protected void onResume() {
        Intent intent=new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(this);

    }

    private void prevThreadBtn() {
        prevThread=new Thread()
        {
            @Override
            public void run() {
                super.run();
                preBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       prevBtnclicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prevBtnclicked() {
        if(musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();
            if(shuffleBoolean&&!repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            } else if (!shuffleBoolean&&!repeatBoolean)
            {
                position=((position-1)<0?(listSongs.size()-1):(position-1));
            }

            uri=Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null)
                    {
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }

            });
            musicService.OnComplete();
            playPauseBtn.setBackgroundResource(R.drawable.baseline_pause);
            musicService.start();

        }
        else
        {
            musicService.stop();
            musicService.release();
            if(shuffleBoolean&&!repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            } else if (!shuffleBoolean&&!repeatBoolean)
            {
                position=((position-1)<0?(listSongs.size()-1):(position-1));
            }
            uri=Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null)
                    {
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }

            });
          musicService.OnComplete();
            playPauseBtn.setBackgroundResource(R.drawable.baseline_play);
        }
    }

    private void nextThreadBtn() {
        nextThread=new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       nextBtnclicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void nextBtnclicked() {
        if(musicService.isPlaying())
        {
            musicService.stop();
            musicService.release();
            if(shuffleBoolean&&!repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            } else if (!shuffleBoolean&&!repeatBoolean)
            {
                position=((position+1)%listSongs.size());
            }

            position=((position+1)%listSongs.size());
            uri=Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer(position);//////////////////
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getCurrentPosition()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null)
                    {
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }

            });
            musicService.OnComplete();
            playPauseBtn.setBackgroundResource(R.drawable.baseline_pause);
            musicService.start();

        }
        else
        {
            musicService.stop();
            musicService.release();
            if(shuffleBoolean&&!repeatBoolean)
            {
                position=getRandom(listSongs.size()-1);
            } else if (!shuffleBoolean&&!repeatBoolean)
            {
                position=((position+1)%listSongs.size());
            }
            uri=Uri.parse(listSongs.get(position).getPath());
            musicService.createMediaPlayer( position);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null)
                    {
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }

            });
            musicService.OnComplete();
            playPauseBtn.setBackgroundResource(R.drawable.baseline_play);
        }
    }

    private int getRandom(int i) {
        Random random= new Random();

        return random.nextInt(i+1);
    }

    private void playThreadBtn() {
        playThread=new Thread()
        {
            @Override
            public void run() {
                    super.run();
                    playPauseBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            playPauseBtnclicked();
                        }
                    });
            }
        };
        playThread.start();
    }

    public void playPauseBtnclicked() {
        if(musicService.isPlaying())
        {
            playPauseBtn.setImageResource( R.drawable.baseline_play);
            musicService.pause();
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null)
                    {
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
        else
        {
            playPauseBtn.setImageResource(R.drawable.baseline_pause);
            musicService.start();
            seekBar.setMax(musicService.getDuration()/1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(musicService!=null)
                    {
                        int mCurrentPosition=musicService.getCurrentPosition()/1000;
                        seekBar.setProgress(mCurrentPosition);

                    }
                    handler.postDelayed(this,1000);
                }
            });
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout="";
        String totalnew="";
        String seconds=String.valueOf(mCurrentPosition%60);
        String minutes=String.valueOf(mCurrentPosition/60);
        totalout=minutes+":"+seconds;
        totalnew=minutes+":"+"0"+seconds;
        if(seconds.length()==1)
        {
            return totalnew;
        }
        else
        {
          return   totalout;
        }

    }

    private void getIntentMethod() {
        position=getIntent().getIntExtra("position",-1);
        String sender=getIntent().getStringExtra("sender");
        if(sender!=null&&sender.equals("albumDetails    "))
        {
            listSongs=albumFiles;
        }
        else
        {
            listSongs=mFiles;
        }
        listSongs=musicFiles;
        if(listSongs!=null)
        {
            playPauseBtn.setImageResource(R.drawable.baseline_pause);
            uri= Uri.parse(listSongs.get(position).getPath());

        }
       //
           // musicService.createMediaPlayer(position);
           //  musicService.start();
        Intent intent=new Intent(this,MusicService.class);
        intent.putExtra("servicePosition",position);
        startService(intent);
        //seekBar.setMax(musicService.getDuration()/1000);
       // metaData(uri);
    }

    private void initViews()
    {
        song_name=findViewById(R.id.song_name);
        artist_name=findViewById(R.id.song_artist);
        duration_play=findViewById(R.id.durationPlayed);
        duration_total=findViewById(R.id.durationTotal);
        cover_art=findViewById(R.id.cover_art);
        nextBtn=findViewById(R.id.next);
        preBtn=findViewById(R.id.id_prev);
        backBtn=findViewById(R.id.back_btn);
        shuffleBtn=findViewById(R.id.shuffle);
        repeatBtn=findViewById(R.id.id_repeat);
        playPauseBtn=findViewById(R.id.play_pause);
        seekBar=findViewById(R.id.seekBar);

    }
    private void metaData(Uri uri)
    {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal=Integer.parseInt(listSongs.get(position).getDuration())/1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art= retriever.getEmbeddedPicture();
        Bitmap bitmap = null;

           // bitmap= BitmapFactory.decodeByteArray(art,0,art.length);

        if(art!=null)
        {

            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAniamtion(this,cover_art,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    if(swatch!=null)
                    {
                        ImageView gredient=findViewById(R.id.imageViewGradient);
                        RelativeLayout mContainer=findViewById(R.id.mContainer);
                        gredient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{swatch.getRgb(),0x00000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    }
                    else {

                        ImageView gredient=findViewById(R.id.imageViewGradient);
                        RelativeLayout mContainer=findViewById(R.id.mContainer);
                        gredient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{0xFF48199C,0x00000000});
                        gredient.setBackground(gradientDrawable);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,new int[]{0xFF48199C,0xFF48199C});
                        mContainer.setBackground(gradientDrawableBg);
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }

            });
        }
        else
        {
            ImageAniamtion(this,cover_art,bitmap);
            ImageView gredient=findViewById(R.id.imageViewGradient);
            RelativeLayout mContainer=findViewById(R.id.mContainer);
            gredient.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }
    }
    public void ImageAniamtion(Context context,ImageView imageView,Bitmap bitmap)
    {
        Animation animOut= AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn= AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        nextBtnclicked();
        if(musicService!=null)
        {
            musicService.createMediaPlayer(position);
            musicService.start();
            musicService.OnComplete();
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.MyBinder myBinder=(MusicService.MyBinder) iBinder;
        musicService=myBinder.getService();
        Toast.makeText(this,"Connected"+musicService,Toast.LENGTH_SHORT).show();
        seekBar.setMax(musicService.getDuration()/1000);
        metaData(uri);
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        musicService.OnComplete();

    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService=null;

    }
}