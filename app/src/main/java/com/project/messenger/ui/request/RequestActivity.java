package com.project.messenger.ui.request;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.messenger.R;
import com.project.messenger.adapters.RequestAdapter;
import com.project.messenger.models.Request;
import com.project.messenger.models.Room;
import com.project.messenger.models.User;

import java.util.ArrayList;

public class RequestActivity extends AppCompatActivity implements RequestAdapter.OnClickRequestListener {
    private static String TAG = "RequestActivity";
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView tvRequestCount;
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
        db.collection("requests")
                .whereEqualTo("to", currentUser.getEmail())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        tvRequestCount.setText("Request count: " + value.size());

                        ArrayList<Request> requests = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            Request request = new Request();
                            request.setId(document.getId());
                            request.setFrom(document.get("from").toString());
                            request.setFromUrl(document.get("fromUserImage").toString());
                            request.setMessage(document.get("message").toString());
                            request.setStatus((Boolean) document.get("status"));
                            requests.add(request);
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                        requestAdapter.setData(requests);
                    }
                });
    }

    private void setUpToolBar() {
        toolbar.setTitle("Requests");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.requestToolBar);
        progressBar = findViewById(R.id.progressBar);
        tvRequestCount = findViewById(R.id.tvRequestCount);
        recyclerView = findViewById(R.id.rcRequest);

        requestAdapter = new RequestAdapter(getLayoutInflater());
        requestAdapter.setListener(this);
        recyclerView.setAdapter(requestAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onAcceptRequest(Request request) {
        db.collection("requests").document(request.getId())
                .update("status", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RequestActivity.this, "You've accepted this request!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }

    @Override
    public void onDenyRequest(Request request) {
        db.collection("requests").document(request.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RequestActivity.this, "You've denied this request!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
    }
}