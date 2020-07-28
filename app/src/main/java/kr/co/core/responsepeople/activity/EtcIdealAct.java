package kr.co.core.responsepeople.activity;

import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
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
import kr.co.core.responsepeople.util.StringUtil;

public class EtcIdealAct extends BaseAct {
    ActivityEtcIdealBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_etc_ideal, null);
        act = this;

        String type = getIntent().getStringExtra("type");
        if (StringUtil.isNull(type))
            finish();

        String[] etcs = null;
        switch (type) {
            case "charm":
                binding.title.setText("매력포인트");
                binding.titleSub.setText("매력포인트를");
                etcs = getResources().getStringArray(R.array.array_charm);
                break;
            case "interest":
                binding.title.setText("관심사");
                binding.titleSub.setText("관심사를");
                etcs = getResources().getStringArray(R.array.array_interest);
                break;
            case "ideal":
                binding.title.setText("이상형");
                binding.titleSub.setText("이상형을");
                etcs = getResources().getStringArray(R.array.array_ideal);
                break;

        }
        if (etcs == null) {
            finish();
        }


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
            Intent intent = new Intent();
            intent.putExtra("type", type);
            intent.putExtra("list", adapter.getSelectedList());
            setResult(RESULT_OK, intent);
            finish();
        });
    }
}
