package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.ProfileDetailAct;
import kr.co.core.responsepeople.adapter.ResponseAdapter;
import kr.co.core.responsepeople.data.ResponseData;
import kr.co.core.responsepeople.data.ResponseData;
import kr.co.core.responsepeople.databinding.FragmentNearBinding;
import kr.co.core.responsepeople.databinding.FragmentResponseBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.GpsInfo;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class ResponseFrag extends BaseFrag implements View.OnClickListener {
    FragmentResponseBinding binding;
    Activity act;

    ResponseAdapter adapter;
    LinearLayoutManager manager;
    ArrayList<ResponseData> list = new ArrayList<>();

    private Timer timer = new Timer();

    int currentPos = -1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_response, container, false);
        act = getActivity();

        manager = new LinearLayoutManager(act);
        adapter = new ResponseAdapter(act, this, list, new ResponseAdapter.CurrentPosListener() {
            @Override
            public void getCurrentIndex(int position) {
                currentPos = position;
            }
        }, new ResponseAdapter.CustomClickListener() {
            @Override
            public void likeClicked() {
                list = adapter.getList();
            }
        });
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setAdapter(adapter);

        getResponseList();

        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == ProfileDetailAct.TYPE_LIKE) {
                if(currentPos >= 0) {
                    ResponseData responseData = list.get(currentPos);
                    responseData.setLike(data.getBooleanExtra("result", false));
                    list.set(currentPos, responseData);
                    adapter.setList(list);
                }
            }
        }
    }

    private void getResponseList() {
        list = new ArrayList<>();
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
                                int total_cnt = StringUtil.getInt(job, "total_cnt");
                                int complet_cnt = StringUtil.getInt(job, "complet_cnt");
                                int matching_cnt = StringUtil.getInt(job, "matching_cnt");

                                int result = 30 + (int) (((double) complet_cnt / (double) total_cnt) * 50) + (matching_cnt * 5);

                                boolean m_salary_result = StringUtil.getStr(job, "m_salary_result").equalsIgnoreCase("Y");
                                boolean m_profile_result = StringUtil.getStr(job, "m_profile1_result").equalsIgnoreCase("Y");
                                boolean f_idx = !StringUtil.isNull(StringUtil.getStr(job, "f_idx"));

                                list.add(new ResponseData(m_idx, m_nick, m_age, m_job, m_location, m_salary, m_profile1, result ,f_idx, m_salary_result, m_profile_result));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.count.setText(String.valueOf(ja.length()));
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
        server.setTag("Response List");
        server.addParams("dbControl", NetUrls.QUESTION_RESPONSE_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    @Override
    public void onClick(View view) {

    }
}
