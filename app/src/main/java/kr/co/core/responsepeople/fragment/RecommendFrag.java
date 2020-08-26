package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.ProfileDetailAct;
import kr.co.core.responsepeople.adapter.MemberAdapter;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.databinding.FragmentRecommendBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class RecommendFrag extends BaseFrag implements SwipeRefreshLayout.OnRefreshListener {
    FragmentRecommendBinding binding;
    Activity act;

    MemberAdapter adapter;
    GridLayoutManager manager;
    ArrayList<MemberData> list = new ArrayList<>();
    int currentPos = -1;

    private boolean isScroll = false;
    private int page = 1;


    String m_age_p = "";
    String m_distance_p = "";
    String m_height_p = "";
    String m_edu_p = "";
    String m_body_p = "";
    String m_religion_p = "";
    String m_drink_p = "";
    String m_smoke_p = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recommend, container, false);
        act = getActivity();

        binding.refreshLayout.setOnRefreshListener(this);

        manager = new GridLayoutManager(act, 2);
        adapter = new MemberAdapter(act, this, list, new MemberAdapter.CustomClickListener() {
            @Override
            public void likeClicked() {
                list = adapter.getList();
            }
        }, new MemberAdapter.CurrentPosListener() {
            @Override
            public void getCurrentIndex(int position) {
                currentPos = position;
            }
        });
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalCount = manager.getItemCount();
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                if (!isScroll) {
                    if (totalCount - 1 == lastItemPosition) {
                        ++page;
                        getListCheck();
                    }
                }
            }
        });


        getListCheck();

        return binding.getRoot();
    }

    private void getListCheck() {
        if(page == 1) {
            getMyInfo();
        } else {
            getPreferList(m_age_p, m_distance_p, m_height_p, m_edu_p, m_body_p, m_religion_p, m_drink_p, m_smoke_p);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ProfileDetailAct.TYPE_LIKE) {
                if (currentPos >= 0) {
                    MemberData memberData = list.get(currentPos);
                    memberData.setLike(data.getBooleanExtra("result", false));
                    list.set(currentPos, memberData);
                    adapter.setList(list);
                }
            }
        }
    }

    private void getMyInfo() {
        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);

                            m_age_p = StringUtil.getStr(job, "m_age_p");
                            m_distance_p = StringUtil.getStr(job, "m_distance_p");
                            m_height_p = StringUtil.getStr(job, "m_height_p");
                            m_edu_p = StringUtil.getStr(job, "m_edu_p");
                            m_body_p = StringUtil.getStr(job, "m_body_p");
                            m_religion_p = StringUtil.getStr(job, "m_religion_p");
                            m_drink_p = StringUtil.getStr(job, "m_drink_p");
                            m_smoke_p = StringUtil.getStr(job, "m_smoke_p");

                            getPreferList(m_age_p, m_distance_p, m_height_p, m_edu_p, m_body_p, m_religion_p, m_drink_p, m_smoke_p);
                        } else {
                            LogUtil.logI(StringUtil.getStr(jo, "message"));
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

        server.setTag("My Info");
        server.addParams("dbControl", NetUrls.MY_INFO);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    private void getPreferList(String m_age_p, String m_distance_p, String m_height_p, String m_edu_p, String m_body_p, String m_religion_p, String m_smoke_p, String m_drink_p) {
        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        LogUtil.logLarge(jo.toString());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            String total = StringUtil.getStr(jo, "total");

                            JSONArray ja = jo.getJSONArray("value");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String m_idx = StringUtil.getStr(job, "m_idx");
                                String m_nick = StringUtil.getStr(job, "m_nick");
                                String m_age = StringUtil.calcAge(StringUtil.getStr(job, "m_birth").substring(0, 4));
                                String m_job = StringUtil.getStr(job, "m_job");
                                String m_location = StringUtil.getStr(job, "m_location");
                                String m_salary = StringUtil.getStr(job, "m_salary");
                                String m_before_profile1 = StringUtil.getStr(job, "m_before_profile1");
                                boolean m_salary_result = StringUtil.getStr(job, "m_salary_result").equalsIgnoreCase("Y");
                                boolean m_profile_result = StringUtil.getStr(job, "m_profile1_result").equalsIgnoreCase("Y");
                                boolean f_idx = !StringUtil.isNull(StringUtil.getStr(job, "f_idx"));

                                list.add(new MemberData(m_idx, m_nick, m_age, m_job, m_location, m_salary, m_before_profile1, true, f_idx, m_salary_result));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });

                            isScroll = false;
                        } else {
                            if(page == 1) {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setList(list);
                                    }
                                });
                            }
                            isScroll = true;
                        }

                    } catch (JSONException e) {
                        isScroll = false;
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    isScroll = false;
                    Common.showToastNetwork(act);
                }


                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (binding.refreshLayout.isRefreshing()) {
                            binding.refreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        };

        server.setTag("Prefer List");
        server.addParams("dbControl", NetUrls.RECOMMEND_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        if (!StringUtil.isNull(m_age_p))
            server.addParams("m_age_p", m_age_p);
        if (!StringUtil.isNull(m_distance_p))
            server.addParams("m_distance_p", m_distance_p);
        if (!StringUtil.isNull(m_height_p))
            server.addParams("m_height_p", m_height_p);
        if (!StringUtil.isNull(m_edu_p))
            server.addParams("m_edu_p", m_edu_p);
        if (!StringUtil.isNull(m_body_p))
            server.addParams("m_body_p", m_body_p);
        if (!StringUtil.isNull(m_religion_p))
            server.addParams("m_religion_p", m_religion_p);
        if (!StringUtil.isNull(m_drink_p))
            server.addParams("m_drink_p", m_drink_p);
        if (!StringUtil.isNull(m_smoke_p))
            server.addParams("m_smoke_p", m_smoke_p);

        server.addParams("pagenum", String.valueOf(page));
        server.execute(true, false);
    }

    @Override
    public void onRefresh() {
        list = new ArrayList<>();
        page = 1;
        getListCheck();
    }
}
