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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nightphantom.movers.MainActivity;
import com.nightphantom.movers.R;
import com.nightphantom.movers.model.User;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerPhone;
    private EditText registerName;

    private Button registerButton;
    private Button loginButton;

    private ProgressBar registerProgress;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        registerEmail = findViewById(R.id.reg_email);
        registerPassword = findViewById(R.id.reg_password);
        registerName = findViewById(R.id.reg_name);
        registerPhone = findViewById(R.id.reg_phone);
        registerButton = findViewById(R.id.reg_btn);
        loginButton = findViewById(R.id.btn_login_reg);
        registerProgress = findViewById(R.id.reg_progress);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email = registerEmail.getText().toString().trim();
                String password = registerPassword.getText().toString().trim();
                final String name = registerName.getText().toString().trim();
                final String phone = registerPhone.getText().toString().trim();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)) {

                        registerProgress.setVisibility(View.VISIBLE);

                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    User user = new User(

                                            name,
                                            email,
                                            phone

                                    );

                                    Map<String, Object> wallet = new HashMap<>();
                                    wallet.put("amount",0);

                                    FirebaseFirestore.getInstance()
                                            .collection("Wallet")
                                            .document(mAuth.getInstance().getCurrentUser().getUid())
                                            .set(wallet)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(RegisterActivity.this, "Wallet Create Successfuly", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    FirebaseFirestore.getInstance()
                                            .collection("Users")
                                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .set(user)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(RegisterActivity.this, "Register Successfuly", Toast.LENGTH_SHORT).show();
                                                    sendToMainActivity();
                                                }
                                            });
                                } else {
                                    Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }

                                registerProgress.setVisibility(View.INVISIBLE);

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
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
