package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.ProfileDetailAct;
import kr.co.core.responsepeople.adapter.HomeAdapter;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.databinding.FragmentHomeBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class HomeFrag extends BaseFrag implements View.OnClickListener {
    FragmentHomeBinding binding;
    Activity act;

    HomeAdapter adapter;
    LinearLayoutManager manager;
    ArrayList<MemberData> list = new ArrayList<>();
    ArrayList<MemberData> list_realTime = new ArrayList<>();

    int currentPos = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        act = getActivity();

        manager = new LinearLayoutManager(act);
        adapter = new HomeAdapter(act, this, list, new HomeAdapter.CurrentPosListener() {
            @Override
            public void getCurrentIndex(int position) {
                currentPos = position;
            }
        }, new HomeAdapter.CustomClickListener() {
            @Override
            public void likeClicked() {
                list = adapter.getList();
            }
        });
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setAdapter(adapter);

        getRecommendList();
        getRealTimeMember();


        binding.profileImg.post(new Runnable() {
            @Override
            public void run() {
                int height = binding.profileImg.getMeasuredWidth();
                Log.e(StringUtil.TAG, "getMeasuredWidth: " + height);
                height = binding.profileImg.getWidth();
                Log.e(StringUtil.TAG, "getWidth: " + height);
                if (height <= 0) {
                    height = getResources().getDimensionPixelSize(R.dimen.home_realtime_height);
                }
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) binding.profileImg.getLayoutParams();
                params.height = height;
                binding.profileImg.setLayoutParams(params);
            }
        });


        binding.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser)
                    LogUtil.logI("fromUser true");
                else
                    LogUtil.logI("fromUser false");

                if (list_realTime.size() > 0 && rating >= 1)
                    evaluationOther(String.valueOf((int) (rating * 2)), list_realTime.get(0).getIdx());

                binding.ratingBar.setRating(0f);
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == ProfileDetailAct.TYPE_LIKE) {
                if(currentPos >= 0) {
                    MemberData memberData = list.get(currentPos);
                    memberData.setLike(data.getBooleanExtra("result", false));
                    list.set(currentPos, memberData);
                    adapter.setList(list);
                }
            }
        }
    }

    private void evaluationOther(String score, String t_idx) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            list_realTime.remove(0);
                            setReaTimeLayout();
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

        server.setTag("Evaluation Other");
        server.addParams("dbControl", NetUrls.EVALUATION);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("t_idx", t_idx);
        server.addParams("mark", score);
        server.execute(true, true);
    }

    private void getRealTimeMember() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        LogUtil.logLarge(jo.toString());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.cardView.setVisibility(View.VISIBLE);
                                    binding.areaNoMember.setVisibility(View.GONE);
                                }
                            });

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
                                boolean m_profile_result = StringUtil.getStr(job, "m_profile1_result").equalsIgnoreCase("Y");
                                boolean f_idx = !StringUtil.isNull(StringUtil.getStr(job, "f_idx"));

                                list_realTime.add(new MemberData(m_idx, m_nick, m_age, m_job, m_location, m_salary, m_profile1, m_profile_result, f_idx, m_salary_result));
                            }

                            setReaTimeLayout();
                        } else {
                            LogUtil.logI("message: " + StringUtil.getStr(jo, "message"));

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.cardView.setVisibility(View.GONE);
                                    binding.areaNoMember.setVisibility(View.VISIBLE);
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

        server.setTag("RealTime Member");
        server.addParams("dbControl", NetUrls.REALTIME_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("m_gender", AppPreference.getProfilePref(act, AppPreference.PREF_GENDER));
        server.execute(true, false);
    }

    private void setReaTimeLayout() {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (list_realTime.size() > 0) {
                    MemberData data = list_realTime.get(0);
                    binding.nick.setText(data.getNick());
                    binding.age.setText(data.getAge());
                    binding.job.setText(data.getJob());
                    binding.location.setText(data.getLocation());
                    if (data.isSalary_ok()) {
                        binding.salary.setText(data.getSalary());
                    } else {
                        binding.salary.setText("연봉 검수중");
                    }

                    Common.processProfileImageRec(act, binding.profileImg, data.getProfile_img(), data.isImage_ok(), 5, 3);
                } else {
                    // 멤버 다떨어졌을때 재검사
                    getRealTimeMember();
                }
            }
        });
    }

    private void getRecommendList() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        LogUtil.logLarge(jo.toString());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {

                            JSONArray ja = jo.getJSONArray("value");
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
                                boolean m_profile_result = StringUtil.getStr(job, "m_profile1_result").equalsIgnoreCase("Y");
                                boolean f_idx = !StringUtil.isNull(StringUtil.getStr(job, "f_idx"));

                                list.add(new MemberData(m_idx, m_nick, m_age, m_job, m_location, m_salary, m_profile1, m_profile_result, f_idx, m_salary_result));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
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

        server.setTag("Online Member");
        server.addParams("dbControl", NetUrls.RECOMMEND_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    @Override
    public void onClick(View view) {

    }
}
