package kr.co.core.responsepeople.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.BaseAct;
import kr.co.core.responsepeople.adapter.SheetAdapter;
import kr.co.core.responsepeople.databinding.DialogRegisterQuestion01Binding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class RegisterQuestion01Dlg extends BaseAct {
    DialogRegisterQuestion01Binding binding;
    Activity act;

    SheetAdapter adapter;
    ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_register_question_01, null);
        act = this;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);


        adapter = new SheetAdapter(act, list, new SheetAdapter.DeleteListener() {
            @Override
            public void onDelete() {
                list = adapter.getList();
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);


        binding.btnClose.setOnClickListener(v -> {
            finish();
        });

        binding.btnAddSheet.setOnClickListener(v -> {
            if (binding.sheet.length() == 0) {
                Common.showToast(act, "항목을 입력해주세요");
            } else if (list.size() > 4) {
                Common.showToast(act, "질문지는 최대 5개까지 등록 가능합니다");
            } else {
                list.add(binding.sheet.getText().toString());
                adapter.setList(list);
                binding.sheet.setText("");
            }
        });

        binding.btnComplete.setOnClickListener(v -> {
            if (binding.question.length() == 0) {
                Common.showToast(act, "질문을 입력해주세요");
            } else if (list.size() < 2) {
                Common.showToast(act, "정답 항목을 2개 이상 등록해주세요");
            } else {
                startActivity(new Intent(act, RegisterQuestion02Dlg.class)
                        .putExtra("question", binding.question.getText().toString())
                        .putExtra("sheet", list)
                );
                overridePendingTransition(R.anim.open, R.anim.close);
                finish();
            }
        });
    }

}

