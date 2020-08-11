package kr.co.core.responsepeople.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.BaseAct;
import kr.co.core.responsepeople.activity.QuestionManageAct;
import kr.co.core.responsepeople.adapter.Sheet02Adapter;
import kr.co.core.responsepeople.data.QuestionReceivedData;
import kr.co.core.responsepeople.data.SheetData;
import kr.co.core.responsepeople.databinding.DialogAnswerBinding;
import kr.co.core.responsepeople.databinding.DialogRegisterQuestion02Binding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class QuestionAnswerDlg extends BaseAct implements View.OnClickListener {
    DialogAnswerBinding binding;
    Activity act;

    ArrayList<SheetData> list_sheet = new ArrayList<>();

    String answer;
    Sheet02Adapter adapter;


    ArrayList<QuestionReceivedData> list = new ArrayList<>();
    QuestionReceivedData data;

    int currentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_answer, null);
        act = this;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        binding.btnClose.setOnClickListener(this);
        binding.btnComplete.setOnClickListener(this);

        list = (ArrayList<QuestionReceivedData>) getIntent().getSerializableExtra("list");
        data = list.get(currentPos);

        setLayout();

        setQuestion();
        setSheetList();

        if (checkNextExist()) {
            binding.btnText.setText("다음");
        } else {
            binding.btnText.setText("완료");
        }
    }

    private void setLayout() {
        // set sheet data
        adapter = new Sheet02Adapter(act, list_sheet, new Sheet02Adapter.SelectListener() {
            @Override
            public void select(int position) {
                answer = list_sheet.get(position).getData();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
    }

    private void setQuestion() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.question.setText(data.getQ_question());
            }
        });
    }

    private boolean checkNextExist() {
        if (list.size() - 1 > currentPos)
            return true;
        else
            return false;
    }

    private void setSheetList() {
        list_sheet = new ArrayList<>();

        String[] sheets = data.getQ_sheet().split(",");
        for (int i = 0; i < sheets.length; i++) {
            list_sheet.add(new SheetData(sheets[i], false));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(list_sheet);
            }
        });
    }

    private void nextProcess() {
        data.setAnswer(answer);
        list.set(currentPos, data);

        answer = "";

        ++currentPos;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (checkNextExist()) {
                    binding.btnText.setText("다음");
                } else {
                    binding.btnText.setText("완료");
                }
            }
        });

        data = list.get(currentPos);
        setQuestion();
        setSheetList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_complete:
                if (StringUtil.isNull(answer)) {
                    Common.showToast(act, "답변을 선택해주세요");
                } else {
                    if (checkNextExist())
                        nextProcess();
                    else {
                        data.setAnswer(answer);
                        list.set(currentPos, data);

                        LogUtil.logI(list.toString());

                        sendQuestion();
                    }
                }
                break;

            case R.id.btn_close:
                finish();
                break;
        }
    }


    private void sendQuestion() {
        for (int i = 0; i < list.size(); i++) {
            int finalI = i;
            ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
                @Override
                public void onAfter(int resultCode, HttpResult resultData) {
                    if (resultData.getResult() != null) {
                        try {
                            JSONObject jo = new JSONObject(resultData.getResult());

                            if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                                if(finalI == list.size() -1) {
                                    Common.showToast(act, StringUtil.getStr(jo, "message"));
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            } else {

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

            server.setTag("Answer One");
            server.addParams("dbControl", NetUrls.QUESTION_ANSWER);
            server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
            server.addParams("qh_idx", list.get(i).getQh_idx());
            server.addParams("qh_answer", list.get(i).getAnswer());
            server.execute(true, false);
        }
    }
}

