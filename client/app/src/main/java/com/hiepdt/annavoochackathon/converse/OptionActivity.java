package com.hiepdt.annavoochackathon.converse;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.models.Post;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

public class OptionActivity extends AppCompatActivity {

    private Button btnContinue;
    private LinearLayout root;

    private ImageView btnBack;
    private EditText edStatus;
    private TextView tag1, tag2, tag3, tag4, tag5, tag6;
    private TextView tag_view_1, tag_view_2, tag_view_3;
    private RelativeLayout btnSwitch;
    private SwitchCompat switchCompat;

    private String[] listTag;
    private boolean[] check;

    private ArrayList<String> listTagView;
    private int COUNT = 0;


    //Preview param
    private CircleImageView avatar;
    private TextView tvName, timestamp, text;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uID;


    private String status;
    private String content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        getSupportActionBar().hide();
        init();
        action();
    }

    private void init() {
        if (getIntent().getExtras().getString("content", "") != null){
            content = getIntent().getExtras().getString("content", "0");
        }
        btnContinue = findViewById(R.id.btnContinue);
        root = findViewById(R.id.root);

        btnBack = findViewById(R.id.btnBack);
        edStatus = findViewById(R.id.edStatus);
        edStatus.setText(content);

        tag1 = findViewById(R.id.tag1);
        tag2 = findViewById(R.id.tag2);
        tag3 = findViewById(R.id.tag3);
        tag4 = findViewById(R.id.tag4);
        tag5 = findViewById(R.id.tag5);
        tag6 = findViewById(R.id.tag6);
        tag_view_1 = findViewById(R.id.tag_view_1);
        tag_view_2 = findViewById(R.id.tag_view_2);
        tag_view_3 = findViewById(R.id.tag_view_3);
        btnSwitch = findViewById(R.id.btnSwitch);
        switchCompat = findViewById(R.id.switchCompat);

        listTag = new String[]{"COVID-19", "Sức khỏe", "Gia đình", "Tình yêu", "Học đường", "Khác"};
        check = new boolean[6];
        listTagView = new ArrayList<>();

        avatar = findViewById(R.id.avatar);
        tvName = findViewById(R.id.tvName);
        timestamp = findViewById(R.id.timestamp);
        text = findViewById(R.id.text);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();

    }

    private void action() {
        status = uID;
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        selectButton(tag1, 0);
        selectButton(tag2, 1);
        selectButton(tag3, 2);
        selectButton(tag4, 3);
        selectButton(tag5, 4);
        selectButton(tag6, 5);

        mDatabase.child("users").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = (String) dataSnapshot.child("name").getValue();
                tvName.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SweetAlertDialog alertDialog = new SweetAlertDialog(OptionActivity.this, SweetAlertDialog.NORMAL_TYPE);
                alertDialog
                        .setContentText("Bạn chắc chắn đăng cảm xúc này chứ?")
                        .setConfirmText("OK")
                        .setCancelText("HỦY")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                Post post = new Post(uID, listTagView, status, content, System.currentTimeMillis());
                                mDatabase.child("posts").push().setValue(post);

                                AlertDialog.Builder builder = new AlertDialog.Builder(OptionActivity.this);
                                View viewInflated = LayoutInflater.from(OptionActivity.this).inflate(R.layout.dialog_success, root, false);
                                builder.setView(viewInflated);
                                final AlertDialog dialog = builder.create();
                                Button button = viewInflated.findViewById(R.id.btnComple);
                                button.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                onBackPressed();
                                            }
                                        }, 800);
                                    }
                                });
                                dialog.show();
                                alertDialog.cancel();
                            }
                        })
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                alertDialog.cancel();
                            }
                        })
                        .showCancelButton(true)
                        .show();


            }
        });
        setAnonymous();

        edStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String _text = edStatus.getText().toString().trim();
                if (!_text.isEmpty()) {
                    content = _text.substring(0, 1).toUpperCase() + _text.substring(1);
                } else {
                    content = "";
                }
                text.setText(content);
                checkContinue();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void selectButton(final TextView tag, final int index) {
        tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check[index] && COUNT < 3) {
                    check[index] = true;
                    COUNT++;
                    tag.setBackgroundResource(R.drawable.button_select);
                    tag.setTextColor(Color.parseColor("#ffffff"));
                    listTagView.add(tag.getText().toString().trim());
                } else if (check[index]) {
                    check[index] = false;
                    COUNT--;
                    tag.setBackgroundResource(R.drawable.button_unselect);
                    tag.setTextColor(Color.parseColor("#808080"));
                    listTagView.remove(tag.getText().toString().trim());
                }
                updateTagView();
                checkContinue();
            }
        });
    }

    private void setAnonymous() {
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    status = "#ẩn danh";
                    tvName.setText(status);
                } else {
                    mDatabase.child("users").child(uID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = (String) dataSnapshot.child("name").getValue();
                            tvName.setText(name);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCompat.toggle();
            }
        });
    }

    private void updateTagView() {
        if (listTagView.size() == 1) {
            tag_view_1.setText(listTagView.get(0));
            tag_view_2.setText("");
            tag_view_3.setText("");
        } else if (listTagView.size() == 2) {
            tag_view_1.setText(listTagView.get(0));
            tag_view_2.setText(listTagView.get(1));
            tag_view_3.setText("");
        } else if (listTagView.size() == 3) {
            tag_view_1.setText(listTagView.get(0));
            tag_view_2.setText(listTagView.get(1));
            tag_view_3.setText(listTagView.get(2));
        } else {
            tag_view_1.setText("");
            tag_view_2.setText("");
            tag_view_3.setText("");
        }
    }

    private void checkContinue() {
        if (listTagView.size() == 3 && !edStatus.getText().toString().isEmpty()) {
            btnContinue.setEnabled(true);
            btnContinue.setBackgroundResource(R.drawable.corner_button_selected);
            btnContinue.setTextColor(Color.parseColor("#ffffff"));
        } else if (listTagView.size() == 0 || edStatus.getText().toString().isEmpty()) {
            btnContinue.setEnabled(false);
            btnContinue.setBackgroundResource(R.drawable.corner_button_unselected);
            btnContinue.setTextColor(Color.parseColor("#cccccc"));
        }
    }
}
