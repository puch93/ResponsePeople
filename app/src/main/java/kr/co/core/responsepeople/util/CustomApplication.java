package kr.co.core.responsepeople.util;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class CustomApplication extends Application {
    private BillingEntireManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(StringUtil.TAG, "onCreate from application");

        initBilling();

        if(AppPreference.getProfilePrefBool(getApplicationContext(), AppPreference.PREF_FIRST_RUNNING))
            checkReferrer();
    }

    private void checkReferrer() {
        InstallReferrerClient referrerClient;

        referrerClient = InstallReferrerClient.newBuilder(this).build();
        referrerClient.startConnection(new InstallReferrerStateListener() {
            @Override
            public void onInstallReferrerSetupFinished(int responseCode) {
                switch (responseCode) {
                    case InstallReferrerClient.InstallReferrerResponse.OK:
                        // Connection established.
                        ReferrerDetails response = null;
                        try {
                            response = referrerClient.getInstallReferrer();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        String referrerUrl = response.getInstallReferrer();
                        long referrerClickTime = response.getReferrerClickTimestampSeconds();
                        long appInstallTime = response.getInstallBeginTimestampSeconds();
                        boolean instantExperienceLaunched = response.getGooglePlayInstantParam();

                        LogUtil.logI("referrerUrl: " + referrerUrl);
                        LogUtil.logI("referrerClickTime: " + referrerClickTime);
                        LogUtil.logI("appInstallTime: " + appInstallTime);
                        LogUtil.logI("instantExperienceLaunched: " + instantExperienceLaunched);

                        String[] referrers = referrerUrl.split("&");
                        for (String splits : referrers) {
                            try {
                                splits = URLDecoder.decode(splits, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            LogUtil.logI("Referrer (before): " + splits);

                            if(splits.contains("participateID")) {
                                String participateID = splits.replace("participateID=", "");
                                LogUtil.logI("Referrer (after): " + participateID);

                                AppPreference.setProfilePref(getApplicationContext(), AppPreference.PREF_ZZAL, participateID);
                                break;
                            }
                        }

                        AppPreference.setProfilePrefBool(getApplicationContext(), AppPreference.PREF_FIRST_RUNNING, false);
                        referrerClient.endConnection();
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED:
                        // API not available on the current Play Store app.
                        break;
                    case InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE:
                        // Connection couldn't be established.
                        break;
                }
            }

            @Override
            public void onInstallReferrerServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    /* billing */
    public BillingEntireManager getManagerObject() {
//        if (null != manager) {
//            return manager;
//        } else {
//            return reInitBilling();
//        }

        return reInitBilling();
    }

    public BillingEntireManager reInitBilling() {
        manager = new BillingEntireManager(getApplicationContext(), new BillingEntireManager.AfterBilling() {
            @Override
            public void sendMessage(final String message, final boolean isLong) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(isLong) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return manager;
    }

    private void initBilling() {
        manager = new BillingEntireManager(getApplicationContext(), new BillingEntireManager.AfterBilling() {
            @Override
            public void sendMessage(final String message, final boolean isLong) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(isLong) {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
