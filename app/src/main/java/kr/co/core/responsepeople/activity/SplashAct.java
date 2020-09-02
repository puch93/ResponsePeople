package kr.co.core.responsepeople.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
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

        // get device version
        try {
            device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // get fcm token
        getFcmToken();

        checkTimer();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkVersion();
            }
        }, 1500);
    }

    private void checkVersion() {
        ReqBasic server = new ReqBasic(this, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                final String res = resultData.getResult();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!StringUtil.isNull(res)) {
                                JSONObject jo = new JSONObject(res);

                                String[] version = StringUtil.getStr(jo, "MEMCODE").split("\\.");
                                String[] version_me = device_version.split("\\.");

                                for (int i = 0; i < 3; i++) {
                                    int tmp1 = Integer.parseInt(version[i]);
                                    int tmp2 = Integer.parseInt(version_me[i]);

                                    if (tmp2 < tmp1) {
                                        android.app.AlertDialog.Builder alertDialogBuilder =
                                                new android.app.AlertDialog.Builder(new ContextThemeWrapper(act, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert));
                                        alertDialogBuilder.setTitle("업데이트");
                                        alertDialogBuilder.setMessage("새로운 버전이 있습니다.")
                                                .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=kr.co.core.responsepeople"));
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.setCanceledOnTouchOutside(false);
                                        alertDialog.show();

                                        return;
                                    }
                                }

                                startProgram();
                            } else {
                                startProgram();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        server.setTag("Version Check");
        server.addParams("dbControl", NetUrls.VERSION);
        server.addParams("thisVer", NetUrls.VERSION);
        server.execute(true, false);
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
                            String m_birth = StringUtil.getStr(job, "m_birth");
                            String m_hp = StringUtil.getStr(job, "m_hp");

                            AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                            AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                            AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);
                            AppPreference.setProfilePref(act, AppPreference.PREF_AGE, StringUtil.calcAge(m_birth.substring(0, 4)));
                            AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, NetUrls.DOMAIN_ORIGIN + m_profile1);
                            AppPreference.setProfilePref(act, AppPreference.PREF_PHONE, m_hp);

                            startActivity(new Intent(act, MainAct.class));
                            finish();
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "message"));

                            if(StringUtil.getStr(jo, "message").equalsIgnoreCase("탈퇴한 회원입니다.")) {
                                Common.showToast(act, StringUtil.getStr(jo, "message"));
                                AppPreference.setProfilePrefBool(act, AppPreference.AUTO_LOGIN, false);
                                startActivity(new Intent(act, LoginAct.class));
                                finish();
                            }
                            if (jo.has("data")) {
                                JSONArray ja = jo.getJSONArray("data");
                                JSONObject job = ja.getJSONObject(0);
                                String type = StringUtil.getStr(jo, "type");
                                String m_idx = StringUtil.getStr(job, "m_idx");
                                String m_gender = StringUtil.getStr(job, "m_gender");
                                String m_id = StringUtil.getStr(job, "m_id");
                                String m_hp = StringUtil.getStr(job, "m_hp");
                                String m_birth = StringUtil.getStr(job, "m_birth");
                                String m_profile1 = StringUtil.getStr(job, "m_profile1");
                                String m_pass = StringUtil.getStr(job, "m_pass");
                                int cnt = StringUtil.getInt(jo, "cnt");

                                AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                                AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                                AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                                AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);
                                AppPreference.setProfilePref(act, AppPreference.PREF_PHONE, m_hp);
                                AppPreference.setProfilePref(act, AppPreference.PREF_AGE, StringUtil.calcAge(m_birth.substring(0, 4)));
                                AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, NetUrls.DOMAIN_ORIGIN + m_profile1);

                                if (!StringUtil.isNull(type)) {
                                    AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                    // 로그인 완료회원 아니면 자동로그인 처리 (로그인시 페이지 이동 고정시킴)
//                                    MemberUtil.setJoinProcess(act, StringUtil.getStr(job, "m_id"), StringUtil.getStr(job, "m_pass"));


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
                                            if(cnt >= 5) {
                                                startActivity(new Intent(act, EvaluationAfterAct.class));
                                            } else {
                                                startActivity(new Intent(act, EvaluationBeforeAct.class));
                                            }
                                            break;
                                        // 평가 완료 안누른 회원
                                        case "waiting":
                                            startActivity(new Intent(act, EvaluationAfterAct.class));
                                            break;

                                        default:
                                            AppPreference.setProfilePrefBool(act, AppPreference.AUTO_LOGIN, false);
                                            break;
                                    }

                                    finish();
                                } else {
                                    AppPreference.setProfilePrefBool(act, AppPreference.AUTO_LOGIN, false);
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
