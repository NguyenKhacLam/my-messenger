package com.project.messenger.ui.messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.project.messenger.R;

public class MessageRoomActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private EditText edSend;
    private ImageView chooseImageBtn, sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_room);

        initView();
        setUpToolbar();
    }

    private void initView() {
        toolbar = findViewById(R.id.messageToolbar);
        recyclerView = findViewById(R.id.rcMessage);
        edSend = findViewById(R.id.edSendMessage);
        chooseImageBtn = findViewById(R.id.btnSendImage);
        sendBtn = findViewById(R.id.btnSend);
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
                finish();
                break;
            case R.id.roomDetails:
                startActivity(new Intent(this, RoomDetailsActivity.class));
                break;
        }
        return true;
    }
}