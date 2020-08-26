package kr.co.core.responsepeople.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.ChatAct;
import kr.co.core.responsepeople.activity.ChatListAct;
import kr.co.core.responsepeople.activity.EvaluationAfterAct;
import kr.co.core.responsepeople.activity.EvaluationBeforeAct;
import kr.co.core.responsepeople.activity.JoinAct;
import kr.co.core.responsepeople.activity.MainAct;
import kr.co.core.responsepeople.activity.PushAct;
import kr.co.core.responsepeople.fragment.BaseFrag;
import kr.co.core.responsepeople.fragment.Join04Frag;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private Context ctx;

    @Override
    public void onNewToken(String token) {
        Log.e(StringUtil.TAG_PUSH, "refreshed token: " + token);
        AppPreference.setProfilePref(getApplicationContext(), AppPreference.PREF_FCM, token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        ctx = getApplicationContext();

        Log.e(StringUtil.TAG_PUSH, "remoteMessage.getData: " + remoteMessage.getData());
        JSONObject jo = new JSONObject(remoteMessage.getData());
        String type = StringUtil.getStr(jo, "type");
        String m_idx = StringUtil.getStr(jo, "m_idx");


        if (!StringUtil.isNull(type) && !StringUtil.isNull(m_idx)) {
            switch (type) {
                case "image":
                    if (Join04Frag.frag != null) {
                        LogUtil.logI("Join04Frag.frag != null");
                        ((Join04Frag) Join04Frag.frag).imageConfirmed();

                        sendDefaultNotification("알림", "프로필 사진 검수가 완료되었습니다", 101);
                    }
                    break;

                case "image_fail":
                    if (Join04Frag.frag != null) {
                        LogUtil.logI("Join04Frag.frag != null");
                        doLogin();
                    }
                    break;


                case "chat":
                    // 채팅 알람 설정확인
                    if (AppPreference.getProfilePrefBool(ctx, AppPreference.PREF_SET_CHAT)) {
                        // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
                        if (!m_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                            if (ChatListAct.act != null) {
                                ((ChatListAct) ChatListAct.act).getChatList();
                            }

                            String room_idx = StringUtil.getStr(jo, "chat_idx");
                            if (ChatAct.real_act == null || !room_idx.equalsIgnoreCase(ChatAct.room_idx)) {
                                sendChattingNotification(jo);
                            }
                        }
                    } else {
                        LogUtil.logI("채팅 알림 비활성화");
                    }
                    break;

                case "follow":
                    if (AppPreference.getProfilePrefBool(ctx, AppPreference.PREF_SET_LIKE))
                        sendDefaultNotification("찜 알림", StringUtil.getStr(jo, "m_nick") + "님이 회원님을 찜 했습니다", 1);
                    else
                        LogUtil.logI("찜 알림 비활성화");
                    break;

                case "question":
                    if (AppPreference.getProfilePrefBool(ctx, AppPreference.PREF_SET_QUESTION))
                        sendDefaultNotification("질문지 알림", StringUtil.getStr(jo, "m_nick") + "님이 회원님에게 질문을 전송했습니다", 2);
                    else
                        LogUtil.logI("질문지 알림 비활성화");
                    break;


                case "preference":
                    if (EvaluationBeforeAct.act != null) {
                        ((EvaluationBeforeAct) EvaluationBeforeAct.act).doLogin();
                    }
                    break;
            }

        } else {
            if (!StringUtil.isNull(type) && type.equalsIgnoreCase("push")) {
                sendSitePUsh(jo);
            }

            if (StringUtil.isNull(type))
                LogUtil.logI(ctx.getClass().getSimpleName() + " --> type is null");
            if (StringUtil.isNull(m_idx))
                LogUtil.logI(ctx.getClass().getSimpleName() + " --> m_idx is null");
        }
    }


    private void sendDefaultNotification(String title, String message, int channelNum) {
        if (MainAct.act != null) {
            ((MainAct) MainAct.act).getQuestionCount(NetUrls.QUESTION_RECEIVED);
        }

        //매니저 설정
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //채널설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "응답남녀", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("응답남녀 알림설정");

            notificationManager.createNotificationChannel(channel);
        }

        //인텐트 설정
        Intent intent = null;
        intent = new Intent(ctx, PushAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //노티 설정
        Notification notification = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .build();

        //푸시 날리기
        notificationManager.notify(channelNum, notification);
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

                                if (!StringUtil.isNull(type)) {
                                    AppPreference.setProfilePref(ctx, AppPreference.PREF_JSON, jo.toString());
                                    ((Join04Frag) Join04Frag.frag).imageFailed();

                                    sendDefaultNotification("알림", "프로필 사진을 다시 등록해주세요", 101);
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

        server.setTag("Login");
        server.addParams("dbControl", NetUrls.LOGIN);
        server.addParams("m_id", AppPreference.getProfilePref(ctx, AppPreference.PREF_ID));
        server.addParams("m_pass", AppPreference.getProfilePref(ctx, AppPreference.PREF_PW));
        server.addParams("fcm", AppPreference.getProfilePref(ctx, AppPreference.PREF_FCM));
        server.addParams("imei", Common.getDeviceId(ctx));
        server.addParams("m_x", AppPreference.getProfilePref(ctx, AppPreference.PREF_LAT));
        server.addParams("m_y", AppPreference.getProfilePref(ctx, AppPreference.PREF_LON));
        server.execute(true, false);
    }


    private void sendSitePUsh(JSONObject jo) {
        String b_title = StringUtil.getStr(jo, "b_title");
        String type = StringUtil.getStr(jo, "type");
        String b_url = StringUtil.getStr(jo, "b_url");
        String b_contents = StringUtil.getStr(jo, "b_contents");


        //매니저 설정
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //채널설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "응답남녀", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("응답남녀 알림설정");

            notificationManager.createNotificationChannel(channel);
        }

        //인텐트 설정
        Intent intent = null;
        if (StringUtil.isNull(b_url)) {
            intent = new Intent(ctx, PushAct.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(b_url));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //노티 설정
        Notification notification = null;
        notification = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(b_title)
                .setContentText(b_contents)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .build();

        //푸시 날리기
        notificationManager.notify(10, notification);
    }


    private void sendChattingNotification(JSONObject jo) {
        String m_nick = StringUtil.getStr(jo, "m_nick");
        String msg = Common.decodeEmoji(StringUtil.getStr(jo, "msg"));
        String type = StringUtil.getStr(jo, "type");
        String m_idx = StringUtil.getStr(jo, "m_idx");
        String chat_idx = StringUtil.getStr(jo, "chat_idx");

        String title = m_nick + "님의 채팅";

        if (Common.isImage(msg)) {
            msg = "이미지";
        }

        //매니저 설정
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //채널설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "응답남녀", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("응답남녀 알림설정");

            notificationManager.createNotificationChannel(channel);
        }

        //인텐트 설정
        Intent intent = new Intent(ctx, ChatAct.class)
                .putExtra("room_idx", chat_idx);


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //노티 설정
        Notification notification = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .build();

        //푸시 날리기
        notificationManager.notify(0, notification);
    }
}
