package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityEnlargeBinding;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;


public class EnlargeAct extends BaseAct {
    ActivityEnlargeBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_enlarge, null);
        act = this;

        String imageUrl = getIntent().getStringExtra("imageUrl");
        if(StringUtil.isNull(imageUrl)) {
            Common.showToast(act, "이미지를 불러올 수 없습니다");
        } else {
            Glide.with(act)
                    .load(imageUrl)
                    .into(binding.photoView);
        }

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
