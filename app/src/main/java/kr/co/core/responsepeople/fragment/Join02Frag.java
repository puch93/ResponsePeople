package kr.co.core.responsepeople.fragment;

import android.app.Activity;
import android.hardware.camera2.params.RecommendedStreamConfigurationMap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.core.responsepeople.R;
import kr.co.core.responsepeople.activity.JoinAct;
import kr.co.core.responsepeople.databinding.FragmentJoin02Binding;
import kr.co.core.responsepeople.util.AppPreference;
import kr.co.core.responsepeople.util.Common;
import kr.co.core.responsepeople.util.StringUtil;

public class Join02Frag extends BaseFrag {
    FragmentJoin02Binding binding;
    Activity act;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_PASSWOLD_REGEX_ALPHA_NUM = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,16}$"); // 영문 대소문자 + 8자리 ~ 16자리까지 가능
    Matcher matcher_id;
    Matcher matcher_pw;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_02, container, false);
        act = getActivity();


        binding.rgGender.setTag("M");
        binding.rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_male)
                    binding.rgGender.setTag("M");
                else
                    binding.rgGender.setTag("G");
            }
        });

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                matcher_id = VALID_EMAIL_ADDRESS_REGEX.matcher(binding.id.getText().toString());
                matcher_pw = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(binding.pw.getText().toString());
                if (binding.id.length() == 0 || !matcher_id.matches()) {
                    Common.showToast(act, "이메일을 확인해주세요");
                } else if (binding.pw.length() == 0 || !matcher_pw.matches()) {
                    Common.showToast(act, "비밀번호를 확인해주세요");
                } else if (binding.pwConfirm.length() == 0 || !binding.pwConfirm.getText().toString().equalsIgnoreCase(binding.pw.getText().toString())) {
                    Common.showToast(act, "비밀번호를 정확하게 입력해주세요");
                } else {
                    nextProcess();
                }
            }
        });

        setTextWatcher();


        return binding.getRoot();
    }

    private void nextProcess() {
        JoinAct.joinData.setId(binding.id.getText().toString());
        JoinAct.joinData.setPw(binding.pw.getText().toString());
        JoinAct.joinData.setGender((String) binding.rgGender.getTag());


        BaseFrag fragment = new Join03Frag();
        ((JoinAct) act).replaceFragment(fragment);
    }

    private void setTextWatcher() {
        binding.id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                matcher_id = VALID_EMAIL_ADDRESS_REGEX.matcher(binding.id.getText().toString());
                if (binding.id.length() == 0 || !matcher_id.matches()) {
                    binding.checkImgEmail.setSelected(false);
                } else {
                    binding.checkImgEmail.setSelected(true);
                }
            }
        });

        binding.pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (!StringUtil.isNull(text)) {
                    Matcher matcher = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(text);
                    if (matcher.find()) {
                        binding.checkImgPw.setSelected(true);
                        if(binding.pw.getText().toString().equalsIgnoreCase(binding.pwConfirm.getText().toString())) {
                            binding.checkImgPwConfirm.setSelected(true);
                        } else {
                            binding.checkImgPwConfirm.setSelected(false);
                        }
                    } else {
                        binding.checkImgPw.setSelected(false);
                        binding.checkImgPwConfirm.setSelected(false);
                    }
                } else {
                    binding.checkImgPw.setSelected(false);
                    binding.checkImgPwConfirm.setSelected(false);
                }


//                matcher_pw = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(binding.pw.getText().toString());
//                if (binding.pw.length() == 0 || !matcher_pw.matches()) {
//                    binding.checkImgPw.setSelected(false);
//                } else {
//                    binding.checkImgPw.setSelected(true);
//                }
            }
        });
        binding.pwConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                matcher_id = VALID_EMAIL_ADDRESS_REGEX.matcher(binding.id.getText().toString());
                matcher_pw = VALID_PASSWOLD_REGEX_ALPHA_NUM.matcher(binding.pw.getText().toString());
                if (binding.pw.length() == 0 || !matcher_pw.matches()) {
                    binding.checkImgPwConfirm.setSelected(false);
                } else if (binding.pwConfirm.length() == 0 || !binding.pwConfirm.getText().toString().equalsIgnoreCase(binding.pw.getText().toString())) {
                    binding.checkImgPwConfirm.setSelected(false);
                } else {
                    binding.checkImgPwConfirm.setSelected(true);

                    if (binding.id.length() == 0 && !matcher_id.matches()) {
                        binding.btnNext.setSelected(false);
                    } else {
                        binding.btnNext.setSelected(true);
                    }
                }
            }
        });
    }
}
