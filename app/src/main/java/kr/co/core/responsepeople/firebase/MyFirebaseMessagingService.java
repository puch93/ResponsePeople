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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.MainAct;
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
                        }
                        break;

                    case "image_fail":
                        if (Join04Frag.frag != null) {
                            LogUtil.logI("Join04Frag.frag != null");
                            ((Join04Frag) Join04Frag.frag).imageFailed();
                        }

                        break;
                }

        } else {
            if (StringUtil.isNull(type))
                LogUtil.logI(ctx.getClass().getSimpleName() + " --> type is null");
            if (StringUtil.isNull(m_idx))
                LogUtil.logI(ctx.getClass().getSimpleName() + " --> m_idx is null");
        }
    }
}
