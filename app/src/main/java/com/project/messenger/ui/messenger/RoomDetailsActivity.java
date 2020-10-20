package com.project.messenger.ui.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.messenger.R;
import com.project.messenger.adapters.UserAdapter;
import com.project.messenger.adapters.UserListAdapter;
import com.project.messenger.models.User;
import com.project.messenger.ui.createRoom.CreateRoomActivity;
import com.project.messenger.utils.LoadingDialog;
import com.project.messenger.utils.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private String roomId;

    private Toolbar toolbar;
    private CircleImageView roomImage;
    private CardView addMem, leaveRoom;
    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;
    private AlertDialog dialog;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Bundle extras = getIntent().getExtras();
        roomId = extras.getString("roomId");
        loadingDialog = new LoadingDialog(this);

        initViews();
        setUpToolbar();
        loadData();
    }

    private void loadData() {
        db.collection("rooms").document(roomId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String email = documentSnapshot.get("users").toString();
                        String[] emailListArray = email.replaceAll("[()\\[\\]]","").split(",");
                        ArrayList<String> emailList = new ArrayList<>();
                        Collections.addAll(emailList, emailListArray);
                        userListAdapter.setData(emailList);
                    }
                });
    }

    private void initViews() {
        toolbar = findViewById(R.id.roomDetailsToolbar);
        roomImage = findViewById(R.id.roomDetailsImage);
        addMem = findViewById(R.id.addMemBtn);
        leaveRoom = findViewById(R.id.leaveRoomBtn);

        recyclerView = findViewById(R.id.rcListRoomUser);
        userListAdapter = new UserListAdapter(getLayoutInflater());
        recyclerView.setAdapter(userListAdapter);

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
                createPopup();
                break;
            case R.id.leaveRoomBtn:
                leaveRoom();
                break;
        }
    }

    private void sendRequestAddMemberByEmail(final String email){
        Map<String, Object> request = new HashMap<>();
        request.put("from", currentUser.getEmail());
        request.put("to", email);
        request.put("message","We need to talk!");
        request.put("roomId", roomId);
        request.put("status", false);
        request.put("fromUserImage", currentUser.getPhotoUrl().toString());
        request.put("createdAt", new Date());

        db.collection("requests").add(request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(RoomDetailsActivity.this, "Request sent to " + email, Toast.LENGTH_SHORT).show();
                        loadingDialog.dismissDialog();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Create request", "onSuccess: " + e.getMessage());
                    }
                });
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

    private void createPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RoomDetailsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_member, null);

        final EditText edEmail = view.findViewById(R.id.edAddMem);
        Button addBtn = view.findViewById(R.id.btnAddMem);
        Button cancelBtn = view.findViewById(R.id.btnCancel);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                final String email = edEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(RoomDetailsActivity.this, "Please enter email name", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if user exists
                    db.collection("users").whereEqualTo("email", email)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){
                                        QuerySnapshot snapshot = task.getResult();

                                        if (!Validate.isValidEmail(email)){
                                            Toast.makeText(RoomDetailsActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismissDialog();
                                        }else {
                                            if (snapshot.isEmpty()){
                                                Toast.makeText(RoomDetailsActivity.this, "Email Not found!", Toast.LENGTH_SHORT).show();
                                                loadingDialog.dismissDialog();
                                            }else {
                                                sendRequestAddMemberByEmail(email);
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog = builder.setView(view).create();
        dialog.show();
    }
}