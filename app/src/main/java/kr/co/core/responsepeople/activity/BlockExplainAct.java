package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityBlockExplainBinding;
import kr.co.core.responsepeople.fragment.BaseFrag;
import kr.co.core.responsepeople.fragment.BlockExplanation01Frag;

public class BlockExplainAct extends BaseAct {
    ActivityBlockExplainBinding binding;
    public static Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_block_explain, null);
        act = this;

        binding.btnClose.setOnClickListener(v -> {
            finish();
        });

        replaceFragment(new BlockExplanation01Frag());
    }

    public void replaceFragment(BaseFrag frag) {
        /* replace fragment */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!(frag instanceof BlockExplanation01Frag)) {
            transaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
        transaction.replace(R.id.replace_area, frag);
        transaction.commit();
    }
}
