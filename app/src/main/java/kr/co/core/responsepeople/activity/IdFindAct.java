package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityIdConfirmBinding;
import kr.co.core.responsepeople.databinding.ActivityPwChangeBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class IdFindAct extends BaseAct {
    ActivityIdConfirmBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_id_confirm, null);
        act = this;

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        binding.btnConfirm.setOnClickListener(v -> {
            if (binding.phone.length() == 0 || !Common.checkCellnum(binding.phone.getText().toString())) {
                Common.showToast(act, "휴대폰번호를 확인해주세요.");
            } else {
                findId();
            }
        });
    }

    private void findId() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));

                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.areaId.setVisibility(View.VISIBLE);
                                    binding.id.setText(StringUtil.getStr(job, "m_id"));
                                }
                            });
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

        server.setTag("Find Id");
        server.addParams("dbControl", NetUrls.FIND_ID);
        server.addParams("m_hp", binding.phone.getText().toString());
        server.execute(true, false);
    }
}
