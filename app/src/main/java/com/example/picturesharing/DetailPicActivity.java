package com.example.picturesharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailPicActivity extends AppCompatActivity {
    private final Gson gson = new Gson();
    private TextView tTname;//个人介绍
    private TextView tTfocusnum;//关注人数
    private TextView tTcollectionnum;//收藏数
    private TextView tTlikenum;//点赞数
    private TextView tTtime;//时间
    private TextView tTcontent;//内容
    private TextView tTtitle;//内容


    private ImageView iVpic;//图片
    String shareId;
    String userId;
    String username;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pic);

        initView();
    }
    private void initView() {

        tTname = findViewById(R.id.username);
        tTfocusnum = findViewById(R.id.focusnum);
        tTcollectionnum = findViewById(R.id.collectionnum);
        tTlikenum = findViewById(R.id.likenum);
        tTtime = findViewById(R.id.time);
        tTcontent = findViewById(R.id.content);
        tTtitle = findViewById(R.id.title);
        iVpic = findViewById(R.id.pic);

        Intent intent = getIntent();
        shareId = intent.getStringExtra("shareId");
        userId = intent.getStringExtra("userId");
        username = intent.getStringExtra("username");
        image = intent.getStringExtra("image");

        Glide
                .with(DetailPicActivity.this)
                .load(image)
                .into(iVpic);
        get();
    }


    private void get(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/detail?shareId=" +
                    shareId +
                    "&userId=" +
                    userId;

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "3d94d2a8649e48cd9bff4d8e106f2e80")
                    .add("appSecret", "371691c334806b1a04dc2a74117e98de5e7f7")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .get()
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
            Log.d("info", dataResponseBody.toString());

            Map<String,Object> data =(Map<String,Object>) dataResponseBody.getData();
            Log.d("info", "onResponse: "+data);
//            Log.d("info_liked","likeNum:"+(double)data.get("likeNum"));


            tTname.setText(username);
//            System.out.println("DetailPicActivity.likeNum:"+(double)data.get("likeNum"));
            if ((Object) data.get("likeNum")==null){
                tTlikenum.setText("0");
            }else {
                tTlikenum.setText(""+(double)data.get("likeNum"));
            }
            if ((Object) data.get("collectNum")==null){
                tTcollectionnum.setText("0");
            }else {
                tTcollectionnum.setText(""+(double)data.get("collectNum"));
            }
            tTtime.setText((String)data.get("createTime"));
            tTcontent.setText((String)data.get("content"));
            tTtitle.setText((String)data.get("title"));


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