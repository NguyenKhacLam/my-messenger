package com.project.messenger.ui.createRoom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.messenger.R;
import com.project.messenger.adapters.UserAdapter;
import com.project.messenger.models.User;
import com.project.messenger.ui.user.LoginActivity;
import com.project.messenger.utils.LoadingDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateRoomActivity extends AppCompatActivity implements TextWatcher, UserAdapter.OnClickUserListener {
    private Toolbar toolbar;
    private EditText edSearchUser;
    private Button doneChoosingBtn;
    private AlertDialog dialog;

    private LoadingDialog loadingDialog;

    private RecyclerView recyclerView;
    private UserAdapter adapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // This array will hold list of request's receiver
    private ArrayList<User> usersRoomList = new ArrayList<>();

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        setUpToolbar();
        loadData();
    }

    private void loadData() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new User("ahsdghajsda","Nguyen Khac Lam","khaclam2409@gmail.com","https://i.pinimg.com/236x/0f/04/f5/0f04f57ceff009be91cf82583a0c50d3.jpg",false, "24/10/2020"));
        users.add(new User("ahsdghajsda","Nguyen The Huy","racruoivkl@gmail.com","https://i.pinimg.com/236x/0f/04/f5/0f04f57ceff009be91cf82583a0c50d3.jpg",false, "24/10/2020"));
        users.add(new User("ahsdghajsda","Nguyen Manh Trung","shippertranduyhung@gmail.com","https://i.pinimg.com/236x/0f/04/f5/0f04f57ceff009be91cf82583a0c50d3.jpg",false, "24/10/2020"));
        users.add(new User("ahsdghajsda","Dinh Manh Cuong","boysuccac@gmail.com","https://i.pinimg.com/236x/0f/04/f5/0f04f57ceff009be91cf82583a0c50d3.jpg",false, "24/10/2020"));
        adapter.setData(users);
    }

    private void initViews() {
        toolbar = findViewById(R.id.createRoomToolbar);
        edSearchUser = findViewById(R.id.edSearchUser);
        doneChoosingBtn = findViewById(R.id.doneChooseFriendBtn);
        recyclerView = findViewById(R.id.rcListUser);

        adapter = new UserAdapter(getLayoutInflater());
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);

        loadingDialog = new LoadingDialog(CreateRoomActivity.this);

        edSearchUser.addTextChangedListener(this);
        doneChoosingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopup();
            }
        });
    }

    private void setUpToolbar() {
        toolbar.setTitle("Choose Users");
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

    private void createPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateRoomActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_create_room,null);

        final EditText edRoomName = view.findViewById(R.id.edRoomName);
        Button createBtn = view.findViewById(R.id.btnCreate);
        Button cancelBtn = view.findViewById(R.id.btnCancel);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                String name = edRoomName.getText().toString().trim();
                if (name.isEmpty()){
                    Toast.makeText(CreateRoomActivity.this, "Please enter room name", Toast.LENGTH_SHORT).show();
                }else {
                    if (usersRoomList.size() == 0){
                        Toast.makeText(CreateRoomActivity.this, "Please select at least one user to chat!", Toast.LENGTH_SHORT).show();
                    }else {
                        for (User user : usersRoomList) {
                            createRequestInDb(user);
                        }
                    }
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

    private void createRequestInDb(User user) {
        Map<String, Object> request = new HashMap<>();
        request.put("from", currentUser.getEmail());
        request.put("to", user.getEmail());
        request.put("message","We need to talk, " + user.getUsername());
        request.put("status", false);
        request.put("fromUserImage", currentUser.getPhotoUrl().toString());
        request.put("createdAt", new Date());

        db.collection("requests").add(request)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                loadingDialog.dismissDialog();
                Log.d("Create request", "onSuccess: Done");
                finish();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Create request", "onSuccess: " + e.getMessage());
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        adapter.getFilter().filter(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    // Event click on checkbox in user
    @Override
    public void onClickUser(User user, int position) {
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
        CheckBox checkBox = holder.itemView.findViewById(R.id.checkBoxUser);

        if (usersRoomList.contains(user)){
            usersRoomList.remove(user);
            checkBox.setChecked(false);
        }else {
            usersRoomList.add(user);
            checkBox.setChecked(true);
        }
    }
}