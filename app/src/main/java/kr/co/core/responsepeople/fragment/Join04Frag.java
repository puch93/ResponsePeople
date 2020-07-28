package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.JoinAct;
import kr.co.core.responsepeople.activity.SalaryAct;
import kr.co.core.responsepeople.adapter.EtcAdapter;
import kr.co.core.responsepeople.adapter.EtcProfileAdapter;
import kr.co.core.responsepeople.adapter.ImageAdapter;
import kr.co.core.responsepeople.data.EtcData;
import kr.co.core.responsepeople.databinding.FragmentJoin03Binding;
import kr.co.core.responsepeople.databinding.FragmentJoin04Binding;
import kr.co.core.responsepeople.dialog.ProfileBirthDlg;
import kr.co.core.responsepeople.dialog.ProfileSimpleDlg;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.OnPhotoAfterAction;
import kr.co.core.responsepeople.util.StringUtil;

import static android.app.Activity.RESULT_OK;

public class Join04Frag extends BaseFrag implements View.OnClickListener {
    FragmentJoin04Binding binding;
    Activity act;
    Fragment frag;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_04, container, false);
        act = getActivity();
        frag = this;

        setClickListener();
        setRecyclerView();

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(StringUtil.TAG, "joinData: " + JoinAct.joinData);
                nextProcess();
            }
        });

        return binding.getRoot();
    }

    private void setRecyclerView() {
        /* image */
        images.add(null);

        adapter = new ImageAdapter(act, images, new ImageAdapter.selectButtonListener() {
            @Override
            public void clicked() {
                Common.getAlbum(frag, act, Common.PHOTO_GALLERY);
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

        binding.location.setTag("location");
        binding.height.setTag("height");
        binding.body.setTag("body");
        binding.edu.setTag("edu");
        binding.job.setTag("job");
        binding.drink.setTag("drink");
        binding.smoke.setTag("smoke");
        binding.religion.setTag("religion");
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
                                .putExtra("type", (String) v.getTag())
                                .putExtra("data", selectedView.getText().toString())
                        , PROFILE);
                break;

            case R.id.birth:
                startActivityForResult(new Intent(act, ProfileBirthDlg.class)
                                .putExtra("type", (String) v.getTag())
                                .putExtra("birth", binding.birth.getText().toString())
                        , BIRTH);
                break;

            case R.id.salary:
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

        // 연봉 파일
        JoinAct.joinData.setSalary_file(salary_file);

        // 이미지 파일
        JoinAct.joinData.setImages(adapter.getList());

//        BaseFrag fragment = new Join05Frag();
//        ((JoinAct) act).replaceFragment(fragment);
        ((JoinAct) act).doJoin();
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
                            list_charm.add(null);
                            list_charm = (ArrayList<EtcData>) data.getSerializableExtra("list");
                            adapter_charm.setList(list_charm);
                            break;
                        case "interest":
                            list_interest = new ArrayList<>();
                            list_interest.add(null);
                            list_interest = (ArrayList<EtcData>) data.getSerializableExtra("list");
                            adapter_interest.setList(list_interest);
                            break;
                        case "ideal":
                            list_ideal = new ArrayList<>();
                            list_ideal.add(null);
                            list_ideal = (ArrayList<EtcData>) data.getSerializableExtra("list");
                            adapter_ideal.setList(list_ideal);
                            break;
                    }
                    break;
                case SALARY:
                    salary_file = null;
                    String salary = data.getStringExtra("salary");
                    binding.salary.setText(salary);
                    salary_file = (File) data.getSerializableExtra("file");
                    break;

                case BIRTH:
                    String birth = data.getStringExtra("birth");
                    binding.birth.setText(birth);
                    break;

                case PROFILE:
                    String value = data.getStringExtra("value");
                    selectedView.setText(value);
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
                            images.add(mImgFilePath);
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
        } else {
            Log.i(StringUtil.TAG, "onActivityResult: RESULT_CANCEL");
        }
    }
}
