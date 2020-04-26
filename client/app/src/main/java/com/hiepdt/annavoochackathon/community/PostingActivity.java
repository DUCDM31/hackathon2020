package com.hiepdt.annavoochackathon.community;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.community.inbox.InboxActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostingActivity extends AppCompatActivity {
    private Button btnStart;
    private ImageView btnBack;

    private CircleImageView avatar;
    private TextView tvName, tvTimestamp, text, tag_view_1, tag_view_2, tag_view_3;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uID;


    private String uid;
    private String topic1, topic2, topic3;
    private String content, status;
    private long timestamp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);
        getSupportActionBar().hide();
        init();
        action();
    }

    private void init() {
        btnBack = findViewById(R.id.btnBack);
        btnStart = findViewById(R.id.btnStart);
        avatar = findViewById(R.id.avatar);
        tvName = findViewById(R.id.tvName);
        tvTimestamp = findViewById(R.id.timestamp);
        text = findViewById(R.id.text);
        tag_view_1 = findViewById(R.id.tag_view_1);
        tag_view_2 = findViewById(R.id.tag_view_2);
        tag_view_3 = findViewById(R.id.tag_view_3);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();

        uid = getIntent().getExtras().getString("uid", "");
        topic1 = getIntent().getExtras().getString("topic1", "");
        topic2 = getIntent().getExtras().getString("topic2", "");
        topic3 = getIntent().getExtras().getString("topic3", "");
        content = getIntent().getExtras().getString("content", "");
        timestamp = getIntent().getExtras().getLong("timestamp", 0);
        status = getIntent().getExtras().getString("status", "");

    }

    private void action() {
        mDatabase.child("users").child(uID).child("connections").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String>list = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String _uid = snapshot.getKey();
                    list.add(_uid);
                }
                if (isContain(list, uid)){
                    btnStart.setEnabled(false);
                    btnStart.setBackgroundResource(R.drawable.corner_button_unselected);
                    btnStart.setTextColor(Color.parseColor("#cccccc"));
                    btnStart.setText("ĐÃ KẾT NỐI");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String key = mDatabase.push().getKey();
                mDatabase.child("users").child(uID).child("connections").child(uid).child("chatId").setValue(key);
                mDatabase.child("users").child(uid).child("connections").child(uID).child("chatId").setValue(key);

                Intent intent = new Intent(PostingActivity.this, InboxActivity.class);
                intent.putExtra("uid", uid);
                intent.putExtra("chatId", key);
                startActivity(intent);
            }
        });

        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("image").exists()) {
                    String image = (String) dataSnapshot.child("image").getValue();
                    Glide.with(avatar).load(image).into(avatar);
                }

                if (status.equalsIgnoreCase("#ẩn danh")) {
                    tvName.setText("#ẩn danh");
                    btnStart.setEnabled(false);
                    btnStart.setBackgroundResource(R.drawable.corner_button_unselected);
                    btnStart.setTextColor(Color.parseColor("#cccccc"));
                    btnStart.setText("ẨN DANH");
                } else {
                    String name = (String) dataSnapshot.child("name").getValue();
                    tvName.setText(name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        int minute = (int) ((System.currentTimeMillis() - timestamp)/60000);
        if (minute == 0){
            tvTimestamp.setText("Vừa xong");
        }
        if (minute > 0 && minute < 60){
            tvTimestamp.setText(minute +" phút trước");
        } else if (minute >= 60 && minute < 60*24){
            tvTimestamp.setText(minute/60 +" giờ trước");
        } else if (minute >= 60*24){
            tvTimestamp.setText(convertTimestampToTime(timestamp, "dd-mm-yyyy"));

        }
        tag_view_1.setText(topic1);
        tag_view_2.setText(topic2);
        tag_view_3.setText(topic3);

        text.setText(content);

    }

    private String convertTimestampToTime(long timestamp, String format){
        Date date = new java.util.Date(timestamp);

        SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    //--------------//
    private boolean isContain(ArrayList<String> arr, String key) {
        for (String s : arr) {
            if (s.equals(key)) {
                return true;
            }
        }
        return false;
    }
}
