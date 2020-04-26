package com.hiepdt.annavoochackathon.intro;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.login.LoginActivity;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.relex.circleindicator.CircleIndicator;

public class IntroduceActivity extends AppCompatActivity {
    ArrayList<Fragment> mListFragment;

    IntroduceAdapter introduceAdapter;

    private ViewPager mViewpager;
    private CircleIndicator circleIndicator;
    private TextView tvSkip;
    private SweetAlertDialog pDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduce);
        getSupportActionBar().hide();
        init();
        action();
    }

    private void init() {
        mViewpager = findViewById(R.id.mViewPager);
        circleIndicator = findViewById(R.id.circleIndi);
        tvSkip = findViewById(R.id.tvSkip);
        mListFragment = new ArrayList<>();
        mListFragment.add(new IntroFragment1());
        mListFragment.add(new IntroFragment2());
        mListFragment.add(new IntroFragment3());

        introduceAdapter = new IntroduceAdapter(getSupportFragmentManager(), mListFragment);
        mViewpager.setAdapter(introduceAdapter);
        circleIndicator.setViewPager(mViewpager);
        introduceAdapter.registerDataSetObserver(circleIndicator.getDataSetObserver());

    }

    private void action() {
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == mListFragment.size() - 1) {
                    tvSkip.setBackgroundResource(R.drawable.corner_button_selected);
                    tvSkip.setTextColor(Color.parseColor("#ffffff"));
                    tvSkip.setText("Bắt đầu");

                } else {
                    tvSkip.setBackgroundColor(Color.parseColor("#ffffff"));
                    tvSkip.setTextColor(getResources().getColor(R.color.start_color));
                    tvSkip.setText("Bỏ qua");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new SweetAlertDialog(IntroduceActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Vui lòng chờ một chút ...");
                pDialog.setCancelable(false);
                pDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(IntroduceActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        pDialog.dismiss();
                    }
                }, 1500);

            }
        });
    }
}
