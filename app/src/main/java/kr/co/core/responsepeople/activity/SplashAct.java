package kr.co.core.responsepeople.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivitySplashBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.inter.OnAfterConnection;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.GpsInfo;
import kr.co.core.responsepeople.util.StringUtil;

public class SplashAct extends BaseAct {
    ActivitySplashBinding binding;
    Activity act;

    private GpsInfo gpsInfo;
    private Timer timer = new Timer();
    private String fcm_token, device_version;
    boolean isReady = false;

    private static final int PERMISSION = 1000;
    private static final int NETWORK = 1001;
    private static final int GPS = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash, null);
        act = this;

        Glide.with(act)
                .load(R.raw.splash_gif_1440)
                .into(binding.splash);

        // get fcm token
        getFcmToken();

        checkTimer();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startProgram();
            }
        }, 1500);
    }

    //로딩중 텍스트 애니메이션
    public void checkTimer() {
        TimerTask adTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isReady && !StringUtil.isNull(AppPreference.getProfilePref(act, AppPreference.PREF_FCM))) {
                            isReady = false;
                            if(!AppPreference.getProfilePrefBool(act, AppPreference.AUTO_LOGIN)) {
                                startActivity(new Intent(act, LoginAct.class));
                                finish();
                            } else {
                                doLogin();
                            }
                            timer.cancel();
                        }
                    }
                }, 0);
            }
        };
        timer.schedule(adTask, 0, 1000);
    }


    private void getFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(StringUtil.TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        fcm_token = task.getResult().getToken();
                        AppPreference.setProfilePref(act, AppPreference.PREF_FCM, fcm_token);
                        Log.i(StringUtil.TAG, "fcm_token: " + fcm_token);
                    }
                });
    }

    private void startProgram() {
        if (!checkPermission()) {
            startActivityForResult(new Intent(act, PermissionAct.class), PERMISSION);
        } else {
            checkSetting();
        }
    }

    private void checkSetting() {
        checkNetwork(new Runnable() {
            @Override
            public void run() {
                gpsInfo = new GpsInfo(act);
                if (gpsInfo.isGetLocation()) {
                    double latitude = gpsInfo.getLatitude();
                    double longitude = gpsInfo.getLongitude();
                    String lat = String.valueOf(latitude);
                    String lon = String.valueOf(longitude);

                    if (lat.equals("0.0")) lat = "35";
                    if (lon.equals("0.0")) lon = "128";

                    AppPreference.setProfilePref(act, AppPreference.PREF_LAT, lat);
                    AppPreference.setProfilePref(act, AppPreference.PREF_LON, lon);

                    isReady = true;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gpsInfo.showSettingsAlert();
                        }
                    });
                }
            }
        });
    }

    //데이터 또는 WIFI 켜져 있는지 확인 / 안켜져있으면 데이터 설정창으로
    private void checkNetwork(final Runnable afterCheckAction) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
                if (capabilities != null) {
                    if (afterCheckAction != null) {
                        afterCheckAction.run();
                    }
                } else {
                    showNetworkAlert();
                }
            }
        } else {
            if (cm != null) {
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo != null) {
                    if (afterCheckAction != null) {
                        afterCheckAction.run();
                    }
                } else {
                    showNetworkAlert();
                }
            }
        }
    }

    //네트워크 연결 다이얼로그
    public void showNetworkAlert() {
        showAlert(act, "네트워크 사용유무", "인터넷이 연결되어 있지 않습니다. \n설정창으로 가시겠습니까?", new OnAlertAfter() {
            @Override
            public void onAfterOk() {
                Intent in = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                in.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(in, NETWORK);
            }

            @Override
            public void onAfterCancel() {
                finish();
            }
        });
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

            ) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(StringUtil.TAG, "resultCode: " + resultCode);

        if (resultCode != RESULT_OK && resultCode != RESULT_CANCELED)
            return;

        switch (requestCode) {
            case PERMISSION:
                if (resultCode == RESULT_CANCELED) {
                    finish();
                } else {
                    startProgram();
                }
                break;

            case GPS:
            case NETWORK:
                checkSetting();
                break;
        }
    }





    private void doLogin() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);
                            String m_idx = StringUtil.getStr(job, "m_idx");
                            String m_gender = StringUtil.getStr(job, "m_gender");
                            String m_id = StringUtil.getStr(job, "m_id");
                            String m_pass = StringUtil.getStr(job, "m_pass");
                            String m_profile1 = StringUtil.getStr(job, "m_profile1");

                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                            AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                            AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);
                            AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, NetUrls.DOMAIN_ORIGIN + m_profile1);

                            startActivity(new Intent(act, MainAct.class));
                            finish();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));

                            if (jo.has("data")) {
                                JSONArray ja = jo.getJSONArray("data");
                                JSONObject job = ja.getJSONObject(0);
                                String type = StringUtil.getStr(jo, "type");
                                String m_idx = StringUtil.getStr(job, "m_idx");
                                String m_gender = StringUtil.getStr(job, "m_gender");
                                String m_id = StringUtil.getStr(job, "m_id");
                                String m_pass = StringUtil.getStr(job, "m_pass");

                                AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                                AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                                AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                                AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);

                                if (!StringUtil.isNull(type)) {
                                    AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                    // 로그인 완료회원 아니면 자동로그인 처리 (로그인시 페이지 이동 고정시킴)
//                                    MemberUtil.setJoinProcess(act, StringUtil.getStr(job, "m_id"), StringUtil.getStr(job, "m_pw"));


                                    switch (type) {
                                        // 프로필 사진 검수중
                                        case "image":
                                            startActivity(new Intent(act, JoinAct.class).putExtra("type", "image"));
                                            break;
                                        // 프로필 재심사
                                        case "image_fail":
                                            startActivity(new Intent(act, JoinAct.class).putExtra("type", "image_fail"));
                                            break;
                                        // 선호 설정 안함
                                        case "prefer":
                                            startActivity(new Intent(act, JoinAct.class).putExtra("type", "prefer"));
                                            break;
                                        // 평가 중인 경우
                                        case "rating":
                                            startActivity(new Intent(act, EvaluationBeforeAct.class));
                                            break;
                                        // 평가 완료 안누른 회원
                                        case "waiting":
                                            startActivity(new Intent(act, EvaluationAfterAct.class));
                                            break;
                                    }


                                    //TODO 주석해제 해줘야함 나중에
                                    finish();
                                }
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

        server.setTag("Auto Login");
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
