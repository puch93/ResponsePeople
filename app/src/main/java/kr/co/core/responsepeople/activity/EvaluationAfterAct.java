package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.MemberBeforeAdapter;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.databinding.ActivityEvaluationAfterBinding;
import kr.co.core.responsepeople.databinding.ActivityEvaluationBeforeBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class EvaluationAfterAct extends BaseAct {
    ActivityEvaluationAfterBinding binding;
    Activity act;

    String avg_text;

    MemberBeforeAdapter adapter;
    GridLayoutManager manager;
    ArrayList<MemberData> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_evaluation_after, null);
        act = this;

        setInfo();

        setLayout();

        manager = new GridLayoutManager(act, 3);
        adapter = new MemberBeforeAdapter(act, list);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setAdapter(adapter);

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

                            String m_age_p = StringUtil.getStr(job, "m_age_p");
                            String m_distance_p = StringUtil.getStr(job, "m_distance_p");
                            String m_height_p = StringUtil.getStr(job, "m_height_p");
                            String m_edu_p = StringUtil.getStr(job, "m_edu_p");
                            String m_body_p = StringUtil.getStr(job, "m_body_p");
                            String m_religion_p = StringUtil.getStr(job, "m_religion_p");
                            String m_drink_p = StringUtil.getStr(job, "m_drink_p");
                            String m_smoke_p = StringUtil.getStr(job, "m_smoke_p");

                            getPreferList(m_age_p, m_distance_p, m_height_p, m_edu_p, m_body_p, m_religion_p, m_drink_p, m_smoke_p);
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

    private void getPreferList(String m_age_p, String m_distance_p, String m_height_p, String m_edu_p, String m_body_p, String m_religion_p, String m_smoke_p, String m_drink_p) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        LogUtil.logLarge(jo.toString());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            String total = StringUtil.getStr(jo, "total");

                            JSONArray ja = jo.getJSONArray("value");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String m_idx = StringUtil.getStr(job, "m_idx");
                                String m_nick = StringUtil.getStr(job, "m_nick");
                                String m_age = StringUtil.calcAge(StringUtil.getStr(job, "m_birth").substring(0, 4));
                                String m_job = StringUtil.getStr(job, "m_job");
                                String m_location = StringUtil.getStr(job, "m_location");
                                String m_salary = StringUtil.getStr(job, "m_salary");
                                String m_profile1 = StringUtil.getStr(job, "m_profile1");
                                boolean m_salary_result = StringUtil.getStr(job, "m_salary_result").equalsIgnoreCase("Y");
                                boolean m_profile_result = StringUtil.getStr(job, "m_profile1_result").equalsIgnoreCase("Y");
                                boolean f_idx = !StringUtil.isNull(StringUtil.getStr(job, "f_idx"));

                                list.add(new MemberData(m_idx, m_nick, m_age, m_job, m_location, m_salary, m_profile1, m_profile_result, f_idx, m_salary_result));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });
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

        server.setTag("Prefer List");
        server.addParams("dbControl", NetUrls.RECOMMEND_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        if (!StringUtil.isNull(m_age_p))
            server.addParams("m_age_p", m_age_p);
        if (!StringUtil.isNull(m_distance_p))
            server.addParams("m_distance_p", m_distance_p);
        if (!StringUtil.isNull(m_height_p))
            server.addParams("m_height_p", m_height_p);
        if (!StringUtil.isNull(m_edu_p))
            server.addParams("m_edu_p", m_edu_p);
        if (!StringUtil.isNull(m_body_p))
            server.addParams("m_body_p", m_body_p);
        if (!StringUtil.isNull(m_religion_p))
            server.addParams("m_religion_p", m_religion_p);
        if (!StringUtil.isNull(m_drink_p))
            server.addParams("m_drink_p", m_drink_p);
        if (!StringUtil.isNull(m_smoke_p))
            server.addParams("m_smoke_p", m_smoke_p);
        server.execute(true, false);
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
//                            Common.showToast(act, StringUtil.getStr(jo, "message"));
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

            if(avg != 0) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.scoreAverage.setText(avg_text);
                        binding.progressBar.setProgress(avg * 10);
                        binding.tierText.setText(Common.calTier(avg));
                        Glide.with(act).load(Common.calTierImage(avg)).into(binding.tierIcon);
                        binding.scoreOver.setText(Common.calTierMessage(avg));
                    }
                });
            } else {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avg_text = "5";

                        binding.scoreAverage.setText(avg_text);
                        binding.progressBar.setProgress(50);
                        binding.tierText.setText(Common.calTier(5));
                        Glide.with(act).load(Common.calTierImage(5)).into(binding.tierIcon);
                        binding.scoreOver.setText(Common.calTierMessage(5));
                    }
                });
            }



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