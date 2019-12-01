package com.nightphantom.movers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nightphantom.movers.adapter.JalurAdapter;
import com.nightphantom.movers.model.Jalur;
import com.nightphantom.movers.R;

public class JalurActivity extends AppCompatActivity {

    public static final String EXTRA_TRAYEK_NAME = "extra_name";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference jalurRef = db.collection("Jalur");

    private JalurAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jalur);

        final String namaTrayek = getIntent().getStringExtra(EXTRA_TRAYEK_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Daftar Jalur "+namaTrayek);
        setUpRecyclerView();

    }

    private void setUpRecyclerView(){
        String namaTrayek = getIntent().getStringExtra(EXTRA_TRAYEK_NAME);

        Query query = jalurRef.whereEqualTo("namaTrayek",namaTrayek)
                .orderBy("jam", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Jalur> options = new FirestoreRecyclerOptions.Builder<Jalur>()
                .setQuery(query, Jalur.class)
                .build();

        adapter = new JalurAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.jalur_recycler_view);
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
