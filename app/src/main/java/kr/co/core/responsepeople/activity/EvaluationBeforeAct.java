package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.MemberBeforeAdapter;
import kr.co.core.responsepeople.data.EtcData;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.databinding.ActivityEvaluationBeforeBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class EvaluationBeforeAct extends BaseAct {
    ActivityEvaluationBeforeBinding binding;
    public static Activity act;
    public static Activity real_act;

    MemberBeforeAdapter adapter;
    GridLayoutManager manager;
    ArrayList<MemberData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_evaluation_before, null);
        act = this;
        real_act = this;

        setLayout();

        manager = new GridLayoutManager(act, 3);
        adapter = new MemberBeforeAdapter(act, list);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);

        getHighScoreMeList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        real_act = null;
    }

    public void doLogin() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);
                            String m_idx = StringUtil.getStr(job, "m_idx");
                            String m_gender = StringUtil.getStr(job, "m_gender");
                            String m_id = StringUtil.getStr(job, "m_id");
                            String m_pass = StringUtil.getStr(job, "m_pass");
                            String m_profile1 = StringUtil.getStr(job, "m_profile1");
                            String m_birth = StringUtil.getStr(job, "m_birth");
                            String m_hp = StringUtil.getStr(job, "m_hp");

                            AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                            AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                            AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);
                            AppPreference.setProfilePref(act, AppPreference.PREF_AGE, StringUtil.calcAge(m_birth.substring(0, 4)));
                            AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, NetUrls.DOMAIN_ORIGIN + m_profile1);
                            AppPreference.setProfilePref(act, AppPreference.PREF_PHONE, m_hp);

                            startActivity(new Intent(act, MainAct.class));
                            finish();
                        } else {
                            if (jo.has("data")) {
                                JSONArray ja = jo.getJSONArray("data");
                                JSONObject job = ja.getJSONObject(0);
                                String type = StringUtil.getStr(jo, "type");
                                String m_idx = StringUtil.getStr(job, "m_idx");
                                String m_gender = StringUtil.getStr(job, "m_gender");
                                String m_id = StringUtil.getStr(job, "m_id");
                                String m_pass = StringUtil.getStr(job, "m_pass");
                                String m_hp = StringUtil.getStr(job, "m_hp");
                                String m_birth = StringUtil.getStr(job, "m_birth");
                                String m_profile1 = StringUtil.getStr(job, "m_profile1");
                                int cnt = StringUtil.getInt(jo, "cnt");

                                AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                                AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                                AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                                AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);
                                AppPreference.setProfilePref(act, AppPreference.PREF_PHONE, m_hp);
                                AppPreference.setProfilePref(act, AppPreference.PREF_AGE, StringUtil.calcAge(m_birth.substring(0, 4)));
                                AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, NetUrls.DOMAIN_ORIGIN + m_profile1);

                                if (!StringUtil.isNull(type)) {
                                    AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                    // 로그인 완료회원 아니면 자동로그인 처리 (로그인시 페이지 이동 고정시킴)
//                                    MemberUtil.setJoinProcess(act, StringUtil.getStr(job, "m_id"), StringUtil.getStr(job, "m_pass"));


                                    switch (type) {
                                        // 평가 중인 경우
                                        case "rating":
                                            if (cnt >= 5) {
                                                startActivity(new Intent(act, EvaluationAfterAct.class));
                                                finish();
                                            } else {
                                                setLayout();
                                            }


                                            // 평가 완료 안누른 회원
                                            break;
                                        case "waiting":
                                            startActivity(new Intent(act, EvaluationAfterAct.class));
                                            finish();
                                            break;
                                    }
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        server.setTag("Auto Login");
        server.addParams("dbControl", NetUrls.LOGIN);
        server.addParams("m_id", AppPreference.getProfilePref(act, AppPreference.PREF_ID));
        server.addParams("m_pass", AppPreference.getProfilePref(act, AppPreference.PREF_PW));
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
        server.addParams("imei", Common.getDeviceId(act));
        server.addParams("m_x", AppPreference.getProfilePref(act, AppPreference.PREF_LAT));
        server.addParams("m_y", AppPreference.getProfilePref(act, AppPreference.PREF_LON));
        server.execute(true, false);
    }


    private void getHighScoreMeList() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        LogUtil.logLarge(jo.toString());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {

                            JSONArray ja = jo.getJSONArray("data");
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
                                    LogUtil.logI(list.toString());
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

        server.setTag("High Score List");
        server.addParams("dbControl", NetUrls.HIGH_SCORE);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
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
                    binding.progressBar.setProgress(avg * 10);
                    if (cnt > 5) {
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