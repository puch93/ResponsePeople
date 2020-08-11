package kr.co.core.responsepeople.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.EtcProfileAdapter;
import kr.co.core.responsepeople.adapter.ImageAdapter;
import kr.co.core.responsepeople.data.EtcData;
import kr.co.core.responsepeople.databinding.ActivityEditBinding;
import kr.co.core.responsepeople.dialog.ProfileBirthDlg;
import kr.co.core.responsepeople.dialog.ProfileSimpleDlg;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.OnPhotoAfterAction;
import kr.co.core.responsepeople.util.StringUtil;

public class EditAct extends BaseAct implements View.OnClickListener {
    ActivityEditBinding binding;
    Activity act;

    // image
    private ArrayList<String> images = new ArrayList<>();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit, null);
        act = this;

        setLayout();
        setRecyclerView();
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
                            for (int i = 1; i < 7; i++) {
                                if (!StringUtil.isNull(StringUtil.getStr(job, "m_profile" + i))) {
                                    images.add(NetUrls.DOMAIN_ORIGIN + StringUtil.getStr(job, "m_profile" + i));
                                } else {
                                    break;
                                }
                            }
                            String m_height = StringUtil.getStr(job, "m_height");
                            String m_body = StringUtil.getStr(job, "m_body");
                            String m_edu = StringUtil.getStr(job, "m_edu");
                            String m_job = StringUtil.getStr(job, "m_job");
                            String m_drink = StringUtil.getStr(job, "m_drink");
                            String m_smoke = StringUtil.getStr(job, "m_smoke");
                            String m_religion = StringUtil.getStr(job, "m_religion");
                            String m_salary = StringUtil.getStr(job, "m_salary");
                            String m_intro = StringUtil.getStr(job, "m_intro");

                            for (String contents : StringUtil.getStr(job, "m_charm").split(",")) {
                                list_charm.add(new EtcData(contents, false));
                            }

                            for (String contents : StringUtil.getStr(job, "m_interest").split(",")) {
                                list_interest.add(new EtcData(contents, false));
                            }

                            for (String contents : StringUtil.getStr(job, "m_ideal").split(",")) {
                                list_ideal.add(new EtcData(contents, false));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.checkImgHeight.setSelected(true);
                                    binding.checkImgBody.setSelected(true);
                                    binding.checkImgDrink.setSelected(true);
                                    binding.checkImgSmoke.setSelected(true);
                                    binding.checkImgReligion.setSelected(true);
                                    binding.checkImgSalary.setSelected(true);

                                    binding.btnEdit.setSelected(true);

                                    adapter.setList(images);
                                    binding.height.setText(m_height);
                                    binding.body.setText(m_body);
                                    binding.drink.setText(m_drink);
                                    binding.smoke.setText(m_smoke);
                                    binding.religion.setText(m_religion);
                                    binding.salary.setText(m_salary);
                                    binding.intro.setText(m_intro);

                                    adapter_charm.setList(list_charm);
                                    adapter_interest.setList(list_interest);
                                    adapter_ideal.setList(list_ideal);
                                }
                            });
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

    private void setLayout() {
        binding.height.setTag(R.string.tag01, binding.checkImgHeight);
        binding.body.setTag(R.string.tag01, binding.checkImgBody);
        binding.drink.setTag(R.string.tag01, binding.checkImgDrink);
        binding.smoke.setTag(R.string.tag01, binding.checkImgSmoke);
        binding.religion.setTag(R.string.tag01, binding.checkImgReligion);
        binding.salary.setTag(R.string.tag01, binding.checkImgSalary);

        binding.height.setOnClickListener(this);
        binding.body.setOnClickListener(this);
        binding.drink.setOnClickListener(this);
        binding.smoke.setOnClickListener(this);
        binding.religion.setOnClickListener(this);
        binding.salary.setOnClickListener(this);
        binding.btnBack.setOnClickListener(this);
        binding.btnEdit.setOnClickListener(this);

        binding.height.setTag(R.string.tag02, "height");
        binding.body.setTag(R.string.tag02, "body");
        binding.drink.setTag(R.string.tag02, "drink");
        binding.smoke.setTag(R.string.tag02, "smoke");
        binding.religion.setTag(R.string.tag02, "religion");
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
                    Common.getAlbum(null, act, Common.PHOTO_GALLERY);
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
                    binding.btnEdit.setSelected(false);
                } else {
                    binding.btnEdit.setSelected(true);
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
        adapter_charm = new EtcProfileAdapter(null, act, list_charm, "charm");
        binding.recyclerViewCharm.setLayoutManager(layoutManager1);
        binding.recyclerViewCharm.setAdapter(adapter_charm);
        binding.recyclerViewCharm.setHasFixedSize(true);
        binding.recyclerViewCharm.setItemViewCacheSize(20);

        list_interest.add(null);
        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(act);
        layoutManager1.setFlexWrap(FlexWrap.WRAP);
        adapter_interest = new EtcProfileAdapter(null, act, list_interest, "interest");
        binding.recyclerViewInterest.setLayoutManager(layoutManager2);
        binding.recyclerViewInterest.setAdapter(adapter_interest);
        binding.recyclerViewInterest.setHasFixedSize(true);
        binding.recyclerViewInterest.setItemViewCacheSize(20);

        list_ideal.add(null);
        FlexboxLayoutManager layoutManager3 = new FlexboxLayoutManager(act);
        layoutManager1.setFlexWrap(FlexWrap.WRAP);
        adapter_ideal = new EtcProfileAdapter(null, act, list_ideal, "ideal");
        binding.recyclerViewIdeal.setLayoutManager(layoutManager3);
        binding.recyclerViewIdeal.setAdapter(adapter_ideal);
        binding.recyclerViewIdeal.setHasFixedSize(true);
        binding.recyclerViewIdeal.setItemViewCacheSize(20);
    }


    private void doEdit(String charm, String ideal, String interest) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            finish();
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

        server.setTag("Edit Info");
        server.addParams("dbControl", NetUrls.EDIT_PROFILE);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("m_body", binding.body.getText().toString());
        server.addParams("m_religion", binding.religion.getText().toString());
        server.addParams("m_drink", binding.drink.getText().toString());
        server.addParams("m_smoke", binding.smoke.getText().toString());
        server.addParams("m_salary", binding.salary.getText().toString());
        server.addParams("m_charm", charm);
        server.addParams("m_ideal", ideal);
        server.addParams("m_interest", interest);
        server.addParams("m_intro", binding.intro.getText().toString());

        new Thread() {
            @Override
            public void run() {
                super.run();

                // 파일
                for (int i = 1; i < images.size(); i++) {
                    File file = null;

                    if (images.get(i).contains("http")) {
                        file = downloadImage(images.get(i));
                        Log.i(StringUtil.TAG, "file name" + i + ": " + file.getName());
                    } else {
                        file = new File(images.get(i));
                    }

                    server.addFileParams("m_profile" + i, file);
                    server.addParams("m_profile" + i + "ck", "Y");
                }

                if (null != salary_file) {
                    server.addFileParams("m_salary_file", salary_file);
                }

                server.execute(true, true);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.body:
            case R.id.drink:
            case R.id.smoke:
            case R.id.religion:
                selectedView = (TextView) v;
                startActivityForResult(new Intent(act, ProfileSimpleDlg.class)
                                .putExtra("type", (String) v.getTag(R.string.tag02))
                                .putExtra("data", selectedView.getText().toString())
                        , PROFILE);
                break;

            case R.id.salary:
                selectedView = (TextView) v;
                startActivityForResult(new Intent(act, SalaryAct.class), SALARY);
                break;

            case R.id.btn_edit:
                if (images.size() < 4) {
                    Common.showToast(act, "이미지를 3장이상 등록해주세요");
                } else if (binding.body.length() == 0) {
                    Common.showToast(act, "체형을 선택해주세요");
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
                    //매력포인트 & 관심사 & 이상형 저장
                    StringBuilder charm = new StringBuilder();
                    for (int i = 1; i < list_charm.size(); i++) {
                        if (i == 1) {
                            charm.append(list_charm.get(i).getContents());
                        } else {
                            charm.append(",").append(list_charm.get(i).getContents());
                        }
                    }

                    StringBuilder interest = new StringBuilder();
                    for (int i = 1; i < list_interest.size(); i++) {
                        if (i == 1) {
                            interest.append(list_interest.get(i).getContents());
                        } else {
                            interest.append(",").append(list_interest.get(i).getContents());
                        }
                    }

                    StringBuilder ideal = new StringBuilder();
                    for (int i = 1; i < list_ideal.size(); i++) {
                        if (i == 1) {
                            ideal.append(list_ideal.get(i).getContents());
                        } else {
                            ideal.append(",").append(list_ideal.get(i).getContents());
                        }
                    }

                    // 이미지 파일
                    images = adapter.getList();

                    doEdit(charm.toString(), ideal.toString(), interest.toString());
                }
                break;

            case R.id.btn_back:
                finish();
                break;
        }
    }


    private boolean checkAllProcess() {
        if (images.size() < 4) {
            return false;
        } else if (binding.height.length() == 0) {
            return false;
        } else if (binding.body.length() == 0) {
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

                    Common.cropImage(null, act, photoUri, Common.PHOTO_CROP, new OnPhotoAfterAction() {
                        @Override
                        public void doPhotoUri(Uri uri) {
                            photoUri = uri;
                        }
                    });
                    break;

                //사진 촬영 결과
                case Common.PHOTO_TAKE:
                    Common.cropImage(null, act, photoUri, Common.PHOTO_CROP, new OnPhotoAfterAction() {
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
                            images.add(mImgFilePath);
                            runOnUiThread(new Runnable() {
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

            // 일반회원가입일때
            if (checkAllProcess()) {
                binding.btnEdit.setSelected(true);
            } else {
                binding.btnEdit.setSelected(false);
            }
        } else {
            Log.i(StringUtil.TAG, "onActivityResult: RESULT_CANCEL");
        }
    }
}