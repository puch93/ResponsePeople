package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.ChatListAdapter;
import kr.co.core.responsepeople.databinding.ActivityChatListBinding;

public class ChatListAct extends BaseAct {
    ActivityChatListBinding binding;
    Activity act;

    ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_list, null);
        act = this;

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);


    }
}