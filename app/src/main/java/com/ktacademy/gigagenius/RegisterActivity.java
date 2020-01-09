package com.ktacademy.gigagenius;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import static com.ktacademy.gigagenius.HttpUtils.isIdValid;
import static com.ktacademy.gigagenius.HttpUtils.isNameVaild;
import static com.ktacademy.gigagenius.HttpUtils.isPwVaild;

public class RegisterActivity extends AppCompatActivity {
    private boolean isMember_idValid = false;
    private boolean isDupValid = false;
    private boolean isMember_pwValid = false;
    private boolean isMember_pwchkValid = false;
    private boolean isMember_nameValid = false;

    private EditText mIdEt;
    private EditText mPwEt;
    private EditText mPwCheckEt;
    private EditText mNameEt;
    private ImageView checkPw;

    private TextView validid;
    private TextView validpw;
    private TextView validname;

    private Button registerBtn;

    private String mId;
    private String mPw;
    private String mName;

    HttpUtils http;
    private String res;
    private MemberVO vo = new MemberVO();
    private Map<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mIdEt = (EditText) findViewById(R.id.mIdEt);
        mPwEt = (EditText) findViewById(R.id.mPwEt);
        mPwCheckEt = (EditText) findViewById(R.id.mPwCheckEt);
        mNameEt = (EditText) findViewById(R.id.mNameEt);
        checkPw = (ImageView) findViewById(R.id.checkPw);

        validid = (TextView) findViewById(R.id.vid);
        validpw = (TextView) findViewById(R.id.vpw);
        validname = (TextView) findViewById(R.id.vname);

        registerBtn = (Button) findViewById(R.id.registerBtn);

        mIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = mIdEt.getText().toString();

                if (isMember_idValid && isDupValid && input.equals(mId)) return;

                if (idCheck(input)) {
                    isDupValid = false;
                    validid.setTextColor(RED);
                    validid.setText("이미 존재하는 아이디입니다.");
                } else if (!isIdValid(validid, input)) {
                    isMember_idValid = false;
                } else {
                    isDupValid = true;
                    isMember_idValid = true;
                    validid.setTextColor(GREEN);
                    validid.setText("사용 가능한 아이디입니다.");
                    mId = input;
                }
            }
        });

        mPwEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = mPwEt.getText().toString();
                if (isMember_pwValid && input.equals(mPw)) return;
                else if (!isPwVaild(validpw, input)) {
                    isMember_pwValid = false;
                } else {
                    isMember_pwValid = true;
                    mPw = input;
                    validpw.setTextColor(GREEN);
                    validpw.setText("사용 가능합니다.");
                }
            }
        });
        mNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = mNameEt.getText().toString();
                if (isMember_nameValid && input.equals(mName)) return;
                if (!isNameVaild(validname, input)) {
                    isMember_nameValid = false;
                } else {
                    isMember_nameValid = true;
                    mName = input;
                    validname.setTextColor(GREEN);
                    validname.setText("사용 가능합니다.");
                }
            }
        });

        mPwCheckEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mPw.equals(mPwCheckEt.getText().toString())) {
                    checkPw.setImageResource(R.drawable.o);
                    isMember_pwchkValid = true;
                } else {
                    checkPw.setImageResource(R.drawable.x);
                    isMember_pwchkValid = false;
                }
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isInputComplete()) {
                    Thread t = new Thread() {
                        public void run() {
                            try {

                                MemberVO tmp = new MemberVO(vo.getMember_no(), mId, mPw, mName);
                                Map<String, String> map = new HashMap<String, String>();
//                                map.put("MemberVO", tmp);
                                map.put("member_id", mId);
                                map.put("member_pw", mPw);
                                map.put("member_mname", mName);


//                                map.put("MemberVO", tmp);
                                String url = "http://70.12.115.73:9090/Chavis/Member/add.do";
                                HttpUtils http = new HttpUtils(HttpUtils.POST, map, url, getApplicationContext());
                                res = http.request();
                            } catch (Exception e) {
                                Log.i("MemberRegisterError", e.toString());
                            }
                        }
                    };
                    t.start();
                    try {
                        t.join();
                        if (res.equals("true")) {
                            Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            RegisterActivity.this.finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isInputComplete() {
        if (isDupValid && isMember_idValid &&
                isMember_nameValid && isMember_pwchkValid && isMember_pwValid)
            return true;
        else
            return false;
    }

    public boolean idCheck(String input) {
        final String id = input;
        map = new HashMap<String, String>();
        Thread t = new Thread() {
            public void run() {
                try {
                    map.put("member_id", id);
                    String url = "http://70.12.115.73:9090/Chavis/Member/dupcheck.do";
                    HttpUtils http = new HttpUtils(HttpUtils.POST, map, url, getApplicationContext());
                    res = http.request();
                } catch (Exception e) {
                    Log.i("MemberIdError", e.toString());
                }
            }
        };
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (Integer.parseInt(res) == 1)
            return true;
        else
            return false;
    }

}