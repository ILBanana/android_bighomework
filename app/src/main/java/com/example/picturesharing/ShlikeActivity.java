package com.example.picturesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ShlikeActivity extends AppCompatActivity {

    private ArrayList<Map<String, Object>> lists;
    private ListView listView;
    private MyAdapter adapter;
    private final Gson gson = new Gson();
    private Context context;
    private View listitem;//list视图
    private View homeactivity;//home视图
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shlike);
        MyApp myApp=(MyApp)this.getApplication();
        userid=myApp.getId();

        get();
//        initView();
    }


    private void initView(List<Map<String, Object>> lists) {
        //找到控件
        listView = findViewById(R.id.listView);
        System.out.println(lists);
        adapter = new MyAdapter(lists,ShlikeActivity.this);
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
            }
        });
    }


    private void get(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/like?userId=" +
                    userid;

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
            Log.d("info", body);
            // 解析json串到自己封装的状态
            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            Log.d("info_datare", dataResponseBody.toString());
            Log.d("info", "onResponse: "+dataResponseBody.getData());
            Map<String,Object> data =(Map<String,Object>) dataResponseBody.getData();

            List<Map<String,Object>> records =  (List<Map<String,Object>>)data.get("records");
            Log.d("info", "onResponse: "+records.toString());

            Log.d("info", "onResponse: "+records.get(0).get("imageUrlList").toString());

            if(dataResponseBody.getData()==null){
                startActivity(new Intent(ShlikeActivity.this,NoThingActivity.class));
            }else {
                lists = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < records.size(); i++) {
//                Log.d("info", "onResponse: "+records.get(i).get("username"));
//                Log.d("info", "onResponse: "+records.get(i).get("title"));
//                Log.d("info", "onResponse: "+records.get(i).get("content")); returnBitMap(str[i])
                    Map<String, Object> map = new HashMap<>();


                    String imgUrlList = records.get(i).get("imageUrlList").toString();
                    imgUrlList = imgUrlList.substring(1, imgUrlList.length() - 1);

                    map.put("image", imgUrlList);
                    map.put("username", (String) records.get(i).get("username"));
                    map.put("title", (String) records.get(i).get("title"));
                    map.put("hasCollect", (Boolean) records.get(i).get("hasCollect"));
                    map.put("hasFocus", (Boolean) records.get(i).get("hasFocus"));
                    map.put("hasLike", (Boolean) records.get(i).get("hasLike"));
                    map.put("likeId", (String) records.get(i).get("likeId"));
                    map.put("collectId", (String) records.get(i).get("collectId"));
                    map.put("id", (String) records.get(i).get("id"));
                    map.put("pUserId", (String) records.get(i).get("pUserId"));
                    map.put("userid", userid);
                    lists.add(map);
                }
                Log.d("info", "onResponse: " + lists);
                initView(lists);//初始化布局
            }


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