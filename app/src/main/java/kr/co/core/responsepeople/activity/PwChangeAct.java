package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityPwChangeBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class PwChangeAct extends BaseAct {
    ActivityPwChangeBinding binding;
    Activity act;

    public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,16}$"); // 영문 대소문자 + 8자리 ~ 16자리까지 가능
    Matcher matcher_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pw_change, null);
        act = this;

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnConfirm.setOnClickListener(v -> {
            matcher_pw = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(binding.pw.getText().toString());
            if (binding.pwOld.length() == 0) {
                Common.showToast(act, "비밀번호를 입력해주세요");
            } else if (binding.pw.length() == 0 || !matcher_pw.matches()) {
                Common.showToast(act, "변경할 비밀번호를 확인해주세요");
            } else if (binding.pwConfirm.length() == 0 || !binding.pwConfirm.getText().toString().equalsIgnoreCase(binding.pw.getText().toString())) {
                Common.showToast(act, "변경할 비밀번호를 정확하게 입력해주세요");
            } else {
                doPasswordChange();
            }
        });
    }


    private void doPasswordChange() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_PW, binding.pw.getText().toString());
                            finish();
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

        server.setTag("Password Change");
        server.addParams("dbControl", NetUrls.PASSWORD_CHANGE);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("m_pass", binding.pwOld.getText().toString());
        server.addParams("m_pass_new", binding.pw.getText().toString());
        server.execute(true, false);
    }

}
