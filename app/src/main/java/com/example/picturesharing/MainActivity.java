package com.example.picturesharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mBtn_register;
    private Button mBtn_sign;
    private EditText mEtusename;
    private EditText mEtpassword;
    private LinearLayout blank;
    private TextView cBforget;

    private final Gson gson = new Gson();
    private String musername;
    private String mpassword;
    private String isLogin;
    String userID;
    String avatar;
    String introduce;
    String sex;
//    private User user;
    private Map<String,String> userData;

//    private Intent nextPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        //找到控件
        mBtn_register = findViewById(R.id.register);
        mBtn_sign = findViewById(R.id.sign);
        mEtusename = findViewById(R.id.username);
        mEtpassword = findViewById(R.id.password);
        blank=findViewById(R.id.linearLayout);
        cBforget=findViewById(R.id.forget_password);

        mBtn_register.setOnClickListener(this);
        mBtn_sign.setOnClickListener(this);
        cBforget.setOnClickListener(this);



        //点击空白区域收起软键盘
        blank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register://注册
                startActivity(new Intent(MainActivity.this,signingActivity.class));
                break;
            case R.id.sign://登录
                //获取信息
                musername =  mEtusename.getText().toString().trim();
                mpassword = mEtpassword.getText().toString().trim();
//                startActivity(new Intent(MainActivity.this,homepage.class));
                post();
//                if(TextUtils.isEmpty(musername)|| TextUtils.isEmpty(mpassword)){
//                    Toast.makeText(getApplicationContext(),"不能为空",Toast.LENGTH_SHORT).show();
//                }else {
//
//                    post();
////                    Toast.makeText(getApplicationContext(),isLogin,Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(MainActivity.this,homepage.class));
//
//                }
                break;
            case R.id.forget_password:
                startActivity(new Intent(MainActivity.this,NoThingActivity.class));
                break;
            default:
                break;
        }
    }

    public void getID(){
        MyApp myApp=(MyApp)this.getApplication();
        myApp.setId(userID);
        myApp.setUsername(musername);
        myApp.setPassword(mpassword);
        myApp.setAvatar(avatar);
        myApp.setIntroduce(introduce);
        myApp.setSex(sex);


    }

    public void post(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/user/login?" +
                    "password=" +
                    mpassword+
                    "&username=" +
                    musername;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "3d94d2a8649e48cd9bff4d8e106f2e80")
                    .add("appSecret", "371691c334806b1a04dc2a74117e98de5e7f7")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, ""))
                    .build();
            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(callback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback callback = new Callback() {
        @Override
        public void onFailure(@NonNull Call call, IOException e) {
            //TODO 请求失败处理
            e.printStackTrace();
        }
        @Override
        public void onResponse(@NonNull Call call, Response response) throws IOException {
            //TODO 请求成功处理
            Type jsonType = new TypeToken<ResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            // 解析json串到自己封装的状态
            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);

            Map<String,String> data = (Map<String,String>)dataResponseBody.getData();
            String msg = dataResponseBody.getMsg();


            new Thread(new Runnable() {
                @Override
                public void run() {
                    //延迟两秒
                    try {
                        Thread.sleep( 1000 );
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(msg.equals("登录成功")){
                                //登录成功、记录id 跳转页面
                                userID = data.get("id");
                                avatar = data.get("avatar");
                                introduce = data.get("introduce");
//                                sex = data.get("sex");
                                Log.d("info", "login: "+msg+"  id:"+userID);
                                getID();
                                Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(MainActivity.this,homepage.class));
                            }else{
                                //输出错误信息
                                Log.d("info", "login: "+msg);
                                Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }).start();
        }
    };

    /**
     * http响应体的封装协议
     * @param <T> 泛型
     */
    public static class ResponseBody <T> {

        /**
         * 业务响应码
         */
        private int code;
        /**
         * 响应提示信息
         */
        private String msg;
        /**
         * 响应数据
         */
        private T data;

        public ResponseBody(){}

        public int getCode() {
            return code;
        }
        public String getMsg() {
            return msg;
        }
        public T getData() {
            return data;
        }

        @NonNull
        @Override
        public String toString() {
            return "ResponseBody{" +
                    "code=" + code +
                    ", msg='" + msg + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

}