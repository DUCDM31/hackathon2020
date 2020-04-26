package com.hiepdt.annavoochackathon.setting;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.login.SplashActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;


public class SettingFragment extends Fragment {

    public static final int PICK_IMAGE = 1;
    private SwitchCompat swLocation, swCam, swMicro;
    private TextView tvName, tvBirthday, tvPhone, tvId;
    private CircleImageView imgAvatar;
    private Button btnLogout;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uID;
//    private LinearLayout btnAvatar;
    private Uri filePath;
    private StorageReference mStorage;
    private SweetAlertDialog pDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        init(v);
        action();
        return v;
    }

    private void init(View v) {
        tvName = v.findViewById(R.id.tvName);
        tvBirthday = v.findViewById(R.id.tvBirthday);
        tvPhone = v.findViewById(R.id.tvPhone);
        tvId = v.findViewById(R.id.tvId);
        imgAvatar = v.findViewById(R.id.imgAvatar);

        swLocation = v.findViewById(R.id.swLocation);
        swMicro = v.findViewById(R.id.swMicro);
        swCam = v.findViewById(R.id.swCam);

        btnLogout = v.findViewById(R.id.btnLogout);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();
//        btnAvatar = v.findViewById(R.id.btnAvatar);
        mStorage = FirebaseStorage.getInstance().getReference();

    }

    private void action() {
        mDatabase.child("users").child(uID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("image").exists()) {
                        Glide.with(imgAvatar).load(dataSnapshot.child("image").getValue()).into(imgAvatar);
                    }
                    String name = (String) dataSnapshot.child("name").getValue();
                    tvName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        tvPhone.setText(mAuth.getCurrentUser().getPhoneNumber());
        tvId.setText(uID);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() != null) {
                    final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
                    dialog.setContentText("Bạn chắc chắn muốn đăng xuất chứ?")
                            .setConfirmText("Đồng ý")
                            .setCancelText("Hủy")
                            .showCancelButton(true)
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    mAuth.signOut();
                                    dialog.cancel();
                                    Intent intent = new Intent(getActivity(), SplashActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            })
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            }
        });

//        btnAvatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select avatar"), PICK_IMAGE);
//            }
//        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivity(intent);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toasty.error(getContext(), "Bạn cần chấp nhận quyền truy cập", Toasty.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE & resultCode == RESULT_OK) {
            filePath = data.getData();
            final SweetAlertDialog alertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.NORMAL_TYPE);
            alertDialog
                    .setContentText("Bạn chắc chắn tải ảnh này chứ?")
                    .setConfirmText("Đồng ý")
                    .setCancelText("Hủy")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            uploadFile();
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
    }

    private void uploadFile() {
        if (filePath != null) {

            pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Đang tải");
            pDialog.setCancelable(false);
            pDialog.show();

            final StorageReference ref = mStorage.child(uID);

            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pDialog.cancel();
                            final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                            dialog.setTitleText("Tải lên")
                                    .setContentText("Tải lên thành công")
                                    .hideConfirmButton()
                                    .show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(final Uri uri) {
                                            mDatabase.child("users").child(uID).child("image").setValue(uri.toString());
                                        }
                                    });
                                }
                            }, 1500);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pDialog.cancel();
                            final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                            dialog.setTitleText("Thất bại")
                                    .setContentText("Tải ảnh thất bại")
                                    .hideConfirmButton()
                                    .show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1500);
                        }
                    });
        }
    }

}
