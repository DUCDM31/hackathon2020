package com.hiepdt.annavoochackathon.community.messenger;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.community.inbox.InboxActivity;
import com.hiepdt.annavoochackathon.models.MesMatch;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesMatchesAdapter extends RecyclerView.Adapter<MesMatchesAdapter.ViewHolder> {

    private static String uID;
    Context context;
    ArrayList<MesMatch> mListMesMatch;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    public MesMatchesAdapter(Context context, ArrayList<MesMatch> mListMesMatch) {
        this.context = context;
        this.mListMesMatch = mListMesMatch;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        final MesMatch mesMatch = mListMesMatch.get(position);
        if (mesMatch.getCreateBy().equals(uID))
            holder.tvMes.setText("Bạn: " + mesMatch.getContent());
        else
            holder.tvMes.setText(mesMatch.getContent());

        mDatabase.child("users").child(mesMatch.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                holder.tvName.setText(name);
                if (dataSnapshot.child("image").exists()){
                    Glide.with(holder.circleImg).load(dataSnapshot.child("image").getValue()).into(holder.circleImg);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, InboxActivity.class);
                intent.putExtra("uid", mesMatch.getUid());
                intent.putExtra("chatID", mesMatch.getChatId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListMesMatch.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout container;
        CircleImageView circleImg;
        TextView tvName;
        TextView tvMes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            circleImg = itemView.findViewById(R.id.circleImg);
            tvName = itemView.findViewById(R.id.tvName);
            tvMes = itemView.findViewById(R.id.tvMes);
        }
    }
}
