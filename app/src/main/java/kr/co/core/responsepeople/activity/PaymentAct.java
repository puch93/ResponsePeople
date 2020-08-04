package kr.co.core.responsepeople.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.databinding.ActivityPaymentBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.BillingEntireManager;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.CustomApplication;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class PaymentAct extends BaseAct implements View.OnClickListener {
    ActivityPaymentBinding binding;
    public static Activity act;
    BillingEntireManager manager;

    ArrayList<String> item_idx_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment, null);
        act = this;

        // billing manager 초기화
        CustomApplication application = (CustomApplication) getApplication();
        manager = application.getManagerObject();


        getItemInfo();
        setLayout();
    }

    private void setLayout() {
        binding.btnBack.setOnClickListener(this);
        binding.item01.setOnClickListener(this);
        binding.item02.setOnClickListener(this);
        binding.item03.setOnClickListener(this);
        binding.item04.setOnClickListener(this);
        binding.item05.setOnClickListener(this);
        binding.item06.setOnClickListener(this);

        binding.item01.setTag(R.string.pay_item_code, Common.ITEM_01_CODE);
        binding.item01.setTag(R.string.pay_item_order, 0);

        binding.item02.setTag(R.string.pay_item_code, Common.ITEM_02_CODE);
        binding.item02.setTag(R.string.pay_item_order, 1);

        binding.item03.setTag(R.string.pay_item_code, Common.ITEM_03_CODE);
        binding.item03.setTag(R.string.pay_item_order, 2);

        binding.item04.setTag(R.string.pay_item_code, Common.ITEM_04_CODE);
        binding.item04.setTag(R.string.pay_item_order, 3);

        binding.item05.setTag(R.string.pay_item_code, Common.ITEM_05_CODE);
        binding.item05.setTag(R.string.pay_item_order, 4);

        binding.item06.setTag(R.string.pay_item_code, Common.ITEM_06_CODE);
        binding.item06.setTag(R.string.pay_item_order, 5);
    }

    private void getItemInfo() {
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

                                String pc_idx = StringUtil.getStr(job, "pc_idx");
                                String pc_site = StringUtil.getStr(job, "pc_site");
                                String pc_name = StringUtil.getStr(job, "pc_name");
                                String pc_cnt = StringUtil.getStr(job, "pc_cnt");
                                String pc_day = StringUtil.getStr(job, "pc_day");
                                String pc_order = StringUtil.getStr(job, "pc_order");
                                String pc_subscription_is = StringUtil.getStr(job, "pc_subscription_is");
                                String pc_price_kr = StringUtil.getStr(job, "pc_price_kr");
                                String pc_price_cn = StringUtil.getStr(job, "pc_price_cn");
                                String pc_price_en = StringUtil.getStr(job, "pc_price_en");
                                String pc_price_jp = StringUtil.getStr(job, "pc_price_jp");

                                item_idx_list.add(pc_idx);
                            }

                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        server.setTag("Payment Item Info");
        server.addParams("dbControl", NetUrls.PAYMENT_ITEM);
        server.execute(true, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_01:
            case R.id.item_02:
            case R.id.item_03:
            case R.id.item_04:
            case R.id.item_05:
            case R.id.item_06:
                if (item_idx_list.size() == 0) {
                    Common.showToast(act, "상품리스트를 불러오고 있습니다. 잠시만 기다려주시기 바랍니다.");
                } else if (manager.getManager_state().equals("N")) {
                    Common.showToast(act, manager.getManager_state_message());
                } else if (manager.getInapp_state().equalsIgnoreCase("pending")) {
                    Common.showToast(act, "카드사 승인중인 결제가 있습니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.");
                } else {
                    LogUtil.logI("item_idx: " + item_idx_list.get((int) v.getTag(R.string.pay_item_order)) + ", item_code: " + (String) v.getTag(R.string.pay_item_code));
                    manager.purchase((String) v.getTag(R.string.pay_item_code), item_idx_list.get((int) v.getTag(R.string.pay_item_order)), act);
                }
                break;

            case R.id.btn_back:
                finish();
                break;
        }
    }
}