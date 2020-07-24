package kr.co.core.responsepeople.activity;

import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.EtcAdapter;
import kr.co.core.responsepeople.data.EtcData;
import kr.co.core.responsepeople.databinding.ActivityEtcIdealBinding;
import kr.co.core.responsepeople.util.LogUtil;

public class EtcIdealAct extends BaseAct {
    ActivityEtcIdealBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_etc_ideal, null);
        act = this;

        //TODO 추후 수정
        binding.title.setText("이상형");
        binding.titleSub.setText("이상형을");
        String[] etcs = getResources().getStringArray(R.array.array_ideal);

        ArrayList<EtcData> list = new ArrayList<>();
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        EtcAdapter adapter = new EtcAdapter(act, list);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);

        for (String etc : etcs) {
            list.add(new EtcData(etc, false));
        }

        adapter.setList(list);

        binding.btnClose.setOnClickListener(v -> {
            finish();
        });

        binding.btnComplete.setOnClickListener(v -> {
            LogUtil.logI(adapter.getSelectedList().toString());
        });
    }
}
