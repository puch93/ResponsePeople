package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityLoginBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.GpsInfo;
import kr.co.core.responsepeople.util.StringUtil;

public class LoginAct extends BaseAct {
    ActivityLoginBinding binding;
    public static Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login, null);
        act = this;

        binding.btnLogin.setOnClickListener(v -> {
            doLogin();
        });
        binding.btnFind.setOnClickListener(v -> {

        });
        binding.btnJoin.setOnClickListener(v -> {
            startActivity(new Intent(act, JoinAct.class).putExtra("type", "default"));
        });
    }


    private void doLogin() {
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

                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                            AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                            AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);

                            startActivity(new Intent(act, MainAct.class));
                            finish();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));

                            if (jo.has("data")) {
                                JSONArray ja = jo.getJSONArray("data");
                                JSONObject job = ja.getJSONObject(0);
                                String type = StringUtil.getStr(jo, "type");
                                String m_idx = StringUtil.getStr(job, "m_idx");
                                String m_gender = StringUtil.getStr(job, "m_gender");
                                String m_id = StringUtil.getStr(job, "m_id");
                                String m_pass = StringUtil.getStr(job, "m_pass");

                                AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                                AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                                AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                                AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);

                                switch (type) {
                                    // 프로필 사진 검수중
                                    case "image":
                                        AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                        startActivity(new Intent(act, JoinAct.class).putExtra("type", "image"));
                                        break;
                                    // 선호 설정 안함
                                    case "prefer":
                                        AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                        startActivity(new Intent(act, JoinAct.class).putExtra("type", "prefer"));
                                        break;
                                    // 평가 중인 경우
                                    case "rating":
                                        AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                        startActivity(new Intent(act, EvaluationBeforeAct.class));
                                        break;
                                    // 평가 완료 안누른 회원
                                    case "waiting":
                                        AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                        startActivity(new Intent(act, EvaluationAfterAct.class));
                                        break;
                                }
                            }
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

        server.setTag("Login");
        server.addParams("dbControl", NetUrls.LOGIN);
        server.addParams("m_id", binding.id.getText().toString());
        server.addParams("m_pass", binding.password.getText().toString());
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
        server.addParams("imei", Common.getDeviceId(act));
        server.addParams("m_x", AppPreference.getProfilePref(act, AppPreference.PREF_LAT));
        server.addParams("m_y", AppPreference.getProfilePref(act, AppPreference.PREF_LON));
        server.execute(true, false);
    }
}
