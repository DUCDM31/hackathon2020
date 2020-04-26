package com.hiepdt.annavoochackathon.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.main.MainActivity;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import es.dmoral.toasty.Toasty;

public class VerifyActivity extends AppCompatActivity {
    private static final String TAG = "VerifyActivity";
    private static String uID;
    int INTERVAL = 60;
    private ImageView btnBack;
    private PinEntryEditText edPin;
    private Button btnContinue;
    private TextView tvCountdown;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog dialog;
    private DatabaseReference mDatabase;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String PHONE_NUMBER = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        getSupportActionBar().hide();
        init();
        action();
    }

    private void init() {
        PHONE_NUMBER = getIntent().getExtras().getString("phone_number");

        btnBack = findViewById(R.id.btnBack);
        edPin = findViewById(R.id.edPin);
        btnContinue = findViewById(R.id.btnContinue);
        tvCountdown = findViewById(R.id.tvCountdown);
    }

    private void action() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvCountdown.setEnabled(false);
        countDown();
        tvCountdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvCountdown.isEnabled()) {
                    resendVerificationCode(PHONE_NUMBER, mResendToken);
                    dialog = new SweetAlertDialog(VerifyActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                    dialog.setTitleText("Đã gửi mã xác thực.")
                            .hideConfirmButton()
                            .show();
                    tvCountdown.setEnabled(false);
                    tvCountdown.setTextColor(Color.parseColor("#000000"));
                    INTERVAL = 60;
                    countDown();
                }
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPhoneNumberWithCode(mVerificationId, edPin.getText().toString());
            }
        });

        setupVerify();

        edPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edPin.getText().length() != 0) {
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

    }

    private void countDown() {

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvCountdown.setText("Gửi lại sau: " + checkDigit(INTERVAL) + " giây");
                INTERVAL--;
            }

            public void onFinish() {
                tvCountdown.setText("GỬI LẠI");
                tvCountdown.setTextColor(Color.parseColor("#ff0000"));
                tvCountdown.setEnabled(true);
            }

        }.start();
    }

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private void setupVerify() {
        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]
                pDialog = new SweetAlertDialog(VerifyActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Đang tải");
                pDialog.setCancelable(false);
                pDialog.show();
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
//                Toast.makeText(getApplication(), "Verify Failed!!", Toast.LENGTH_SHORT).show();
                // [START_EXCLUDE silent]
//                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    edPin.setError("Mã xác thực không đúng");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Lỗi không xác định",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

//                Toast.makeText(getApplication(), "Code sent!!", Toast.LENGTH_SHORT).show();
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };
        // [END phone_auth_callbacks]

        startPhoneNumberVerification(PHONE_NUMBER);
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            // [END verify_with_code]
            signInWithPhoneAuthCredential(credential);
        } catch (IllegalArgumentException e) {
            Toasty.error(VerifyActivity.this, "Mã xác thực không đúng.", Toasty.LENGTH_SHORT).show();
        }

    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            final FirebaseUser user = task.getResult().getUser();
                            uID = user.getUid();

                            mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Intent intent;
                                    if (dataSnapshot.child(uID).exists()) {
                                        intent = new Intent(VerifyActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    } else {
                                        intent = new Intent(VerifyActivity.this, InfoActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(getApplication(), "Signin Failed!!", Toast.LENGTH_SHORT).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                dialog = new SweetAlertDialog(VerifyActivity.this, SweetAlertDialog.ERROR_TYPE);
                                dialog.setTitleText("Xác thực thất bại.")
                                        .hideConfirmButton()
                                        .show();
                                // [END_EXCLUDE]
                            }
                        }
                    }
                });
    }
    // [END sign_in_with_phone]


    @Override
    protected void onStop() {
        super.onStop();
        if (pDialog != null)
            pDialog.dismiss();
        if (dialog != null)
            dialog.dismiss();
    }
}
