package kr.co.core.responsepeople;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import kr.co.core.responsepeople.databinding.FragmentJoin01Binding;

public class Join01Frag extends BaseFrag {
    FragmentJoin01Binding binding;
    Activity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_01, container,false);
        act = getActivity();

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextProcess();
            }
        });
        return binding.getRoot();
    }

    private void nextProcess() {
        BaseFrag fragment = new Join02Frag();
        ((JoinAct) act).replaceFragment(fragment);
    }
}
