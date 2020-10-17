package com.project.messenger.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.project.messenger.R;
import com.project.messenger.ui.messenger.RoomDetailsActivity;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private TextView settings, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setUpToolBar();
    }

    private void setUpToolBar() {
        toolbar.setTitle("My profile");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.profileToolBar);
        settings = findViewById(R.id.btnSettings);
        logout = findViewById(R.id.btnLogout);

        settings.setOnClickListener(this);
        logout.setOnClickListener(this);
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
            case R.id.btnSettings:
                break;
            case R.id.btnLogout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "You've signed out!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}