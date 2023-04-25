package com.sunwookim.mediaplayer.activities;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.v4.media.session.MediaSessionCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.media.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import com.sunwookim.mediaplayer.fragments.AlbumsFragment;
import com.sunwookim.mediaplayer.models.DataReading;
import com.sunwookim.mediaplayer.notification.NotiService;
import com.sunwookim.mediaplayer.R;
import com.sunwookim.mediaplayer.models.Song;
import com.sunwookim.mediaplayer.adapters.SongAdapter;
import com.sunwookim.mediaplayer.fragments.SongsFragment;
import com.sunwookim.mediaplayer.adapters.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WAKE_LOCK;
import static com.sunwookim.mediaplayer.notification.NofiticationCenter.channel_1_ID;
import static com.sunwookim.mediaplayer.adapters.SongAdapter.songs;

public class  MainActivity extends AppCompatActivity {

    private int Storage_Permission_code=1;
    private static final String TAG = "MainActivity";
    DataReading dataReading;
    protected static MainActivity instance;
    private byte arts[];

    private HashMap<String, List<Song> >albums=new HashMap<>();
    public static  ArrayList<ArrayList<Song>>al=new ArrayList<>();
    
    private NotificationManagerCompat notificationManager;
    MediaMetadataRetriever metadataRetriever;
    private MediaSessionCompat mediaSession;

    static TabLayout tableLayout;
    static ViewPager viewPager;
    protected ViewPagerAdapter viewPagerAdapter;
    static LinearLayout miniplayer;
    static TextView textView;
    public static   ImageView imageView;
    public static Notification notification;
    private PlayerActivity playerActivity;
    private static boolean hideTitle = false;
    public static boolean taskback = false;
    private static MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
        taskback = pref.getBoolean("task", false);

        textView=findViewById(R.id.mini_player_title);
        tableLayout=findViewById(R.id.table_Layout);
        viewPager=findViewById(R.id.view_Pager);
        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());


        viewPagerAdapter.addFragment(new SongsFragment(),"Songs");
        viewPagerAdapter.addFragment(new AlbumsFragment(),"Albums");

        viewPager.setAdapter(viewPagerAdapter);
        tableLayout.setupWithViewPager(viewPager);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setElevation(0);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        imageView=findViewById(R.id.mini_player_play_pause_button);
        miniplayer=findViewById(R.id.mini_player);
        imageView.setBackgroundResource(R.drawable.ic_baseline_play_arrow);
        intializeMini();
        notificationManager = NotificationManagerCompat.from(this);

        mediaSession = new MediaSessionCompat(this, "tag");
        instance=this;

        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
            requestStoragePermission();
        }else {
         start();
        }

       if(PlayerActivity.playin){
           imageView.setBackgroundResource(R.drawable.ic_baseline_pause);
       }else{
           imageView.setBackgroundResource(R.drawable.ic_baseline_play_arrow);
       }

    }

    private void titleBar(){
        ActionBar actionBar=getSupportActionBar();
        if(hideTitle){
            actionBar.hide();
        }else{
            actionBar.show();
        }
    }

    public static void hideAll(boolean hide){
        if(hide){
            textView.setVisibility(View.INVISIBLE);
            tableLayout.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.INVISIBLE);
            tableLayout.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            miniplayer.setVisibility(View.INVISIBLE);
            hideTitle = true;
        }else{
            textView.setVisibility(View.VISIBLE);
            tableLayout.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            tableLayout.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);
            miniplayer.setVisibility(View.VISIBLE);
            hideTitle = false;
        }
        MainActivity.getInstance().titleBar();
    }

    //미니 플레이어
    public void intializeMini(){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
                final SharedPreferences.Editor edit = pref.edit();
                taskback = false;
                edit.putBoolean("task",false);
                if(PlayerActivity.playin){
                    imageView.setBackgroundResource(R.drawable.ic_baseline_pause);
                }else {
                    imageView.setBackgroundResource(R.drawable.ic_baseline_play_arrow);
                }
                try{
                    PlayerActivity.getInstance().play();
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        miniplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
                final SharedPreferences.Editor edit = pref.edit();
                taskback = false;
                edit.putBoolean("task",false);
                if(songs.isEmpty()) {
                    Toast.makeText(MainActivity.this, "There are no playlists.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public static MainActivity getInstance() {
        return instance;
    }

   //권한
    private void requestStoragePermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(this).setTitle("Permission Needed").setMessage("Need to read and record songs from your storage.").setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,RECORD_AUDIO,READ_PHONE_STATE,CALL_PHONE,WAKE_LOCK},Storage_Permission_code);

                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                }
            }).create().show();

        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,RECORD_AUDIO,READ_PHONE_STATE,CALL_PHONE,WAKE_LOCK},Storage_Permission_code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==Storage_Permission_code){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
            start();
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show();
            }
        }
    }

    //시작
    private void start() {
       dataReading=new DataReading(this);
       songs=new ArrayList<>();
       songs.add(new Song());
       ArrayList<Song> songs = dataReading.getAllAudioFromDevice();
       Collections.sort(songs);
       SongAdapter.songs=songs;
       albums=dataReading.getAlbums();
       textView.setText("Choose your music.");
       try {
           int position= PlayerActivity.getInstance().getPosition();
           playerActivity.setData(position);
       }catch(NullPointerException e){
           e.printStackTrace();
       }
       shift();
    }

    public void shift(){
        Iterator it = albums.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            al.add((ArrayList<Song>) pair.getValue());
            it.remove(); // avoids a ConcurrentModificationException
        }
    }

    //검색
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem searchItem=menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               SongsFragment.search(newText);
                return false;
            }
        });
        return true;
    }

    //알림 생성
    public void sendOnChannel(String name,String artist,int position){
        //▶❚❚ ▷| |◁
            Intent intent = new Intent(MainActivity.getInstance(), PlayerActivity.class).putExtra("index", 0).putExtra("val", 0).putExtra("from",false);
            PendingIntent content = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);

            int plaorpa;
            if(PlayerActivity.playin){
                plaorpa=R.drawable.ic_baseline_pause;
            }else{
                plaorpa=R.drawable.ic_baseline_play_arrow;
            }
            metadataRetriever = new MediaMetadataRetriever();
            try {
                metadataRetriever.setDataSource(songs.get(position).getPath());
            }catch (Exception e){
                e.printStackTrace();
            }
            arts= metadataRetriever.getEmbeddedPicture();
            Bitmap artwork;
            try {
                artwork=BitmapFactory.decodeByteArray(arts,0,arts.length);
            }catch (Exception e){
                artwork = BitmapFactory.decodeResource(getResources(), R.drawable.track_2);
            }
            try {
                MediaSessionCompat mediaSession = new MediaSessionCompat(getApplicationContext(), "session tag");
            }catch (Exception e){
                e.printStackTrace();
            }

            MediaSessionCompat.Token token = mediaSession.getSessionToken();

            notification = new androidx.core.app.NotificationCompat.Builder(this, channel_1_ID)
                    .setSmallIcon(R.drawable.music_note_24dp)
                    .setContentTitle(name)
                    .setContentText(artist)
                    .setSubText("Now Playing!")
                    .setLargeIcon(artwork)
                    .addAction(R.drawable.previous_24dp, "Previous", playbackAction(3))
                    .addAction(plaorpa, "Pause", playbackAction(1))
                    .addAction(R.drawable.next_24dp, "Next", playbackAction(2))
                    .setContentIntent(content)
                    .setStyle(new NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(token))
                    .build();

            if(PlayerActivity.isPlayin()){
                notification.flags = Notification.FLAG_NO_CLEAR;
            }else {
                notification.flags = Notification.FLAG_AUTO_CANCEL;
            }
            notificationManager.notify(1, notification);

    }

    private PendingIntent playbackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, NotiService.class);
        switch (actionNumber) {
            case 1:
                // Pause
                playbackAction.setAction("com.mypackage.ACTION_PAUSE_MUSIC");
                return PendingIntent.getService(this, actionNumber, playbackAction, PendingIntent.FLAG_MUTABLE);
            case 2:
                // Next
                playbackAction.setAction("com.mypackage.ACTION_NEXT_MUSIC");
                return PendingIntent.getService(this, actionNumber, playbackAction, PendingIntent.FLAG_MUTABLE);
            case 3:
                // Previous
                playbackAction.setAction("com.mypackage.ACTION_PREV_MUSIC");
                return PendingIntent.getService(this, actionNumber, playbackAction, PendingIntent.FLAG_MUTABLE);
            default:
                break;
        }
        return null;
    }

   //시스템 백버튼누를시 홈버튼효과랑동일하게 설정
    @Override
    public void onBackPressed() {
        SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
        final SharedPreferences.Editor edit = pref.edit();
        taskback = true;
        edit.putBoolean("task",true);
        moveTaskToBack(true);
        onResume();
    }

    @Override
    public void onUserLeaveHint() {
        SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
        final SharedPreferences.Editor edit = pref.edit();
        taskback = true;
        edit.putBoolean("task",true);
        onResume();
        super.onUserLeaveHint();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
