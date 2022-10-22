package com.example.mediaplayer.notification;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.example.mediaplayer.activities.MainActivity;
import com.example.mediaplayer.activities.PlayerActivity;

import java.util.Random;

public class NotiService extends Service {

    private NotificationManagerCompat notificationManager;
    public static boolean playin, shuffleBoolean, repeatBoolean;
    private PlayerActivity playerActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences pref = getSharedPreferences("Setting", MODE_PRIVATE);
        shuffleBoolean = pref.getBoolean("Shuffle", false);
        repeatBoolean = pref.getBoolean("Repeat", false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        Intent notificationIntent = new Intent(this, MainActivity.class);

        handleIncomingActions(intent);

        return START_NOT_STICKY;
    }

    //렌덤
    private int getRandom(int size) {
        Random random = new Random();
        return  random.nextInt(size + 1);
    }

    //알림창에서 컨트롤
    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;
        int pos= PlayerActivity.getInstance().getPosition();
        int position = PlayerActivity.getInstance().getData();

        String actionString = playbackAction.getAction();

            if (actionString.equalsIgnoreCase("com.mypackage.ACTION_PAUSE_MUSIC")) {
                if( PlayerActivity.playin){
                    PlayerActivity.getInstance().pause();
                }
                else{
                    PlayerActivity.getInstance().play();
                }
        } else if (actionString.equalsIgnoreCase("com.mypackage.ACTION_NEXT_MUSIC")) {
                PlayerActivity.getInstance().getData();
                PlayerActivity.getInstance().musicNext(true);
//                PlayerActivity.getInstance().getActivity();


        } else if (actionString.equalsIgnoreCase("com.mypackage.ACTION_PREV_MUSIC")) {
                PlayerActivity.getInstance().getData();
                PlayerActivity.getInstance().musicPrev(true);
//                PlayerActivity.getInstance().getActivity();

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}