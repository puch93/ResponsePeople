package kr.co.core.responsepeople.activity;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityMainBinding;
import kr.co.core.responsepeople.fragment.BaseFrag;
import kr.co.core.responsepeople.fragment.HomeFrag;
import kr.co.core.responsepeople.fragment.NearFrag;
import kr.co.core.responsepeople.fragment.OnlineFrag;
import kr.co.core.responsepeople.fragment.RecommendFrag;
import kr.co.core.responsepeople.fragment.ResponseFrag;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.BackPressCloseHandler;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class MainAct extends BaseAct implements View.OnClickListener {
    ActivityMainBinding binding;
    Activity act;

    FragmentManager fragmentManager;
    private BackPressCloseHandler backPressCloseHandler;
    private int currentPos = 0;


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

            case R.id.btn_setting:
                doLogout();
                break;

        }
    }


    private void doLogout() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            AppPreference.setProfilePrefBool(act, AppPreference.AUTO_LOGIN, false);
                            startActivity(new Intent(act, LoginAct.class));
                            finish();
                            finishAffinity();
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

        server.setTag("Logout");
        server.addParams("dbControl", NetUrls.LOGOUT);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}
