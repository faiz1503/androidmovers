package com.nightphantom.movers.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nightphantom.movers.MainActivity;
import com.nightphantom.movers.R;
import com.nightphantom.movers.activity.ArticleDetailActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    CardView click;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_history, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_timeline);


        click = rootView.findViewById(R.id.cardNews);

        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goDetail = new Intent(getContext(), ArticleDetailActivity.class);
                startActivity(goDetail);
            }
        });


        return rootView;
    }




}
