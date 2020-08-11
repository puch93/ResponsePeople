package kr.co.core.responsepeople.receiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.EvaluationAfterAct;
import kr.co.core.responsepeople.activity.EvaluationBeforeAct;
import kr.co.core.responsepeople.activity.JoinAct;
import kr.co.core.responsepeople.activity.MainAct;
import kr.co.core.responsepeople.activity.PushAct;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;


/**
 * Intent 플래그
 * FLAG_ONE_SHOT : 한번만 사용하고 다음에 이 PendingIntent가 불려지면 Fail을 함
 * FLAG_NO_CREATE : PendingIntent를 생성하지 않음. PendingIntent가 실행중인것을 체크를 함
 * FLAG_CANCEL_CURRENT : 실행중인 PendingIntent가 있다면 기존 인텐트를 취소하고 새로만듬
 * FLAG_UPDATE_CURRENT : 실행중인 PendingIntent가 있다면  Extra Data만 교체함
 * <p>
 * AlarmType
 * RTC_WAKEUP : 대기모드에서도 알람이 작동함을 의미함
 * RTC : 대기모드에선 알람을 작동안함
 */

public class AlarmReceiver extends BroadcastReceiver {
    private Context ctx;
    public static final String TAG = "TEST_HOME";
    Intent intent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TEST_HOME", "onReceive");

        ctx = context;
        this.intent = intent;

        String idx = intent.getStringExtra("idx");
        if (!StringUtil.isNull(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX)) && !StringUtil.isNull(idx)) {
            if (idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                sendProcess();
            }
        }
    }

    private void sendProcess() {
        ComponentName comp = new ComponentName(ctx.getPackageName(),
                AlarmReceiver.class.getName());
        startWakefulService(ctx, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

        doLogin();
    }


    /* 푸시보내기 기본*/
    public void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        //If on Oreo then notification required a notification channel.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon);


        Intent intent = new Intent(ctx, PushAct.class);


        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Notification notification = builder.build();

        notification.flags = notification.flags | notification.FLAG_AUTO_CANCEL;

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(pendingIntent);
        notification = builder.build();
        notificationManager.notify(1, notification);
    }


    private void doLogin() {
        ReqBasic server = new ReqBasic(ctx, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                        } else {
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

                                AppPreference.setProfilePref(ctx, AppPreference.PREF_JSON, jo.toString());
                                AppPreference.setProfilePref(ctx, AppPreference.PREF_MIDX, m_idx);
                                AppPreference.setProfilePref(ctx, AppPreference.PREF_GENDER, m_gender);
                                AppPreference.setProfilePref(ctx, AppPreference.PREF_ID, m_id);
                                AppPreference.setProfilePref(ctx, AppPreference.PREF_PW, m_pass);
                                AppPreference.setProfilePref(ctx, AppPreference.PREF_PHONE, m_hp);
                                AppPreference.setProfilePref(ctx, AppPreference.PREF_AGE, StringUtil.calcAge(m_birth.substring(0, 4)));
                                AppPreference.setProfilePref(ctx, AppPreference.PREF_IMAGE, NetUrls.DOMAIN_ORIGIN + m_profile1);
                                AppPreference.setProfilePrefBool(ctx, AppPreference.AUTO_LOGIN, true);

                                if (!StringUtil.isNull(type)) {
                                    AppPreference.setProfilePref(ctx, AppPreference.PREF_JSON, jo.toString());
                                    // 로그인 완료회원 아니면 자동로그인 처리 (로그인시 페이지 이동 고정시킴)
//                                    MemberUtil.setJoinProcess(ctx, StringUtil.getStr(job, "m_id"), StringUtil.getStr(job, "m_pass"));

                                    sendNotification("평가완료", "회원님의 평가가 완료되었습니다");

                                    if (EvaluationBeforeAct.real_act != null) {
                                        EvaluationBeforeAct.real_act.finish();
                                    }
                                    ctx.startActivity(new Intent(ctx, EvaluationAfterAct.class));
                                }
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        server.setTag("Auto Login");
        server.addParams("dbControl", NetUrls.LOGIN);
        server.addParams("m_id", AppPreference.getProfilePref(ctx, AppPreference.PREF_ID));
        server.addParams("m_pass", AppPreference.getProfilePref(ctx, AppPreference.PREF_PW));
        server.addParams("fcm", AppPreference.getProfilePref(ctx, AppPreference.PREF_FCM));
        server.addParams("imei", Common.getDeviceId(ctx));
        server.addParams("m_x", AppPreference.getProfilePref(ctx, AppPreference.PREF_LAT));
        server.addParams("m_y", AppPreference.getProfilePref(ctx, AppPreference.PREF_LON));
        server.execute(true, false);
    }
}
