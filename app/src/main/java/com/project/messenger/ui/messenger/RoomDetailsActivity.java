package com.project.messenger.ui.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.messenger.R;
import com.project.messenger.adapters.UserAdapter;
import com.project.messenger.ui.createRoom.CreateRoomActivity;
import com.project.messenger.utils.LoadingDialog;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private String roomId;

    private Toolbar toolbar;
    private CircleImageView roomImage;
    private CardView addMem, leaveRoom;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Bundle extras = getIntent().getExtras();
        roomId = extras.getString("roomId");
        initViews();
        setUpToolbar();
    }

    private void initViews() {
        toolbar = findViewById(R.id.roomDetailsToolbar);
        roomImage = findViewById(R.id.roomDetailsImage);
        addMem = findViewById(R.id.addMemBtn);
        leaveRoom = findViewById(R.id.leaveRoomBtn);

        addMem.setOnClickListener(this);
        leaveRoom.setOnClickListener(this);
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addMemBtn:
                Toast.makeText(this, "Add members", Toast.LENGTH_SHORT).show();
                break;
            case R.id.leaveRoomBtn:
                leaveRoom();
                break;
        }
    }

    private void leaveRoom() {
        db.collection("rooms").document(roomId)
                .update("users", FieldValue.arrayRemove(currentUser.getEmail()))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(RoomDetailsActivity.this, "You've leaved this room!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}