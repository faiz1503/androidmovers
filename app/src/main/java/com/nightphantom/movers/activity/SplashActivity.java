package com.nightphantom.movers.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.nightphantom.movers.R;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler;
    private Runnable mRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

       mRunnable = new Runnable() {
           @Override
           public void run() {
               startActivity(new Intent(getApplicationContext(), LoginActivity.class));
           }
       };


     mHandler = new Handler();
     mHandler.postDelayed(mRunnable, 2000);

    }

    @Override
    protected void onDestroy(){

        super.onDestroy();
        if(mHandler!= null && mRunnable != null){
            mHandler.removeCallbacks(mRunnable);
        }
    }
}
