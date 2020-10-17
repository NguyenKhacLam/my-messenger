package com.project.messenger.ui.createRoom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.project.messenger.R;
import com.project.messenger.adapters.UserAdapter;
import com.project.messenger.models.User;

import java.util.ArrayList;

public class CreateRoomActivity extends AppCompatActivity implements TextWatcher {
    private Toolbar toolbar;
    private EditText edSearchUser;
    private Button doneChoosingBtn;
    private AlertDialog dialog;

    private RecyclerView recyclerView;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

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
        recyclerView.setAdapter(adapter);

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

    private void createPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateRoomActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_create_room,null);

        final EditText edRoomName = view.findViewById(R.id.edRoomName);
        Button createBtn = view.findViewById(R.id.btnCreate);
        Button cancelBtn = view.findViewById(R.id.btnCancel);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edRoomName.getText().toString().trim();
                if (name.isEmpty()){
                    Toast.makeText(CreateRoomActivity.this, "Please enter room name", Toast.LENGTH_SHORT).show();
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
}