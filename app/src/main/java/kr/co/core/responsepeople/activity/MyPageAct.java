package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityMyPageBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class MyPageAct extends BaseAct implements View.OnClickListener {
    ActivityMyPageBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page, null);
        act = this;

        binding.nick.setText(AppPreference.getProfilePref(act, AppPreference.PREF_NICK));
        binding.age.setText(AppPreference.getProfilePref(act, AppPreference.PREF_AGE));

        binding.btnSetting.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.btnCharge.setOnClickListener(this);

        binding.menuLike.setOnClickListener(this);
        binding.menuPrefer.setOnClickListener(this);
        binding.menuTier.setOnClickListener(this);
        binding.menuCustomer.setOnClickListener(this);
        binding.menuBlock.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getMyInfo();
    }

    private void getMyInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);

                            String m_nick = StringUtil.getStr(job, "m_nick");
                            String m_age = StringUtil.calcAge(StringUtil.getStr(job, "m_birth").substring(0, 4));
                            String m_job = StringUtil.getStr(job, "m_job");
                            String m_location = StringUtil.getStr(job, "m_location");
                            String m_salary = StringUtil.getStr(job, "m_salary");
                            String m_profile1 = StringUtil.getStr(job, "m_profile1");
                            boolean m_profile1_result = StringUtil.getStr(job, "m_profile1_result").equalsIgnoreCase("Y");
                            boolean m_salary_result = StringUtil.getStr(job, "m_salary_result").equalsIgnoreCase("Y");

                            AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, NetUrls.DOMAIN_ORIGIN + m_profile1);

                            // 평점
                            String m_pref_mark = StringUtil.getStr(job, "m_pref_mark");


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Common.processProfileImageCircle(act, binding.profileImg, m_profile1, m_profile1_result, 2, 5);

                                    binding.nick.setText(m_nick);
                                    binding.age.setText(m_age);
                                    binding.jobTop.setText(m_job);
                                    binding.location.setText(m_location);
                                    if(m_salary_result) {
                                        binding.salary.setText(m_salary);
                                    } else {
                                        binding.salary.setText("연봉 검수중");
                                    }

                                    binding.score.setText(m_pref_mark);

                                    // 티어 텍스트
                                    binding.tier.setText(Common.calTier(Float.parseFloat(m_pref_mark)));
                                    // 티어 아이콘
                                    Glide.with(act).load(Common.calTierImage(Float.parseFloat(m_pref_mark))).into(binding.iconTier);
                                }
                            });


                        } else {
                            LogUtil.logI(StringUtil.getStr(jo, "message"));
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

        server.setTag("My Info");
        server.addParams("dbControl", NetUrls.MY_INFO);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_edit:
                startActivity(new Intent(act, EditAct.class));
                break;

            case R.id.btn_charge:
                startActivity(new Intent(act, PaymentAct.class));
            case R.id.btn_setting:
                break;
            case R.id.menu_tier:
                startActivity(new Intent(act, TierAct.class));
                break;

            case R.id.menu_prefer:
                startActivity(new Intent(act, PreferAct.class));
            case R.id.menu_like:
                startActivity(new Intent(act, LikeAct.class));
                break;
            case R.id.menu_customer:
            case R.id.menu_block:
                break;
        }
    }
}