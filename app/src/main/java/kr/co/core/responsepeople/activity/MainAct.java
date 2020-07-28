package kr.co.core.responsepeople.activity;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityMainBinding;
import kr.co.core.responsepeople.fragment.BaseFrag;
import kr.co.core.responsepeople.fragment.OnlineFrag;
import kr.co.core.responsepeople.util.BackPressCloseHandler;

public class MainAct extends BaseAct implements View.OnClickListener {
    ActivityMainBinding binding;
    Activity act;

    FragmentManager fragmentManager;
    private BackPressCloseHandler backPressCloseHandler;
    private int currentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main, null);
        act = this;

        backPressCloseHandler = new BackPressCloseHandler(this);
        fragmentManager = getSupportFragmentManager();
        binding.menu01Area.setOnClickListener(this);
        binding.menu02Area.setOnClickListener(this);
        binding.menu03Area.setOnClickListener(this);
        binding.menu04Area.setOnClickListener(this);
        binding.menu05Area.setOnClickListener(this);
    }

    private void switchLayout(View view) {
        binding.menu01Area.setSelected(false);
        binding.menu02Area.setSelected(false);
        binding.menu03Area.setSelected(false);
        binding.menu04Area.setSelected(false);
        binding.menu05Area.setSelected(false);

        view.setSelected(true);
    }

    public void replaceFragment(BaseFrag frag, String tag) {
        /* replace fragment */
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.replace_area, frag, tag);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu01_area:
            case R.id.menu02_area:
            case R.id.menu03_area:
            case R.id.menu04_area:
            case R.id.menu05_area:
                switchLayout(view);
                replaceFragment(new OnlineFrag(), "online");
                break;

        }
    }
}
