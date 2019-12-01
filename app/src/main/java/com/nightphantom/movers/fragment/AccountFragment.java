package com.nightphantom.movers.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Source;
import com.nightphantom.movers.MainActivity;
import com.nightphantom.movers.R;
import com.nightphantom.movers.activity.LoginActivity;
import com.nightphantom.movers.activity.UpdateProfile;
import com.nightphantom.movers.model.User;

import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    private FirebaseAuth mAuth;
    private TextView textEmail, textName, textPhone, btnRating, btnHelp, btnTerms, btnPrivacy, btnChangeProfile;
    private Button btnLogout;
    LinearLayout linearLayout;
    private RatingBar ratingBar;
    private EditText feedback;
    private Button btnSubmit;
    private TextView valueRating;
    private FirebaseFirestore mFirestore;



    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_account, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_profile);

        //Load user detail
        getUserDetail();


//------------------------------------------Initial identifier by id-------------------------------------------
        textEmail = rootView.findViewById(R.id.text_profile_email);
        textName = rootView.findViewById(R.id.text_profile_name);
        textPhone = rootView.findViewById(R.id.text_profile_phone);

        ratingBar = rootView.findViewById(R.id.ratingBar);
        feedback = rootView.findViewById(R.id.txtReview);
        valueRating =rootView.findViewById(R.id.valueRating);
        btnSubmit = rootView.findViewById(R.id.btnSubmitReview);
        btnLogout = rootView.findViewById(R.id.button_logout);
        btnHelp = rootView.findViewById(R.id.text_menu_help);
        btnTerms = rootView.findViewById(R.id.text_menu_terms);
        btnPrivacy = rootView.findViewById(R.id.text_menu_privacy);
        btnRating = rootView.findViewById(R.id.text_menu_rating);
        btnChangeProfile = rootView.findViewById(R.id.text_profile_update);


//--------------------------------------Initial For Rating------------------------------------------

        //get the bottom sheet view
        linearLayout = rootView.findViewById(R.id.bottom_sheet);
        //init the bottom sheet view
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //firebase authentication
        mAuth = FirebaseAuth.getInstance();
        //firestore db
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valueRating.setText(String.valueOf(rating));
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                valueRating.setText(String.valueOf(rating));
            }
        });


//--------------------------------------Handling Click Event---------------------------------------

        //handling click for submit review
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String rb = valueRating.getText().toString();
                String fb = feedback.getText().toString();
                Map<String, String> uMap = new HashMap<>();
                //default value rating bar
                ratingBar.setRating(5);
                uMap.put("rating",rb);
                uMap.put("review",fb);
                db.collection("Reviews")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .set(uMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getContext(), "Thank you for your feedback!",Toast.LENGTH_LONG).show();
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(getContext(), "Error in" + error,Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        //handling click for logout button
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                sendToLogin();

            }
        });

        //handling click to showing bottom sheet rating
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        btnChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goUpdate = new Intent(getActivity(), UpdateProfile.class);
                startActivity(goUpdate);
            }
        });

//------------------------------------------Check User Active---------------------------------------

        if (currentUser != null)
        {
            //Do something here
        }
        if (currentUser == null)
        {
            sendToLogin();
        }
        return rootView;
    }
//------------------------------------------------Method--------------------------------------------
    private void sendToLogin() {
        Intent loginIntent = new Intent(getContext(), LoginActivity.class);
        startActivity(loginIntent);
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
                        textName.setText(user.getName());
                        textPhone.setText(user.getPhone());
                        textEmail.setText(user.getEmail());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }
    private void sendToMain() {
        Intent mainIntent = new Intent(getContext(), MainActivity.class);
        startActivity(mainIntent);
    }

}
