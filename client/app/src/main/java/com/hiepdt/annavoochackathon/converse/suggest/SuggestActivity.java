package com.hiepdt.annavoochackathon.converse.suggest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.models.Center;

import java.util.ArrayList;

public class SuggestActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_LOCATION = 1;
    private ImageView btnBack;
    private RecyclerView mRecycleView;
    private SuggestAdapter mAdapter;
    private ArrayList<Center> mListCenter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String uID;
    private GoogleMap map;
    private LocationManager locationManager;
    private String latitude, longitude;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        getSupportActionBar().hide();
        init();
        action();
    }

    private void init() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getCurrentUser().getUid();

        btnBack = findViewById(R.id.btnBack);
        mRecycleView = findViewById(R.id.mRecycleView);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        mRecycleView.setLayoutManager(layoutManager1);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());

        mListCenter = new ArrayList<>();

        mAdapter = new SuggestAdapter(this, mListCenter);
        mRecycleView.setAdapter(mAdapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void action() {
        getData();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getData() {
        mDatabase.child("center").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mListCenter.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String url = (String) snapshot.child("url").getValue();
                    String name = (String) snapshot.child("name").getValue();
                    String address = (String) snapshot.child("address").getValue();
                    String phone = (String) snapshot.child("phone").getValue();
                    String open = (String) snapshot.child("open").getValue();
                    double lat = (double) snapshot.child("lat").getValue();
                    double lon = (double) snapshot.child("lon").getValue();

                    Center center = new Center(url, name, address, phone, open, lat, lon);
                    mListCenter.add(center);
                    map.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lon))
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(center))));
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private Bitmap getMarkerBitmapFromView(Center center) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_suggest, null);
        ImageView markerImageView = customMarkerView.findViewById(R.id.image);
        TextView name = customMarkerView.findViewById(R.id.tvName);
        markerImageView.setImageResource(R.drawable.city);
        name.setText(center.getName());

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.addMarker(new MarkerOptions()
                .position(new LatLng(21.030710, 105.805469)));
        map.addCircle(new CircleOptions()
                .center(new LatLng(21.030710, 105.805469))
                .radius(8000)
                .strokeWidth(0f));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.030710, 105.805469), 11.0f));

    }

}
