package kr.co.core.responsepeople.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import kr.co.core.responsepeople.data.ImageData;
import kr.co.core.responsepeople.fragment.ImageFrag;


public class ImagePagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<ImageData> list = new ArrayList<>();

    public ImagePagerAdapter(FragmentManager fm, ArrayList<ImageData> list) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.list = list;
    }

    @Override
    public Fragment getItem(int i) {
        ImageFrag currentFragment = new ImageFrag();
        currentFragment.setData(list.get(i));
        return currentFragment;
    }

    public void setList(ArrayList<ImageData> list) {
            this.list = list;
            notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}