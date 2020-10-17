package com.project.messenger.ui.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.messenger.R;
import com.project.messenger.adapters.RoomAdapter;
import com.project.messenger.models.Room;
import com.project.messenger.ui.createRoom.CreateRoomActivity;
import com.project.messenger.ui.request.RequestActivity;
import com.project.messenger.ui.user.ProfileActivity;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class HomeActivity extends AppCompatActivity implements RoomAdapter.OnClickRoomListener, View.OnClickListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private EditText edSearch;
    private FloatingActionButton fabCreateRoom;
    private ImageView userImage, requestBtn, editProfileBtn;

    private RecyclerView recyclerView;
    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        loadData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Glide.with(this).load(currentUser.getPhotoUrl()).into(userImage);
    }

    private void loadData() {
        ArrayList<Room> rooms = new ArrayList<>();
        rooms.add(new Room("ashdgahssd", "Qwerty", "https://i.pinimg.com/564x/d6/c0/37/d6c0373a51aa7de02862a96b75b11826.jpg", "21/122/2020"));
        rooms.add(new Room("asdfgdfdsf", "K32B1", "https://i.pinimg.com/564x/d6/c0/37/d6c0373a51aa7de02862a96b75b11826.jpg", "21/122/2020"));
        rooms.add(new Room("fgrhtyjghj", "VRV", "https://i.pinimg.com/564x/d6/c0/37/d6c0373a51aa7de02862a96b75b11826.jpg", "21/122/2020"));
        rooms.add(new Room("ashdgrahfhsd", "HYS", "https://i.pinimg.com/564x/d6/c0/37/d6c0373a51aa7de02862a96b75b11826.jpg", "21/122/2020"));
        roomAdapter.setData(rooms);
    }

    private void initViews() {
        edSearch = findViewById(R.id.edSearchRoom);
        fabCreateRoom = findViewById(R.id.fabCreateRoom);
        recyclerView = findViewById(R.id.rcListRoom);
        userImage = findViewById(R.id.userImage);
        requestBtn = findViewById(R.id.requestBtn);
        editProfileBtn = findViewById(R.id.editProfileBtn);

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
        intent.putExtra("room", room);
        startActivity(intent);
    }

    private void swipeRecyclerView(RecyclerView recyclerView, final RoomAdapter adapter){
        ItemTouchHelper.SimpleCallback simpleCallbackDelete = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Room room = adapter.getData().get(viewHolder.getAdapterPosition());
                switch (direction){
                    case ItemTouchHelper.LEFT:
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
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
        switch (v.getId()){
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
}