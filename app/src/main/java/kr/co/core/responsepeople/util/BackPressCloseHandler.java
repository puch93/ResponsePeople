package kr.co.core.responsepeople.util;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0;
    private Toast toast;
    private Activity act;

    public BackPressCloseHandler(Activity activity) {
        this.act = activity;
    }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            doLogout();

            act.moveTaskToBack(true);
            act.finish();

            toast.cancel();
        }
    }

    private void doLogout() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            if (!AppPreference.getProfilePrefBool(act, AppPreference.AUTO_LOGIN)) {
                                AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, "-1");

                                NotificationManager notificationManager = (NotificationManager) act.getSystemService(Context.NOTIFICATION_SERVICE);
                                notificationManager.cancelAll();
                            }
                            act.finish();
                            act.finishAffinity();
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

    private void showGuide() {
        toast = Toast.makeText(act, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

}
