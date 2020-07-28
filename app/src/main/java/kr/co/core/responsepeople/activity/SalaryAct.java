package kr.co.core.responsepeople.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivitySalaryBinding;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class SalaryAct extends BaseAct {
    ActivitySalaryBinding binding;
    Activity act;

    private static final int UPLOAD = 101;
    private File sendFile;
    private boolean isAttached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_salary, null);
        act = this;

        binding.rgSalary.setTag("비공개");
        binding.rgSalary.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                binding.rgSalary.setTag(((RadioButton) findViewById(checkedId)).getText().toString());
                if(checkedId == R.id.rb_private) {
                    sendFile = null;
                    isAttached = false;
                    binding.btnUploadFileText.setText("급여명세서 이미지 첨부");
                }
            }
        });
        binding.btnUploadFile.setOnClickListener(v -> {
            if(!((String)binding.rgSalary.getTag()).equalsIgnoreCase("비공개")) {
                if (isAttached) {
                    Common.showToast(act, "이미 첨부되었습니다");
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(intent, UPLOAD);
                }
            } else {
                Common.showToast(act, "비공개 일때는 첨부하실 수 없습니다");
            }
        });
        binding.btnComplete.setOnClickListener(v -> {
            String selectedData = (String) binding.rgSalary.getTag();
            if (!selectedData.equalsIgnoreCase("비공개")) {
                if (isAttached) {
                    setResult(RESULT_OK, new Intent()
                            .putExtra("salary", selectedData)
                            .putExtra("file", sendFile)
                    );
                } else {
                    Common.showToast(act, "급여명세서를 첨부해주세요");
                }
            } else {
                setResult(RESULT_OK, new Intent().putExtra("salary", "비공개"));
                finish();
            }
        });
        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == UPLOAD) {
                sendFile = new File(StringUtil.getPath(this, data.getData()));
                binding.btnUploadFileText.setText("첨부 완료");
                isAttached = true;
            }
        }
    }
}
