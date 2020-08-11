package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.BlockExplainAct;
import kr.co.core.responsepeople.activity.TermAct;
import kr.co.core.responsepeople.databinding.FragmentBlockExplanation01Binding;
import kr.co.core.responsepeople.databinding.FragmentBlockExplanation02Binding;
import kr.co.core.responsepeople.util.Common;

public class BlockExplanation02Frag extends BaseFrag {
    FragmentBlockExplanation02Binding binding;
    Activity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_block_explanation_02, container, false);
        act = getActivity();

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.ckConfirm.isChecked()) {
                    ((BlockExplainAct) act).replaceFragment(new BlockExplanation03Frag());
                } else {
                    Common.showToast(act, "위의 내용을 확인해주신 후, 확인표시에 체크 부탁드립니다");
                }
            }
        });

        binding.btnTermPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, TermAct.class).putExtra("type", TermAct.TYPE_PRIVATE));
            }
        });

        return binding.getRoot();
    }
}
