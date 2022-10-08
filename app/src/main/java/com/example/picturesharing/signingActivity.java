package com.example.picturesharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

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

public class signingActivity extends AppCompatActivity implements View.OnClickListener{
    //控件
    private EditText mEtReUsername;
    private EditText mEtpassword;
    private EditText mEtRepassword;
    private EditText mEtvercode;
    private Button mBtRegister;
    private ConstraintLayout blank;
    String username;
    String password;
    //验证码
    private String realCode;
    private ImageView mIvReShowcode;
    //接口

    private Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signing);

        initView();

        //将验证码用图片的形式显示出来
        mIvReShowcode.setImageBitmap(Code.getInstance().createBitmap());
        realCode = Code.getInstance().getCode().toLowerCase();
    }

    private void initView() {
        //找到控件
        mEtReUsername = findViewById(R.id.username_sign);
        mEtpassword = findViewById(R.id.password_sign);
        mEtRepassword = findViewById(R.id.password_sign_again);
        mEtvercode = findViewById(R.id.VerCode);
        mBtRegister = findViewById(R.id.sign_true);
        mIvReShowcode = findViewById(R.id.iv_registeractivity_showCode);
        blank = findViewById(R.id.ConstraintLayout);
        //设置点击事件，两个可点击事件，一个是验证码位置，一个是注册按钮
        mIvReShowcode.setOnClickListener(this);
        mBtRegister.setOnClickListener(this);

        //点击空白区域收起软键盘
        blank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    public void onClick(View view) {
        //点击事件
        switch (view.getId()){
            case R.id.iv_registeractivity_showCode:    //改变随机验证码的生成
                mIvReShowcode.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                break;
            case R.id.sign_true://注册按钮
                //获取用户输入的信息
                username = mEtReUsername.getText().toString().trim();
                String password1 = mEtpassword.getText().toString().trim();
                String password2 = mEtRepassword.getText().toString().trim();
//                对比验证码
                String verCode = mEtvercode.getText().toString().toLowerCase();
                if (verCode.equals(realCode)){
                    if(password1.equals(password2)){
                        password = password1;
                    }else {
                        Toast.makeText(getApplicationContext(),"两次密码不一致",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(),"验证码不正确",Toast.LENGTH_SHORT).show();
                }

                post();
                break;

        }

    }

    public void post(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/user/register";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("Accept", "application/json, text/plain, */*")
                    .add("appId", "3d94d2a8649e48cd9bff4d8e106f2e80")
                    .add("appSecret", "371691c334806b1a04dc2a74117e98de5e7f7")
                    .add("Content-Type", "application/json")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("password", username);
            bodyMap.put("username", password);
            // 将Map转换为字符串类型加入请求体中
            String body = gson.toJson(bodyMap);

            MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(RequestBody.create(MEDIA_TYPE_JSON, body))
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
//            Log.d("info", body);
            // 解析json串到自己封装的状态
            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Map<String,String> reData = (Map<String, String>) dataResponseBody.getData();

//在子线程中加载UI
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
                            if(dataResponseBody.getCode() == 200){
                                Toast.makeText(getApplicationContext(),"注册成功",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(signingActivity.this,MainActivity.class));
                                Log.d("info", "Sign onResponse: 注册成功");
                            }else {
                                Toast.makeText(getApplicationContext(),dataResponseBody.getMsg(),Toast.LENGTH_SHORT).show();
                                Log.d("info", "Sign onResponse: 注册失败");
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