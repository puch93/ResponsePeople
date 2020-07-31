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
import kr.co.core.responsepeople.databinding.ActivityEvaluationAfterBinding;
import kr.co.core.responsepeople.databinding.ActivityEvaluationBeforeBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class EvaluationAfterAct extends BaseAct {
    ActivityEvaluationAfterBinding binding;
    Activity act;

    String avg_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_evaluation_after, null);
        act = this;

        setInfo();

        setLayout();
    }

    private void setLayout() {
        binding.btnComplete.setOnClickListener(v -> {
            doEvaluationComplete();
        });
    }

    private void doEvaluationComplete() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            startActivity(new Intent(act, MainAct.class));
                            act.finish();
                            if(LoginAct.act != null) {
                                LoginAct.act.finish();
                            }
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
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

        server.setTag("Evaluation Complete");
        server.addParams("dbControl", NetUrls.EVALUATION_COMPLETE);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("m_pref_mark", avg_text);
        server.execute(true, false);
    }

    private void setInfo() {
        try {
            JSONObject jo = new JSONObject(AppPreference.getProfilePref(act, AppPreference.PREF_JSON));
            avg_text = StringUtil.getStr(jo, "avg");
            int avg = StringUtil.getInt(jo, "avg");

            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.scoreAverage.setText(String.valueOf(avg));
                    binding.progressBar.setProgress(avg);
                    binding.tierText.setText(Common.calTier(avg));
                    Glide.with(act).load(Common.calTierImage(avg)).into(binding.tierIcon);
                    binding.scoreOver.setText(Common.calTierMessage(avg));
                }
            });



            JSONArray ja = jo.getJSONArray("data");
            JSONObject job = ja.getJSONObject(0);

            String m_profile1 = NetUrls.DOMAIN_ORIGIN + StringUtil.getStr(job, "m_profile1");
            String m_nick = StringUtil.getStr(job, "m_nick");
            String m_location = StringUtil.getStr(job, "m_location");
            String m_job = StringUtil.getStr(job, "m_job");
            String m_salary = StringUtil.getStr(job, "m_salary");

            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(act).load(m_profile1).into(binding.profileImg);
                    binding.nick.setText(m_nick);
                    binding.location.setText(m_location);
                    binding.job.setText(m_job);
                    binding.salary.setText(m_salary);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}