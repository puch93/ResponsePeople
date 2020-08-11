package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.QuestionSendAdapter;
import kr.co.core.responsepeople.data.QuestionManageData;
import kr.co.core.responsepeople.databinding.ActivityQuestionManageBinding;
import kr.co.core.responsepeople.databinding.ActivityQuestionSendBinding;
import kr.co.core.responsepeople.dialog.RegisterQuestion01Dlg;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class QuestionSendAct extends BaseAct {
    ActivityQuestionSendBinding binding;
    public static Activity act;

    QuestionSendAdapter adapter;
    ArrayList<QuestionManageData> list = new ArrayList<>();
    String t_idx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_question_send, null);
        act = this;

        t_idx = getIntent().getStringExtra("t_idx");

        adapter = new QuestionSendAdapter(act, list, new QuestionSendAdapter.SelectListener() {
            @Override
            public void selected() {
                list = adapter.getList();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);

        getQuestionList();

        binding.btnBack.setOnClickListener(view -> {
            finish();
        });

        binding.btnSend.setOnClickListener(v -> {
            int count = 0;
            ArrayList<String> idxs = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelect()) {
                    ++count;
                    idxs.add(list.get(i).getIdx());
                }
            }

            if (count < 3 || count > 5) {
                Common.showToast(act, "질문은 최소 3개, 최대 5개까지 전송 가능합니다");
            } else {
                for (int i = 0; i < idxs.size(); i++) {
                    int finalI = i;
                    ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
                        @Override
                        public void onAfter(int resultCode, HttpResult resultData) {
                            if (resultData.getResult() != null) {
                                try {
                                    JSONObject jo = new JSONObject(resultData.getResult());

                                    if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                                        if(finalI == idxs.size()-1) {
                                            doSendQuestion();
                                        }
                                    } else {
                                        if(finalI == 0) {
                                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                                            finish();
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

                    server.setTag("Send Question");
                    server.addParams("dbControl", NetUrls.QUESTION_SEND);
                    server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
                    server.addParams("t_idx", t_idx);
                    server.addParams("q_idx", idxs.get(i));
                    server.execute(true, false);

                }
            }
        });
    }

    private void doSendQuestion() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            finish();
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

        server.setTag("Send Question Chk");
        server.addParams("dbControl", NetUrls.QUESTION_ANSWER_CHK);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("t_idx", t_idx);
        server.execute(true, true);
    }

    public void getQuestionList() {
        list = new ArrayList<>();
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
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

        server.setTag("My Question");
        server.addParams("dbControl", NetUrls.QUESTION_MY_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}