package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.NoticeAdapter;
import kr.co.core.responsepeople.data.NoticeData;
import kr.co.core.responsepeople.databinding.ActivityNoticeBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class NoticeAct extends AppCompatActivity {
    ActivityNoticeBinding binding;
    Activity act;

    LinearLayoutManager manager;
    ArrayList<NoticeData> list = new ArrayList<>();
    NoticeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notice, null);
        act = this;


        /* set recycler view */
        manager = new LinearLayoutManager(act);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);

        adapter = new NoticeAdapter(act, list);
        binding.recyclerView.setAdapter(adapter);

        getNoticeList();

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }


    private void getNoticeList() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);

                                String b_idx = StringUtil.getStr(job, "b_idx");
                                String b_title = StringUtil.getStr(job, "b_title");
                                String b_contents = StringUtil.getStr(job, "b_contents");
                                String b_user_idx = StringUtil.getStr(job, "b_user_idx");
                                String m_name = StringUtil.getStr(job, "m_name");
                                String b_regdate = StringUtil.getStr(job, "b_regdate");

                                list.add(new NoticeData(b_idx, b_title, b_regdate, b_contents));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });
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

        server.setTag("Notice List");
        server.addParams("dbControl", NetUrls.NOTICE);
        server.addParams("BCODE",  "1");
        server.execute(true, false);
    }
}
