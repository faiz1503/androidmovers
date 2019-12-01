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
import com.nightphantom.movers.model.History;

public class HistoryAdapter extends FirestoreRecyclerAdapter<History, HistoryAdapter.HistoryHolder> {

    public OnItemClickListener listener;

    public HistoryAdapter(@NonNull FirestoreRecyclerOptions<History> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull HistoryHolder historyHolder, int i, @NonNull History history) {
        historyHolder.textViewAmount.setText("Rp " + String.valueOf(history.getAmount()));
        historyHolder.textViewDate.setText(history.getDate());
        historyHolder.textViewType.setText(history.getType());
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,
                parent, false);
        return new HistoryHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class HistoryHolder extends RecyclerView.ViewHolder {
        TextView textViewAmount;
        TextView textViewDate;
        TextView textViewType;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);
            textViewAmount = itemView.findViewById(R.id.text_view_amount);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewType = itemView.findViewById(R.id.text_view_type);

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
