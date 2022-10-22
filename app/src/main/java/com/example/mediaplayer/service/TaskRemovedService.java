package com.example.mediaplayer.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

public class TaskRemovedService extends Service {
    private NotificationManagerCompat notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //어플 완전히 종료시 알림삭제
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            notificationManager.cancelAll();
            try {
                Thread.sleep(10);
                notificationManager.cancelAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
                notificationManager.cancelAll();
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        stopSelf();
    }

}
