package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.BlockAdapter;
import kr.co.core.responsepeople.data.BlockData;
import kr.co.core.responsepeople.databinding.ActivityBlockBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

public class BlockAct extends BaseAct {
    ActivityBlockBinding binding;
    Activity act;

    ArrayList<BlockData> list = new ArrayList<>();
    BlockAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_block, null);
        act = this;

        adapter = new BlockAdapter(act, list, new BlockAdapter.NumberClickListener() {
            @Override
            public void clicked() {
                list = adapter.getList();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);

        getContacts(AppPreference.getProfilePref(act, AppPreference.PREF_PHONE));

        binding.btnBack.setOnClickListener(v -> {
            finish();
        });

        getBlockList();
    }

    private void getContacts(final String hp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                list = new ArrayList<>();

                String total = "";

                // 1. Resolver 가져오기(데이터베이스 열어주기)
                ContentResolver resolver = getContentResolver();

                // 2. 전화번호가 저장되어 있는 테이블 주소값(Uri)을 가져오기
                Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                // 3. 테이블에 정의된 칼럼 가져오기
                String[] projection = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                        , ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        , ContactsContract.CommonDataKinds.Phone.NUMBER};

                // 4. ContentResolver로 쿼리를 날림 -> resolver 가 provider 에게 쿼리하겠다고 요청
                Cursor cursor = resolver.query(phoneUri, projection, null, null, ContactsContract.Data.RAW_CONTACT_ID + " ASC");

                // 5. 커서로 리턴된다. 반복문을 돌면서 cursor 에 담긴 데이터를 하나씩 추출
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        // 4.1 이름으로 인덱스를 찾아준다
                        int idIndex = cursor.getColumnIndex(projection[0]); // 이름을 넣어주면 그 칼럼을 가져와준다.
                        int nameIndex = cursor.getColumnIndex(projection[1]);
                        int numberIndex = cursor.getColumnIndex(projection[2]);

                        // 4.2 해당 index 를 사용해서 실제 값을 가져온다.
                        String id = cursor.getString(idIndex);
                        String name = cursor.getString(nameIndex);
                        String number = cursor.getString(numberIndex);

                        Log.e(StringUtil.TAG, "id: " + id);
                        Log.e(StringUtil.TAG, "name: " + name);
                        Log.e(StringUtil.TAG, "number: " + number);
                        Log.e(StringUtil.TAG, "--------------------------------------------");


                        number = number.replace(" ", "");
                        number = number.replace("-", "");
                        number = number.replace("//", "");

                        if (!hp.equalsIgnoreCase(number)) {
                            list.add(new BlockData(number, name, false));
                        }
                    }
                }
                // 데이터 계열은 반드시 닫아줘야 한다.
                cursor.close();

//                Log.e(StringUtil.TAG, "total: " + total);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.count.setText(String.valueOf(list.size()));
                    }
                });
                //실제
//                setKnowPeople(total);
                //테스트
//                setKnowPeople("01077475545");
            }
        }).start();


    }

    private void getBlockList() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("value");
                            ArrayList<String> nums = new ArrayList<>();
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                nums.add(StringUtil.getStr(job, "mb_hp"));
                            }

                            if (nums.size() != 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    for (int j = 0; j < nums.size(); j++) {
                                        if (list.get(i).getNumber().equalsIgnoreCase(nums.get(j))) {
                                            BlockData data = list.get(i);
                                            data.setSelected(true);
                                            list.set(i, data);
                                        }
                                    }
                                }
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });

                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });

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

        server.setTag("Block List");
        server.addParams("dbControl", NetUrls.BLOCK_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}
