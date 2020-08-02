package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.QuestionManagerAdpater;
import kr.co.core.responsepeople.data.QuestionManageData;
import kr.co.core.responsepeople.databinding.ActivityQuestionManageBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class QuestionManageAct extends BaseAct {
    ActivityQuestionManageBinding binding;
    Activity act;

    QuestionManagerAdpater adapter;
    ArrayList<QuestionManageData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question_manage, null);
        act = this;

        adapter = new QuestionManagerAdpater(act, list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);

        getQuestionList();

        binding.btnBack.setOnClickListener(view -> {
            finish();
        });

        binding.btnRegister.setOnClickListener(view -> {

        });
    }

    private void getQuestionList() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);

                                String q_idx = StringUtil.getStr(job, "q_idx");
                                String q_m_idx = StringUtil.getStr(job, "q_m_idx");
                                String q_question = StringUtil.getStr(job, "q_question");
                                String[] q_sheet = StringUtil.getStr(job, "q_sheet").split(",");
                                String q_answer = StringUtil.getStr(job, "q_answer");
                                String q_order = StringUtil.getStr(job, "q_order");
                                String q_regdate = StringUtil.getStr(job, "q_regdate");

                                list.add(new QuestionManageData(q_idx, q_question, q_answer, q_sheet));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
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

        server.setTag("My Question");
        server.addParams("dbControl", NetUrls.QUESTION_MY_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}