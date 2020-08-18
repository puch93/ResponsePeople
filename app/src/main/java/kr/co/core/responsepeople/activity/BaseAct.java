package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.WindowManager;

import kr.co.core.responsepeople.server.inter.OnAfterConnection;

public class BaseAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    public interface OnAlertAfter {
        void onAfterOk();
        void onAfterCancel();
    }

    public void showAlert(Activity act, String title, String contents, final OnAlertAfter onAlertAfter) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(contents);

        // ok
        alertDialog.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onAlertAfter.onAfterOk();
                        dialog.cancel();
                    }
                });
        // cancel
        alertDialog.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        onAlertAfter.onAfterCancel();
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
}
