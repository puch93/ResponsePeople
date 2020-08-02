package kr.co.core.responsepeople.util;

import android.content.Context;

public class MemberUtil {
    public static void setJoinProcess(Context ctx, String id, String pw) {
        AppPreference.setProfilePrefBool(ctx, AppPreference.AUTO_LOGIN, true);
        AppPreference.setProfilePref(ctx, AppPreference.PREF_ID, id);
        AppPreference.setProfilePref(ctx, AppPreference.PREF_PW, pw);
    }
}
