package com.project.messenger.utils;

import java.util.HashMap;

public class Constants {
    public static String KEY_FCM_TOKEN = "fdm_token";
    public static String SENDER_ID = "16462944457";
    public static String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    public static String REMOTE_MSG_TYPE = "type";
    public static String REMOTE_MSG_INVITATION = "invitation";
    public static String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static String REMOTE_MSG_DATA = "data";
    public static String REMOTE_REGISTRATION_IDS = "registration_ids";

    public static String REMOTE_MSG_INVITATION_RESPONSE = "invitationResponse";
    public static String REMOTE_MSG_INVITATION_ACCEPTED = "accepted";
    public static String REMOTE_MSG_INVITATION_REJECTED = "rejected";
    public static String REMOTE_MSG_INVITATION_CANCELED = "cancelled";

    public static String REMOTE_MSG_MEETING_ROOM = "meetingRoom";

    public static HashMap<String, String> getRemoteMessageHeader(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key=AAAAA9VEmMk:APA91bGu-j2uV-SZ0kSshvz88ngnvcGpfSI8lyFc7xQccsP8IZbgiJLO-6cJaFTUHp_NXB1tkWickjZvd_9yQsDBaKzGHfMAisOlKBLAZ5zw5LqAzwH1JNh-PPSWJKhKGLwMcx62yWVr"
        );

        headers.put(Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }
}
