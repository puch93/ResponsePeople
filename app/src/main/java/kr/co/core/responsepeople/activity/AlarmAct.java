package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityAlarmBinding;
import kr.co.core.responsepeople.databinding.ActivityTierBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class AlarmAct extends BaseAct {
    ActivityAlarmBinding binding;
    Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm, null);
        act = this;

        binding.swQuestion.setChecked(AppPreference.getProfilePrefBool(act, AppPreference.PREF_SET_QUESTION));
        binding.swLike.setChecked(AppPreference.getProfilePrefBool(act, AppPreference.PREF_SET_LIKE));
        binding.swChatting.setChecked(AppPreference.getProfilePrefBool(act, AppPreference.PREF_SET_CHAT));

        binding.swQuestion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreference.setProfilePrefBool(act, AppPreference.PREF_SET_QUESTION, isChecked);
            }
        });

        binding.swLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreference.setProfilePrefBool(act, AppPreference.PREF_SET_LIKE, isChecked);
            }
        });

        binding.swChatting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreference.setProfilePrefBool(act, AppPreference.PREF_SET_CHAT, isChecked);
            }
        });



        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnLogout.setOnClickListener(v -> {
            showAlert(act, "로그아웃", "로그아웃 하시겠습니까?", new OnAlertAfter() {
                @Override
                public void onAfterOk() {
                    doLogout();
                }

                @Override
                public void onAfterCancel() {

                }
            });
        });

        binding.btnPwChange.setOnClickListener(v -> {
            startActivity(new Intent(act, PwChangeAct.class));
        });
    }

    private void doLogout() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            NotificationManager notificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancelAll();

                            AppPreference.setProfilePrefBool(act, AppPreference.AUTO_LOGIN, false);
                            startActivity(new Intent(act, LoginAct.class));
                            finish();
                            finishAffinity();
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

        server.setTag("Logout");
        server.addParams("dbControl", NetUrls.LOGOUT);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}