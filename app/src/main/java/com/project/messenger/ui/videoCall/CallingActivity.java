package com.project.messenger.ui.videoCall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.project.messenger.R;
import com.project.messenger.models.ApiResponse;
import com.project.messenger.networking.ApiBuilder;
import com.project.messenger.ui.messenger.MessageRoomActivity;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CallingActivity extends AppCompatActivity implements Session.SessionListener, PublisherKit.PublisherListener {
    private static String API_KEY = "46968394";
    private static String SESSION_ID = "1_MX40Njk2ODM5NH5-MTYwMzg1NTU1NjA4OX56bDRpU21FMFZkQllIanZ5OHpzSUFkenR-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00Njk2ODM5NCZzaWc9OWNlMjdkNTY0YTAyNGZhNWNhODRiYWI5NjE1MThiNGE3MDU4ZGJkNDpzZXNzaW9uX2lkPTFfTVg0ME5qazJPRE01Tkg1LU1UWXdNemcxTlRVMU5qQTRPWDU2YkRScFUyMUZNRlprUWxsSWFuWjVPSHB6U1VGa2VuUi1mZyZjcmVhdGVfdGltZT0xNjAzODU1NjA3Jm5vbmNlPTAuODE3MzgxODQ1MDAxMTQyNSZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNjAzODU5MjA2JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String TAG = CallingActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_PERM = 123;
    private static final int RC_VIDEO_APP_PERM = 124;
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private CardView leaveRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);

        mPublisherViewContainer = (FrameLayout) findViewById(R.id.publisher_container);
        mSubscriberViewContainer = (FrameLayout) findViewById(R.id.subscriber_container);
        leaveRoom = findViewById(R.id.leaveCallbtn);
        leaveRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCalling();
            }
        });
        requestPermissions();
    }

    private void cancelCalling() {
        mSession.disconnect();
        mSubscriber.destroy();
        mPublisher.destroy();
        Intent intent = new Intent(getApplicationContext(), MessageRoomActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions(){
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize and connect to the session
            fetchSessionConnectionData();
        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    public void fetchSessionConnectionData() {
        ApiBuilder.getInstance2().getSession().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                API_KEY = response.body().getApiKey();
                SESSION_ID  = response.body().getSessionId();
                TOKEN  = response.body().getToken();

                Log.d(TAG, "API_KEY: " + API_KEY);
                Log.d(TAG, "SESSION_ID: " + SESSION_ID);
                Log.d(TAG, "TOKEN: " + TOKEN);

                mSession = new Session.Builder(getApplicationContext(), API_KEY, SESSION_ID).build();
                mSession.setSessionListener(CallingActivity.this);
                mSession.connect(TOKEN);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.e(TAG, "Web Service error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onConnected(Session session) {
        Log.d(TAG, "Session Connected");
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(CallingActivity.this);
        mPublisherViewContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.d(TAG, "Session Disconnected");
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.d(TAG, "Session Received");
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.d(TAG, "Session Dropped");
        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(TAG, "Session error: " + opentokError.getMessage());
    }

    // Publisher
    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.d(TAG, "Publisher onStreamCreated");
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.d(TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(TAG, "Publisher error: " + opentokError.getMessage());
    }
}