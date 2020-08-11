package kr.co.core.responsepeople.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.DialogGalleryBinding;
import kr.co.core.responsepeople.databinding.DialogProfileSimpleBinding;
import kr.co.core.responsepeople.util.StringUtil;

public class GalleryDlg extends AppCompatActivity {
    DialogGalleryBinding binding;
    Activity act;

    public static final String CAMERA = "camera";
    public static final String ALBUM = "gallery";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_gallery, null);
        act = this;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        binding.btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("result", ALBUM);
            setResult(RESULT_OK, intent);
            finish();
        });

        binding.btnCamera.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("result", CAMERA);
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}