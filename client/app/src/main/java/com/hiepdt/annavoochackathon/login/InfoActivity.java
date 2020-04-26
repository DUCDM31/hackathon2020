package com.hiepdt.annavoochackathon.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hiepdt.annavoochackathon.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class InfoActivity extends AppCompatActivity {
    private ImageView btnBack;
    private EditText edName;
    private Button btnContinue;
    private CheckBox btnTerm;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().hide();
        init();
        action();
    }

    private void init() {
        btnBack = findViewById(R.id.btnBack);
        edName = findViewById(R.id.edName);
        btnContinue = findViewById(R.id.btnContinue);
        btnTerm = findViewById(R.id.btnTerm);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
    }

    private void action() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        edName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edName.getText().length() != 0) {
                    btnContinue.setBackgroundResource(R.drawable.corner_button_selected);
                    btnContinue.setTextColor(Color.parseColor("#ffffff"));
                    btnContinue.setEnabled(true);
                } else {
                    btnContinue.setBackgroundResource(R.drawable.corner_button_unselected);
                    btnContinue.setTextColor(Color.parseColor("#cccccc"));
                    btnContinue.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!btnTerm.isChecked()) {
                    SweetAlertDialog dialog = new SweetAlertDialog(InfoActivity.this, SweetAlertDialog.WARNING_TYPE);
                    dialog.setTitleText("Vui lòng đồng ý với điều khoản.")
                            .hideConfirmButton()
                            .show();
                    return;
                }
                final SweetAlertDialog alertDialog = new SweetAlertDialog(InfoActivity.this, SweetAlertDialog.NORMAL_TYPE);
                alertDialog
                        .setContentText("Bạn chắc chắn sử dụng tên này chứ?")
                        .setConfirmText("OK")
                        .setCancelText("HỦY")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                String name = edName.getText().toString().trim();
                                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                                mDatabase.child("users").child(uID).child("name").setValue(name);
                                Intent intent = new Intent(InfoActivity.this, LoginSuccessActivity.class);
                                startActivity(intent);
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
    }
}
