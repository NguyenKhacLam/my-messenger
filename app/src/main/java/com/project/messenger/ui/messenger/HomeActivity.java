package com.project.messenger.ui.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.messenger.R;
import com.project.messenger.adapters.RoomAdapter;
import com.project.messenger.models.Room;
import com.project.messenger.models.User;
import com.project.messenger.ui.createRoom.CreateRoomActivity;
import com.project.messenger.ui.request.RequestActivity;
import com.project.messenger.ui.user.ProfileActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeActivity extends AppCompatActivity implements RoomAdapter.OnClickRoomListener, View.OnClickListener, TextWatcher {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private EditText edSearch;
    private FloatingActionButton fabCreateRoom;
    private ImageView userImage, requestBtn, editProfileBtn;

    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        loadData();
    }

    private void loadData() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(this).load(currentUser.getPhotoUrl()).into(userImage);

        checkHasRequest();
        showRoomList();
    }

    private void initViews() {
        edSearch = findViewById(R.id.edSearchRoom);
        fabCreateRoom = findViewById(R.id.fabCreateRoom);
        recyclerView = findViewById(R.id.rcListRoom);
        userImage = findViewById(R.id.userImage);
        requestBtn = findViewById(R.id.requestBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);

        edSearch.addTextChangedListener(this);
        requestBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);
        fabCreateRoom.setOnClickListener(this);

        roomAdapter = new RoomAdapter(getLayoutInflater());
        roomAdapter.setListener(this);
        recyclerView.setAdapter(roomAdapter);

        swipeRecyclerView(recyclerView, roomAdapter);
    }

    @Override
    public void onClickRoom(Room room) {
        Intent intent = new Intent(this, MessageRoomActivity.class);
        intent.putExtra("roomId", room.getId());
        startActivity(intent);
    }

    private void swipeRecyclerView(RecyclerView recyclerView, final RoomAdapter adapter) {
        ItemTouchHelper.SimpleCallback simpleCallbackDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Room room = adapter.getData().get(viewHolder.getAdapterPosition());
                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        deleteRoomInDb(room.getId());
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.colorRed))
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(ContextCompat.getColor(HomeActivity.this, R.color.colorWhite))
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(simpleCallbackDelete).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.requestBtn:
                startActivity(new Intent(this, RequestActivity.class));
                break;
            case R.id.editProfileBtn:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.fabCreateRoom:
                startActivity(new Intent(this, CreateRoomActivity.class));
                break;
        }
    }

    private void checkHasRequest() {
        db.collection("requests")
                .whereEqualTo("to", currentUser.getEmail())
                .whereEqualTo("status", false)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        CardView dot = findViewById(R.id.requestDot);
                        if (value.size() > 0) {
                            dot.setVisibility(View.VISIBLE);
                        } else {
                            dot.setVisibility(View.GONE);
                        }
                    }
                });
    }


    private void showRoomList() {
        db.collection("rooms")
                .whereArrayContains("users", currentUser.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        ArrayList<Room> rooms = new ArrayList<>();
                        Log.d("Room", "onEvent: " + value.size());
                        for (QueryDocumentSnapshot document : value){
                            Room room = new Room();
                            room.setId(document.getId());
                            room.setName(document.get("name").toString());
                            room.setImageUrl(document.get("imageUrl").toString());
                            room.setCreatedAt(document.get("createdAt").toString());

                            rooms.add(room);
                        }
                        roomAdapter.setData(rooms);
                    }
                });
    }

    // On edSearch Changed
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        roomAdapter.getFilter().filter(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private void deleteRoomInDb(String id) {
        db.collection("rooms").document(id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(HomeActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}