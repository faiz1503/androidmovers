package com.nightphantom.movers.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.nightphantom.movers.MainActivity;
import com.nightphantom.movers.R;
import com.nightphantom.movers.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText loginEmailField, loginPasswordField;
    private FirebaseAuth mAuth;
    private ProgressBar loginProgress;
    Button loginButton;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setPersistenceEnabled(true)
                .build();
        firebaseFirestore.setFirestoreSettings(settings);

        loginEmailField = findViewById(R.id.login_email);
        loginPasswordField = findViewById(R.id.login_password);
        loginPasswordField = findViewById(R.id.login_password);


        loginButton = findViewById(R.id.login_btn);

        registerButton = findViewById(R.id.reg_btn);

        loginProgress = findViewById(R.id.login_progress);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String loginEmail = loginEmailField.getText().toString();
                String loginPass = loginPasswordField.getText().toString();

                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)) {
                    loginProgress.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                sendToMainActivity();

                            } else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();

                            }

                            loginProgress.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            sendToMainActivity();
        }
    }


    private void sendToMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);

        finish();

    }
}
