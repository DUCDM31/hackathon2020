package com.hiepdt.annavoochackathon.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hiepdt.annavoochackathon.R;

public class LoginActivity extends AppCompatActivity {

    private EditText edPhone;
    private Button btnContinue;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        init();
        action();
    }

    private void init() {
        edPhone = findViewById(R.id.edPhone);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void action() {
        edPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edPhone.getText().length() != 0) {
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
            public void onClick(View view) {
                if (!validatePhoneNumber()) {
                    return;
                }
                String phoneNumber = edPhone.getText().toString().trim();
                if (phoneNumber.charAt(0) == '0') {
                    phoneNumber = phoneNumber.substring(1);
                }
                Intent intent = new Intent(LoginActivity.this, VerifyActivity.class);
                intent.putExtra("phone_number", "+84" + phoneNumber);
                startActivity(intent);
            }
        });
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = edPhone.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            edPhone.setError("Số diện thoại không hợp lệ.");
            return false;
        }

        return true;
    }
}
