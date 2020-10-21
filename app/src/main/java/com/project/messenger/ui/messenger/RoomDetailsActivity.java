package com.project.messenger.ui.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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
    private static final int GALLERY_CODE = 1;
    private String roomId;

    private Toolbar toolbar;
    private TextView roomName;
    private CircleImageView roomImage;
    private CardView addMem, leaveRoom, changeRoomName, changeImage;
    private RecyclerView recyclerView;
    private UserListAdapter userListAdapter;
    private AlertDialog dialog;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private LoadingDialog loadingDialog;
    private Uri imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        storageReference = FirebaseStorage.getInstance().getReference();

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

                        String name = documentSnapshot.get("name").toString();
                        roomName.setText(name);
                        Glide.with(RoomDetailsActivity.this).load(documentSnapshot.get("imageUrl").toString()).into(roomImage);

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
        roomName = findViewById(R.id.roomDetailsName);
        addMem = findViewById(R.id.addMemBtn);
        leaveRoom = findViewById(R.id.leaveRoomBtn);
        changeRoomName = findViewById(R.id.changeRoomNameBtn);
        changeImage = findViewById(R.id.changeImageBtn);

        recyclerView = findViewById(R.id.rcListRoomUser);
        userListAdapter = new UserListAdapter(getLayoutInflater());
        recyclerView.setAdapter(userListAdapter);

        addMem.setOnClickListener(this);
        leaveRoom.setOnClickListener(this);
        changeRoomName.setOnClickListener(this);
        changeImage.setOnClickListener(this);
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
            case R.id.changeRoomNameBtn:
                createChangeNamePopup();
                break;
            case R.id.changeImageBtn:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK){
            if (data != null){
                loadingDialog.startLoadingDialog();
                imageUrl = data.getData();
                roomImage.setImageURI(imageUrl);

                if (imageUrl != null){
                    final StorageReference filePath = storageReference.child("room_images")
                            .child("my_image_" + Timestamp.now().getSeconds());
                    filePath.putFile(imageUrl)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    loadingDialog.dismissDialog();
                                    Toast.makeText(RoomDetailsActivity.this, "", Toast.LENGTH_SHORT).show();
                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String img_url = uri.toString();
                                            db.collection("rooms").document(roomId)
                                                    .update("imageUrl", img_url);
                                        }
                                    });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingDialog.dismissDialog();
                                    Log.e("Fail", "onFailure: " + e.getMessage());
                                }
                            });
                }
            }
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

    private void createChangeNamePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RoomDetailsActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_change_room_name, null);

        final EditText edEmail = view.findViewById(R.id.edRoomName);
        Button acceptBtn = view.findViewById(R.id.btnAccept);
        Button cancelBtn = view.findViewById(R.id.btnCancel);

        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                final String name = edEmail.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(RoomDetailsActivity.this, "Please enter new name", Toast.LENGTH_SHORT).show();
                } else {
                    db.collection("rooms").document(roomId)
                            .update("name", name)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RoomDetailsActivity.this, "Change room name successfully!", Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismissDialog();
                                }
                            });
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadData();
            }
        });

        dialog = builder.setView(view).create();
        dialog.show();
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