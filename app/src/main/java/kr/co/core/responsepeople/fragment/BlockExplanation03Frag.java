package kr.co.core.responsepeople.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.BlockAct;
import kr.co.core.responsepeople.activity.BlockExplainAct;
import kr.co.core.responsepeople.activity.TermAct;
import kr.co.core.responsepeople.databinding.FragmentBlockExplanation01Binding;
import kr.co.core.responsepeople.databinding.FragmentBlockExplanation03Binding;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;

public class BlockExplanation03Frag extends BaseFrag {
    FragmentBlockExplanation03Binding binding;
    Activity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_block_explanation_03, container, false);
        act = getActivity();

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.ckConfirm.isChecked()) {
                    if (checkPermission()) {
                        showAlert();
                    } else {
                        requestPermission();
                    }
                } else {
                    Common.showToast(act, "위의 내용을 확인해주신 후, 확인표시에 체크 부탁드립니다");
                }
            }
        });

        binding.btnTermPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, TermAct.class).putExtra("type", TermAct.TYPE_PRIVATE));
            }
        });


        return binding.getRoot();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    act.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            ) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS
            }, 0);
        }
    }

    private void showAlert() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("지인 만나지 않기");
        alertDialog.setMessage("OK 버튼 클릭시 회원님의 연락처 정보를 가져옵니다. 동의하시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(act, BlockAct.class));
                        act.finish();
                        dialog.cancel();
                    }
                });
        // cancel
        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    act.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            ) {
                act.finish();
                Common.showToast(act, "다시 묻지 않음 을 선택한 경우, 설정 -> 애플리케이션(해당 앱) -> 앱 권한에서 승인 부탁드립니다");
            } else {
                showAlert();
            }
        }
    }
}
