package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.EvaluationBeforeAct;
import kr.co.core.responsepeople.activity.JoinAct;
import kr.co.core.responsepeople.activity.MainAct;
import kr.co.core.responsepeople.databinding.FragmentJoin03Binding;
import kr.co.core.responsepeople.databinding.FragmentJoin05Binding;
import kr.co.core.responsepeople.dialog.ProfileSimpleDlg;
import kr.co.core.responsepeople.receiver.AlarmReceiver;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class Join05Frag extends BaseFrag implements View.OnClickListener {
    FragmentJoin05Binding binding;
    Activity act;
    static final int PROFILE = 1001;

    float value = 0f;
    TextView selectedView;

    String age;
    String distance;
    String height;
    String edu;
    String body;
    String religion;
    String drink;
    String smoke;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_05, container, false);
        act = getActivity();

        setClickListener();
        setSeekBar();

        return binding.getRoot();
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

    private void setSeekBar() {
        binding.seekBarAge.setRange(20, 55);
        binding.seekBarAge.setProgress(20, 55);
        binding.seekBarAge.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                binding.ageStart.setText(String.valueOf((int) leftValue));
                binding.ageEnd.setText(String.valueOf((int) rightValue));
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
        binding.seekBarHeight.setProgress(120, 200);
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
        binding.seekBarDistance.setProgress(8);
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
                age = binding.ageStart.getText().toString() + "," + (binding.ageEnd.getText().toString().equalsIgnoreCase("55") ? "100" : binding.ageEnd.getText().toString()) ;
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
                            setAlarm();
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
        server.addParams("m_pref_result", "R");
        server.execute(true, false);




//        if (!StringUtil.isNull(edu) && !edu.equalsIgnoreCase("상관없음"))
//            server.addParams("m_edu", edu);
//        else
//            server.addParams("m_edu", "");
//        if (!StringUtil.isNull(body) && !body.equalsIgnoreCase("상관없음"))
//            server.addParams("m_body", body);
//        else
//            server.addParams("m_body", "");
//        if (!StringUtil.isNull(religion) && !religion.equalsIgnoreCase("상관없음"))
//            server.addParams("m_religion", religion);
//        else
//            server.addParams("m_religion", "");
//        if (!StringUtil.isNull(drink) && !drink.equalsIgnoreCase("상관없음"))
//            server.addParams("m_drink", drink);
//        else
//            server.addParams("m_drink", "");
//        if (!StringUtil.isNull(smoke) && !smoke.equalsIgnoreCase("상관없음"))
//            server.addParams("m_smoke", smoke);
//        else
//            server.addParams("m_smoke", "");
    }

    private void setAlarm() {
        /* initialize alarm service */
        Calendar mCalendar = Calendar.getInstance();

        /* set compare data */
        int LAST_DAY_OF_YEAR = mCalendar.getMaximum(Calendar.DAY_OF_YEAR);
        int NOW_DAY_OF_YEAR = mCalendar.get(Calendar.DAY_OF_YEAR);


        /* set calendar */
        Calendar calendar01 = setCalendar(NOW_DAY_OF_YEAR, LAST_DAY_OF_YEAR);

        /* set alarm manager */
        AlarmManager manager = (AlarmManager) act.getSystemService(Context.ALARM_SERVICE);

        /* set pending intent */
        PendingIntent pendingIntent01 = PendingIntent.getBroadcast(act, 0, new Intent(act, AlarmReceiver.class).putExtra("idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX)), PendingIntent.FLAG_UPDATE_CURRENT);

        /* register alarm (버전별로 따로) */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(StringUtil.TAG, "first");
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar01.getTimeInMillis(), pendingIntent01);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.e("TEST_HOME", "second");
            manager.setExact(AlarmManager.RTC_WAKEUP, calendar01.getTimeInMillis(), pendingIntent01);
        } else {
            Log.e("TEST_HOME", "third");
            manager.set(AlarmManager.RTC_WAKEUP, calendar01.getTimeInMillis(), pendingIntent01);
        }
    }

    private Calendar setCalendar(int NOW_DAY_OF_YEAR, int LAST_DAY_OF_YEAR) {
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, 5);

        // manager.set 할때 현재시간보다 이전 시간대면, 리시버가 바로 실행됨, 다음날로 지정해줘야 함 => ex) 세팅시간 8:00, 현재시간 9:20 이면, 다음날 8:00로 지정
//        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
//            Log.e(StringUtil.TAG, "현재시간보다 작음");
//
//            // 올해의 마지막 날일 경우
//            if (NOW_DAY_OF_YEAR == LAST_DAY_OF_YEAR) {
//                NOW_DAY_OF_YEAR = 1;
//            } else {
//                NOW_DAY_OF_YEAR++;
//            }
//            calendar.set(Calendar.DAY_OF_YEAR, NOW_DAY_OF_YEAR);
//        }

        return calendar;
    }
}
