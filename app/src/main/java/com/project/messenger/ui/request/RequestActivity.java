package com.project.messenger.ui.request;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.project.messenger.R;
import com.project.messenger.adapters.RequestAdapter;
import com.project.messenger.models.Request;
import com.project.messenger.models.Room;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        initViews();
        loadData();
        setUpToolBar();
    }

    private void loadData() {
        ArrayList<Request> requests = new ArrayList<>();
        requests.add(new Request("asdajsdnaasas","Rác rưởi vkl","Mệt vkl",false, "https://i.pinimg.com/564x/6e/d7/85/6ed7853fc5154d29856ec94d9aa4efb7.jpg"));
        requests.add(new Request("asdajsdnaasas","Shipper trần duy dưng","Làm thế nào?",false, "https://i.pinimg.com/564x/6e/d7/85/6ed7853fc5154d29856ec94d9aa4efb7.jpg"));
        requests.add(new Request("asdajsdnaasas","Bé",":->",false, "https://i.pinimg.com/564x/6e/d7/85/6ed7853fc5154d29856ec94d9aa4efb7.jpg"));
        requestAdapter.setData(requests);
    }

    private void setUpToolBar() {
        toolbar.setTitle("Requests");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.requestToolBar);
        recyclerView = findViewById(R.id.rcRequest);

        requestAdapter = new RequestAdapter(getLayoutInflater());
        recyclerView.setAdapter(requestAdapter);
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
}