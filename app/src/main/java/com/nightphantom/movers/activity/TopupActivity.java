package com.nightphantom.movers.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;
import com.nightphantom.movers.R;

import java.text.NumberFormat;
import java.util.Locale;

public class TopupActivity extends AppCompatActivity implements View.OnClickListener {
    Toolbar toolbar;
    RelativeLayout atm, mobile, merchant, mobank;
    private TextView mUserWalletAmount;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);
        getUserWallet();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_topup);

        mUserWalletAmount = findViewById(R.id.text_mopay_amount);
        atm = (RelativeLayout) findViewById(R.id.atm);
        mobile = (RelativeLayout) findViewById(R.id.mobile);
        merchant = (RelativeLayout) findViewById(R.id.merchant);

        atm.setOnClickListener(this);
        mobile.setOnClickListener(this);
        merchant.setOnClickListener(this);

        final RelativeLayout atm = findViewById(R.id.atm);
        atm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtmActivity AtmActivityDialog = AtmActivity.newInstance();
                AtmActivityDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });

        final RelativeLayout mobile = findViewById(R.id.mobile);
        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobileActivity MobileActivityDialog = MobileActivity.newInstance();
                MobileActivityDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });

        final RelativeLayout merchant = findViewById(R.id.merchant);
        merchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MerchantActivity MerchantActivityDialog = MerchantActivity.newInstance();
                MerchantActivityDialog.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
            }
        });
    }

    public void onClick(View v) {
        Intent i;
        if (v.getId() == R.id.atm) {
            startActivity(new Intent(this, TopupActivity.class));
        } else if (v.getId() == R.id.mobile) {
            startActivity(new Intent(this, TopupActivity.class));
        } else if (v.getId() == R.id.merchant) {
            startActivity(new Intent(this, TopupActivity.class));
        }
    }

    public void callATMPage(View view) {
        Intent i = new Intent(this, AtmActivity.class);
        startActivity(i);
    }

    public void getUserWallet(){
        //firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //firestore db
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        DocumentReference walletRef = db.collection("Wallet").document(mAuth.getCurrentUser().getUid());
        Source source = Source.SERVER;
        walletRef.get(source).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    //Wallet wallet = documentSnapshot.toObject(Wallet.class);
                    int amount = documentSnapshot.getLong("amount").intValue();
                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                    if (documentSnapshot.exists()) {
                        mUserWalletAmount.setText(formatRupiah.format((double)amount));
                    } else {
                        Toast.makeText(getApplicationContext(), "Tidak ada dokumen yang dimaksud", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Error pada: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}
