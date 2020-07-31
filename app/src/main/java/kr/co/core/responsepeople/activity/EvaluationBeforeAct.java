package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.data.EtcData;
import kr.co.core.responsepeople.databinding.ActivityEvaluationBeforeBinding;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class EvaluationBeforeAct extends BaseAct {
    ActivityEvaluationBeforeBinding binding;
    Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_evaluation_before, null);
        act = this;

        setLayout();
    }

    private void setLayout() {
        try {
            JSONObject jo = new JSONObject(AppPreference.getProfilePref(act, AppPreference.PREF_JSON));
            int avg = StringUtil.getInt(jo, "avg");
            int cnt = StringUtil.getInt(jo, "cnt");

            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.scoreAverage.setText(String.valueOf(avg));
                    binding.progressBar.setProgress(avg);
                    if(cnt > 5) {
                        binding.reviewCount.setText("5");
                        binding.progress.setText("100%");
                    } else {
                        binding.reviewCount.setText(String.valueOf(cnt));
                        binding.progress.setText(cnt * 20 + "%");
                    }
                    binding.scoreOver.setText(Common.calTierMessage(avg));
                }
            });



            JSONArray ja = jo.getJSONArray("data");
            JSONObject job = ja.getJSONObject(0);

            String m_profile1 = StringUtil.getStr(job, "m_profile1");
            String m_nick = StringUtil.getStr(job, "m_nick");
            String m_location = StringUtil.getStr(job, "m_location");
            String m_job = StringUtil.getStr(job, "m_job");
            String m_salary = StringUtil.getStr(job, "m_salary");

            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(act).load(NetUrls.DOMAIN_ORIGIN + m_profile1).into(binding.profileImg);
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