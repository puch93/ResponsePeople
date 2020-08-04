package kr.co.core.responsepeople.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import kr.co.core.responsepeople.fragment.AlarmReceiveFrag;
import kr.co.core.responsepeople.fragment.AlarmResponseFrag;
import kr.co.core.responsepeople.fragment.BaseFrag;
import kr.co.core.responsepeople.fragment.ImageFrag;


public class QuestionAlarmPagerAdapter extends FragmentStatePagerAdapter {
    public QuestionAlarmPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int i) {
        BaseFrag frag;
        if(i == 0) {
            frag = new AlarmReceiveFrag();
        } else {
            frag = new AlarmResponseFrag();
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}