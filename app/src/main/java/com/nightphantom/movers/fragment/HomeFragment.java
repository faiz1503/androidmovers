package com.nightphantom.movers.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.nightphantom.movers.R;
import com.nightphantom.movers.activity.BarcodeActivity;
import com.nightphantom.movers.activity.HistoryActivity;
import com.nightphantom.movers.activity.TopupActivity;
import com.nightphantom.movers.activity.TrayekActivity;
import com.nightphantom.movers.adapter.HalteAdapter;
import com.nightphantom.movers.directionhelpers.FetchURL;
import com.nightphantom.movers.model.Halte;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback{
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(getContext(), "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: ready");

        mMap = googleMap;

        halteRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Halte halte = documentSnapshot.toObject(Halte.class);
                            createMarker(halte.getLocation().getLatitude()
                                    , halte.getLocation().getLongitude()
                                    , halte.getName());
                            markersArray.add(halte);
                        }
                    }
                });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(myLoc == null)
                    getDeviceLocation();

                marker.showInfoWindow();

                Toast.makeText(getActivity(), marker.getTitle(), Toast.LENGTH_SHORT).show();
                new FetchURL(getActivity()).execute(getUrl(myLoc
                        , marker.getPosition(), "driving"), "driving");

                List<LatLng> tempLatLng = new ArrayList<LatLng> ();
                tempLatLng.add(myLoc);
                tempLatLng.add(marker.getPosition());

                zoomRoute(tempLatLng);

                return true;
            }
        });

        if (mLoationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }
    }

    private static final String TAG = "HomeFragment";

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int ERROR_DIALOG_REQUEST = 9001;

    //Widgets
    private EditText mSearchText;
    private ImageView mGps, mNearYou, btnQr, btnTopup, btnHistory, transparentImageView;
    private ScrollView mScrollView;
    private View rootView;
    private TextView mUserWalletAmount;

    //vars
    private Boolean mLoationPermissionsGranted = false;
    public static GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FirebaseAuth mAuth;
    private LatLng myLoc, targetHalte;
    public static Polyline currentPolyline;
    private ArrayList<Halte> markersArray = new ArrayList<Halte>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference halteRef = db.collection("Halte");

    private HalteAdapter adapter;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_home, container, false);

        getUserWallet();

        mSearchText = rootView.findViewById(R.id.input_search);
        btnQr = rootView.findViewById(R.id.btnQr);
        btnTopup = rootView.findViewById(R.id.btn_menu_topUp);
        btnHistory = rootView.findViewById(R.id.btn_menu_history);
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), HistoryActivity.class);
                startActivity(i);
            }
        });
        mUserWalletAmount = rootView.findViewById(R.id.text_mopay_amount);
        //Maps
        isServicesOK();
        mGps = (ImageView) rootView.findViewById(R.id.ic_gps);
        transparentImageView = (ImageView) rootView.findViewById(R.id.transparent_image);
        mNearYou = (ImageView) rootView.findViewById(R.id.ic_near_you);
        getLocationPermission();
        //QR Create
        btnQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goQr = new Intent(getContext(), BarcodeActivity.class);
                startActivity(goQr);
            }
        });

        btnTopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goTopUp = new Intent(getContext(), TopupActivity.class);
                startActivity(goTopUp);
            }
        });

        setUpRecyclerView();

        return rootView;
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
                        Toast.makeText(getContext(), "Tidak ada dokumen yang dimaksud", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Error pada: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });


        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        mNearYou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myLoc != null){
                    final Location mMyLocation = new Location("My Location");
                    mMyLocation.setLatitude(myLoc.latitude);
                    mMyLocation.setLongitude(myLoc.longitude);
                    Collections.sort(markersArray, new Comparator<Halte>() {

                                @Override
                                public int compare(Halte a, Halte b) {
                                    Location locationA = new Location("point A");
                                    locationA.setLatitude(a.getLocation().getLatitude());
                                    locationA.setLongitude(a.getLocation().getLongitude());
                                    Location locationB = new Location("point B");
                                    locationB.setLatitude(b.getLocation().getLatitude());
                                    locationB.setLongitude(b.getLocation().getLongitude());
                                    float distanceOne = mMyLocation.distanceTo(locationA);
                                    float distanceTwo = mMyLocation.distanceTo(locationB);
                                    return Float.compare(distanceOne, distanceTwo);
                                }
                            }
                    );
                    LatLng tempMarker = new LatLng(markersArray.get(0).getLocation().getLatitude(),markersArray.get(0).getLocation().getLongitude());
                    new FetchURL(getActivity()).execute(getUrl(myLoc
                            , tempMarker
                            , "driving"), "driving");

                    List<LatLng> tempLatLng = new ArrayList<LatLng> ();
                    tempLatLng.add(myLoc);
                    tempLatLng.add(tempMarker);

                    zoomRoute(tempLatLng);
                }
            }
        });


        hideSoftKeyboard();
    }

    private void geoLocate(){
        Log.d(TAG, "getLocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(getContext());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG, "getLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "getLocate: found a location: " + address.toString());
//            Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),DEFAULT_ZOOM,address.getAddressLine(0));
        }
    }

    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());

        try{
            if (mLoationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                ((com.google.android.gms.tasks.Task) location).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location)task.getResult();

                            myLoc = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    DEFAULT_ZOOM,
                                    "My Location");
                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(getContext(), "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d(TAG, "getDeviceLocation: Security Exception: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom,String title){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng" + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals("My Location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void initMap(){
        Log.d(TAG, "initMap: initialize map");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mScrollView = (ScrollView) rootView.findViewById(R.id.sv_container);

        transparentImageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        mScrollView.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        mScrollView.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });

        mapFragment.getMapAsync(this);
    }

    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: gettting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLoationPermissionsGranted = true;
                initMap();
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLoationPermissionsGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLoationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLoationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: Checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG,"isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(),available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void setUpRecyclerView(){
        Query query = halteRef.orderBy("name",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<Halte> options = new FirestoreRecyclerOptions.Builder<Halte>()
                .setQuery(query, Halte.class)
                .build();

        adapter = new HalteAdapter(options);

        RecyclerView recyclerView = rootView.findViewById(R.id.halte_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new HalteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                Halte halte = documentSnapshot.toObject(Halte.class);
                String path = documentSnapshot.getReference().getPath();//result : Halte/'Document ID'

                Intent goTrayek= new Intent( getContext(), TrayekActivity.class);
                goTrayek.putExtra(TrayekActivity.EXTRA_PATH,path);
                goTrayek.putExtra(TrayekActivity.EXTRA_HALTE_NAME,halte.getName());
                startActivity(goTrayek);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(@DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(getContext(), R.drawable.ic_map_marker);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(getContext(), vectorDrawableResourceId);
        vectorDrawable.setBounds(12, 5, vectorDrawable.getIntrinsicWidth()+12, vectorDrawable.getIntrinsicHeight()+5);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //Direction
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    protected Marker createMarker(double latitude, double longitude, String title) {

        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .icon(bitmapDescriptorFromVector(R.drawable.ic_halte_white)));
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 100;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

}
