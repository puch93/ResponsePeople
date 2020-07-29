package kr.co.core.responsepeople.util;

import android.app.Activity;
import android.util.Log;

public class LogUtil {
    public static void logI(String contents) {
        Log.i("TEST_HOME", contents);
    }

    public static void logI(Activity act, String contents) {
        Log.i(act.getClass().getSimpleName(), contents);
    }

    public static void logLarge(String str) {
        if (str.length() > 1500) {
            Log.i("TEST_HOME", str.substring(0, 1500));
            logLarge(str.substring(1500));
        } else {
            Log.i("TEST_HOME", str); // continuation
        }
    }
}
