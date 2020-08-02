package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.JoinAct;
import kr.co.core.responsepeople.adapter.MemberAdapter;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.databinding.FragmentJoin01Binding;
import kr.co.core.responsepeople.databinding.FragmentOnlineBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class OnlineFrag extends BaseFrag implements View.OnClickListener {
    FragmentOnlineBinding binding;
    Activity act;

    MemberAdapter adapter;
    LinearLayoutManager manager;
    ArrayList<MemberData> list = new ArrayList<>();

    private boolean isScroll = false;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_online, container, false);
        act = getActivity();

        manager = new GridLayoutManager(act, 2);
        adapter = new MemberAdapter(act, list, new MemberAdapter.CustomClickListener() {
            @Override
            public void likeClicked() {
                list = adapter.getList();
            }
        });
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setAdapter(adapter);

//        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                int totalCount = manager.getItemCount();
//                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
//                if (!isScroll) {
//                    if (totalCount - 1 == lastItemPosition) {
//                        ++page;
//                        getOnlineMember();
//                    }
//                }
//            }
//        });

        binding.nestedScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) binding.nestedScrollView.getChildAt(binding.nestedScrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (binding.nestedScrollView.getHeight() + binding.nestedScrollView
                        .getScrollY()));

                if (diff == 0) {
                    if (!isScroll) {
                        Log.e(StringUtil.TAG, "onScrollChange");
                        ++page;
                        getOnlineMember();
                    }
                }
            }
        });

        getOnlineMember();
        return binding.getRoot();
    }

    private void getOnlineMember() {
        isScroll = true;
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
                                boolean m_salary_result = StringUtil.getStr(job, "m_salary_result").equalsIgnoreCase("Y");
                                boolean m_profile_result = StringUtil.getStr(job, "m_profile_result").equalsIgnoreCase("Y");
                                boolean f_idx = !StringUtil.isNull(StringUtil.getStr(job, "f_idx"));

                                list.add(new MemberData(m_idx, m_nick, m_age, m_job, m_location, m_salary, m_profile1, m_profile_result, f_idx, m_salary_result));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(page == 1) {
                                        binding.count.setText(StringUtil.setNumComma(StringUtil.getInt(jo, "total")));
                                    }
                                    adapter.setList(list);
                                }
                            });
                            isScroll = false;
                        } else {
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
            }
        };

        server.setTag("Online Member");
        server.addParams("dbControl", NetUrls.ONLINE_MEMBER);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("pagenum", String.valueOf(page));
        server.execute(true, false);
    }

    @Override
    public void onClick(View view) {

    }
}
