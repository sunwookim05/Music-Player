package com.sunwookim.mediaplayer.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sunwookim.mediaplayer.R;
import com.sunwookim.mediaplayer.adapters.SongAdapter;
import com.sunwookim.mediaplayer.adapters.SongAlbumAdapter;
import com.sunwookim.mediaplayer.models.Song;
import com.sunwookim.mediaplayer.notification.NofiticationCenter;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity {
    public static final int AUDIO_PERMISSION_REQUEST_CODE = 102;

    public static final String[] WRITE_EXTERNAL_STORAGE_PERMS = {
            Manifest.permission.RECORD_AUDIO
    };

    public static boolean playin, shuffleBoolean, repeatBoolean, call;
    private ArrayList<Song> Asongs;
    private static SeekBar mSeekBar;
    private static PlayerActivity instance;
    int position;
    private static TextView curTime, totTime;
    private TextView songTitle, artistname;
    private static ImageView pause, prev, next, back_btn, repeat_btn, shuffle_btn;
    private ImageView imageView;
    protected int val;
    protected boolean place = false;
    private Palette.Swatch DarkVibrantSwatch;
    private Palette.Swatch darkMutedSwatch;
    public static boolean taskback = false;
    private static boolean npBtn = false;

    public static boolean playCheck = true;

    protected NofiticationCenter nofiticationCenter;
    protected LinearLayout linearLayout, linear1;
    private static MediaPlayer mMediaPlayer;
    private NotificationManagerCompat notificationManager;
    private BarVisualizer mVisualizer;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getData() {
        return position;
    }

    public int getActivity() {
        if (taskback == false) {
            Intent intent = new Intent(MainActivity.getInstance(), PlayerActivity.class).putExtra("index", 0).putExtra("val", 0).putExtra("from", false);
            startActivity(intent);
        } else {
            setData(position);
        }
        return position;
    }

    public void resetPlayer() {
        position = (position - 1) % Asongs.size();
        MainActivity.getInstance().sendOnChannel(Asongs.get(position).getName(), Asongs.get(position).getArtist(), position);
        setPosition(position);
        setData(position);
        getData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_player);

        SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
        shuffleBoolean = pref.getBoolean("Shuffle", false);
        repeatBoolean = pref.getBoolean("Repeat", false);
        taskback = false;

        shuffle_btn = findViewById(R.id.shuffle_btn);
        repeat_btn = findViewById(R.id.repeat_btn);
        back_btn = findViewById(R.id.back_btn);
        nofiticationCenter = new NofiticationCenter();
        mSeekBar = findViewById(R.id.seek);
        songTitle = findViewById(R.id.song_name);
        artistname = findViewById(R.id.artist_name);
        totTime = findViewById(R.id.total_time);
        pause = findViewById(R.id.pause);
        linearLayout = findViewById(R.id.linear_layout);
        prev = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        curTime = findViewById(R.id.current_time);
        imageView = findViewById(R.id.imageplayer);
        linear1 = findViewById(R.id.linear1);
        Intent i = getIntent();
        Bundle bundle = i.getExtras();
        MainActivity.hideAll(true);
        try {
            position = bundle.getInt("index");
            val = bundle.getInt("val");
            place = bundle.getBoolean("from");
            setData(position);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mVisualizer = findViewById(R.id.bar);

        //셔플버튼 on/off 유무

        if (!shuffleBoolean) {
            shuffle_btn.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);

        } else {
            shuffle_btn.setBackgroundResource(R.drawable.ic_shuffle_on_icon);

        }

        //반복버튼 on/off 유무

        if (!repeatBoolean) {
            repeat_btn.setBackgroundResource(R.drawable.ic_repeat_icon);
        } else {
            repeat_btn.setBackgroundResource(R.drawable.ic_repeat_on_icon);
        }

        if (isPlayin()) {
            pause.setBackgroundResource(R.drawable.ic_baseline_pause);
        } else {
            pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow);
        }

        if (instance != null && place == false) {
            songTitle.setText(instance.songTitle.getText());
            artistname.setText(instance.artistname.getText());
            totTime.setText(instance.totTime.getText());
            curTime.setText(instance.curTime.getText());
            imageView.setImageDrawable(instance.imageView.getDrawable());
            initiateSeekBar();
            Asongs = instance.Asongs;
            position = instance.position;
            setData(position);
        } else {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
            }
            instance = this;
            if (val == 1) {
                Asongs = SongAlbumAdapter.albumSong;
            } else {
                Asongs = SongAdapter.songs;
            }
            try {
                MainActivity.imageView.setBackgroundResource(R.drawable.ic_baseline_pause);
                initPlayer(position);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        Buttons();
        try {
            setData(position);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static PlayerActivity getInstance() {
        return instance;
    }

    //제목,이미지,아티스트 설정
    public void setData(int position) {
        try {
            String name = Asongs.get(position).getName();
            String artist = Asongs.get(position).getArtist();
            songTitle.setText(name);
            artistname.setText(artist);
            MainActivity.getInstance().textView.setText(name);
            try {
                int audioSessionId = mMediaPlayer.getAudioSessionId();
                if (audioSessionId != -1) {
                    mVisualizer.setAudioSessionId(audioSessionId);
                }
                Glide
                        .with(getApplicationContext())
                        .load(ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), Asongs.get(position).getAlbumID()).toString())
                        .thumbnail(0.2f)
                        .centerCrop()
                        .placeholder(R.drawable.track)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                setBackground();
                                return false;
                            }
                        })
                        .into(imageView);
            } catch (Exception e) {
                Glide.with(this).load(R.drawable.track).into(imageView);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //시크바
    public static void initiateSeekBar() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                PlayerActivity.getInstance().pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                PlayerActivity.getInstance().play();
            }
        });

    }

    //렌덤
    private int getRandom(int size) {
        Random random = new Random();
        return random.nextInt(size + 1);
    }

    //버튼
    public void Buttons() {

        //셔플,한곡반복 on/off 저장
        SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
        final SharedPreferences.Editor edit = pref.edit();

        //뒤로가기
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        //재생,중지
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        //이전곡
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicPrev(true);
            }
        });

        //다음곡
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicNext(true);
            }
        });

        //셔플
        shuffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBoolean) {
                    shuffleBoolean = false;
                    shuffle_btn.setBackgroundResource(R.drawable.ic_shuffle_black_24dp);
                    edit.putBoolean("Shuffle", false);

                } else {
                    shuffleBoolean = true;
                    shuffle_btn.setBackgroundResource(R.drawable.ic_shuffle_on_icon);
                    edit.putBoolean("Shuffle", true);

                }
                edit.commit();
            }
        });

        //한곡반복
        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (repeatBoolean) {
                    repeatBoolean = false;
                    repeat_btn.setBackgroundResource(R.drawable.ic_repeat_icon);
                    edit.putBoolean("Repeat", false);
                } else {
                    repeatBoolean = true;
                    repeat_btn.setBackgroundResource(R.drawable.ic_repeat_on_icon);
                    edit.putBoolean("Repeat", true);
                }
                edit.commit();
            }
        });
    }

    //음악 위치
    public void initPlayer(final int position) {

        playin = true;

        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.reset();
        }

        try {
            String name = Asongs.get(position).getName();
            String artist = Asongs.get(position).getArtist();
            setData(position);
            MainActivity.getInstance().sendOnChannel(name, artist, position);
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e);
        }

        try {
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(Asongs.get(position).getPath()));
        } catch (Exception e) {
            e.printStackTrace();
            resetPlayer();
        }

        int audioSessionId = mMediaPlayer.getAudioSessionId();
        if (audioSessionId != -1) {
            mVisualizer.setAudioSessionId(audioSessionId);
        }

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                String totalTime = createTimeLabel(mMediaPlayer.getDuration());
                totTime.setText(totalTime);
                mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
                mMediaPlayer.start();
                pause.setBackgroundResource(R.drawable.ic_baseline_pause);

            }
        });

        //음악이 끝났을때
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                musicNext(false);
            }

        });
        initiateSeekBar();

        //재생 위치 1초마다 표시
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer != null && !Thread.currentThread().isInterrupted()) {
                    try {
                        if (mMediaPlayer.isPlaying()) {
                            Message msg = new Message();
                            msg.what = mMediaPlayer.getCurrentPosition();
                            msg.arg1 = mMediaPlayer.getDuration();
                            handler.sendMessage(msg);
                            Thread.sleep(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        if (npBtn){
            npBtn = false;
            thread.interrupt();
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int current_position = msg.what;
            mSeekBar.setMax(mMediaPlayer.getDuration() / 1000);
            mSeekBar.setProgress(current_position / 1000);
            System.out.println(mSeekBar.getProgress());
            String cTime = createTimeLabel(current_position);
            curTime.setText(cTime);
            totTime.setText(createTimeLabel(msg.arg1));
        }
    };

    public void musicNext(boolean btn) {
        npBtn = true;
        if (btn) {
            SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
            final SharedPreferences.Editor edit = pref.edit();
            taskback = false;
            edit.putBoolean("task", false);
            if (shuffleBoolean) {
                position = getRandom(Asongs.size() + 1);
            } else {
                position = (position + 1) % Asongs.size();
            }
        } else {
            if (shuffleBoolean && !repeatBoolean) {
                position = getRandom(Asongs.size() + 1);
            } else if (!shuffleBoolean && !repeatBoolean) {
                position = (position + 1) % Asongs.size();
            }
        }
        try {
            setPosition(position);
            MainActivity.getInstance().sendOnChannel(Asongs.get(position).getName(), Asongs.get(position).getArtist(), position);
            setData(position);
            initPlayer(position);
        }catch (IndexOutOfBoundsException e){
            System.out.println(e);
        }
    }

    public void musicPrev(boolean btn) {
        if (mMediaPlayer.getCurrentPosition() >= 1000) {
            mMediaPlayer.seekTo(0);
        } else {
            npBtn = true;
            if (btn) {
                SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
                final SharedPreferences.Editor edit = pref.edit();
                taskback = false;
                edit.putBoolean("task", false);
                if (shuffleBoolean) {
                    position = getRandom(Asongs.size());
                } else {
                    position = (position - 1) < 0 ? (Asongs.size() - 1) : (position - 1);
                }
            } else {
                if (shuffleBoolean && !repeatBoolean) {
                    position = getRandom(Asongs.size());
                } else if (!shuffleBoolean && !repeatBoolean) {
                    position = (position - 1) < 0 ? (Asongs.size() - 1) : (position - 1);
                }
            }
            try {
                setPosition(position);
                MainActivity.getInstance().sendOnChannel(Asongs.get(position).getName(), Asongs.get(position).getArtist(), position);
                setData(position);
                initPlayer(position);
            }catch (IndexOutOfBoundsException e){
                System.out.println(e);
            }
        }
    }

    //재생/중지
    public void play() {
        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            playin = true;
            mMediaPlayer.start();
            pause.setBackgroundResource(R.drawable.ic_baseline_pause);
            MainActivity.getInstance().sendOnChannel( Asongs.get(position).getName(), Asongs.get(position).getArtist(),position);
            MainActivity.imageView.setBackgroundResource(R.drawable.ic_baseline_pause);
        } else {
            pause();
        }

    }

    public void pause() {
        if (mMediaPlayer.isPlaying()) {
            playin = false;
            mMediaPlayer.pause();
            pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow);
            MainActivity.getInstance().sendOnChannel( Asongs.get(position).getName(), Asongs.get(position).getArtist(),position);
            MainActivity.imageView.setBackgroundResource(R.drawable.ic_baseline_play_arrow);
        }
    }

    public String createTimeLabel(int duration) {
        String timeLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timeLabel += min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    //백그라운드 설정
    public void setBackground() {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Palette.from(bitmap).maximumColorCount(32).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                DarkVibrantSwatch = palette.getDarkVibrantSwatch();
                darkMutedSwatch = palette.getDarkMutedSwatch();
                linearLayout.setBackgroundColor(0x00000000);
                linear1.setBackgroundColor(0x00000000);
            }
        });
    }

    public static boolean isPlayin() {
        return playin;
    }

    @Override
    public void onPause() {
        try {
            super.onPause();
            try {
                Thread.sleep(10);
                super.onStart();
                super.onResume();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //뒤로가기
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(PlayerActivity.getInstance(), MainActivity.class);
        playCheck = false;
        SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
        final SharedPreferences.Editor edit = pref.edit();
        edit.commit();
        setData(position);
        taskback = true;
        edit.putBoolean("task", true);
        if (val == 1) {
            AlbumActivity.hideAll(false);
        } else {
            MainActivity.hideAll(false);
        }
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void onUserLeaveHint() {
        playCheck = false;
        SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
        final SharedPreferences.Editor edit = pref.edit();
        taskback = true;
        edit.putBoolean("task", true);
        if (val == 1) {
            AlbumActivity.hideAll(false);
        } else {
            MainActivity.hideAll(false);
        }
        super.onUserLeaveHint();
    }

    //어플 완전히 종료시 알림삭제
    @Override
    public void onDestroy() {
        playCheck = false;
        if (val == 1) {
            AlbumActivity.hideAll(false);
        } else {
            MainActivity.hideAll(false);
        }
        super.onDestroy();
    }
}