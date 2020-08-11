package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityCustomerBinding;

public class CustomerAct extends BaseAct implements View.OnClickListener {
    ActivityCustomerBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_customer, null);
        act = this;

        binding.btnBack.setOnClickListener(this);
        binding.btnNotice.setOnClickListener(this);
        binding.btnQna.setOnClickListener(this);
        binding.btnTermUse.setOnClickListener(this);
        binding.btnTermGps.setOnClickListener(this);
        binding.btnTermPrivate.setOnClickListener(this);

        binding.btnTermUse.setTag(TermAct.TYPE_USE);
        binding.btnTermGps.setTag(TermAct.TYPE_GPS);
        binding.btnTermPrivate.setTag(TermAct.TYPE_PRIVATE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_notice:
                startActivity(new Intent(act, NoticeAct.class));
                break;
            case R.id.btn_qna:
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("text/email");
                email.setPackage("com.google.android.gm");
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"facetalk2020@gmail.com"});
                startActivity(email);
                break;


            case R.id.btn_term_use:
            case R.id.btn_term_gps:
            case R.id.btn_term_private:
                startActivity(new Intent(act, TermAct.class).putExtra("type", (String) v.getTag()));
                break;
        }
    }
}
