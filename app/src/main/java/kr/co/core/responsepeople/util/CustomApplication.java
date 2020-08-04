package kr.co.core.responsepeople.util;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class CustomApplication extends Application {
    private BillingEntireManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(StringUtil.TAG, "onCreate from application");

        initBilling();
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
