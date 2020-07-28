package kr.co.core.responsepeople.util;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    /* profile string key */
    public static final String PREF_MIDX = "midx";
    public static final String PREF_FCM = "fcm";
    public static final String PREF_UNIQ = "uniq";

    public static final String PREF_ID = "id";
    public static final String PREF_PW = "pw";
    public static final String PREF_NICK = "nick";
    public static final String PREF_GENDER = "gender";

    public static final String PREF_LAT = "latitude";
    public static final String PREF_LON = "longitude";


    public static final String PREF_NOTICE_WOMEN_CHECK_DATE = "notice_women_date";
    public static final String PREF_NOTICE_MAN_CHECK_DATE = "notice_man_date";

    /* profile boolean key */
    public static final String PREF_SET_CHAT = "chat";
    public static final String PREF_SET_VIDEO = "video";
    public static final String PREF_SET_VOICE = "voice";
    public static final String PREF_SET_SOUND = "sound";
    public static final String PREF_SET_VIBRATE = "vibrate";
    public static final String PREF_SET_NONE = "none";

    public static final String PREF_SET_WITHDRAWAL = "withdrawal";


    public static final String PREF_NOTICE_WOMEN_CHECK = "notice_women";
    public static final String PREF_NOTICE_MAN_CHECK = "notice_man";


    public static final String CALL_CONNECT_TIME = "connect_time";
    public static final String CALL_DISCONNECT_TIME = "disconnect_time";


    // profile string
    public static void setProfilePref(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getProfilePref(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        return pref.getString(key, null);
    }


    // profile boolean
    public static void setProfilePrefBool(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static Boolean getProfilePrefBool(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        if (key.equalsIgnoreCase(PREF_SET_SOUND) || key.equalsIgnoreCase(PREF_SET_VIBRATE) || key.equalsIgnoreCase(PREF_SET_NONE))
            return pref.getBoolean(key, false);
        else
            return pref.getBoolean(key, true);
    }


    // profile string
    public static void setProfilePrefLong(Context context, String key, long value) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static long getProfilePrefLong(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        return pref.getLong(key, 0);
    }

}
