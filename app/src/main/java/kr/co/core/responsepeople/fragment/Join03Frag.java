package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.JoinAct;
import kr.co.core.responsepeople.data.JoinData;
import kr.co.core.responsepeople.databinding.FragmentJoin03Binding;
import kr.co.core.responsepeople.server.ReqBasic;
import kr.co.core.responsepeople.server.netUtil.HttpResult;
import kr.co.core.responsepeople.server.netUtil.NetUrls;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class Join03Frag extends BaseFrag {
    FragmentJoin03Binding binding;
    Activity act;

    private String phoneNum = "";
    private boolean isAuthSucceed = false;
    private boolean isAuthProceeding = false;
    private boolean isAuthTimeAfter = false;
    int totalTime = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_03, container, false);
        act = getActivity();

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAuthSucceed) {
                    Common.showToast(act, "전화번호 인증을 먼저 진행해주세요");
                } else {
                    nextProcess();
                }
            }
        });

        binding.btnRegAuthNum.setOnClickListener(v -> {
            if (binding.phoneNum.length() == 0 || !Common.checkCellnum(binding.phoneNum.getText().toString())) {
                Common.showToast(act, "휴대폰번호를 확인해주세요.");
            } else if (isAuthProceeding) {
                Common.showToast(act, "유효시간이 지난 후 시도해주세요.");
            } else {
                binding.authNum.setText("");
                isAuthSucceed = false;
                isAuthTimeAfter = false;
                phoneNum = binding.phoneNum.getText().toString();
                auth_request();
            }
        });
        binding.btnConfirmAuthNum.setOnClickListener(v -> {
            if (isAuthProceeding) {
                auth_confirm();
            } else {
                if (isAuthTimeAfter) {
                    Common.showToast(act, "유효시간이 지났습니다. 다시 요청해주세요.");
                } else {
                    Common.showToast(act, "인증번호를 먼저 요청해주세요.");
                }
            }
        });

        setTextWatcher();
        return binding.getRoot();
    }

    private void nextProcess() {
        JoinAct.joinData.setHp(binding.phoneNum.getText().toString());

        BaseFrag fragment = new Join04Frag();
        ((JoinAct) act).replaceFragment(fragment);
    }

    public void auth_request() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.timeArea.setVisibility(View.VISIBLE);
                                }
                            });

                            isAuthProceeding = true;

                            totalTime = 180000;
                            new CountDownTimer(180000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    totalTime -= 1000;

                                    int minute = (int) (totalTime / (1000 * 60)) % 60;
                                    int second = (int) (totalTime / 1000) % 60;

                                    binding.tick.setText(String.format("%02d", minute) + ":" + String.format("%02d", second));
                                }

                                @Override
                                public void onFinish() {
                                    act.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            isAuthProceeding = false;
                                            isAuthTimeAfter = true;
                                            binding.timeArea.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }
                            }.start();


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

        server.setTag("Request Auth");
        server.addParams("dbControl", NetUrls.AUTH_REQUEST);
        server.addParams("m_hp", binding.phoneNum.getText().toString());
        server.execute(true, false);
    }

    public void auth_confirm() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            isAuthSucceed = true;
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.checkImgAuth.setSelected(true);
                                    binding.btnNext.setSelected(true);
                                    nextProcess();
                                }
                            });
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

        server.setTag("Request Auth");
        server.addParams("dbControl", NetUrls.AUTH_CONFIRM);
        server.addParams("m_hp", binding.phoneNum.getText().toString());
        server.addParams("m_OTP", binding.authNum.getText().toString());
        server.execute(true, false);
    }

    private void setTextWatcher() {
        binding.phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.phoneNum.length() == 0 || !Common.checkCellnum(binding.phoneNum.getText().toString())) {
                    binding.checkImgPhone.setSelected(false);
                } else {
                    binding.checkImgPhone.setSelected(true);
                }
            }
        });
    }
}
