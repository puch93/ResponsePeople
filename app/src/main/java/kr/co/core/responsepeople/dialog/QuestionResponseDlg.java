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
import kr.co.core.responsepeople.adapter.Sheet03Adapter;
import kr.co.core.responsepeople.data.QuestionReceivedData;
import kr.co.core.responsepeople.data.SheetData;
import kr.co.core.responsepeople.databinding.DialogAnswerBinding;
import kr.co.core.responsepeople.databinding.DialogResponseBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class QuestionResponseDlg extends BaseAct implements View.OnClickListener {
    DialogResponseBinding binding;
    Activity act;

    ArrayList<SheetData> list_sheet = new ArrayList<>();

    String answer;
    Sheet03Adapter adapter;


    ArrayList<QuestionReceivedData> list = new ArrayList<>();
    QuestionReceivedData data;

    int currentPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_response, null);
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
        adapter = new Sheet03Adapter(act, list_sheet, new Sheet03Adapter.SelectListener() {
            @Override
            public void select(int position) {

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
            if(sheets[i].equalsIgnoreCase(data.getQh_answer())) {
                list_sheet.add(new SheetData(sheets[i], true));
            } else {
                list_sheet.add(new SheetData(sheets[i], false));
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setList(list_sheet);
            }
        });
    }

    private void nextProcess() {
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
                if (checkNextExist())
                    nextProcess();
                else {
                    finish();
                }
                break;

            case R.id.btn_close:
                finish();
                break;
        }
    }
}

