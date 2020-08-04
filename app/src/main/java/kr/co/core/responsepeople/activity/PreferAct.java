package kr.co.core.responsepeople.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityPreferBinding;
import kr.co.core.responsepeople.dialog.ProfileSimpleDlg;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class PreferAct extends BaseAct implements View.OnClickListener {
    ActivityPreferBinding binding;
    Activity act;

    static final int PROFILE = 1001;

    float value = 0f;
    TextView selectedView;

    int age_left = 0;
    int age_right = 0;
    int height_left = 0;
    int height_right = 0;
    int distance_value = 0;


    String age;
    String distance;
    String height;
    String edu;
    String body;
    String religion;
    String drink;
    String smoke;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prefer, null);
        act = this;

        setClickListener();

        getMyInfo();
    }

    private void setClickListener() {
        binding.areaEdu.setOnClickListener(this);
        binding.areaBody.setOnClickListener(this);
        binding.areaReligion.setOnClickListener(this);
        binding.areaDrink.setOnClickListener(this);
        binding.areaSmoke.setOnClickListener(this);
        binding.btnComplete.setOnClickListener(this);

        binding.areaEdu.setTag(R.string.tag01, "edu_prefer");
        binding.areaBody.setTag(R.string.tag01, "body_prefer");
        binding.areaReligion.setTag(R.string.tag01, "religion_prefer");
        binding.areaDrink.setTag(R.string.tag01, "drink_prefer");
        binding.areaSmoke.setTag(R.string.tag01, "smoke_prefer");

        binding.areaEdu.setTag(R.string.tag02, binding.edu);
        binding.areaBody.setTag(R.string.tag02, binding.body);
        binding.areaReligion.setTag(R.string.tag02, binding.religion);
        binding.areaDrink.setTag(R.string.tag02, binding.drink);
        binding.areaSmoke.setTag(R.string.tag02, binding.smoke);
    }

    private void getMyInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);

                            String m_age_p = StringUtil.getStr(job, "m_age_p");
                            String m_distance_p = StringUtil.getStr(job, "m_distance_p");
                            String m_height_p = StringUtil.getStr(job, "m_height_p");
                            String m_edu_p = StringUtil.getStr(job, "m_edu_p");
                            String m_body_p = StringUtil.getStr(job, "m_body_p");
                            String m_religion_p = StringUtil.getStr(job, "m_religion_p");
                            String m_drink_p = StringUtil.getStr(job, "m_drink_p");
                            String m_smoke_p = StringUtil.getStr(job, "m_smoke_p");

                            String[] ages = m_age_p.split(",");
                            age_left = Integer.parseInt(ages[0]);
                            age_right = Integer.parseInt(ages[1]);
                            if(age_right > 55) {
                                age_right = 55;
                            }

                            String[] heights = m_height_p.split(",");
                            height_left = Integer.parseInt(heights[0]);
                            height_right = Integer.parseInt(heights[1]);

                            distance_value = 0;
                            binding.distance.setText(m_distance_p);
                            switch (m_distance_p) {
                                case "12":
                                    distance_value = 1;
                                    break;
                                case "20":
                                    distance_value = 2;
                                    break;
                                case "36":
                                    distance_value = 3;
                                    break;
                                case "60":
                                    distance_value = 4;
                                    break;
                                case "120":
                                    distance_value = 5;
                                    break;
                                case "180":
                                    distance_value = 6;
                                    break;
                                case "240":
                                    distance_value = 7;
                                    break;
                                case "300":
                                    distance_value = 8;
                                    break;
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.edu.setText(!StringUtil.isNull(m_edu_p) ? m_edu_p : "상관없음");
                                    binding.body.setText(!StringUtil.isNull(m_body_p) ? m_body_p : "상관없음");
                                    binding.religion.setText(!StringUtil.isNull(m_religion_p) ? m_religion_p : "상관없음");
                                    binding.drink.setText(!StringUtil.isNull(m_drink_p) ? m_drink_p : "상관없음");
                                    binding.smoke.setText(!StringUtil.isNull(m_smoke_p) ? m_smoke_p : "상관없음");
                                    setSeekBar();
                                }
                            });


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


    private void setSeekBar() {
        binding.seekBarAge.setRange(20, 55);
        binding.seekBarAge.setProgress(age_left, age_right);
        binding.seekBarAge.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                binding.ageStart.setText(String.valueOf((int) leftValue));
                binding.ageEnd.setText(String.valueOf((int) rightValue));

                age_right = (int) rightValue;
                if(rightValue >= 55.0 ) {
                    age_right = 100;
                }

                if (rightValue == 55.0) {
                    binding.age55.setVisibility(View.VISIBLE);
                } else {
                    binding.age55.setVisibility(View.GONE);
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
            }
        });


        binding.seekBarHeight.setRange(120, 200);
        binding.seekBarHeight.setProgress(height_left, height_right);
        binding.seekBarHeight.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                binding.heightStart.setText(String.valueOf((int) leftValue));
                binding.heightEnd.setText(String.valueOf((int) rightValue));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });

        binding.seekBarDistance.setRange(1, 8);
        binding.seekBarDistance.setProgress(distance_value);
        binding.seekBarDistance.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                value = leftValue;
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                Log.i(StringUtil.TAG, "onStopTrackingTouch value: " + value);

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch ((int) value) {
                            case 1:
                                binding.distance.setText("12");
                                break;
                            case 2:
                                binding.distance.setText("20");
                                break;
                            case 3:
                                binding.distance.setText("36");
                                break;
                            case 4:
                                binding.distance.setText("60");
                                break;
                            case 5:
                                binding.distance.setText("120");
                                break;
                            case 6:
                                binding.distance.setText("180");
                                break;
                            case 7:
                                binding.distance.setText("240");
                                break;
                            case 8:
                                binding.distance.setText("300");
                                break;
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(StringUtil.TAG, "onActivityResult: ");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PROFILE:
                    String value = data.getStringExtra("value");
                    selectedView.setText(value);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.area_edu:
            case R.id.area_body:
            case R.id.area_religion:
            case R.id.area_drink:
            case R.id.area_smoke:
                selectedView = (TextView) v.getTag(R.string.tag02);

                startActivityForResult(new Intent(act, ProfileSimpleDlg.class)
                                .putExtra("type", (String) v.getTag(R.string.tag01))
                                .putExtra("data", selectedView.getText().toString())
                        , PROFILE);
                break;

            case R.id.btn_complete:
                age = binding.ageStart.getText().toString() + "," + String.valueOf(age_right);
                distance = binding.distance.getText().toString();
                height = binding.heightStart.getText().toString() + "," + binding.heightEnd.getText().toString();
                edu = binding.edu.getText().toString();
                body = binding.body.getText().toString();
                religion = binding.religion.getText().toString();
                drink = binding.drink.getText().toString();
                smoke = binding.smoke.getText().toString();

                registerPrefer();
                break;
        }
    }

    private void registerPrefer() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            act.startActivity(new Intent(act, EvaluationBeforeAct.class));
                            act.finish();
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

        server.setTag("Prefer Set");
        server.addParams("dbControl", NetUrls.PREFER);
        //필수
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("m_age", age);
        server.addParams("m_distance", distance);
        server.addParams("m_height", height);
        //선택
        if (!StringUtil.isNull(edu) && !edu.equalsIgnoreCase("상관없음"))
            server.addParams("m_edu", edu);
        if (!StringUtil.isNull(body) && !body.equalsIgnoreCase("상관없음"))
            server.addParams("m_body", body);
        if (!StringUtil.isNull(religion) && !religion.equalsIgnoreCase("상관없음"))
            server.addParams("m_religion", religion);
        if (!StringUtil.isNull(drink) && !drink.equalsIgnoreCase("상관없음"))
            server.addParams("m_drink", drink);
        if (!StringUtil.isNull(smoke) && !smoke.equalsIgnoreCase("상관없음"))
            server.addParams("m_smoke", smoke);
        server.execute(true, false);
    }

    private void checkLoginState() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));

                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);
                            String m_idx = StringUtil.getStr(job, "m_idx");
                            String m_gender = StringUtil.getStr(job, "m_gender");

                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);

                            startActivity(new Intent(act, MainAct.class));
                            act.finish();
                        } else {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);
                            String type = StringUtil.getStr(jo, "type");
                            String m_idx = StringUtil.getStr(job, "m_idx");
                            String m_gender = StringUtil.getStr(job, "m_gender");
                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);

                            switch (type) {
//                                // 프로필 사진 검수중
//                                case "image":
//                                    AppPreference.setProfilePref(act, AppPreference.PREF_JSON, job.toString());
//                                    startActivity(new Intent(act, JoinAct.class).putExtra("type", "image"));
//                                    break;

                                case "prefer":
                                    AppPreference.setProfilePref(act, AppPreference.PREF_JSON, job.toString());
//                                    startActivity(new Intent(act, JoinAct.class).putExtra("type", "prefer"));
                                    break;
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

        server.setTag("Check Login State");
        server.addParams("dbControl", NetUrls.LOGIN);
        server.addParams("m_id", AppPreference.getProfilePref(act, AppPreference.PREF_ID));
        server.addParams("m_pass", AppPreference.getProfilePref(act, AppPreference.PREF_PW));
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
        server.addParams("imei", Common.getDeviceId(act));
        server.addParams("m_x", AppPreference.getProfilePref(act, AppPreference.PREF_LAT));
        server.addParams("m_y", AppPreference.getProfilePref(act, AppPreference.PREF_LON));
        server.execute(true, false);
    }
}