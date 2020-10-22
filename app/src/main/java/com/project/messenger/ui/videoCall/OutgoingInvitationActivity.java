package com.project.messenger.ui.videoCall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.project.messenger.R;
import com.project.messenger.networking.ApiBuilder;
import com.project.messenger.ui.messenger.HomeActivity;
import com.project.messenger.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingInvitationActivity extends AppCompatActivity {
    private PreferenceManager preferenceManager;
    private String inviterToken = null;

    private ImageView userImage;
    private TextView username, userEmail;
    private CardView cancelBtn;

    String meetingType = null;
    String roomId = null;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);

        setUpCalling();
        initViews();
    }

    private void setUpCalling() {
//        preferenceManager = new PreferenceManager(getApplicationContext());
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            inviterToken = task.getResult().getToken();
                        }
                    }
                });
    }

    private void initViews() {
        userImage = findViewById(R.id.userSenderImage);
        username = findViewById(R.id.userSenderName);
        userEmail = findViewById(R.id.userSenderEmail);
        cancelBtn = findViewById(R.id.cancelCallBtn);

        Bundle extras = getIntent().getExtras();
        username.setText(extras.getString("userName"));
        userEmail.setText(extras.getString("userEmail"));
        Glide.with(this).load(extras.getString("userImage")).into(userImage);
        meetingType = extras.getString("meetingType");
        roomId = extras.getString("roomId");

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (meetingType != null && currentUser != null){
            db.collection("rooms").document(roomId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            String[] userList = documentSnapshot.get("users").toString().replaceAll("[()\\[\\]]", "").replace(" ", "").split(",");
                            for (String user: userList) {
                                if (user != currentUser.getEmail()){
                                    db.collection("users")
                                            .whereEqualTo("email",user)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        ArrayList<String> fcmTokens = new ArrayList<>();
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            String fcmToken = document.get("fcmToken").toString();
                                                            fcmTokens.add(fcmToken);
                                                        }
                                                        initialMeeting(meetingType, fcmTokens);
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
        }
    }

    private void initialMeeting(String meetingType, ArrayList<String> receiverToken){
        try {
            Log.d("Array", "initialMeeting:  " + receiverToken.size());
            JSONArray tokens = new JSONArray(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put("username", currentUser.getDisplayName());
            data.put("email", currentUser.getEmail());
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);
            Log.d("JSON", "initialMeeting: " + body.toString());
            Log.d("JSONArray", "initialMeeting: " + tokens);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void sendRemoteMessage(String remoteMessageBody, final String type){
        ApiBuilder.getInstance().sendRemoteMessage(
                Constants.getRemoteMessageHeader(), remoteMessageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                if (response.isSuccessful()){
                    if (type.equals(Constants.REMOTE_MSG_INVITATION)){
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitation sent successfully!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    Log.e("FDM", "Fail: " + response );
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("FDM", "onResponse: " + t.getMessage());
                finish();
            }
        });
    }
}