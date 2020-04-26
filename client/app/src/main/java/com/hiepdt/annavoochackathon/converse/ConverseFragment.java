package com.hiepdt.annavoochackathon.converse;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hiepdt.annavoochackathon.R;

public class ConverseFragment extends Fragment {
    private LinearLayout btnContinue, tvGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_converse, container, false);

        init(v);
        action();
        return v;
    }

    private void init(View v) {
        btnContinue = v.findViewById(R.id.btnContinue);
        tvGroup = v.findViewById(R.id.tvGroup);
    }

    private void action() {
        tvGroup.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.from_left));
        btnContinue.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.frombottom));
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SupportActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            }
        });

    }
}
