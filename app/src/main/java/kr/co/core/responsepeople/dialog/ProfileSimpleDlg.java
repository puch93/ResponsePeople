package kr.co.core.responsepeople.dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.DialogProfileSimpleBinding;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class ProfileSimpleDlg extends AppCompatActivity {
    DialogProfileSimpleBinding binding;
    Activity act;

    public static final String TYPE_LOCATION = "location";
    public static final String TYPE_HEIGHT = "height";
    public static final String TYPE_BODY = "body";
    public static final String TYPE_EDU = "edu";
    public static final String TYPE_JOB = "job";
    public static final String TYPE_RELIGION = "religion";
    public static final String TYPE_DRINK = "drink";
    public static final String TYPE_SMOKE = "smoke";

    public static final String TYPE_EDU_PREFER = "edu_prefer";
    public static final String TYPE_BODY_PREFER = "body_prefer";
    public static final String TYPE_RELIGION_PREFER = "religion_prefer";
    public static final String TYPE_DRINK_PREFER = "drink_prefer";
    public static final String TYPE_SMOKE_PREFER = "smoke_prefer";

    private String type, selectedData;
    private String[] array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_profile_simple, null);
        act = this;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        type = getIntent().getStringExtra("type");
        selectedData = getIntent().getStringExtra("data");
        if (StringUtil.isNull(type)) {
            finish();
        } else {
            switch (type) {
                case TYPE_LOCATION:
                    array = getResources().getStringArray(R.array.array_location);
                    binding.title.setText("지역");
                    break;
                case TYPE_HEIGHT:
                    array = getResources().getStringArray(R.array.array_height);
                    binding.title.setText("키");
                    break;
                case TYPE_BODY:
                    array = getResources().getStringArray(R.array.array_body);
                    binding.title.setText("체형");
                    break;
                case TYPE_EDU:
                    array = getResources().getStringArray(R.array.array_edu);
                    binding.title.setText("학력");
                    break;
                case TYPE_JOB:
                    array = getResources().getStringArray(R.array.array_job);
                    binding.title.setText("직업");
                    break;
                case TYPE_RELIGION:
                    array = getResources().getStringArray(R.array.array_religion);
                    binding.title.setText("종교");
                    break;
                case TYPE_DRINK:
                    array = getResources().getStringArray(R.array.array_drink);
                    binding.title.setText("음주여부");
                    break;
                case TYPE_SMOKE:
                    array = getResources().getStringArray(R.array.array_smoke);
                    binding.title.setText("흡연여부");
                    break;

                case TYPE_EDU_PREFER:
                    array = getResources().getStringArray(R.array.array_edu_prefer);
                    binding.title.setText("학력");
                    break;
                case TYPE_BODY_PREFER:
                    array = getResources().getStringArray(R.array.array_body_prefer);
                    binding.title.setText("체형");
                    break;
                case TYPE_RELIGION_PREFER:
                    array = getResources().getStringArray(R.array.array_religion_prefer);
                    binding.title.setText("종교");
                    break;
                case TYPE_DRINK_PREFER:
                    array = getResources().getStringArray(R.array.array_drink_prefer);
                    binding.title.setText("음주여부");
                    break;
                case TYPE_SMOKE_PREFER:
                    array = getResources().getStringArray(R.array.array_smoke_prefer);
                    binding.title.setText("흡연여부");
                    break;
            }

            binding.picker.setMinValue(0);
            binding.picker.setMaxValue(array.length - 1);
            binding.picker.setDisplayedValues(array);
            binding.picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            binding.picker.setWrapSelectorWheel(false);

            if (!StringUtil.isNull(selectedData)) {
                for (int i = 0; i < array.length; i++) {
                    if (array[i].equalsIgnoreCase(selectedData)) {
                        binding.picker.setValue(i);
                        break;
                    }
                }
            }


            binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String selected = array[binding.picker.getValue()];

                    Intent intent = new Intent();
                    intent.putExtra("value", selected);
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
}
