package com.hiepdt.annavoochackathon.converse.suggest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.models.Center;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class SuggestAdapter extends RecyclerView.Adapter<SuggestAdapter.ViewHolder> {
    Context mContext;
    ArrayList<Center> mListCenter;


    public SuggestAdapter(Context mContext, ArrayList<Center> mListCenter) {
        this.mContext = mContext;
        this.mListCenter = mListCenter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_suggest, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Center center = mListCenter.get(position);
        holder.name.setText(center.getName());
        holder.address.setText(center.getAddress());
        holder.open.setText(center.getOpen());
        holder.distance.setText("300m");
        if (center.getUrl() != null){
            Glide.with(holder.image).load(center.getUrl()).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return mListCenter.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, address, open, distance;
        private RoundedImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvName);
            address = itemView.findViewById(R.id.address);
            open = itemView.findViewById(R.id.open);
            distance = itemView.findViewById(R.id.distance);
            image = itemView.findViewById(R.id.image);

        }
    }
}
