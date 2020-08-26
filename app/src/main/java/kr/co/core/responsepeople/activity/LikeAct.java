package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.LikeAdapter;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.databinding.ActivityLikeBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class LikeAct extends BaseAct {
    ActivityLikeBinding binding;
    Activity act;
    ArrayList<MemberData> list = new ArrayList<>();
    LikeAdapter adapter;

    int currentPos = -1;

    String dbControl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_like, null);
        act = this;

        adapter = new LikeAdapter(act, list);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(act, 3));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setAdapter(adapter);

        dbControl = getIntent().getStringExtra("dbControl");

        if(!dbControl.equalsIgnoreCase(NetUrls.LIKED)) {
            binding.title.setText("내가 찜한 사람");
            binding.titleSub.setText("내가 찜한 이성 ");
        }

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list = new ArrayList<>();
                getLikeList();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLikeList();
    }

    private void getLikeList() {
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
                                String m_before_profile1 = StringUtil.getStr(job, "m_before_profile1");
                                boolean m_profile_result = StringUtil.getStr(job, "m_profile1_result").equalsIgnoreCase("Y");

                                list.add(new MemberData(m_idx, m_nick, m_age, m_job, m_location, null, m_before_profile1, m_profile_result, false, false));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.count.setText(StringUtil.setNumComma(StringUtil.getInt(jo, "total")));

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

        server.setTag("Like Member");
        server.addParams("dbControl", dbControl);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}
