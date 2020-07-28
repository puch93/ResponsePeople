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
        replaceFragment(new Join01Frag());

        binding.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void doJoin() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {

                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Join");
        server.addParams("dbControl", NetUrls.JOIN);
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
        server.addParams("imei", Common.getDeviceId(act));

        server.addParams("m_id", joinData.getId());
        server.addParams("m_pass", joinData.getPw());
        server.addParams("m_nick", joinData.getNick());
        server.addParams("m_gender", joinData.getGender());
        server.addParams("m_hp", joinData.getHp());
        server.addParams("m_birth", joinData.getBirth());
        server.addParams("m_location", joinData.getLocation());
        server.addParams("m_height", joinData.getHeight());
        server.addParams("m_edu", joinData.getEdu());
        server.addParams("m_body", joinData.getBody());
        server.addParams("m_job", joinData.getJob());
        server.addParams("m_religion", joinData.getReligion());
        server.addParams("m_drink", joinData.getDrink());
        server.addParams("m_smoke", joinData.getSmoke());
        server.addParams("m_salary", joinData.getSalary());
//        server.addParams("m_charm", joinData.getCharm());
//        server.addParams("m_ideal", joinData.getIdeal());
//        server.addParams("m_interest", joinData.getInterest());

        server.addParams("m_charm", "재주가 많아요");
        server.addParams("m_ideal", "피부가 좋은 사람");
        server.addParams("m_interest", "피트니스");

        // 파일
        for (int i = 1; i < joinData.getImages().size(); i++) {
            String filePath = joinData.getImages().get(i);
            File file = new File(filePath);
            server.addFileParams("m_profile" + i, file);
        }
        if (null != joinData.getSalary_file()) {
            server.addFileParams("m_salary_file", joinData.getSalary_file());
        }
        server.execute(true, false);
    }

    public void replaceFragment(BaseFrag frag) {
        if (frag instanceof Join01Frag) {
        } else if (frag instanceof Join02Frag) {
            binding.indicator.animatePageSelected(1);
        } else if (frag instanceof Join03Frag) {
            binding.indicator.animatePageSelected(2);
        } else if (frag instanceof Join04Frag) {
            binding.indicator.animatePageSelected(3);
        } else {
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