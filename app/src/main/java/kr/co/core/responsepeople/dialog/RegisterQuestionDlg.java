package kr.co.core.responsepeople.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.BaseAct;
import kr.co.core.responsepeople.databinding.DialogProfileBirthBinding;
import kr.co.core.responsepeople.databinding.DialogRegisterQuestionBinding;
import kr.co.core.responsepeople.util.StringUtil;

public class RegisterQuestionDlg extends BaseAct {
    DialogRegisterQuestionBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_register_question, null);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        binding.btnClose.setOnClickListener(v -> {
            finish();
        });

        binding.btnAddSheet.setOnClickListener(v -> {

        });

        binding.btnComplete.setOnClickListener(v -> {

        });
    }
}
