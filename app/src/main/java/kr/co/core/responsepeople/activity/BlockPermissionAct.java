package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityBlockExplainBinding;
import kr.co.core.responsepeople.databinding.ActivityBlockPermissionBinding;

public class BlockPermissionAct extends AppCompatActivity {
    ActivityBlockPermissionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_block_permission, null);

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }
}
