package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.QuestionAlarmPagerAdapter;
import kr.co.core.responsepeople.databinding.ActivityQuestionAlarmBinding;

public class QuestionAlarmAct extends BaseAct {
    ActivityQuestionAlarmBinding binding;
    Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question_alarm, null);
        act = this;


        binding.viewPager.setAdapter(new QuestionAlarmPagerAdapter(getSupportFragmentManager()));

        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.tabLayout.getTabAt(0).select();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}