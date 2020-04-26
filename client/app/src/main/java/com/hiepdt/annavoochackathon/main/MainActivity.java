package com.hiepdt.annavoochackathon.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.hiepdt.annavoochackathon.R;
import com.hiepdt.annavoochackathon.community.CommunityFragment;
import com.hiepdt.annavoochackathon.converse.ConverseFragment;
import com.hiepdt.annavoochackathon.setting.SettingFragment;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private CircleIndicator indicator;

    private ArrayList<Fragment> mListFragment;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        init();
        action();
    }

    private void init() {
        indicator = findViewById(R.id.circleIndi);
        mViewPager = findViewById(R.id.mViewPager);
        mListFragment = new ArrayList<>();
        mListFragment.add(new SettingFragment());
        mListFragment.add(new ConverseFragment());
        mListFragment.add(new CommunityFragment());

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), mListFragment);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(1);

        indicator.setViewPager(mViewPager);
        mAdapter.registerDataSetObserver(indicator.getDataSetObserver());
    }

    private void action() {

    }
}
