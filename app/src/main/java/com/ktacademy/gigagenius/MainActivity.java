package com.ktacademy.gigagenius;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private Intent intent;

    String member_id;
    String member_pw;
    String res = null;
    MemberVO vo;
    Map<String, String> map = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        Button registerBtn = (Button) findViewById(R.id.registerBtn);

        final EditText idEditView = (EditText) findViewById(R.id.idEditView);
        final EditText pwEditView = (EditText) findViewById(R.id.pwEditView);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                member_id = idEditView.getText().toString();
                member_pw = pwEditView.getText().toString();
                if (member_id.length() != 0 && member_pw.length() != 0) {
                    Thread t = new Thread() {
                        public void run() {
                            try {
                                map.put("member_id", member_id);
                                map.put("member_pw", member_pw);
                                String url = "http://70.12.115.73:9090/Chavis/Member/login.do";
                                HttpUtils http = new HttpUtils(HttpUtils.POST, map, url, getApplicationContext());
                                res = http.request();
                                Log.i("MemberLoginError", res);
                            } catch (Exception e) {
                                Log.i("MemberLoginError", e.toString());
                            }
                        }
                    };
                    t.start();

                    try {
                        t.join();
                        ObjectMapper mapper = new ObjectMapper();
                        vo = mapper.readValue(res, MemberVO.class);
                        // 바꿔바꿔
                        if (vo.equals("200")) {
                            // 자동 로그인 등록
                            SharedPreferences preferences = getSharedPreferences("preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();

                            editor.putString("myObject", res);
                            editor.commit();
                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                            i.putExtra("vo", vo);
                            i.putExtra("fragment", "login");
                            startActivity(i);
                        } else {
                            Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JsonParseException e) {
                        e.printStackTrace();
                    } catch (JsonMappingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                }
            }

        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 회원가입
                Intent i = new Intent(
                        getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

}}