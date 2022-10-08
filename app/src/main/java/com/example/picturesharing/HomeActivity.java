package com.example.picturesharing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
public class HomeActivity extends Fragment {
    private final Gson gson = new Gson();
    private Context context;
    private View listitem;//list视图
    private View homeactivity;//home视图
//    private RecyclerView lvCardList;

    private ArrayList<Map<String, Object>> lists;
//    private SimpleAdapter adapter;
    private ListView listView;
    private MyAdapter adapter;
    String musername;
    String mpassword;
    String userid;


    private String[] theme = {"张三","李四","王五"};
    private String[] content ={"我是张三，你好","我是李四，你好","我是王五，你好"};
    private int[] imageViews = {R.mipmap.ic_launcher,R.drawable.bg3,R.drawable.bg4};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getActivity();
        homeactivity = inflater.inflate(R.layout.activity_home,container,false);
        listitem = inflater.inflate(R.layout.list_item,container,false);

        userid = getArguments().getString("id");
        System.out.println("HomeActivity.hpassword"+userid);
        get();
        initView();//初始化布局

        return homeactivity;
    }

//    List<Map<String, Object>> lists
    private void initView() {
        listView = homeactivity.findViewById(R.id.listView);

        //设置ListView子项的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("HomeActivity.listview");
//                startActivity(new Intent(getActivity(),NoThingActivity.class));
            }
        });

    }

    private void initData(List<Map<String, Object>> lists) {
        System.out.println(lists);

        Log.d("info", "onResponse: " + lists);
        adapter = new MyAdapter(lists,getActivity());
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.setAdapter(adapter);
                System.out.println("刷新");
            }
        });

    }

    private void get(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share?size=20&userId="+userid;

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
            Log.d("info_body", body);
            // 解析json串到自己封装的状态
            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info_datare", dataResponseBody.toString());

            Map<String,Object> data =(Map<String,Object>) dataResponseBody.getData();

            List<Map<String,Object>> records =  (List<Map<String,Object>>)data.get("records");
            Log.d("info", "onResponse: "+records.toString());

            Log.d("info", "onResponse: "+records.get(0).get("imageUrlList").toString());


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
//            Log.d("info", "onResponse: " + lists);
            initData(lists);//初始化布局
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