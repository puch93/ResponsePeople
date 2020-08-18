package kr.co.core.responsepeople.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.MemberBeforeAdapter;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.databinding.ActivityEvaluationAfterBinding;
import kr.co.core.responsepeople.databinding.ActivityEvaluationBeforeBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.RequestHttpURLConnection;
import kr.co.core.responsepeople.util.StringUtil;

public class EvaluationAfterAct extends BaseAct {
    ActivityEvaluationAfterBinding binding;
    Activity act;

    String avg_text;

    MemberBeforeAdapter adapter;
    GridLayoutManager manager;
    ArrayList<MemberData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_evaluation_after, null);
        act = this;

        setInfo();

        setLayout();

        manager = new GridLayoutManager(act, 3);
        adapter = new MemberBeforeAdapter(act, list);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setAdapter(adapter);

        getMyInfo();
    }

    private void getMyInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);

                            String m_age_p = StringUtil.getStr(job, "m_age_p");
                            String m_distance_p = StringUtil.getStr(job, "m_distance_p");
                            String m_height_p = StringUtil.getStr(job, "m_height_p");
                            String m_edu_p = StringUtil.getStr(job, "m_edu_p");
                            String m_body_p = StringUtil.getStr(job, "m_body_p");
                            String m_religion_p = StringUtil.getStr(job, "m_religion_p");
                            String m_drink_p = StringUtil.getStr(job, "m_drink_p");
                            String m_smoke_p = StringUtil.getStr(job, "m_smoke_p");

                            getPreferList(m_age_p, m_distance_p, m_height_p, m_edu_p, m_body_p, m_religion_p, m_drink_p, m_smoke_p);
                        } else {
                            LogUtil.logI(StringUtil.getStr(jo, "message"));
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

        server.setTag("My Info");
        server.addParams("dbControl", NetUrls.MY_INFO);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    private void getPreferList(String m_age_p, String m_distance_p, String m_height_p, String m_edu_p, String m_body_p, String m_religion_p, String m_smoke_p, String m_drink_p) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        LogUtil.logLarge(jo.toString());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            String total = StringUtil.getStr(jo, "total");

                            JSONArray ja = jo.getJSONArray("value");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String m_idx = StringUtil.getStr(job, "m_idx");
                                String m_nick = StringUtil.getStr(job, "m_nick");
                                String m_age = StringUtil.calcAge(StringUtil.getStr(job, "m_birth").substring(0, 4));
                                String m_job = StringUtil.getStr(job, "m_job");
                                String m_location = StringUtil.getStr(job, "m_location");
                                String m_salary = StringUtil.getStr(job, "m_salary");
                                String m_profile1 = StringUtil.getStr(job, "m_profile1");
                                boolean m_salary_result = StringUtil.getStr(job, "m_salary_result").equalsIgnoreCase("Y");
                                boolean m_profile_result = StringUtil.getStr(job, "m_profile1_result").equalsIgnoreCase("Y");
                                boolean f_idx = !StringUtil.isNull(StringUtil.getStr(job, "f_idx"));

                                list.add(new MemberData(m_idx, m_nick, m_age, m_job, m_location, m_salary, m_profile1, m_profile_result, f_idx, m_salary_result));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });
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

        server.setTag("Prefer List");
        server.addParams("dbControl", NetUrls.RECOMMEND_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        if (!StringUtil.isNull(m_age_p))
            server.addParams("m_age_p", m_age_p);
        if (!StringUtil.isNull(m_distance_p))
            server.addParams("m_distance_p", m_distance_p);
        if (!StringUtil.isNull(m_height_p))
            server.addParams("m_height_p", m_height_p);
        if (!StringUtil.isNull(m_edu_p))
            server.addParams("m_edu_p", m_edu_p);
        if (!StringUtil.isNull(m_body_p))
            server.addParams("m_body_p", m_body_p);
        if (!StringUtil.isNull(m_religion_p))
            server.addParams("m_religion_p", m_religion_p);
        if (!StringUtil.isNull(m_drink_p))
            server.addParams("m_drink_p", m_drink_p);
        if (!StringUtil.isNull(m_smoke_p))
            server.addParams("m_smoke_p", m_smoke_p);
        server.execute(true, false);
    }


    private void setLayout() {
        binding.btnComplete.setOnClickListener(v -> {
            doEvaluationComplete();
        });
    }


    private void doLogin() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);
                            String m_idx = StringUtil.getStr(job, "m_idx");
                            String m_gender = StringUtil.getStr(job, "m_gender");
                            String m_id = StringUtil.getStr(job, "m_id");
                            String m_pass = StringUtil.getStr(job, "m_pass");
                            String m_profile1 = StringUtil.getStr(job, "m_profile1");
                            String m_birth = StringUtil.getStr(job, "m_birth");
                            String m_hp = StringUtil.getStr(job, "m_hp");

                            AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                            AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                            AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);
                            AppPreference.setProfilePref(act, AppPreference.PREF_AGE, StringUtil.calcAge(m_birth.substring(0, 4)));
                            AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, NetUrls.DOMAIN_ORIGIN + m_profile1);
                            AppPreference.setProfilePref(act, AppPreference.PREF_PHONE, m_hp);

                            startActivity(new Intent(act, MainAct.class));
                            finish();
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "message"));

                            if (jo.has("data")) {
                                JSONArray ja = jo.getJSONArray("data");
                                JSONObject job = ja.getJSONObject(0);
                                String type = StringUtil.getStr(jo, "type");
                                String m_idx = StringUtil.getStr(job, "m_idx");
                                String m_gender = StringUtil.getStr(job, "m_gender");
                                String m_id = StringUtil.getStr(job, "m_id");
                                String m_hp = StringUtil.getStr(job, "m_hp");
                                String m_birth = StringUtil.getStr(job, "m_birth");
                                String m_profile1 = StringUtil.getStr(job, "m_profile1");
                                String m_pass = StringUtil.getStr(job, "m_pass");
                                int cnt = StringUtil.getInt(jo, "cnt");

                                AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                                AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                                AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                                AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);
                                AppPreference.setProfilePref(act, AppPreference.PREF_PHONE, m_hp);
                                AppPreference.setProfilePref(act, AppPreference.PREF_AGE, StringUtil.calcAge(m_birth.substring(0, 4)));
                                AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, NetUrls.DOMAIN_ORIGIN + m_profile1);

                                if (!StringUtil.isNull(type)) {
                                    AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                    // 로그인 완료회원 아니면 자동로그인 처리 (로그인시 페이지 이동 고정시킴)
//                                    MemberUtil.setJoinProcess(act, StringUtil.getStr(job, "m_id"), StringUtil.getStr(job, "m_pass"));


                                    switch (type) {
                                        // 프로필 사진 검수중
                                        case "image":
                                            startActivity(new Intent(act, JoinAct.class).putExtra("type", "image"));
                                            break;
                                        // 프로필 재심사
                                        case "image_fail":
                                            startActivity(new Intent(act, JoinAct.class).putExtra("type", "image_fail"));
                                            break;
                                        // 선호 설정 안함
                                        case "prefer":
                                            startActivity(new Intent(act, JoinAct.class).putExtra("type", "prefer"));
                                            break;
                                        // 평가 중인 경우
                                        case "rating":
                                            if (cnt >= 5) {
                                                startActivity(new Intent(act, EvaluationAfterAct.class));
                                            } else {
                                                startActivity(new Intent(act, EvaluationBeforeAct.class));
                                            }
                                            break;
                                        // 평가 완료 안누른 회원
                                        case "waiting":
                                            startActivity(new Intent(act, EvaluationAfterAct.class));
                                            break;

                                        default:
                                            AppPreference.setProfilePrefBool(act, AppPreference.AUTO_LOGIN, false);
                                            break;
                                    }

                                    finish();
                                } else {
                                    AppPreference.setProfilePrefBool(act, AppPreference.AUTO_LOGIN, false);
                                }
                            }
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

        server.setTag("Auto Login");
        server.addParams("dbControl", NetUrls.LOGIN);
        server.addParams("m_id", AppPreference.getProfilePref(act, AppPreference.PREF_ID));
        server.addParams("m_pass", AppPreference.getProfilePref(act, AppPreference.PREF_PW));
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
        server.addParams("imei", Common.getDeviceId(act));
        server.addParams("m_x", AppPreference.getProfilePref(act, AppPreference.PREF_LAT));
        server.addParams("m_y", AppPreference.getProfilePref(act, AppPreference.PREF_LON));
        server.execute(true, false);
    }


    private void doEvaluationComplete() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            if (LoginAct.act != null) {
                                LoginAct.act.finish();
                            }

                            if (!StringUtil.isNull(AppPreference.getProfilePref(act, AppPreference.PREF_ZZAL))) {
//                                String url = "https://api-test.zzal.funple.com/advertising/offerwall/complete/action";
                                String url = "https://api.zzal.funple.com/advertising/offerwall/complete/action";
                                ContentValues values = new ContentValues();
                                values.put("advertiseID", "b978797cfb5340a3a6e203407a190a16");
                                values.put("participateID", AppPreference.getProfilePref(act, AppPreference.PREF_ZZAL));
                                NetworkTask networkTask = new NetworkTask(url, values);
                                networkTask.execute();
                            }

                            doLogin();
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

        server.setTag("Evaluation Complete");
        server.addParams("dbControl", NetUrls.EVALUATION_COMPLETE);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("m_pref_mark", avg_text);
        server.execute(true, false);
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        @Override
        protected String doInBackground(Void... params) {

            Log.i(StringUtil.TAG, "doInBackground: ");
            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i(StringUtil.TAG, "onPostExecute: ");
            //doInBackground()로 부터 리턴된 값이 onPostExecute()의 매개변수로 넘어오므로 s를 출력한다.
            LogUtil.logI("ZZAL: " + s);
        }
    }


    private void setInfo() {
        try {
            JSONObject jo = new JSONObject(AppPreference.getProfilePref(act, AppPreference.PREF_JSON));
            avg_text = StringUtil.getStr(jo, "avg");
            int avg = StringUtil.getInt(jo, "avg");

            if (avg != 0) {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.scoreAverage.setText(avg_text);
                        binding.progressBar.setProgress(avg * 10);
                        binding.tierText.setText(Common.calTier(avg));
                        Glide.with(act).load(Common.calTierImage(avg)).into(binding.tierIcon);
                        binding.scoreOver.setText(Common.calTierMessage(avg));
                    }
                });
            } else {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        avg_text = "5";

                        binding.scoreAverage.setText(avg_text);
                        binding.progressBar.setProgress(50);
                        binding.tierText.setText(Common.calTier(5));
                        Glide.with(act).load(Common.calTierImage(5)).into(binding.tierIcon);
                        binding.scoreOver.setText(Common.calTierMessage(5));
                    }
                });
            }


            JSONArray ja = jo.getJSONArray("data");
            JSONObject job = ja.getJSONObject(0);

            String m_profile1 = NetUrls.DOMAIN_ORIGIN + StringUtil.getStr(job, "m_profile1");
            String m_nick = StringUtil.getStr(job, "m_nick");
            String m_location = StringUtil.getStr(job, "m_location");
            String m_job = StringUtil.getStr(job, "m_job");
            String m_salary = StringUtil.getStr(job, "m_salary");

            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(act).load(m_profile1).into(binding.profileImg);
                    binding.nick.setText(m_nick);
                    binding.location.setText(m_location);
                    binding.job.setText(m_job);
                    binding.salary.setText(m_salary);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}