package com.nightphantom.movers.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nightphantom.movers.R;
import com.nightphantom.movers.adapter.HistoryAdapter;
import com.nightphantom.movers.model.History;

public class HistoryActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference historyRef = db.collection("History/" + mAuth.getCurrentUser().getUid() + "/transaction");

    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("History");
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        Query query = historyRef
                .orderBy("date", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<History> options = new FirestoreRecyclerOptions.Builder<History>()
                .setQuery(query, History.class)
                .build();

        adapter = new HistoryAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.history_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
