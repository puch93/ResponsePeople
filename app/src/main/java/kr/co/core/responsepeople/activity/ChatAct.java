package kr.co.core.responsepeople.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.adapter.ChatAdapter;
import kr.co.core.responsepeople.data.ChatData;
import kr.co.core.responsepeople.databinding.ActivityChatBinding;
import kr.co.core.responsepeople.server.JSONUrl;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.ChatValues;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.LogUtil;
import kr.co.core.responsepeople.util.OnPhotoAfterAction;
import kr.co.core.responsepeople.util.StringUtil;
import okhttp3.OkHttpClient;

public class ChatAct extends BaseAct implements View.OnClickListener {
    ActivityChatBinding binding;
    public static Activity act;

    private String room_idx, t_idx;
    private io.socket.client.Socket mSocket;
    private boolean exitState = false;
    private ChatAdapter adapter;
    private ArrayList<ChatData> list = new ArrayList<>();

    String imgPath, nowDate, outType, outCheck;

    /* 이미지 보내기 관련 */
    private Uri photoUri;
    private String mImgFilePath;
    private static final int PICK_DIALOG = 1000;
    private static final int PHOTO_GALLERY = 1001;
    private static final int PHOTO_TAKE = 1002;
    private static final int PHOTO_CROP = 1003;

    private String otherImage, otherNick, otherAge;
    private boolean otherImageOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat, null);
        act = this;

        room_idx = getIntent().getStringExtra("room_idx").replace("R", "");
        t_idx = getIntent().getStringExtra("t_idx");

        getOtherProfile();
    }

    private void setLayout() {
        binding.btnBack.setOnClickListener(this);
        binding.btnPhoto.setOnClickListener(this);
        binding.btnSend.setOnClickListener(this);

        // EditText 포커스될때 키보드가 UI 가리는 것 막음
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


        // set recycler view
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(act));

        adapter = new ChatAdapter(act, room_idx, list);
        adapter.setOtherInfo(otherImage, otherNick, otherAge, otherImageOk);
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    binding.recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

            case R.id.btn_photo:
                break;

            case R.id.btn_send:
                if (binding.contents.length() == 0) {
                    Common.showToast(act, "내용을 입력해주세요");
                } else {
                    try {
                        sendMessage(URLEncoder.encode(binding.contents.getText().toString(), "UTF-8"), "text");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //실제채팅전송
                    binding.contents.setText("");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }, 300);
                }
                break;
        }
    }

    //채팅푸시전송
    private void sendMessage(final String contents, final String type) {
        try {
            JSONObject sendData = new JSONObject();
            sendData.put("user_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
            sendData.put("room_idx", room_idx);
            sendData.put("site_idx", "1");
            sendData.put("msg_type", type);
            sendData.put("c_msg", URLEncoder.encode(binding.contents.getText().toString(), "UTF-8"));
            mSocket.emit(ChatValues.SETCHATSAVE, sendData);
            LogUtil.logI("sendData: " + sendData);

            sendPush(contents, type);
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void sendPush(final String contents, final String type) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            LogUtil.logI(StringUtil.getStr(jo, "message"));
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

        server.setTag("Send Push");
        server.addParams("dbControl", NetUrls.CHAT_PUSH);
        server.addParams("send_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("receive_idx", t_idx);
        server.addParams("type", type);
        server.addParams("msg", contents);
        server.addParams("chat_idx", room_idx);
        server.execute(true, false);
    }


    private void getOtherProfile() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            JSONObject job = ja.getJSONObject(0);

                            otherImage = StringUtil.getStr(jo, "m_profile1");
                            otherNick = StringUtil.getStr(job, "m_nick");
                            otherAge = StringUtil.calcAge(StringUtil.getStr(job, "m_birth").substring(0, 4));
                            otherImageOk = StringUtil.getStr(job, "m_profile_result").equalsIgnoreCase("Y");


                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.nick.setText(otherNick);
                                    binding.age.setText(otherAge);
                                }
                            });

                            setLayout();
                            setupSocketClient();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            finish();
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

        server.setTag("Other Profile");
        server.addParams("dbControl", NetUrls.VIEW_PROFILE_ADMIN);
        server.addParams("m_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("t_idx", t_idx);
        server.execute(true, false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_DIALOG:
                    String type = data.getStringExtra("result");

                    if (type.equalsIgnoreCase("camera")) {
                        //촬영하기
                        Common.takePhoto(null, act, PHOTO_TAKE, new OnPhotoAfterAction() {
                            @Override
                            public void doPhotoUri(Uri uri) {
                                photoUri = uri;
                            }
                        });
                    } else if (type.equalsIgnoreCase("album")) {
                        //갤러리
                        Common.getAlbum(null, act, PHOTO_GALLERY);
                    }
                    break;

                //사진 갤러리 결과
                case PHOTO_GALLERY:
                    if (data == null) {
                        Common.showToast(act, "사진불러오기 실패! 다시 시도해주세요.");
                        return;
                    }

                    photoUri = data.getData();

                    Common.cropImage(null, act, photoUri, PHOTO_CROP, new OnPhotoAfterAction() {
                        @Override
                        public void doPhotoUri(Uri uri) {
                            photoUri = uri;
                        }
                    });
                    break;

                //사진 촬영 결과
                case PHOTO_TAKE:
                    Common.cropImage(null, act, photoUri, PHOTO_CROP, new OnPhotoAfterAction() {
                        @Override
                        public void doPhotoUri(Uri uri) {
                            photoUri = uri;
                        }
                    });
                    break;

                //사진 크롭 결과
                case PHOTO_CROP:
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


                        File imageFile = new File(mImgFilePath);
                        if (imageFile.length() > 10000000) {
                            Common.showToast(act, "파일 용량이 초과되었습니다. 다른사진을 선택해주세요");
                            mImgFilePath = "";
                        } else {
                            // 사진 추가
                            uploadImage(imageFile);
                        }

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
        }
    }

    private void uploadImage(File file) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            sendMessage(StringUtil.getStr(jo, "img"), "image");
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

        server.setTag("Chat Upload Image");
        server.addParams("dbControl", NetUrls.CHAT_UPLOAD_IMAGE);
        if(!StringUtil.isNull(mImgFilePath)) {
            server.addFileParams("image", file);
        }
        server.execute(true, false);
    }


    /* 소켓 */
    private void setupSocketClient() {
        try {
            Log.i(StringUtil.TAG, "setupSocketClient");
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                    return myTrustedAnchors;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .sslSocketFactory(sc.getSocketFactory()).build();

            // default settings for all sockets
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            IO.setDefaultOkHttpCallFactory(okHttpClient);

            // set as an option
            IO.Options opts = new IO.Options();
            opts.callFactory = okHttpClient;
            opts.webSocketFactory = okHttpClient;
            mSocket = IO.socket(ChatValues.SOCKET_URL);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(ChatValues.GETCHATS, getChats);
            mSocket.on(ChatValues.READADD, checkRoom);
            mSocket.on(ChatValues.LASTCHATCONTENTS, chatRecive);
            mSocket.connect();
            System.out.println("socket setup!!! ");
        } catch (URISyntaxException e) {
            Log.i(StringUtil.TAG, "URISyntaxException");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Log.i(StringUtil.TAG, "NoSuchAlgorithmException");
            e.printStackTrace();
        } catch (KeyManagementException e) {
            Log.i(StringUtil.TAG, "KeyManagementException");
            e.printStackTrace();
        }
    }

    private Emitter.Listener checkRoom = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //Log.i("TEST_SOCKET", "roomExit");
            Log.d(StringUtil.TAG, "채팅===" + "checkRoom : ");
            JSONObject readData = (JSONObject) args[0];
            //Log.i("TEST_SOCKET", "getReadsMember: " + readData);
            Log.d(StringUtil.TAG, "채팅===" + "checkRoom : " + readData);
            //System.out.println("Socket exit  ");
            //client_count--;
            //Log.i("TEST_TEST", "client_count--: " + client_count);
        }
    };

    private Emitter.Listener chatRecive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("TEST_SOCKET", "chatRecive");
            Log.d(StringUtil.TAG, "채팅===" + "채팅받아오기");
            JSONObject roomData = (JSONObject) args[0];
            for (int k = 0; k < args.length; k++) {
                Log.d(StringUtil.TAG, "채팅===" + "채팅받아오기" + "args[" + k + "]: " + args[k]);
            }

            try {
                JSONArray chats = roomData.getJSONArray("chat");
                final JSONObject jo = chats.getJSONObject(0);
                if (StringUtil.getStr(jo, "c_msg_type").equalsIgnoreCase("out")) {
                    outCheck = "Y";
                }


                Log.e(StringUtil.TAG, "chatRecive data: " + jo);

                if (StringUtil.getStr(jo, "c_msg_type").equalsIgnoreCase("out")) {
                    outCheck = "Y";
                }

                // 유저 인덱스
                String c_user_idx = StringUtil.getStr(jo, "c_user_idx");
                // 메시지
                String c_msg = Common.decodeEmoji(StringUtil.getStr(jo, "c_msg"));
                // 보낸 시간
                String c_regdate = StringUtil.converTime(StringUtil.getStr(jo, "c_regdate"), "a hh:mm");

                String in_cnt = StringUtil.getStr(jo, "in_cnt");
                String c_msg_type = StringUtil.getStr(jo, "c_msg_type");
                String c_user_gender = StringUtil.getStr(jo, "c_user_gender");
                // 데이트라인 데이터 추가
                String dateLine = StringUtil.converTime(StringUtil.getStr(jo, "c_regdate"), "yyyy.MM.dd");

                // 데이트라인 확인 후 추가
//                if (i > 0) {
//                    if (!list.get(list.size() - 1).getDate_line().equals(dateLine)) {
//                        ChatData data = new ChatData(dateLine, ChatValues.MSG_DATELINE);
//                        list.add(data);
//                    }
//                } else {
//                    ChatData data = new ChatData(dateLine, ChatValues.MSG_DATELINE);
//                    list.add(data);
//                }


                list.add(new ChatData(c_user_idx, isImage(c_msg), c_regdate, dateLine, c_msg, in_cnt.equals("0")));


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setList(list);
                        binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }




            /*try {
                JSONArray chats = roomData.getJSONArray("chat");
                final JSONObject jsonObject = chats.getJSONObject(0);
                final String c_idx = StringUtil.getStr(jsonObject, "c_idx");

                //String c_msg = StringUtil.getStr(jsonObject, "c_msg");
                final String c_user_idx = StringUtil.getStr(jsonObject, "c_user_idx");
                String c_time = StringUtil.getStr(jsonObject, "c_regdate").replaceAll("T", " ").replaceAll("Z", "");
                String c_read_count = StringUtil.getStr(jsonObject, "c_read_cnt");
                String c_type = StringUtil.getStr(jsonObject, "c_msg_type");
                String c_flag_delete = StringUtil.getStr(jsonObject, "c_flag_delete");
                String c_limit_time = converLimitTime(StringUtil.getStr(jsonObject, "c_expirationdate"));
                String c_name = StringUtil.getStr(jsonObject,"m_name");
                String c_userImg = StringUtil.getStr(jsonObject,"m_photo");
                int c_count = Integer.parseInt(rCount) - Integer.parseInt(StringUtil.getStr(jsonObject, "in_cnt"));
                String c_systemCheck = StringUtil.getStr(jsonObject,"c_system_msg_is");
                if(c_systemCheck.equalsIgnoreCase("Y")){
                    c_type = "system";
                }
                String c_msg;
                if(c_type.equalsIgnoreCase("text")){
                    c_msg = StringUtil.decodeEmoji(StringUtil.getStr(jsonObject, "c_msg"));
                }else{
                    c_msg = StringUtil.getStr(jsonObject, "c_msg");
                }
                //Log.d(StringUtil.TAG,"메세지 값 => 유저인덱스 : "+talker+" 메세지 : "+msg+ "룸넘버 : "+StringUtil.getStr(jsonObject,"c_room_idx")+"제이슨 길이 : "+jsonArray.length());

                final ChatMessage message = new ChatMessage(c_idx, c_msg, c_user_idx, converTime(c_time), Integer.toString(c_count), c_type, c_flag_delete, c_limit_time,c_name,c_userImg,c_systemCheck);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!c_user_idx.equalsIgnoreCase(AppUserData.getData(act, "userIdx"))) {
                            mAdapter.addItems(message);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    binding.rcvList.scrollToPosition(mAdapter.getItemCount()-1);
                                    //binding.rcvList.smoothScrollToPosition(mAdapter.getItemCount()-1);
                                }
                            }, 300);
                        } else {
                            if(message.getcType().equalsIgnoreCase("text")){
                                mAdapter.setItem(message);
                            }else{
                                mAdapter.addItems(message);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    binding.rcvList.scrollToPosition(mAdapter.getItemCount()-1);
                                    //binding.rcvList.smoothScrollToPosition(mAdapter.getItemCount()-1);
                                }
                            }, 300);
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
    };


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject roomData = new JSONObject();
            Log.e(StringUtil.TAG_CHAT, "onConnect");
            try {

                roomData.put("user_idx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
                roomData.put("room_idx", room_idx);
                roomData.put("site_idx", "1");
                mSocket.emit(ChatValues.READADD, roomData);
                mSocket.emit(ChatValues.SETROOMREADIS, roomData);
                Log.i(StringUtil.TAG, "onConnect Put: " + roomData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    // 채팅 내역(이전 대화 내용)
    private Emitter.Listener getChats = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(StringUtil.TAG_CHAT, "getChats");
            JSONObject rcvData = (JSONObject) args[0];
            LogUtil.logLarge("getChats rcvData(getChats): " + rcvData);

            try {
                list = new ArrayList<>();


                JSONObject jObject = new JSONObject(rcvData.optString("roomUser"));
                Log.i(StringUtil.TAG, "방인원수 : " + StringUtil.getStr(jObject, "count"));
                Log.i(StringUtil.TAG, "jObject : " + jObject);

                // 방안에있는 유저 데이터
                JSONArray users = new JSONArray(StringUtil.getStr(jObject, "users"));
                for (int u = 0; u < users.length(); u++) {
                    JSONObject uo = users.getJSONObject(u);
                    if (!StringUtil.getStr(uo, "cr_user_idx").equalsIgnoreCase(AppPreference.getProfilePref(act, AppPreference.PREF_MIDX))) {

                    }
                }


                JSONArray ja = new JSONArray(StringUtil.getStr(rcvData, "chats"));
                if (ja.length() > 0) {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        Log.e(StringUtil.TAG, "chat_list(" + i + "): " + jo);

                        if (StringUtil.getStr(jo, "c_msg_type").equalsIgnoreCase("out")) {
                            outCheck = "Y";
                        }

                        // 유저 인덱스
                        String c_user_idx = StringUtil.getStr(jo, "c_user_idx");
                        // 메시지
                        String c_msg = Common.decodeEmoji(StringUtil.getStr(jo, "c_msg"));
                        // 보낸 시간
                        String c_regdate = StringUtil.converTime(StringUtil.getStr(jo, "c_regdate"), "a hh:mm");

                        String in_cnt = StringUtil.getStr(jo, "in_cnt");
                        String c_msg_type = StringUtil.getStr(jo, "c_msg_type");
                        String c_user_gender = StringUtil.getStr(jo, "c_user_gender");

                        // 데이트라인 데이터 추가
                        String dateLine = StringUtil.converTime(StringUtil.getStr(jo, "c_regdate"), "yyyy.MM.dd");

                        // 데이트라인 확인 후 추가
                        if (i > 0) {
                            if (!list.get(list.size() - 1).getDate_line().equals(dateLine)) {
                                ChatData data = new ChatData(dateLine, ChatValues.MSG_DATELINE);
                                list.add(data);
                            }
                        } else {
                            ChatData data = new ChatData(dateLine, ChatValues.MSG_DATELINE);
                            list.add(data);
                        }

                        list.add(new ChatData(c_user_idx, isImage(c_msg), c_regdate, dateLine, c_msg, in_cnt.equals("0")));
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setList(list);
                            binding.recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    });
                }

            } catch (
                    JSONException e) {
                Log.i(StringUtil.TAG, "JSONException: " + e.toString());
                e.printStackTrace();
            }
        }
    };


    private Emitter.Listener getReads = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject readData = (JSONObject) args[0];
            LogUtil.logLarge("getReads: " + readData);
        }
    };


    // 실시간 메세지 처리
    private Emitter.Listener onChatReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject rcvData = (JSONObject) args[0];
            LogUtil.logLarge("rcvData(onChatReceive): " + rcvData);
            String selectDate = null;

            try {
                JSONObject from = new JSONObject(rcvData.getString("from"));
                Log.e(StringUtil.TAG, "onChatReceive (from): " + from);

                JSONObject chat = new JSONObject(from.getString("chat"));
                Log.e(StringUtil.TAG, "onChatReceive (chat): " + chat);

                // 유저 인덱스
                String m_idx = StringUtil.getStr(chat, "user_idx");

                // 메시지
                String contents = Common.decodeEmoji(StringUtil.getStr(chat, "c_msg"));

                // 보낸 시간
                String send_time = StringUtil.converTime(StringUtil.getStr(chat, "c_regdate"), "a hh:mm");

                //읽음처리
                boolean isRead = false;
                String[] idxs = StringUtil.getStr(chat, "read_user_idx").split(",");
                if (idxs.length > 1)
                    isRead = true;


                list.add(new ChatData(m_idx, isImage(contents), send_time, "", contents, isRead));


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setList(list);
                        binding.recyclerView.smoothScrollToPosition(adapter.getItemCount());
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    private String isImage(String msg) {
        String reg = "^([\\S]+(\\.(?i)(jpg|png|jpeg))$)";

        return msg.matches(reg) ? "image" : "text";
    }
}