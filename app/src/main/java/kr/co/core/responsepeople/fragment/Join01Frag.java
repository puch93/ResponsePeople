package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.JoinAct;
import kr.co.core.responsepeople.databinding.FragmentJoin01Binding;
import kr.co.core.responsepeople.util.Common;

public class Join01Frag extends BaseFrag implements CheckBox.OnCheckedChangeListener, View.OnClickListener {
    FragmentJoin01Binding binding;
    Activity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_01, container,false);
        act = getActivity();
        binding.ckGps.setOnCheckedChangeListener(this);
        binding.ckPrivate.setOnCheckedChangeListener(this);
        binding.ckUse.setOnCheckedChangeListener(this);

        binding.btnTermUse.setOnClickListener(this);
        binding.btnTermGps.setOnClickListener(this);
        binding.btnTermPrivate.setOnClickListener(this);

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.ckUse.isChecked()) {
                    Common.showToast(act, "이용 약관에 동의해주세요");
                } else if(!binding.ckPrivate.isChecked()) {
                    Common.showToast(act, "개인정보 처리방침에 동의해주세요");
                } else if(!binding.ckGps.isChecked()) {
                    Common.showToast(act, "위치 정보 제공에 동의해주세요");
                } else {
                    nextProcess();
                }
            }
        });

        binding.btnAgreeAll.setOnClickListener(v -> {
            if(binding.btnAgreeAll.isSelected()) {
                binding.btnAgreeAll.setSelected(false);
                binding.ckPrivate.setChecked(false);
                binding.ckUse.setChecked(false);
                binding.ckGps.setChecked(false);
            } else {
                binding.btnAgreeAll.setSelected(true);
                binding.ckPrivate.setChecked(true);
                binding.ckUse.setChecked(true);
                binding.ckGps.setChecked(true);
            }
        });
        return binding.getRoot();
    }

    private void nextProcess() {
        BaseFrag fragment = new Join02Frag();
        ((JoinAct) act).replaceFragment(fragment);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(binding.ckUse.isChecked() && binding.ckPrivate.isChecked() && binding.ckGps.isChecked()) {
            binding.btnAgreeAll.setSelected(true);
            binding.btnNext.setSelected(true);
        } else {
            binding.btnAgreeAll.setSelected(false);
            binding.btnNext.setSelected(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_term_use:
            case R.id.btn_term_private:
            case R.id.btn_term_gps:
                break;
        }
    }
}
