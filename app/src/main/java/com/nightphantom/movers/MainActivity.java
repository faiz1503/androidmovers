package com.nightphantom.movers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nightphantom.movers.directionhelpers.TaskLoadedCallback;
import com.nightphantom.movers.fragment.AccountFragment;
import com.nightphantom.movers.fragment.HistoryFragment;
import com.nightphantom.movers.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, TaskLoadedCallback {
    LinearLayout btn_scanqr, btn_topup, btn_history;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_scanqr = findViewById(R.id.btn_menu_scanQr);
        btn_topup = findViewById(R.id.btn_menu_topUp);
        btn_history = findViewById(R.id.btn_menu_history);


        // kita set default nya Home Fragment
        loadFragment(new HomeFragment());
        // inisialisasi BottomNavigaionView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_main);
        // beri listener pada saat item/menu bottomnavigation terpilih
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

    }

    // method listener untuk logika pemilihan
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()){
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;
            case R.id.navigation_history:
                fragment = new HistoryFragment();
                break;
            case R.id.navigation_profile:
                fragment = new AccountFragment();
                break;
        }
        return loadFragment(fragment);
    }
    // load fragment yang sesuai
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (HomeFragment.currentPolyline != null)
            HomeFragment.currentPolyline.remove();
//        HomeFragment.currentPolyline = HomeFragment.mMap.addPolyline((PolylineOptions) values[0]);
        PolylineOptions po = (PolylineOptions)values[0];
        po.color(Color.BLACK).width(5);
        po.color(getApplicationContext().getResources().getColor(R.color.colorBlue50))
                .width(4);
        HomeFragment.currentPolyline = HomeFragment.mMap.addPolyline(po);
    }
}
