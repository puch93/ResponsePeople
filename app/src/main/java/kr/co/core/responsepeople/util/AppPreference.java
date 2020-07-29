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


    /* 가입시 사용 */
    public static final String STATE_IMAGE = "join_image"; // 프로필 사진 검수여부
    public static final String STATE_PREFER = "join_prefer"; // 선호 설정 여부
    public static final String STATE_REVIEWING = "join_reviewing"; // 평가중
    public static final String STATE_COMPLETE = "join_complete"; // 평가완료


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
