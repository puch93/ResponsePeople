package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.AlarmResponseAdapter;
import kr.co.core.responsepeople.data.QuestionReceivedData;
import kr.co.core.responsepeople.databinding.FragmentQuestionAlarm01Binding;
import kr.co.core.responsepeople.databinding.FragmentQuestionAlarm02Binding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class AlarmResponseFrag extends BaseFrag {
    FragmentQuestionAlarm02Binding binding;
    Activity act;

    LinkedHashMap<String, ArrayList<QuestionReceivedData>> hashMap = new LinkedHashMap<>();
    ArrayList<ArrayList<QuestionReceivedData>> list = new ArrayList<>();

    AlarmResponseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_question_alarm_02, container, false);
        act = getActivity();

        adapter = new AlarmResponseAdapter(act, list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        getQuestionData();
    }

    private void getQuestionData() {
        hashMap = new LinkedHashMap<>();
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
                                String q_sheet = StringUtil.getStr(job, "q_sheet");
                                String q_answer = StringUtil.getStr(job, "q_answer");
                                String q_regdate = StringUtil.getStr(job, "q_regdate");
                                String qh_idx = StringUtil.getStr(job, "qh_idx");
                                String qh_answer = StringUtil.getStr(job, "qh_answer");
                                String qh_regdate = StringUtil.getStr(job, "qh_regdate");

                                String m_age = StringUtil.calcAge(StringUtil.getStr(job, "m_birth").substring(0, 4));
                                String m_nick = StringUtil.getStr(job, "m_nick");
                                String m_before_profile1 = StringUtil.getStr(job, "m_before_profile1");

                                if (hashMap.containsKey(q_m_idx)) {
                                    ArrayList<QuestionReceivedData> tmp_list = hashMap.get(q_m_idx);
                                    tmp_list.add(new QuestionReceivedData(q_idx, q_m_idx, q_question, q_sheet, q_answer, q_regdate, qh_idx, qh_answer, qh_regdate, m_nick, m_age, m_before_profile1, true));
                                    hashMap.put(q_m_idx, tmp_list);
                                    LogUtil.logI(list.toString());
                                } else {
                                    ArrayList<QuestionReceivedData> tmp_list = new ArrayList<>();
                                    tmp_list.add(new QuestionReceivedData(q_idx, q_m_idx, q_question, q_sheet, q_answer, q_regdate, qh_idx, qh_answer, qh_regdate, m_nick, m_age, m_before_profile1, true));
                                    hashMap.put(q_m_idx, tmp_list);
                                }
                            }

                            list = new ArrayList<>(hashMap.values());
                            LogUtil.logI(list.toString());

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });
                        } else {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });
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

        server.setTag("Question Response");
        server.addParams("dbControl", NetUrls.QUESTION_ANSWER_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}
