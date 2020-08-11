package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.EtcData;
import kr.co.core.responsepeople.databinding.ActivityEvaluationAfterBinding;
import kr.co.core.responsepeople.databinding.ActivityTierBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class TierAct extends BaseAct {
    ActivityTierBinding binding;
    Activity act;

    String avg_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tier, null);
        act = this;

        getMyInfo();

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
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

                            String m_profile1 = NetUrls.DOMAIN_ORIGIN + StringUtil.getStr(job, "m_profile1");
                            String m_nick = StringUtil.getStr(job, "m_nick");
                            String m_location = StringUtil.getStr(job, "m_location");
                            String m_job = StringUtil.getStr(job, "m_job");
                            String m_salary = StringUtil.getStr(job, "m_salary");
                            String m_pref_mark = StringUtil.getStr(job, "m_pref_mark");
                            int m_pref_mark_int = StringUtil.getInt(job, "m_pref_mark");
                            boolean m_salary_result = StringUtil.getStr(job, "m_salary_result").equalsIgnoreCase("Y");

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(act).load(m_profile1).into(binding.profileImg);
                                    binding.nick.setText(m_nick);
                                    binding.location.setText(m_location);
                                    binding.job.setText(m_job);
                                    if (m_salary_result)
                                        binding.salary.setText(m_salary);
                                    else
                                        binding.salary.setText("연봉 검수중");


                                    binding.scoreAverage.setText(m_pref_mark);
                                    int progress = (int) ((Float.parseFloat(m_pref_mark) * 10));
                                    binding.progressBar.setProgress(progress);

                                    binding.tierText.setText(Common.calTier(Float.parseFloat(m_pref_mark)));
                                    Glide.with(act).load(Common.calTierImage(Float.parseFloat(m_pref_mark))).into(binding.tierIcon);
                                    binding.scoreOver.setText(Common.calTierMessage(Float.parseFloat(m_pref_mark)));
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
}