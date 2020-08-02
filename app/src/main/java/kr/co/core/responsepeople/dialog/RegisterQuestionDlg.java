package kr.co.core.responsepeople.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.BaseAct;
import kr.co.core.responsepeople.databinding.DialogRegisterQuestion01Binding;

public class RegisterQuestionDlg extends BaseAct {
    DialogRegisterQuestion01Binding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_register_question_01, null);

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
