package kr.co.core.responsepeople.activity;

import androidx.ads.identifier.AdvertisingIdClient;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityMainBinding;
import kr.co.core.responsepeople.fragment.BaseFrag;
import kr.co.core.responsepeople.fragment.HomeFrag;
import kr.co.core.responsepeople.fragment.NearFrag;
import kr.co.core.responsepeople.fragment.OnlineFrag;
import kr.co.core.responsepeople.fragment.RecommendFrag;
import kr.co.core.responsepeople.fragment.ResponseFrag;
import kr.co.core.responsepeople.receiver.AlarmReceiver;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.BackPressCloseHandler;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.RequestHttpURLConnection;
import kr.co.core.responsepeople.util.StringUtil;

public class MainAct extends BaseAct implements View.OnClickListener {
    ActivityMainBinding binding;
    public static Activity act;

    FragmentManager fragmentManager;
    private BackPressCloseHandler backPressCloseHandler;

    ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main, null);
        act = this;

        backPressCloseHandler = new BackPressCloseHandler(this);
        fragmentManager = getSupportFragmentManager();

        binding.btnQuestion.setOnClickListener(this);
        binding.btnAlarm.setOnClickListener(this);
        binding.btnChat.setOnClickListener(this);
        binding.btnSetting.setOnClickListener(this);
        binding.menu01Area.setOnClickListener(this);
        binding.menu02Area.setOnClickListener(this);
        binding.menu03Area.setOnClickListener(this);
        binding.menu04Area.setOnClickListener(this);
        binding.menu05Area.setOnClickListener(this);

        binding.menu01Area.performClick();
    }


    public void getQuestionCount(String dbControl) {
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

                                String q_m_idx = StringUtil.getStr(job, "q_m_idx");


                                if (!list.contains(q_m_idx))
                                    list.add(q_m_idx);
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.countReceive.setVisibility(View.VISIBLE);
                                    binding.countReceive.setText(String.valueOf(list.size()));

                                }
                            });
                        } else {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.countReceive.setText("0");
                                    binding.countReceive.setVisibility(View.GONE);
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

        server.setTag("Question " + dbControl);
        server.addParams("dbControl", dbControl);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!StringUtil.isNull(AppPreference.getProfilePref(act, AppPreference.PREF_IMAGE)))
            Glide.with(act).load(AppPreference.getProfilePref(act, AppPreference.PREF_IMAGE)).transform(new CircleCrop()).into(binding.profileImg);

        getQuestionCount(NetUrls.QUESTION_RECEIVED);
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    private void switchLayout(View view) {
        binding.menu01Area.setSelected(false);
        binding.menu02Area.setSelected(false);
        binding.menu03Area.setSelected(false);
        binding.menu04Area.setSelected(false);
        binding.menu05Area.setSelected(false);

        view.setSelected(true);
    }

    public void replaceFragment(BaseFrag frag, String tag) {
        /* replace fragment */
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fade_in, R.anim.anim_fade_out);
        transaction.replace(R.id.replace_area, frag, tag);
        transaction.commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.menu01_area:
                switchLayout(view);
                replaceFragment(new HomeFrag(), "home");
                break;
            case R.id.menu02_area:
                switchLayout(view);
                replaceFragment(new RecommendFrag(), "recommend");
                break;
            case R.id.menu03_area:
                switchLayout(view);
                replaceFragment(new OnlineFrag(), "online");
                break;
            case R.id.menu04_area:
                switchLayout(view);
                replaceFragment(new NearFrag(), "near");
                break;
            case R.id.menu05_area:
                switchLayout(view);
                replaceFragment(new ResponseFrag(), "response");
                break;


            case R.id.btn_question:
                startActivity(new Intent(act, QuestionManageAct.class));
                break;

            case R.id.btn_alarm:
                startActivity(new Intent(act, QuestionAlarmAct.class));
                break;
            case R.id.btn_chat:
                startActivity(new Intent(act, ChatListAct.class));
                break;
            case R.id.btn_setting:
                startActivity(new Intent(act, MyPageAct.class));
                break;

        }
    }
}
