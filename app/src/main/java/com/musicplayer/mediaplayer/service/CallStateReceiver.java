package com.musicplayer.mediaplayer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.musicplayer.mediaplayer.activities.PlayerActivity;

public class CallStateReceiver extends BroadcastReceiver {
    static String mLastState;
    static final String TAG = "CallStateListner";
    public static boolean call;
    private static MediaPlayer mMediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Call");

        // 전화 수신 체크
        CallReceivedChk(context, intent);
    }

    private void CallReceivedChk(Context context, Intent intent) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                String mState = String.valueOf(state);
                if (mState.equals(mLastState)) {
                    return;
                } else {
                    mLastState = mState;
                }

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(TAG, "call false");
                        PlayerActivity.getInstance().play();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d(TAG, "call true");
                        PlayerActivity.getInstance().pause();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(TAG, "call Ringing");
                        PlayerActivity.getInstance().pause();
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);
    }
}