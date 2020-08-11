package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityTermBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class TermAct extends BaseAct {
    ActivityTermBinding binding;
    Activity act;

    public static final String TYPE_USE = "di_terms";
    public static final String TYPE_PRIVATE = "di_personal_information";
    public static final String TYPE_GPS = "di_location_information";

    public String type = TYPE_USE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_term, null);
        act = this;

        type = getIntent().getStringExtra("type");

        switch (type) {
            case TYPE_USE:
                binding.title.setText("이용 약관");
                break;
            case TYPE_PRIVATE:
                binding.title.setText("개인정보 처리방침");
                break;
            case TYPE_GPS:
                binding.title.setText("위치정보서비스 이용약관");
                break;
            default:
                finish();
        }

        getTerm();

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void getTerm() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);

                            String terms = StringUtil.getStr(job, type);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.contents.setText(terms);
                                }
                            });

                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            finish();
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

        server.setTag("Term");
        server.addParams("dbControl", NetUrls.TERM);
        server.execute(true, false);
    }
}
