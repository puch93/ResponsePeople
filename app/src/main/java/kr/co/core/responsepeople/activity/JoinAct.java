package kr.co.core.responsepeople.activity;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import kr.co.core.responsepeople.fragment.BaseFrag;
import kr.co.core.responsepeople.fragment.Join01Frag;
import kr.co.core.responsepeople.fragment.Join02Frag;
import kr.co.core.responsepeople.fragment.Join03Frag;
import kr.co.core.responsepeople.fragment.Join04Frag;
import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityJoinBinding;

public class JoinAct extends BaseAct {
    ActivityJoinBinding binding;
    public static FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join, null);

        binding.indicator.createIndicators(5, 0);

        fragmentManager = getSupportFragmentManager();

        replaceFragment(new Join01Frag());

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void replaceFragment(BaseFrag frag) {
        if (frag instanceof Join01Frag) {
        } else if (frag instanceof Join02Frag) {
            binding.indicator.animatePageSelected(1);
        } else if (frag instanceof Join03Frag) {
            binding.indicator.animatePageSelected(2);
        } else if (frag instanceof Join04Frag) {
            binding.indicator.animatePageSelected(3);
        } else {
            binding.indicator.animatePageSelected(4);
        }

        /* replace fragment */
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!(frag instanceof Join01Frag)) {
            transaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
        transaction.replace(R.id.area_replace, frag);
        transaction.commit();
    }
}