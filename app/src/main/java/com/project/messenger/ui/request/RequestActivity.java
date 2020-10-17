package com.project.messenger.ui.request;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.messenger.R;
import com.project.messenger.adapters.RequestAdapter;
import com.project.messenger.models.Request;
import com.project.messenger.models.Room;
import com.project.messenger.models.User;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity {
    private static String TAG = "RequestActivity";
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private RequestAdapter requestAdapter;

    private Toolbar toolbar;

    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        initViews();
        loadData();
        setUpToolBar();
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        requests.add(new Request("asdajsdnaasas","Rác rưởi vkl","Mệt vkl",false, "https://i.pinimg.com/564x/6e/d7/85/6ed7853fc5154d29856ec94d9aa4efb7.jpg"));
//        requests.add(new Request("asdajsdnaasas","Shipper trần duy dưng","Làm thế nào?",false, "https://i.pinimg.com/564x/6e/d7/85/6ed7853fc5154d29856ec94d9aa4efb7.jpg"));
//        requests.add(new Request("asdajsdnaasas","Bé",":->",false, "https://i.pinimg.com/564x/6e/d7/85/6ed7853fc5154d29856ec94d9aa4efb7.jpg"));
        db.collection("requests").whereEqualTo("to", currentUser.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        ArrayList<Request> requests = new ArrayList<>();
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                Request request = new Request();
                                request.setId(document.getId());
                                request.setFrom(document.get("from").toString());
                                request.setFromUrl(document.get("fromUserImage").toString());
                                request.setMessage(document.get("message").toString());
                                request.setStatus((Boolean) document.get("status"));
                                Log.d(TAG, "onComplete: " + document.get("status"));
                                requests.add(request);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            requestAdapter.setData(requests);
                        }else {
                            Log.e(TAG, "onComplete: Something went wrong!" );
                        }
                    }
                });
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
        progressBar = findViewById(R.id.progressBar);
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