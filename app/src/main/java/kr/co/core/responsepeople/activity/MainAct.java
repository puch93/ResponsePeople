package kr.co.core.responsepeople.activity;

import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityMainBinding;

public class MainAct extends BaseAct {
    ActivityMainBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main, null);
        act = this;
    }
}
