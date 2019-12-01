package com.nightphantom.movers.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nightphantom.movers.R;

public class AtmActivity extends BottomSheetDialogFragment {

    public static AtmActivity newInstance() {
        AtmActivity fragment = new AtmActivity();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.activity_atm, null);
        dialog.setContentView(contentView);
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }
}
