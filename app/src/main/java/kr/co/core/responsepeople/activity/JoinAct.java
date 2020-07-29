package kr.co.core.responsepeople.activity;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import kr.co.core.responsepeople.data.JoinData;
import kr.co.core.responsepeople.fragment.BaseFrag;
import kr.co.core.responsepeople.fragment.Join01Frag;
import kr.co.core.responsepeople.fragment.Join02Frag;
import kr.co.core.responsepeople.fragment.Join03Frag;
import kr.co.core.responsepeople.fragment.Join04Frag;
import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityJoinBinding;
import kr.co.core.responsepeople.fragment.Join05Frag;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class JoinAct extends BaseAct {
    ActivityJoinBinding binding;
    Activity act;

    public static FragmentManager fragmentManager;

    public static JoinData joinData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join, null);
        act = this;

        binding.indicator.createIndicators(5, 0);

        fragmentManager = getSupportFragmentManager();

        joinData = new JoinData();

        boolean fromLogin = getIntent().getBooleanExtra("fromLogin", false);
        if (fromLogin) {
            Join04Frag join04Frag = new Join04Frag();
            join04Frag.setFromLogin(true);
            replaceFragment(new Join04Frag());
        } else {
            replaceFragment(new Join05Frag());
        }

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void replaceFragment(BaseFrag frag) {
        if (frag instanceof Join01Frag) {
            binding.title.setText("이용 약관 동의");
        } else if (frag instanceof Join02Frag) {
            binding.title.setText("계정 생성");
            binding.indicator.animatePageSelected(1);
        } else if (frag instanceof Join03Frag) {
            binding.title.setText("본인 인증");
            binding.indicator.animatePageSelected(2);
        } else if (frag instanceof Join04Frag) {
            binding.title.setText("프로필 작성");
            binding.indicator.animatePageSelected(3);
        } else {
            binding.title.setText("선호 설정");
            binding.indicator.animatePageSelected(4);
        }

        /* replace fragment */
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (!(frag instanceof Join01Frag)) {
            transaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
        transaction.replace(R.id.area_replace, frag);
        transaction.commit();
    }
}