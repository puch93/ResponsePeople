package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RatingBar;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.EtcFixAdapter;
import kr.co.core.responsepeople.adapter.ImagePagerAdapter;
import kr.co.core.responsepeople.data.ImageData;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.databinding.ActivityProfileDetailBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class ProfileDetailAct extends BaseAct implements View.OnClickListener {
    ActivityProfileDetailBinding binding;
    Activity act;
    String type;
    String y_idx;

    ImagePagerAdapter imagePagerAdapter;
    ArrayList<ImageData> list_image = new ArrayList<>();
    FragmentManager fragmentManager;

    EtcFixAdapter adapter_charm;
    EtcFixAdapter adapter_interest;
    EtcFixAdapter adapter_ideal;

    ArrayList<String> list_charm = new ArrayList<>();
    ArrayList<String> list_interest = new ArrayList<>();
    ArrayList<String> list_ideal = new ArrayList<>();

    public static final int TYPE_LIKE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_detail, null);
        act = this;

        type = getIntent().getStringExtra("type");
        y_idx = getIntent().getStringExtra("y_idx");

        setLayout();

        if (!StringUtil.isNull(type)) {
            viewOtherProfile();
        } else {
            Common.showToast(act, "일시적인 오류입니다.");
        }
    }

    private void setLayout() {
        binding.btnBack.setOnClickListener(this);
        binding.btnChat.setOnClickListener(this);
        binding.btnLike.setOnClickListener(this);
        binding.btnQuestion.setOnClickListener(this);

        /* set view pager height */
        binding.viewPager.post(new Runnable() {
            @Override
            public void run() {
                int height = binding.viewPager.getMeasuredWidth();
                Log.e(StringUtil.TAG, "getMeasuredWidth: " + height);
                height = binding.viewPager.getWidth();
                Log.e(StringUtil.TAG, "getWidth: " + height);
                if (height <= 0) {
                    height = getResources().getDimensionPixelSize(R.dimen.profile_detail_default_height);
                }
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.viewPager.getLayoutParams();
                params.height = height;
                binding.viewPager.setLayoutParams(params);

                ConstraintLayout.LayoutParams params_filter = (ConstraintLayout.LayoutParams) binding.blackFilter.getLayoutParams();
                params.height = height;
                binding.blackFilter.setLayoutParams(params);
            }
        });

        /* set view pager (image) */
        fragmentManager = getSupportFragmentManager();
        imagePagerAdapter = new ImagePagerAdapter(fragmentManager, list_image);
        binding.viewPager.setAdapter(imagePagerAdapter);


        /* etc */
        FlexboxLayoutManager layoutManager1 = new FlexboxLayoutManager(act);
        layoutManager1.setFlexWrap(FlexWrap.WRAP);
        adapter_charm = new EtcFixAdapter(act, list_charm);
        binding.recyclerViewCharm.setLayoutManager(layoutManager1);
        binding.recyclerViewCharm.setAdapter(adapter_charm);
        binding.recyclerViewCharm.setHasFixedSize(true);
        binding.recyclerViewCharm.setItemViewCacheSize(20);

        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(act);
        layoutManager1.setFlexWrap(FlexWrap.WRAP);
        adapter_interest = new EtcFixAdapter(act, list_interest);
        binding.recyclerViewInterest.setLayoutManager(layoutManager2);
        binding.recyclerViewInterest.setAdapter(adapter_interest);
        binding.recyclerViewInterest.setHasFixedSize(true);
        binding.recyclerViewInterest.setItemViewCacheSize(20);

        FlexboxLayoutManager layoutManager3 = new FlexboxLayoutManager(act);
        layoutManager1.setFlexWrap(FlexWrap.WRAP);
        adapter_ideal = new EtcFixAdapter(act, list_ideal);
        binding.recyclerViewIdeal.setLayoutManager(layoutManager3);
        binding.recyclerViewIdeal.setAdapter(adapter_ideal);
        binding.recyclerViewIdeal.setHasFixedSize(true);
        binding.recyclerViewIdeal.setItemViewCacheSize(20);
    }

    private void doLike(String t_idx) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    boolean select_state = !binding.btnLike.isSelected();
                                    binding.btnLike.setSelected(select_state);
                                    setResult(RESULT_OK, new Intent().putExtra("result", select_state));
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

        server.setTag("Like Member");
        server.addParams("dbControl", NetUrls.LIKE);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("t_idx", t_idx);
        server.execute(true, true);
    }
    private void viewOtherProfile() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);

                            for (int i = 1; i < 7; i++) {
                                if (!StringUtil.isNull(StringUtil.getStr(job, "m_profile" + i))) {
                                    list_image.add(new ImageData(StringUtil.getStr(job, "m_profile" + i), StringUtil.getStr(job, "m_profile"+ i +"_result").equalsIgnoreCase("Y")));
                                } else {
                                    if(i == 1) {
                                        list_image.add(new ImageData("", true));
                                    }
                                    break;
                                }
                            }

                            String m_nick = StringUtil.getStr(job, "m_nick");
                            String m_location = StringUtil.getStr(job, "m_location");
                            String m_birth = StringUtil.getStr(job, "m_birth");
                            String m_height = StringUtil.getStr(job, "m_height");
                            String m_body = StringUtil.getStr(job, "m_body");
                            String m_edu = StringUtil.getStr(job, "m_edu");
                            String m_job = StringUtil.getStr(job, "m_job");
                            String m_drink = StringUtil.getStr(job, "m_drink");
                            String m_smoke = StringUtil.getStr(job, "m_smoke");
                            String m_religion = StringUtil.getStr(job, "m_religion");
                            String m_salary = StringUtil.getStr(job, "m_salary");
                            String m_intro = StringUtil.getStr(job, "m_intro");
                            String p_mark = StringUtil.getStr(job, "p_mark");
                            boolean m_salary_result = StringUtil.getStr(job, "m_salary_result").equalsIgnoreCase("Y");
                            boolean f_idx = !StringUtil.isNull(StringUtil.getStr(job, "f_idx"));

                            list_charm.addAll(Arrays.asList(StringUtil.getStr(job, "m_charm").split(",")));

                            list_interest.addAll(Arrays.asList(StringUtil.getStr(job, "m_interest").split(",")));

                            list_ideal.addAll(Arrays.asList(StringUtil.getStr(job, "m_ideal").split(",")));

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(StringUtil.isNull(p_mark)) {
                                        binding.areaEvalAfter.setVisibility(View.GONE);
                                        binding.areaEvalBefore.setVisibility(View.VISIBLE);
                                        binding.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                            @Override
                                            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                                if (fromUser)
                                                    LogUtil.logI("fromUser true");
                                                else
                                                    LogUtil.logI("fromUser false");

                                                if (rating >= 1)
                                                    evaluationOther(String.valueOf((int) (rating * 2)), y_idx);

                                                binding.ratingBar.setRating(0f);
                                            }
                                        });
                                    } else {
                                        binding.areaEvalBefore.setVisibility(View.GONE);
                                        binding.areaEvalAfter.setVisibility(View.VISIBLE);
                                        binding.score.setText(p_mark);
                                    }


                                    binding.btnLike.setSelected(f_idx);

                                    imagePagerAdapter.setList(list_image);

                                    binding.nick.setText(m_nick);
                                    binding.age.setText(StringUtil.calcAge(m_birth.substring(0, 4)));
                                    binding.jobTop.setText(m_job);
                                    binding.location.setText(m_location);

                                    if(m_salary_result) {
                                        binding.salaryTop.setText(m_salary);
                                        binding.salary.setText(m_salary);
                                    } else{
                                        binding.salaryTop.setText("연봉 검수중");
                                        binding.salary.setText("연봉 검수중");
                                    }

                                    binding.intro.setText(m_intro);

                                    binding.job.setText(m_job);
                                    binding.edu.setText(m_edu);

                                    binding.body.setText(m_body);
                                    binding.drink.setText(m_drink);
                                    binding.smoke.setText(m_smoke);
                                    binding.religion.setText(m_religion);

                                    adapter_charm.setList(list_charm);
                                    adapter_interest.setList(list_interest);
                                    adapter_ideal.setList(list_ideal);

                                    binding.indicator.setViewPager(binding.viewPager);
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

        server.setTag("Other Profile");
        server.addParams("dbControl", NetUrls.VIEW_PROFILE);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("t_idx", y_idx);
        server.addParams("pv_type", type);
        server.execute(true, false);
    }

    private void evaluationOther(String score, String t_idx) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            viewOtherProfile();
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

    private void checkRoom() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            // 방 없을때 생성
                            String room_idx = StringUtil.getStr(jo, "room_idx");
                            startActivity(new Intent(act, ChatAct.class)
                                    .putExtra("t_idx", y_idx)
                                    .putExtra("room_idx", room_idx)
                            );
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

        server.setTag("Check Room");
        server.addParams("dbControl", NetUrls.CHECK_ROOM);
        server.addParams("user_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("guest_idx_ar", y_idx);
        server.execute(true, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_like:
                doLike(y_idx);
                break;

            case R.id.btn_question:
                act.startActivity(new Intent(act, QuestionSendAct.class).putExtra("t_idx", y_idx));
                break;

            case R.id.btn_chat:
                checkRoom();
                break;
        }
    }
}