package com.project.messenger.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.project.messenger.ui.videoCall.IncomingInvitationActivity;
import com.project.messenger.utils.Constants;

public class MessagingServices extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.d("FCM", "Token: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "remoteMessage: " + remoteMessage.getNotification().getBody());

//        if (remoteMessage.getNotification() != null){
//            String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);
//
//            if (type != null){
//                if (type.equals(Constants.REMOTE_MSG_INVITATION)){
//                    Intent intent = new Intent(getApplicationContext(), IncomingInvitationActivity.class);
//                    intent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE));
//                    intent.putExtra("username", remoteMessage.getData().get("username"));
//                    intent.putExtra("email", remoteMessage.getData().get("email"));
//
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
//            }
//        }
    }
}
