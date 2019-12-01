package com.nightphantom.movers.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.nightphantom.movers.R;
import com.nightphantom.movers.model.User;

import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class UpdateProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private TextView get_name, get_phone;
    private Button btn_update;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Update Profile");
        //Load user detail
        getUserDetail();

        get_name = findViewById(R.id.get_name);
        get_phone = findViewById(R.id.get_phone);
        btn_update = findViewById(R.id.btn_update);

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = get_name.getText().toString();
                String phone = get_phone.getText().toString();
                Map<String, Object> uMap = new HashMap<>();
                uMap.put("name",name);
                uMap.put("phone", phone);
                db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(uMap, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "Profile success update!",Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(getApplicationContext(), "Error in" + error,Toast.LENGTH_LONG).show();
                    }
                });


            }
        });


    }

    private void getUserDetail(){
        //firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //firestore db
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference userRef = db.collection("Users").document(mAuth.getCurrentUser().getUid());
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    User user = document.toObject(User.class);
                    if (document.exists()) {
                        get_name.setText(user.getName());
                        get_phone.setText(user.getPhone());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }
}

