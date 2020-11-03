package com.project.messenger.ui.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.messenger.R;
import com.project.messenger.adapters.MessageAdapter;
import com.project.messenger.models.Message;
import com.project.messenger.ui.videoCall.OutgoingInvitationActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MessageRoomActivity extends AppCompatActivity implements View.OnClickListener {
    private String roomId;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    private EditText edSend;
    private TextView tvRoomName;
    private ImageView chooseImageBtn, sendBtn, roomImage;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_room);

        initView();
        setUpToolbar();
        loadData();
    }

    private void loadData() {
        Bundle extras = getIntent().getExtras();
        roomId = extras.getString("roomId");

        tvRoomName.setText(extras.getString("roomName"));
        Glide.with(this).load(extras.getString("roomImage")).into(roomImage);

        showMessages(roomId);
    }

    private void initView() {
        toolbar = findViewById(R.id.messageToolbar);
        recyclerView = findViewById(R.id.rcMessage);
        edSend = findViewById(R.id.edSendMessage);
        roomImage = findViewById(R.id.roomMesImage);
        tvRoomName = findViewById(R.id.roomMesName);

        chooseImageBtn = findViewById(R.id.btnSendImage);
        sendBtn = findViewById(R.id.btnSend);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        messageAdapter = new MessageAdapter(getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);
        Log.d("TAG", "initView: " + messageAdapter.getItemCount());
//        recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());

        sendBtn.setOnClickListener(this);
        chooseImageBtn.setOnClickListener(this);
    }

    private void setUpToolbar() {
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.roomDetails:
                Intent intent = new Intent(this, RoomDetailsActivity.class);
                intent.putExtra("roomId", roomId);
                startActivity(intent);
                break;
            case R.id.roomCall:
                Intent intentCall = new Intent(this, OutgoingInvitationActivity.class);
                
                intentCall.putExtra("userName", currentUser.getDisplayName());
                intentCall.putExtra("userEmail", currentUser.getEmail());
                intentCall.putExtra("userImage", currentUser.getPhotoUrl().toString());
                intentCall.putExtra("meetingType", "video");
                intentCall.putExtra("roomId", roomId);
                startActivity(intentCall);
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSend:
                createMessage();
                break;
            case R.id.btnSendImage:
                break;
        }
    }

    private void showMessages(String roomId) {
        db.collection("rooms")
                .document(roomId)
                .collection("messages")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        ArrayList<Message> messages = new ArrayList<>();
                        for (QueryDocumentSnapshot doccument : value){
                            Message message = new Message();

                            message.setId(doccument.getId());
                            message.setContent(doccument.get("content").toString());
                            message.setSenderName(doccument.get("senderName").toString());
                            message.setSenderEmail(doccument.get("senderEmail").toString());
                            message.setSenderImage(doccument.get("senderImage").toString());
                            message.setCreatedAt(doccument.get("createdAt").toString());

                            messages.add(message);
                        }

                        messageAdapter.setData(messages);
                    }
                });
    }

    private void createMessage() {
        String content = edSend.getText().toString().trim();
        if (content.isEmpty()){
            Toast.makeText(this, "Please enter a message!", Toast.LENGTH_SHORT).show();
        }

        Map<String,Object> message = new HashMap<>();
        message.put("content", content);
        message.put("senderName", currentUser.getDisplayName());
        message.put("senderEmail", currentUser.getEmail());
        message.put("senderImage", currentUser.getPhotoUrl().toString());
        message.put("createdAt", new Date());

        db.collection("rooms")
                .document(roomId)
                .collection("messages")
                .add(message)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        edSend.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
}