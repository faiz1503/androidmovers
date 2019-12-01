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
import com.nightphantom.movers.model.Trayek;

public class TrayekAdapter extends FirestoreRecyclerAdapter<Trayek, TrayekAdapter.TrayekHolder> {

    public OnItemClickListener listener;

    public TrayekAdapter(@NonNull FirestoreRecyclerOptions<Trayek> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TrayekHolder trayekHolder, int i, @NonNull Trayek trayek) {
        trayekHolder.textViewNama.setText(trayek.getName());
        trayekHolder.textViewKodeBus.setText("Plat Bus: " + trayek.getKode_bus());
    }

    @NonNull
    @Override
    public TrayekHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trayek_item,
                parent, false);
        return new TrayekHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class TrayekHolder extends RecyclerView.ViewHolder{
        TextView textViewNama;
        TextView textViewKodeBus;

        public TrayekHolder(@NonNull View itemView) {
            super(itemView);
            textViewNama = itemView.findViewById(R.id.text_view_nama_trayek);
            textViewKodeBus = itemView.findViewById(R.id.text_view_kode_bus);

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
