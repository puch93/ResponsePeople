package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.FragmentJoin03Binding;
import kr.co.core.responsepeople.databinding.FragmentJoin05Binding;
import kr.co.core.responsepeople.util.StringUtil;

public class Join05Frag extends BaseFrag {
    FragmentJoin05Binding binding;
    Activity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_05, container, false);
        act = getActivity();

        binding.seekBarAge.setRange(20, 55);
        binding.seekBarAge.setProgress(20, 25);
        binding.seekBarAge.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                binding.ageStart.setText(String.valueOf((int)leftValue));
                binding.ageEnd.setText(String.valueOf((int)rightValue));
                if (rightValue == 55.0) {
                    binding.age55.setVisibility(View.VISIBLE);
                } else {
                    binding.age55.setVisibility(View.GONE);
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });


        binding.seekBarHeight.setRange(120, 200);
        binding.seekBarHeight.setProgress(120, 140);
        binding.seekBarHeight.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                binding.heightStart.setText(String.valueOf((int)leftValue));
                binding.heightEnd.setText(String.valueOf((int)rightValue));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        binding.seekBarDistance.setRange(1, 8);
        binding.seekBarDistance.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                Log.i(StringUtil.TAG, "onStopTrackingTouch: " + view.getLeft());
            }
        });

        return binding.getRoot();
    }
}
