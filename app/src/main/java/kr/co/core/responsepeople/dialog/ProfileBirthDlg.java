package kr.co.core.responsepeople.dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.BaseAct;
import kr.co.core.responsepeople.databinding.DialogProfileBirthBinding;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class ProfileBirthDlg extends BaseAct {
    DialogProfileBirthBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_profile_birth, null);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        String receivedData = getIntent().getStringExtra("birth");
        if (!StringUtil.isNull(receivedData)) {
            int year = Integer.parseInt(receivedData.substring(0, 4));
            int month = Integer.parseInt(receivedData.substring(4, 6)) -1;
            int day = Integer.parseInt(receivedData.substring(6));
            binding.picker.init(year, month, day, null);
        } else {
            binding.picker.init(1970, 0, 1, null);
        }

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = binding.picker.getYear();
                int month = binding.picker.getMonth() + 1;
                int day = binding.picker.getDayOfMonth();

                String birth = String.format("%4d%02d%02d", year, month, day);

                Intent intent = new Intent();
                intent.putExtra("birth", birth);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
