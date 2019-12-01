package com.nightphantom.movers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import com.nightphantom.movers.adapter.TrayekAdapter;
import com.nightphantom.movers.model.Trayek;
import com.nightphantom.movers.R;

public class TrayekActivity extends AppCompatActivity {
    public static final String EXTRA_PATH = "extra_path";
    public static final String EXTRA_HALTE_NAME = "extra_halte_name";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference trayekRef;

    private TrayekAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trayek);

        String path = getIntent().getStringExtra(EXTRA_PATH);
        String namaHalte = getIntent().getStringExtra(EXTRA_HALTE_NAME);
        trayekRef = db.document(path).collection("Trayek");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Daftar Trayek " + namaHalte);
        setUpRecyclerView();
    }

    private void setUpRecyclerView(){
        Query query = trayekRef.orderBy("name", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Trayek> options = new FirestoreRecyclerOptions.Builder<Trayek>()
                .setQuery(query, Trayek.class)
                .build();

        adapter = new TrayekAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.trayek_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new TrayekAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Trayek trayek = documentSnapshot.toObject(Trayek.class);
                Intent goJalur = new Intent(TrayekActivity.this,JalurActivity.class);
                goJalur.putExtra(JalurActivity.EXTRA_TRAYEK_NAME,trayek.getName());
                startActivity(goJalur); //you can also open another activity
            }
        });
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
