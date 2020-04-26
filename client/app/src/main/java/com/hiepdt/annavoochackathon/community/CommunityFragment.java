package com.hiepdt.annavoochackathon.community;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.community.messenger.MessageActivity;
import com.hiepdt.annavoochackathon.models.Post;

import java.util.ArrayList;

public class CommunityFragment extends Fragment {


    private RecyclerView mRecycleView;
    private ArrayList<Post> mListPost;
    private PostAdapter mAdapter;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uID;

    private TextView tvNotify;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_community, container, false);
        init(v);
        action();
        return v;
    }

    private void init(View v) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();

        tvNotify = v.findViewById(R.id.tvNotify);
        mRecycleView = v.findViewById(R.id.mRecycleView);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        mRecycleView.setLayoutManager(layoutManager1);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());

        mListPost = new ArrayList<>();
        mAdapter = new PostAdapter(getContext(), mListPost);
        mRecycleView.setAdapter(mAdapter);


        getData();
    }

    private void action() {
        tvNotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getData() {
        mDatabase.child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListPost.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String uid = (String) snapshot.child("uid").getValue();
                        if (uid.equalsIgnoreCase(uID)){
                            return;
                        }
                        String content = (String) snapshot.child("content").getValue();
                        String status = "";
                        if (snapshot.child("status").exists()) {
                            status = (String) snapshot.child("status").getValue();
                        }
                        ArrayList<String> topics = new ArrayList<>();
                        String topic1 = (String) snapshot.child("topics").child("0").getValue();
                        String topic2 = (String) snapshot.child("topics").child("1").getValue();
                        String topic3 = (String) snapshot.child("topics").child("2").getValue();
                        topics.add(topic1);
                        topics.add(topic2);
                        topics.add(topic3);

                        long timestamp = (long) snapshot.child("timestamp").getValue();

                        mListPost.add(new Post(uid, topics, status, content, timestamp));
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
