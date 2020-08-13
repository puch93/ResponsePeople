package kr.co.core.responsepeople.receiver;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.EvaluationAfterAct;
import kr.co.core.responsepeople.activity.EvaluationBeforeAct;
import kr.co.core.responsepeople.activity.PushAct;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;
public class InstallReferrerReceiver extends BroadcastReceiver {
    private Context ctx;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TEST_HOME", "onReceive");
        ctx = context;

        String referrer = "";
        if(intent.getAction().equals("com.android.vending.INSTALL_REFERRER")) {
            Bundle bundle = intent.getExtras();
            referrer = bundle.getString("referrer");
            try {
                referrer = URLDecoder.decode(referrer, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            LogUtil.logI("Referrer (before): " + referrer);
            String participateID = referrer.replace("participateID=", "");
            LogUtil.logI("Referrer (after): " + participateID);
            AppPreference.setProfilePref(ctx, AppPreference.PREF_ZZAL, participateID);
        }
    }
}