package kr.co.core.responsepeople.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ItemProfileImageBinding;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class ImageFrag extends BaseFrag {
    private ItemProfileImageBinding binding;
    private AppCompatActivity act;
    private String image;
    private boolean isImageOk;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_profile_image, container, false);
        act = (AppCompatActivity) getActivity();

        Common.processProfileImageRec(act, binding.ivProfile, image, isImageOk, 5, 3);

        return binding.getRoot();
    }


    public void setData(String image, boolean isImageOk) {
        this.image = image;
        this.isImageOk = isImageOk;
    }
}