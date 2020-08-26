package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.ChatListAdapter;
import kr.co.core.responsepeople.data.ChatListData;
import kr.co.core.responsepeople.databinding.ActivityChatListBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.ChatValues;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class ChatListAct extends BaseAct {
    ActivityChatListBinding binding;
    public static Activity act;

    ChatListAdapter adapter;
    ArrayList<ChatListData> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_list, null);
        act = this;

        adapter = new ChatListAdapter(act, list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getChatList();
    }

    public void getChatList() {
        list = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);

                                if (job.has("lastchats")) {
                                    JSONArray ja_chat = job.getJSONArray("lastchats");
                                    JSONObject job_chat = ja_chat.getJSONObject(0);

                                    // job_chat
                                    String room_idx = StringUtil.getStr(job_chat, "c_room_idx");
                                    String contents = Common.decodeEmoji(StringUtil.getStr(job_chat, "c_msg"));
                                    String send_date = StringUtil.getStr(job_chat, "c_regdate");
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    send_date = Common.formatImeString(format.parse(send_date), act);

                                    if (Common.isImage(contents)) {
                                        contents = "이미지";
                                    }


                                    // job
                                    String unread_count = StringUtil.getStr(job, "notreadcount");
                                    String y_nick = StringUtil.getStr(job, "m_nick");
                                    String y_idx = StringUtil.getStr(job, "m_idx");
                                    String y_age = StringUtil.calcAge(StringUtil.getStr(job, "m_birth").substring(0, 4));
                                    String y_profile_img = StringUtil.getStr(job, "m_before_profile1");

                                    list.add(new ChatListData(room_idx, contents, send_date, unread_count, y_idx, y_nick, y_age, y_profile_img, true));
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });

                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    list = new ArrayList<>();
                                    adapter.setList(list);
                                }
                            });
                        }

                    } catch (JSONException | ParseException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Chat List");
        server.addParams("dbControl", NetUrls.CHAT_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}