package com.hiepdt.annavoochackathon.community.inbox;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.models.Messenger;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxActivity extends AppCompatActivity {

    private ImageView btnBack;

    private String uid;
    private String chatId;

    private CircleImageView imgAvatar, avatar;
    private TextView tvName, tvTimestamp, tvName2;
    private RecyclerView recyclerChat;
    private ArrayList<Messenger>mListMes;
    private InboxAdapter mAdapter;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        getSupportActionBar().hide();
        init();
        action();
    }

    private void init() {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();

        btnBack = findViewById(R.id.btnBack);
        uid = getIntent().getExtras().getString("uid", "");
        chatId = getIntent().getExtras().getString("chatId", "");

        imgAvatar = findViewById(R.id.imgAvatar);
        avatar = findViewById(R.id.avatar);
        tvName = findViewById(R.id.tvName);
        tvName2 = findViewById(R.id.tvName2);
        tvTimestamp = findViewById(R.id.tvTimestamp);

        mListMes = new ArrayList<>();
        recyclerChat = findViewById(R.id.recyclerChat);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerChat.setLayoutManager(layoutManager1);
        recyclerChat.setNestedScrollingEnabled(false);
        recyclerChat.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new InboxAdapter(this, mListMes);
        recyclerChat.setAdapter(mAdapter);
    }

    private void action() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                tvName.setText(name);
                if (dataSnapshot.child("image").exists()){
                    Glide.with(imgAvatar).load(dataSnapshot.child("image").getValue()).into(imgAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
