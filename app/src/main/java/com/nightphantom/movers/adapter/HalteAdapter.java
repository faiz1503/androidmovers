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
import com.nightphantom.movers.model.Halte;

public class HalteAdapter extends FirestoreRecyclerAdapter<Halte, HalteAdapter.HalteHolder> {

    public OnItemClickListener listener;

    public HalteAdapter(@NonNull FirestoreRecyclerOptions<Halte> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HalteHolder halteHolder, int i, @NonNull Halte halte) {
        halteHolder.textViewNama.setText(halte.getName());
        halteHolder.textViewAlamat.setText(halte.getAddress());
    }

    @NonNull
    @Override
    public HalteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.halte_item,
                parent, false);
        return new HalteHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class HalteHolder extends RecyclerView.ViewHolder {
        TextView textViewNama;
        TextView textViewAlamat;

        public HalteHolder(@NonNull View itemView) {
            super(itemView);
            textViewNama = itemView.findViewById(R.id.text_view_nama_halte);
            textViewAlamat = itemView.findViewById(R.id.text_view_alamat);

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
}
