package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.LoginAct;
import kr.co.core.responsepeople.activity.ProfileDetailAct;
import kr.co.core.responsepeople.adapter.MemberAdapter;
import kr.co.core.responsepeople.data.MemberData;
import kr.co.core.responsepeople.databinding.FragmentNearBinding;
import kr.co.core.responsepeople.databinding.FragmentRecommendBinding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.GpsInfo;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class NearFrag extends BaseFrag implements View.OnClickListener {
    FragmentNearBinding binding;
    Activity act;

    MemberAdapter adapter;
    GridLayoutManager manager;
    ArrayList<MemberData> list = new ArrayList<>();
    private GpsInfo gpsInfo;

    private Timer timer = new Timer();
    int currentPos = -1;

    private boolean isScroll = false;
    private int page = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_near, container, false);
        act = getActivity();

        binding.refreshLayout.setEnabled(false);

        Glide.with(act)
                .load(AppPreference.getProfilePref(act, AppPreference.PREF_IMAGE))
                .transform(new CircleCrop())
                .into(binding.profileImg);


        manager = new GridLayoutManager(act, 2);
        adapter = new MemberAdapter(act, this, list, new MemberAdapter.CustomClickListener() {
            @Override
            public void likeClicked() {
                list = adapter.getList();
            }
        }, new MemberAdapter.CurrentPosListener() {
            @Override
            public void getCurrentIndex(int position) {
                currentPos = position;
            }
        });
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalCount = manager.getItemCount();
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                if (!isScroll) {
                    if (totalCount - 1 == lastItemPosition) {
                        ++page;
                        getNearList();
                    }
                }
            }
        });




        binding.btnSearch.setOnClickListener(view -> {
            gpsInfo = new GpsInfo(act);
            if (gpsInfo.isGetLocation()) {
                double latitude = gpsInfo.getLatitude();
                double longitude = gpsInfo.getLongitude();
                String lat = String.valueOf(latitude);
                String lon = String.valueOf(longitude);

                if (lat.equals("0.0")) lat = "35";
                if (lon.equals("0.0")) lon = "128";

                AppPreference.setProfilePref(act, AppPreference.PREF_LAT, lat);
                AppPreference.setProfilePref(act, AppPreference.PREF_LON, lon);

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.btnSearch.setVisibility(View.GONE);
                        binding.searchText.setVisibility(View.VISIBLE);
                        startTimer();
                    }
                });

                getNearList();
            } else {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSettingsAlert();
                    }
                });
            }
        });

        return binding.getRoot();
    }

    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("GPS 사용유무");
        alertDialog.setMessage("GPS가 연결되어 있지 않습니다. \n설정창으로 가시겠습니까?");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ((Activity)act).startActivityForResult(intent, 1002);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == ProfileDetailAct.TYPE_LIKE) {
                if(currentPos >= 0) {
                    MemberData memberData = list.get(currentPos);
                    memberData.setLike(data.getBooleanExtra("result", false));
                    list.set(currentPos, memberData);
                    adapter.setList(list);
                }
            }
        }
    }


    public void startTimer() {
        TimerTask adTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.searchText.setText("검색중.");
                    }
                }, 0);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.searchText.setText("검색중..");
                    }
                }, 400);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.searchText.setText("검색중...");
                    }
                }, 800);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.searchText.setText("검색중....");
                    }
                }, 1200);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.searchText.setText("검색중.");
                    }
                }, 1200);

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.searchText.setText("검색중..");
                        binding.searchArea.setVisibility(View.GONE);

                        binding.refreshLayout.setEnabled(true);
                        binding.refreshLayout.setOnRefreshListener(listener);
                        timer.cancel();
                    }
                }, 2000);

            }
        };
        timer.schedule(adTask, 0, 2000);
    }

    SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            list = new ArrayList<>();
            page = 1;
            getNearList();
        }
    };

    private void getNearList() {
        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        LogUtil.logLarge(jo.toString());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            String total = StringUtil.getStr(jo, "total");

                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String m_idx = StringUtil.getStr(job, "m_idx");
                                String m_nick = StringUtil.getStr(job, "m_nick");
                                String m_age = StringUtil.calcAge(StringUtil.getStr(job, "m_birth").substring(0, 4));
                                String m_job = StringUtil.getStr(job, "m_job");
                                String m_location = StringUtil.getStr(job, "m_location");
                                String m_salary = StringUtil.getStr(job, "m_salary");
                                String m_before_profile1 = StringUtil.getStr(job, "m_before_profile1");
                                boolean m_salary_result = StringUtil.getStr(job, "m_salary_result").equalsIgnoreCase("Y");
                                boolean m_profile_result = StringUtil.getStr(job, "m_profile1_result").equalsIgnoreCase("Y");
                                boolean f_idx = !StringUtil.isNull(StringUtil.getStr(job, "f_idx"));
                                list.add(new MemberData(m_idx, m_nick, m_age, m_job, m_location, m_salary, m_before_profile1, true, f_idx, m_salary_result));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.count.setText(total);
                                    adapter.setList(list);
                                }
                            });

                            isScroll = false;
                        } else {
                            if(page == 1) {
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.count.setText("0");
                                        adapter.setList(list);
                                    }
                                });
                            }
                            isScroll = true;
                        }

                    } catch (JSONException e) {
                        isScroll = false;
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    isScroll = false;
                    Common.showToastNetwork(act);
                }

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (binding.refreshLayout.isRefreshing()) {
                            binding.refreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        };
        server.setTag("Near List");
        server.addParams("dbControl", NetUrls.NEAR_LIST);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("m_x", AppPreference.getProfilePref(act, AppPreference.PREF_LAT));
        server.addParams("m_y", AppPreference.getProfilePref(act, AppPreference.PREF_LON));
        server.addParams("pagenum", String.valueOf(page));
        server.execute(true, false);
    }

    @Override
    public void onClick(View view) {

    }
}
