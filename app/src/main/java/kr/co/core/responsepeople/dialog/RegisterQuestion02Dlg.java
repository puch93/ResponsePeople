package kr.co.core.responsepeople.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.BaseAct;
import kr.co.core.responsepeople.activity.QuestionManageAct;
import kr.co.core.responsepeople.adapter.Sheet02Adapter;
import kr.co.core.responsepeople.adapter.SheetAdapter;
import kr.co.core.responsepeople.data.SheetData;
import kr.co.core.responsepeople.databinding.DialogRegisterQuestion01Binding;
import kr.co.core.responsepeople.databinding.DialogRegisterQuestion02Binding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class RegisterQuestion02Dlg extends BaseAct {
    DialogRegisterQuestion02Binding binding;
    Activity act;

    ArrayList<SheetData> list = new ArrayList<>();
    String question;

    String answer;
    Sheet02Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_register_question_02, null);
        act = this;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        question = getIntent().getStringExtra("question");
        binding.question.setText(question);

        ArrayList<String> list_received = (ArrayList<String>) getIntent().getSerializableExtra("sheet");

        for (int i = 0; i < list_received.size(); i++) {
            list.add(new SheetData(list_received.get(i), false));
        }

        adapter = new Sheet02Adapter(act, list, new Sheet02Adapter.SelectListener() {
            @Override
            public void select(int position) {
                answer = list.get(position).getData();
            }
        });


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);


        binding.btnClose.setOnClickListener(v -> {
            finish();
        });


        binding.btnComplete.setOnClickListener(v -> {
            if (StringUtil.isNull(answer)) {
                Common.showToast(act, "답변을 선택해주세요");
            } else {
                String sheet = null;
                for (int i = 0; i < list.size(); i++) {
                    if (i == 0) {
                        sheet = list.get(i).getData();
                    } else {
                        sheet = sheet + "," + list.get(i).getData();
                    }

                    if (list.get(i).isChecked()) {
                        answer = list.get(i).getData();
                    }
                }

                registerQuestion(sheet);

//                LogUtil.logI("sheet: " + sheet + ", answer: " + answer);
            }
        });
    }

    private void registerQuestion(String sheet) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            if (QuestionManageAct.act != null)
                                ((QuestionManageAct) QuestionManageAct.act).getQuestionList();
                            finish();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Register Question");
        server.addParams("dbControl", NetUrls.QUESTION_SAVE);
        server.addParams("q_question", binding.question.getText().toString());
        server.addParams("q_sheet", sheet);
        server.addParams("q_answer", answer);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}

