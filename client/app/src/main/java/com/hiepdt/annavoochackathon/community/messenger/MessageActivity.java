package com.hiepdt.annavoochackathon.community.messenger;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.hiepdt.annavoochackathon.models.MesMatch;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    private ImageView btnBack;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uID;

    private RecyclerView mRecycleView;
    private ArrayList<MesMatch> mListMesMatch;
    private MesMatchesAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().hide();
        init();
        action();

    }

    private void init() {
        btnBack = findViewById(R.id.btnBack);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();

        mRecycleView = findViewById(R.id.mRecycleView);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(MessageActivity.this, RecyclerView.VERTICAL, false);
        mRecycleView.setNestedScrollingEnabled(false);
        mRecycleView.setLayoutManager(layoutManager2);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mListMesMatch = new ArrayList<>();
//        mListMesMatch.add(new MesMatch());
//        mListMesMatch.add(new MesMatch());
        mAdapter = new MesMatchesAdapter(MessageActivity.this, mListMesMatch);
        mRecycleView.setAdapter(mAdapter);

    }

    private void action() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getData();

    }

    private void getData() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListMesMatch.clear();
                for (DataSnapshot snapshot : dataSnapshot.child("users").child(uID).child("connections").getChildren()) {
                    String uid = snapshot.getKey();
                    String chatId = (String) snapshot.child("chatId").getValue();
                    String createBy = "";
                    String content = "";
                    for (DataSnapshot snapshot2 : dataSnapshot.child("chats").child(chatId).getChildren()) {
                        content = (String) snapshot2.child("content").getValue();
                        createBy = (String) snapshot2.child("createBy").getValue();
                    }

                    mListMesMatch.add(new MesMatch(uid, chatId, content, createBy));
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
