package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.EvaluationAfterAct;
import kr.co.core.responsepeople.activity.EvaluationBeforeAct;
import kr.co.core.responsepeople.activity.JoinAct;
import kr.co.core.responsepeople.activity.LoginAct;
import kr.co.core.responsepeople.activity.MainAct;
import kr.co.core.responsepeople.activity.SalaryAct;
import kr.co.core.responsepeople.adapter.EtcAdapter;
import kr.co.core.responsepeople.adapter.EtcProfileAdapter;
import kr.co.core.responsepeople.adapter.ImageAdapter;
import kr.co.core.responsepeople.data.EtcData;
import kr.co.core.responsepeople.data.ImageEditData;
import kr.co.core.responsepeople.databinding.FragmentJoin03Binding;
import kr.co.core.responsepeople.databinding.FragmentJoin04Binding;
import kr.co.core.responsepeople.dialog.ProfileBirthDlg;
import kr.co.core.responsepeople.dialog.ProfileSimpleDlg;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.MemberUtil;
import kr.co.core.responsepeople.util.OnPhotoAfterAction;
import kr.co.core.responsepeople.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class Join04Frag extends BaseFrag implements View.OnClickListener {
    FragmentJoin04Binding binding;
    Activity act;
    public static Fragment frag;

    // image
    private ArrayList<ImageEditData> images = new ArrayList<>();
    private ImageAdapter adapter;
    private Uri photoUri;
    private String mImgFilePath;
    private File salary_file;

    // etc
    private ArrayList<EtcData> list_charm = new ArrayList<>();
    private ArrayList<EtcData> list_interest = new ArrayList<>();
    private ArrayList<EtcData> list_ideal = new ArrayList<>();
    private EtcProfileAdapter adapter_charm;
    private EtcProfileAdapter adapter_interest;
    private EtcProfileAdapter adapter_ideal;


    TextView selectedView;
    static final int PROFILE = 1001;
    static final int BIRTH = 1002;
    static final int SALARY = 1003;
    static final int ETC = 1004;

    public static final Pattern VALID_NICK = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-힣]+$"); // 한글, 숫자만 허용
    Matcher matcher_nick;

    private AppCompatDialog progressDialog;

    private String fromType = "";


    public void setFromType(String fromType) {
        this.fromType = fromType;
    }

    public void imageConfirmed() {
        LogUtil.logI("imageConfirmed called");
        setProgressDialogDismiss();

        doLogin();
    }

    public void imageFailed() {
        LogUtil.logI("imageFailed called");
        setProgressDialogDismiss();

        Common.showToast(act, "이미지를 재등록해주세요");

        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.nestedScrollView.scrollTo(0, 0);
                binding.imageFailText.setVisibility(View.VISIBLE);
                binding.btnNext.setOnClickListener(view -> {
                    // 프로필 사진 재검수 요청
                    if (images.size() < 4) {
                        Common.showToast(act, "이미지를 3장이상 등록해주세요");
                    } else {
                        boolean isOk = true;
                        for (int i = 1; i < images.size(); i++) {
                            if (!StringUtil.isNull(images.get(i).getImageState()) &&
                                    (images.get(i).getImageState().equalsIgnoreCase("N") || images.get(i).getImageState().equalsIgnoreCase("F"))) {
                                isOk = false;
                                break;
                            }
                        }

                        if (isOk) {
                            doRequestCheck();
                        } else {
                            Common.showToast(act, "불합격된 사진이 있습니다. 사진을 삭제후 다른사진으로 등록해주세요.");
                        }
                    }
                });

                images = new ArrayList<>();
                images.add(null);

                binding.nick.setClickable(false);
                binding.nick.setEnabled(false);
                binding.intro.setClickable(false);
                binding.intro.setEnabled(false);

                binding.location.setClickable(false);
                binding.birth.setClickable(false);
                binding.height.setClickable(false);
                binding.body.setClickable(false);
                binding.edu.setClickable(false);
                binding.job.setClickable(false);
                binding.drink.setClickable(false);
                binding.smoke.setClickable(false);
                binding.religion.setClickable(false);
                binding.salary.setClickable(false);

                adapter_charm.setClickable(false);
                adapter_interest.setClickable(false);
                adapter_ideal.setClickable(false);

                binding.btnNextText.setText("재검수 요청하기");

                binding.checkImgNick.setSelected(true);
                binding.checkImgLocation.setSelected(true);
                binding.checkImgBirth.setSelected(true);
                binding.checkImgHeight.setSelected(true);
                binding.checkImgBody.setSelected(true);
                binding.checkImgEdu.setSelected(true);
                binding.checkImgJob.setSelected(true);
                binding.checkImgDrink.setSelected(true);
                binding.checkImgSmoke.setSelected(true);
                binding.checkImgReligion.setSelected(true);
                binding.checkImgSalary.setSelected(true);
            }
        });


        setFromLoginLayout();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_04, container, false);
        act = getActivity();
        frag = this;

        progressDialog = new AppCompatDialog(act);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.dialog_state_image);


        setLayout();
        setRecyclerView();

        if (StringUtil.isNull(fromType)) {
            setClickListener();
            setTextWatcher();

            binding.btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (StringUtil.isNull(fromType)) {
                        matcher_nick = VALID_NICK.matcher(binding.nick.getText().toString());

                        if (images.size() < 4) {
                            Common.showToast(act, "이미지를 3장이상 등록해주세요");
                        } else if (binding.nick.length() == 0 || !matcher_nick.matches()) {
                            Common.showToast(act, "닉네임을 확인해주세요");
                        } else if (binding.location.length() == 0) {
                            Common.showToast(act, "지역을 선택해주세요");
                        } else if (binding.birth.length() == 0) {
                            Common.showToast(act, "생년월일을 선택해주세요");
                        } else if (binding.height.length() == 0) {
                            Common.showToast(act, "키를 선택해주세요");
                        } else if (binding.body.length() == 0) {
                            Common.showToast(act, "체형을 선택해주세요");
                        } else if (binding.edu.length() == 0) {
                            Common.showToast(act, "학력을 선택해주세요");
                        } else if (binding.job.length() == 0) {
                            Common.showToast(act, "직업을 선택해주세요");
                        } else if (binding.drink.length() == 0) {
                            Common.showToast(act, "음주여부를 선택해주세요");
                        } else if (binding.smoke.length() == 0) {
                            Common.showToast(act, "흡연여부를 선택해주세요");
                        } else if (binding.religion.length() == 0) {
                            Common.showToast(act, "종교를 선택해주세요");
                        } else if (binding.salary.length() == 0) {
                            Common.showToast(act, "연봉을 선택해주세요");
                        } else if (binding.intro.length() == 0) {
                            Common.showToast(act, "소개말을 입력해주세요");
                        } else if (list_charm.size() < 2) {
                            Common.showToast(act, "매력포인트를 선택해주세요");
                        } else if (list_interest.size() < 2) {
                            Common.showToast(act, "관심사를 선택해주세요");
                        } else if (list_ideal.size() < 2) {
                            Common.showToast(act, "이상형을 선택해주세요");
                        } else {
                            nextProcess();
                        }
                    }
                }
            });
        } else if (fromType.equalsIgnoreCase("image")) {
            setProgressDialogShow();
            setFromLoginLayout();
        } else if (fromType.equalsIgnoreCase("image_fail")) {
            imageFailed();
        }

        return binding.getRoot();
    }

    private void setLayout() {
        binding.location.setTag(R.string.tag01, binding.checkImgLocation);
        binding.birth.setTag(R.string.tag01, binding.checkImgBirth);
        binding.height.setTag(R.string.tag01, binding.checkImgHeight);
        binding.body.setTag(R.string.tag01, binding.checkImgBody);
        binding.edu.setTag(R.string.tag01, binding.checkImgEdu);
        binding.job.setTag(R.string.tag01, binding.checkImgJob);
        binding.drink.setTag(R.string.tag01, binding.checkImgDrink);
        binding.smoke.setTag(R.string.tag01, binding.checkImgSmoke);
        binding.religion.setTag(R.string.tag01, binding.checkImgReligion);
        binding.salary.setTag(R.string.tag01, binding.checkImgSalary);

        binding.nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.nick.length() > 0) {
                    binding.checkImgNick.setSelected(true);
                } else {
                    binding.checkImgNick.setSelected(false);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        frag = null;
    }


    // 재검수요청
    private void doRequestCheck() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            setProgressDialogShow();
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
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

        server.setTag("Request ReCheck");
        server.addParams("dbControl", NetUrls.REQUEST_CHECK);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));

        new Thread() {
            @Override
            public void run() {
                super.run();

                // 파일
                ArrayList<ImageEditData> list_image = adapter.getList();
                Log.i(StringUtil.TAG, "doRequestCheck: " + list_image);
                for (int i = 1; i < list_image.size(); i++) {
                    File file = null;

                    if (list_image.get(i).getImageUrl().contains("http")) {
                        file = downloadImage(list_image.get(i).getImageUrl());
                        Log.i(StringUtil.TAG, "file name" + i + ": " + file.getName());
                    } else {
                        file = new File(list_image.get(i).getImageUrl());
                    }

                    server.addFileParams("m_profile" + i, file);
                    server.addParams("m_profile" + i + "ck", "Y");
                }
                server.execute(true, false);
            }
        }.start();
    }

    public File downloadImage(String imgUrl) {
        Bitmap img = null;
        File f = null;
        Log.e(StringUtil.TAG, "imgUrl: " + imgUrl);

        try {
            f = createImageFile();
            URL url = new URL(imgUrl);
            URLConnection conn = url.openConnection();

            int nSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
            img = BitmapFactory.decodeStream(bis);

            bis.close();

            FileOutputStream out = new FileOutputStream(f);
            img.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            img.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return f;
    }

    private File createImageFile() throws IOException {
//        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
//        String imageFileName = "thefiven" + timeStamp;
        String imageFileName = String.valueOf(System.currentTimeMillis());

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/RESPONSEPEOPLE");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return File.createTempFile(imageFileName, ".png", storageDir);
    }


    private void setProgressDialogShow() {
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    private void setProgressDialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void setFromLoginLayout() {
        try {
            JSONObject jo = new JSONObject(AppPreference.getProfilePref(act, AppPreference.PREF_JSON));
            JSONArray ja = jo.getJSONArray("data");
            JSONObject job = ja.getJSONObject(0);

//            if (fromType.equalsIgnoreCase("image")) {
//                for (int i = 1; i < 7; i++) {
//                    if (!StringUtil.isNull(StringUtil.getStr(job, "m_profile" + i))) {
//                        images.add(new ImageEditData(NetUrls.DOMAIN_ORIGIN + StringUtil.getStr(job, "m_profile" + i), StringUtil.getStr(job, "m_profile" + i + "_result")));
//                    } else {
//                        break;
//                    }
//                }
//            }
            for (int i = 1; i < 7; i++) {
                if (!StringUtil.isNull(StringUtil.getStr(job, "m_profile" + i))) {
                    images.add(new ImageEditData(NetUrls.DOMAIN_ORIGIN + StringUtil.getStr(job, "m_profile" + i), StringUtil.getStr(job, "m_profile" + i + "_result")));
                } else {
                    break;
                }
            }

            String m_nick = StringUtil.getStr(job, "m_nick");
            String m_location = StringUtil.getStr(job, "m_location");
            String m_birth = StringUtil.getStr(job, "m_birth");
            String m_height = StringUtil.getStr(job, "m_height");
            String m_body = StringUtil.getStr(job, "m_body");
            String m_edu = StringUtil.getStr(job, "m_edu");
            String m_job = StringUtil.getStr(job, "m_job");
            String m_drink = StringUtil.getStr(job, "m_drink");
            String m_smoke = StringUtil.getStr(job, "m_smoke");
            String m_religion = StringUtil.getStr(job, "m_religion");
            String m_salary = StringUtil.getStr(job, "m_salary");
            String m_intro = StringUtil.getStr(job, "m_intro");

            list_charm = new ArrayList<>();
            for (String contents : StringUtil.getStr(job, "m_charm").split(",")) {
                list_charm.add(new EtcData(contents, false));
            }

            list_interest = new ArrayList<>();
            for (String contents : StringUtil.getStr(job, "m_interest").split(",")) {
                list_interest.add(new EtcData(contents, false));
            }

            list_ideal = new ArrayList<>();
            for (String contents : StringUtil.getStr(job, "m_ideal").split(",")) {
                list_ideal.add(new EtcData(contents, false));
            }

            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    if (fromType.equalsIgnoreCase("image"))
//                        adapter.setList(images);

                    adapter.setList(images);

                    binding.nick.setText(m_nick);
                    binding.location.setText(m_location);
                    binding.birth.setText(m_birth);
                    binding.height.setText(m_height);
                    binding.body.setText(m_body);
                    binding.edu.setText(m_edu);
                    binding.job.setText(m_job);
                    binding.drink.setText(m_drink);
                    binding.smoke.setText(m_smoke);
                    binding.religion.setText(m_religion);
                    binding.salary.setText(m_salary);
                    binding.intro.setText(m_intro);

                    adapter_charm.setList(list_charm);
                    adapter_interest.setList(list_interest);
                    adapter_ideal.setList(list_ideal);

                    // 재검수 요청일때
                    if (images.size() < 4) {
                        binding.btnNext.setSelected(false);
                    } else {
                        binding.btnNext.setSelected(true);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void doJoin() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            setProgressDialogShow();
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);

                            MemberUtil.setJoinProcess(act, StringUtil.getStr(job, "m_id"), StringUtil.getStr(job, "m_pass"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, StringUtil.getStr(job, "m_idx"));

                            if (LoginAct.act != null) {
                                LoginAct.act.finish();
                            }
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

        server.setTag("Join");
        server.addParams("dbControl", NetUrls.JOIN);
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
        server.addParams("imei", Common.getDeviceId(act));

        server.addParams("m_id", JoinAct.joinData.getId());
        server.addParams("m_pass", JoinAct.joinData.getPw());
        server.addParams("m_nick", JoinAct.joinData.getNick());
        server.addParams("m_gender", JoinAct.joinData.getGender());
        server.addParams("m_hp", JoinAct.joinData.getHp());
        server.addParams("m_birth", JoinAct.joinData.getBirth());
        server.addParams("m_location", JoinAct.joinData.getLocation());
        server.addParams("m_height", JoinAct.joinData.getHeight());
        server.addParams("m_edu", JoinAct.joinData.getEdu());
        server.addParams("m_body", JoinAct.joinData.getBody());
        server.addParams("m_job", JoinAct.joinData.getJob());
        server.addParams("m_religion", JoinAct.joinData.getReligion());
        server.addParams("m_drink", JoinAct.joinData.getDrink());
        server.addParams("m_smoke", JoinAct.joinData.getSmoke());
        server.addParams("m_salary", JoinAct.joinData.getSalary());
        server.addParams("m_charm", JoinAct.joinData.getCharm());
        server.addParams("m_ideal", JoinAct.joinData.getIdeal());
        server.addParams("m_interest", JoinAct.joinData.getInterest());
        server.addParams("m_intro", JoinAct.joinData.getIntro());

        // 파일
        for (int i = 1; i < JoinAct.joinData.getImages().size(); i++) {
            String filePath = JoinAct.joinData.getImages().get(i);
            File file = new File(filePath);
            server.addFileParams("m_profile" + i, file);
        }
        if (null != JoinAct.joinData.getSalary_file()) {
            server.addFileParams("m_salary_file", JoinAct.joinData.getSalary_file());
        }
        server.execute(true, false);
    }


    private void setTextWatcher() {
        binding.nick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (checkAllProcess()) {
                    binding.btnNext.setSelected(true);
                } else {
                    binding.btnNext.setSelected(false);
                }
            }
        });

        binding.intro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (checkAllProcess()) {
                    binding.btnNext.setSelected(true);
                } else {
                    binding.btnNext.setSelected(false);
                }
            }
        });

    }


    private boolean checkAllProcess() {
        matcher_nick = VALID_NICK.matcher(binding.nick.getText().toString());

        if (images.size() < 4) {
            return false;
        } else if (binding.nick.length() == 0 || !matcher_nick.matches()) {
            return false;
        } else if (binding.location.length() == 0) {
            return false;
        } else if (binding.birth.length() == 0) {
            return false;
        } else if (binding.height.length() == 0) {
            return false;
        } else if (binding.body.length() == 0) {
            return false;
        } else if (binding.edu.length() == 0) {
            return false;
        } else if (binding.job.length() == 0) {
            return false;
        } else if (binding.drink.length() == 0) {
            return false;
        } else if (binding.smoke.length() == 0) {
            return false;
        } else if (binding.religion.length() == 0) {
            return false;
        } else if (binding.salary.length() == 0) {
            return false;
        } else if (binding.intro.length() == 0) {
            return false;
        } else if (list_charm.size() < 2) {
            return false;
        } else if (list_interest.size() < 2) {
            return false;
        } else if (list_ideal.size() < 2) {
            return false;
        } else {
            return true;
        }
    }

    private void setRecyclerView() {
        /* image */
        images.add(null);

        adapter = new ImageAdapter(act, images, new ImageAdapter.ButtonClickListener() {
            @Override
            public void selectButtonClicked() {
                Log.i(StringUtil.TAG, "images: " + images);
                if (images.size() > 6) {
                    Common.showToast(act, "사진은 최대 6장까지 등록 가능합니다");
                } else {
                    Common.getAlbum(frag, act, Common.PHOTO_GALLERY);
                }
            }

            @Override
            public void expand(String image) {
                binding.expandView.setVisibility(View.VISIBLE);
                Glide.with(act).load(image).into(binding.expandImage);
            }

            @Override
            public void deleteButtonClicked() {
                binding.expandView.setVisibility(View.GONE);
                images = adapter.getList();

                if (images.size() < 4) {
                    binding.btnNext.setSelected(false);
                } else {
                    binding.btnNext.setSelected(true);
                }
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act, RecyclerView.HORIZONTAL, false));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setItemViewCacheSize(20);




        /* etc */
        list_charm.add(null);
        FlexboxLayoutManager layoutManager1 = new FlexboxLayoutManager(act);
        layoutManager1.setFlexWrap(FlexWrap.WRAP);
        adapter_charm = new EtcProfileAdapter(this, act, list_charm, "charm");
        binding.recyclerViewCharm.setLayoutManager(layoutManager1);
        binding.recyclerViewCharm.setAdapter(adapter_charm);
        binding.recyclerViewCharm.setHasFixedSize(true);
        binding.recyclerViewCharm.setItemViewCacheSize(20);

        list_interest.add(null);
        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(act);
        layoutManager1.setFlexWrap(FlexWrap.WRAP);
        adapter_interest = new EtcProfileAdapter(this, act, list_interest, "interest");
        binding.recyclerViewInterest.setLayoutManager(layoutManager2);
        binding.recyclerViewInterest.setAdapter(adapter_interest);
        binding.recyclerViewInterest.setHasFixedSize(true);
        binding.recyclerViewInterest.setItemViewCacheSize(20);

        list_ideal.add(null);
        FlexboxLayoutManager layoutManager3 = new FlexboxLayoutManager(act);
        layoutManager1.setFlexWrap(FlexWrap.WRAP);
        adapter_ideal = new EtcProfileAdapter(this, act, list_ideal, "ideal");
        binding.recyclerViewIdeal.setLayoutManager(layoutManager3);
        binding.recyclerViewIdeal.setAdapter(adapter_ideal);
        binding.recyclerViewIdeal.setHasFixedSize(true);
        binding.recyclerViewIdeal.setItemViewCacheSize(20);
    }

    private void setClickListener() {
        binding.location.setOnClickListener(this);
        binding.birth.setOnClickListener(this);
        binding.height.setOnClickListener(this);
        binding.body.setOnClickListener(this);
        binding.edu.setOnClickListener(this);
        binding.job.setOnClickListener(this);
        binding.drink.setOnClickListener(this);
        binding.smoke.setOnClickListener(this);
        binding.religion.setOnClickListener(this);
        binding.salary.setOnClickListener(this);

        binding.location.setTag(R.string.tag02, "location");
        binding.height.setTag(R.string.tag02, "height");
        binding.body.setTag(R.string.tag02, "body");
        binding.edu.setTag(R.string.tag02, "edu");
        binding.job.setTag(R.string.tag02, "job");
        binding.drink.setTag(R.string.tag02, "drink");
        binding.smoke.setTag(R.string.tag02, "smoke");
        binding.religion.setTag(R.string.tag02, "religion");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.location:
            case R.id.height:
            case R.id.body:
            case R.id.edu:
            case R.id.job:
            case R.id.drink:
            case R.id.smoke:
            case R.id.religion:
                selectedView = (TextView) v;
                startActivityForResult(new Intent(act, ProfileSimpleDlg.class)
                                .putExtra("type", (String) v.getTag(R.string.tag02))
                                .putExtra("data", selectedView.getText().toString())
                        , PROFILE);
                break;

            case R.id.birth:
                selectedView = (TextView) v;
                startActivityForResult(new Intent(act, ProfileBirthDlg.class)
                                .putExtra("type", (String) v.getTag(R.string.tag02))
                                .putExtra("birth", binding.birth.getText().toString())
                        , BIRTH);
                break;

            case R.id.salary:
                selectedView = (TextView) v;
                startActivityForResult(new Intent(act, SalaryAct.class), SALARY);
                break;
        }
    }

    private void nextProcess() {
        JoinAct.joinData.setNick(binding.nick.getText().toString());
        JoinAct.joinData.setLocation(binding.location.getText().toString());
        JoinAct.joinData.setBirth(binding.birth.getText().toString());
        JoinAct.joinData.setHeight(binding.height.getText().toString());
        JoinAct.joinData.setBody(binding.body.getText().toString());
        JoinAct.joinData.setEdu(binding.edu.getText().toString());
        JoinAct.joinData.setJob(binding.job.getText().toString());
        JoinAct.joinData.setDrink(binding.drink.getText().toString());
        JoinAct.joinData.setSmoke(binding.smoke.getText().toString());
        JoinAct.joinData.setReligion(binding.religion.getText().toString());
        JoinAct.joinData.setSalary(binding.salary.getText().toString());
        JoinAct.joinData.setIntro(binding.intro.getText().toString());

        //매력포인트 & 관심사 & 이상형 저장
        StringBuilder charm = new StringBuilder();
        for (int i = 1; i < list_charm.size(); i++) {
            if (i == 1) {
                charm.append(list_charm.get(i).getContents());
            } else {
                charm.append(",").append(list_charm.get(i).getContents());
            }
        }
        JoinAct.joinData.setCharm(charm.toString());

        StringBuilder interest = new StringBuilder();
        for (int i = 1; i < list_interest.size(); i++) {
            if (i == 1) {
                interest.append(list_interest.get(i).getContents());
            } else {
                interest.append(",").append(list_interest.get(i).getContents());
            }
        }
        JoinAct.joinData.setInterest(interest.toString());

        StringBuilder ideal = new StringBuilder();
        for (int i = 1; i < list_ideal.size(); i++) {
            if (i == 1) {
                ideal.append(list_ideal.get(i).getContents());
            } else {
                ideal.append(",").append(list_ideal.get(i).getContents());
            }
        }
        JoinAct.joinData.setIdeal(ideal.toString());

        // 연봉 파일
        JoinAct.joinData.setSalary_file(salary_file);

        // 이미지 파일
        ArrayList<String> send_images = new ArrayList<>();
        send_images.add(null);
        for (int i = 1; i < adapter.getList().size(); i++) {
            send_images.add(adapter.getList().get(i).getImageUrl());
        }
        JoinAct.joinData.setImages(send_images);

        Log.i(StringUtil.TAG, "joinData: " + JoinAct.joinData);

        doJoin();
//        doReCheckTest();
//        doTestEditProfile();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(StringUtil.TAG, "onActivityResult: ");
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ETC:
                    String type = data.getStringExtra("type");
                    switch (type) {
                        case "charm":
                            list_charm = new ArrayList<>();
                            list_charm = (ArrayList<EtcData>) data.getSerializableExtra("list");
                            list_charm.add(0, null);
                            adapter_charm.setList(list_charm);
                            break;
                        case "interest":
                            list_interest = new ArrayList<>();
                            list_interest = (ArrayList<EtcData>) data.getSerializableExtra("list");
                            list_interest.add(0, null);
                            adapter_interest.setList(list_interest);
                            break;
                        case "ideal":
                            list_ideal = new ArrayList<>();
                            list_ideal = (ArrayList<EtcData>) data.getSerializableExtra("list");
                            list_ideal.add(0, null);
                            adapter_ideal.setList(list_ideal);
                            break;
                    }
                    break;
                case SALARY:
                    salary_file = null;
                    String salary = data.getStringExtra("salary");
                    binding.salary.setText(salary);
                    salary_file = (File) data.getSerializableExtra("file");

                    ((ImageView) selectedView.getTag(R.string.tag01)).setSelected(true);
                    break;

                case BIRTH:
                    String birth = data.getStringExtra("birth");
                    binding.birth.setText(birth);
                    ((ImageView) selectedView.getTag(R.string.tag01)).setSelected(true);
                    break;

                case PROFILE:
                    String value = data.getStringExtra("value");
                    selectedView.setText(value);
                    ((ImageView) selectedView.getTag(R.string.tag01)).setSelected(true);
                    break;


                //사진 갤러리 결과
                case Common.PHOTO_GALLERY:
                    if (data == null) {
                        Common.showToast(act, "사진불러오기 실패! 다시 시도해주세요.");
                        return;
                    }

                    photoUri = data.getData();

                    Common.cropImage(frag, act, photoUri, Common.PHOTO_CROP, new OnPhotoAfterAction() {
                        @Override
                        public void doPhotoUri(Uri uri) {
                            photoUri = uri;
                        }
                    });
                    break;

                //사진 촬영 결과
                case Common.PHOTO_TAKE:
                    Common.cropImage(frag, act, photoUri, Common.PHOTO_CROP, new OnPhotoAfterAction() {
                        @Override
                        public void doPhotoUri(Uri uri) {
                            photoUri = uri;
                        }
                    });
                    break;

                //사진 크롭 결과
                case Common.PHOTO_CROP:
                    mImgFilePath = photoUri.getPath();
                    Log.i(StringUtil.TAG, "mImgFilePath: " + mImgFilePath);
                    if (StringUtil.isNull(mImgFilePath)) {
                        Common.showToast(act, "사진자르기 실패! 다시 시도해주세요.");
                        return;
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bm = BitmapFactory.decodeFile(mImgFilePath, options);

                    Bitmap resize = null;
                    try {
                        File resize_file = new File(mImgFilePath);
                        FileOutputStream out = new FileOutputStream(resize_file);

                        int width = bm.getWidth();
                        int height = bm.getHeight();

                        if (width > 1024) {
                            int resizeHeight = 0;
                            if (height > 768) {
                                resizeHeight = 768;
                            } else {
                                resizeHeight = height / (width / 1024);
                            }

                            resize = Bitmap.createScaledBitmap(bm, 1024, resizeHeight, true);
                            resize.compress(Bitmap.CompressFormat.PNG, 100, out);
                        } else {
                            resize = Bitmap.createScaledBitmap(bm, width, height, true);
                            resize.compress(Bitmap.CompressFormat.PNG, 100, out);
                        }
                        Log.e("TEST_HOME", "mImgFilePath: " + mImgFilePath);


                        // 최종
                        File imageFile = new File(mImgFilePath);
                        if (imageFile.length() > 1000000) {
                            Common.showToast(act, "파일 용량이 초과되었습니다. 다른사진을 선택해주세요");
                        } else {
                            // 사진 추가
                            images.add(new ImageEditData(mImgFilePath, ""));
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(images);
                                }
                            });
                        }

                        mImgFilePath = "";

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    MediaScannerConnection.scanFile(act, new String[]{photoUri.getPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });
                    break;
            }

            if (StringUtil.isNull(fromType)) {
                // 일반회원가입일때
                if (checkAllProcess()) {
                    binding.btnNext.setSelected(true);
                } else {
                    binding.btnNext.setSelected(false);
                }
            } else {
                // 재검수 요청일때
                if (images.size() < 4) {
                    binding.btnNext.setSelected(false);
                } else {
                    binding.btnNext.setSelected(true);
                }
            }
        } else {
            Log.i(StringUtil.TAG, "onActivityResult: RESULT_CANCEL");
        }
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
                            act.finish();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));

                            if (jo.has("data")) {
                                JSONArray ja = jo.getJSONArray("data");
                                JSONObject job = ja.getJSONObject(0);
                                String type = StringUtil.getStr(jo, "type");
                                String m_idx = StringUtil.getStr(job, "m_idx");
                                String m_gender = StringUtil.getStr(job, "m_gender");
                                String m_id = StringUtil.getStr(job, "m_id");
                                String m_hp = StringUtil.getStr(job, "m_hp");
                                String m_pass = StringUtil.getStr(job, "m_pass");
                                String m_birth = StringUtil.getStr(job, "m_birth");
                                String m_profile1 = StringUtil.getStr(job, "m_profile1");

                                AppPreference.setProfilePref(act, AppPreference.PREF_JSON, jo.toString());
                                AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, m_idx);
                                AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, m_gender);
                                AppPreference.setProfilePref(act, AppPreference.PREF_ID, m_id);
                                AppPreference.setProfilePref(act, AppPreference.PREF_PW, m_pass);
                                AppPreference.setProfilePref(act, AppPreference.PREF_PHONE, m_hp);
                                AppPreference.setProfilePref(act, AppPreference.PREF_AGE, StringUtil.calcAge(m_birth.substring(0, 4)));
                                AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, NetUrls.DOMAIN_ORIGIN + m_profile1);
                                AppPreference.setProfilePrefBool(act, AppPreference.AUTO_LOGIN, true);

                                BaseFrag fragment = new Join05Frag();
                                ((JoinAct) act).replaceFragment(fragment);
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
}
