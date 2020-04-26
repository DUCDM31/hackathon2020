package com.hiepdt.annavoochackathon.community;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.models.Post;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context mContext;
    ArrayList<Post> mListPost;
    private DatabaseReference mDatabase;

    public PostAdapter(Context mContext, ArrayList<Post> mListPost) {
        this.mContext = mContext;
        this.mListPost = mListPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post post = mListPost.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostingActivity.class);
                intent.putExtra("uid", post.getUid());
                intent.putExtra("topic1", post.getTopics().get(0));
                intent.putExtra("topic2", post.getTopics().get(1));
                intent.putExtra("topic3", post.getTopics().get(2));

                intent.putExtra("status", post.getStatus());
                intent.putExtra("content", post.getContent());
                intent.putExtra("timestamp", post.getTimestamp());

                mContext.startActivity(intent);
            }
        });

        if (post.getStatus().equalsIgnoreCase("#ẩn danh")){
            holder.name.setText("#ẩn danh");
        } else {
            mDatabase.child("users").child(post.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = (String) dataSnapshot.child("name").getValue();
                    holder.name.setText(name);
                    if (dataSnapshot.child("image").exists()) {
                        String image = (String) dataSnapshot.child("image").getValue();
                        Glide.with(holder.avatar).load(image).into(holder.avatar);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        int minute = (int) ((System.currentTimeMillis() - post.getTimestamp())/60000);
        if (minute < 60){
            holder.timestamp.setText(minute +" phút trước");
        } else if (minute >= 60 && minute < 60*24){
            holder.timestamp.setText(minute/60 +" giờ trước");
        } else if (minute >= 60*24){
            holder.timestamp.setText(convertTimestampToTime(post.getTimestamp(), "dd-mm-yyyy"));

        }
        holder.text.setText(post.getContent());
        holder.tag_view_1.setText(post.getTopics().get(0));
        holder.tag_view_2.setText(post.getTopics().get(1));
        holder.tag_view_3.setText(post.getTopics().get(2));

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PostingActivity.class);
                intent.putExtra("uid", post.getUid());
                intent.putExtra("topic1", post.getTopics().get(0));
                intent.putExtra("topic2", post.getTopics().get(1));
                intent.putExtra("topic3", post.getTopics().get(2));

                intent.putExtra("status", post.getStatus());
                intent.putExtra("content", post.getContent());
                intent.putExtra("timestamp", post.getTimestamp());

                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mListPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView avatar;
        private TextView name, timestamp, text, tag_view_1, tag_view_2, tag_view_3, more;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvName);
            avatar = itemView.findViewById(R.id.avatar);
            timestamp = itemView.findViewById(R.id.timestamp);
            text = itemView.findViewById(R.id.text);
            tag_view_1 = itemView.findViewById(R.id.tag_view_1);
            tag_view_2 = itemView.findViewById(R.id.tag_view_2);
            tag_view_3 = itemView.findViewById(R.id.tag_view_3);
            more = itemView.findViewById(R.id.more);
        }
    }
    private String convertTimestampToTime(long timestamp, String format){
        Date date = new java.util.Date(timestamp);

        SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
        String formattedDate = sdf.format(date);

        return formattedDate;
    }
}
