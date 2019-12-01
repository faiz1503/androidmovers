package com.nightphantom.movers.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.nightphantom.movers.R;
import com.nightphantom.movers.model.Jalur;

public class JalurAdapter extends FirestoreRecyclerAdapter<Jalur, JalurAdapter.JalurHolder> {

    public OnItemClickListener listener;

    public JalurAdapter(@NonNull FirestoreRecyclerOptions<Jalur> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull JalurHolder jalurHolder, int i, @NonNull Jalur jalur) {
        jalurHolder.textViewAsal.setText("Dari: " + jalur.getAsal());
        jalurHolder.textViewTujuan.setText("Tujuan: " + jalur.getTujuan());
        jalurHolder.textViewJam.setText("Waktu Tiba: " + timeConverter(jalur.getJam()));
    }

    @NonNull
    @Override
    public JalurHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.jalur_item,
                parent, false);
        return new JalurHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class JalurHolder extends RecyclerView.ViewHolder{
        TextView textViewAsal;
        TextView textViewTujuan;
        TextView textViewJam;

        public JalurHolder(@NonNull View itemView) {
            super(itemView);
            textViewAsal = itemView.findViewById(R.id.text_view_asal_jalur);
            textViewTujuan = itemView.findViewById(R.id.text_view_tujuan_jalur);
            textViewJam = itemView.findViewById(R.id.text_view_jam);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        //sending data from adapter to MainActivity
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }
    //this method should below NoteHolder | RecyclerView.ViewHolder  Class
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    private String timeConverter(double x){
        String result = "";

        double lowerRound = Math.floor(x);
        int hours = (int)lowerRound;
        double leftOver = Math.abs(x-lowerRound * 5.0);
        int minutes = (int)leftOver;

        if(hours < 10)result += "0";

        result += hours + ":" + minutes;

        return result;
    }
}
