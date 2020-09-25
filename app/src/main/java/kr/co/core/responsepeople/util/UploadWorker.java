package kr.co.core.responsepeople.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.core.responsepeople.server.netUtil.NetUrls;

public class UploadWorker extends Worker {

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {
        // Do the work here--in this case, upload the images.
        Timer timer = new Timer();
        TimerTask adTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        GpsInfo gpsInfo = new GpsInfo(getApplicationContext());
                        if (gpsInfo.isGetLocation()) {
                            double latitude = gpsInfo.getLatitude();
                            double longitude = gpsInfo.getLongitude();
                            String lat = String.valueOf(latitude);
                            String lon = String.valueOf(longitude);

                            if (lat.equals("0.0")) lat = "35";
                            if (lon.equals("0.0")) lon = "128";

                            AppPreference.setProfilePref(getApplicationContext(), AppPreference.PREF_LAT, lat);
                            AppPreference.setProfilePref(getApplicationContext(), AppPreference.PREF_LON, lon);

                            doGpsUpload();
                            Log.d("TEST@@", "CHECK POINT0");
                        }
                    }
                }, 0);

            }
        };
        timer.schedule(adTask, 0, 10 * 1000);
        // Indicate whether the task finished successfully with the Result

        return Result.success();
    }

    public void doGpsUpload() {
        Log.d("TEST@@", "CHECK POINT START");
        final String Url = "https://qnating.adamstore.co.kr/lib/control.siso";
        final CHttpUrlConnection conn = new CHttpUrlConnection();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String result;
                Log.d("TEST@@", "CHECK POINT THREAD");
                HashMap<String, String> mParam = new HashMap<>();
                mParam.put("dbControl", NetUrls.GPS_UPLOAD);
                mParam.put("m_idx", AppPreference.getProfilePref(getApplicationContext(), AppPreference.PREF_MIDX));
                mParam.put("m_x", AppPreference.getProfilePref(getApplicationContext(), AppPreference.PREF_LAT));
                mParam.put("m_y", AppPreference.getProfilePref(getApplicationContext(), AppPreference.PREF_LON));
                result = conn.sendPost(Url, mParam);
                Log.d("TEST@@", "CHECK POINT1");

//                getApplicationContext().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Log.d("TEST@@", "CHECK POINT2");
//                            JSONObject jo = new JSONObject(result);
//                            String status = StringUtil.getStr(jo, "result");
//
//                            if (status.equalsIgnoreCase("Y")) {
////                                Category cData = mGson.fromJson(jo.toString(), Category.class);
////                                doGetSubCategory(cData, 0);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
            }
        }).start();
    }
}
