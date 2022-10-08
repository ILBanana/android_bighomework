package com.example.picturesharing;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

public class MyAdapter extends BaseAdapter {
    private final Gson gson = new Gson();
    private List<Map<String, Object>> Datas;
    private Context mContext;
    String url = "https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2022/10/01/7254eeb0-933b-4f3f-8ae9-3132eacc16b6.jpg" ;

    int l = 0;
    int c = 0;
    int f = 0;
    Boolean hasCollect;
    Boolean hasFocus;
    Boolean hasLike;
    String userId;//登录用户ID
    String focusUserId;//被关注用户ID
    String shareId;//图片分享ID
    String likeId;//点赞ID
    String collectId;
    int Like;
    public MyAdapter(List<Map<String, Object>> datas, Context mContext) {
        Datas = datas;
        this.mContext = mContext;
        Log.d("info", "onResponse.MyAdapter: " + datas);
    }

    /**
     * 返回item的个数
     * @return
     */
    @Override
    public int getCount() {
        return Datas.size();
    }

    /**
     * 返回每一个item对象
     * @param i
     * @return
     */
    @Override
    public Object getItem(int i) {
        return Datas.get(i);
    }

    /**
     * 返回每一个item的id
     * @param i
     * @return
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * 暂时不做优化处理，后面会专门整理BaseAdapter的优化
     * @param i
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(R.layout.list_item,viewGroup,false);

        ImageView imageView = (ImageView) view.findViewById(R.id.image_list);
        TextView author = (TextView) view.findViewById(R.id.author_list);
        TextView title = (TextView) view.findViewById(R.id.title_list);
        TextView content = (TextView) view.findViewById(R.id.content_list);
        RelativeLayout card = (RelativeLayout) view.findViewById(R.id.relative);

        ImageView collection = (ImageView) view.findViewById(R.id.collection_list);
        ImageView like = (ImageView) view.findViewById(R.id.like_list);
        ImageView focus = (ImageView) view.findViewById(R.id.focus_list);
//        imageView.setImageResource(Datas.get(i).getImageId());
        hasCollect = (Boolean) Datas.get(i).get("hasCollect");
        hasFocus = (Boolean) Datas.get(i).get("hasFocus");
        hasLike = (Boolean) Datas.get(i).get("hasLike");
        userId = (String) Datas.get(i).get("userid");
//        Log.d("info", "onResponse.MyAdapter.getView: " + hasLike+"...."+i);




//显示内容
        author.setText((String)Datas.get(i).get("username"));
        title.setText((String)Datas.get(i).get("title"));
        content.setText((String)Datas.get(i).get("content"));

//        显示图片
        Glide
                .with(mContext)
                .load((String)Datas.get(i).get("image"))
                .into(imageView);
//        显示收藏图标
        if (hasCollect == true){
            collection.setImageResource(R.mipmap.collectiontrue);
        }else {
            collection.setImageResource(R.mipmap.collection);
        }
//        显示关注图标
        if (hasFocus == true){
            focus.setImageResource(R.mipmap.guanzhu1);
        }else {
            focus.setImageResource(R.mipmap.guanzhu);
        }

//        显示点赞图标
        if (hasLike == true){
            like.setImageResource(R.mipmap.liketrue);

        }else {
            like.setImageResource(R.mipmap.like);
        }
            focus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hasFocus = !hasFocus;
                    if(hasFocus){
                        focus.setImageResource(R.mipmap.guanzhu1);
                        focusUserId = (String) Datas.get(i).get("pUserId");
                        focusPost();
                    }else {
                        focus.setImageResource(R.mipmap.guanzhu);
                        focusUserId = (String) Datas.get(i).get("pUserId");
                        cancelfocusPost();
                    }

                }
            });

            collection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hasCollect = !hasCollect;
                    if(hasCollect){
                        collection.setImageResource(R.mipmap.collectiontrue);
                        shareId = (String) Datas.get(i).get("id");

                        collectPost();
                    }else {
                        collection.setImageResource(R.mipmap.collection);
                        collectId = (String) Datas.get(i).get("collectId");
                        cancelcollectPost();
                    }

                }
            });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hasLike = !hasLike;
                if(hasLike){
                    like.setImageResource(R.mipmap.liketrue);
                    shareId = (String) Datas.get(i).get("id");
                    likePost();
                }else{
                    like.setImageResource(R.mipmap.like);
                    likeId = (String) Datas.get(i).get("likeId");
                    cancellikePost();
                }

            }
        });

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareId = (String) Datas.get(i).get("id");
                Intent intent;
                intent = new Intent(mContext, DetailPicActivity.class);
                intent.putExtra("shareId", shareId);
                intent.putExtra("userId", userId);
                intent.putExtra("username", (String)Datas.get(i).get("username"));
                intent.putExtra("image", (String)Datas.get(i).get("image"));
                mContext.startActivity(intent);

            }
        });
//        此处需要返回view 不能是view中某一个
        return view;
    }


    class ViewHoder{
        private ImageView imageView;
        private TextView author;
        private TextView title;
        private TextView content;
    }
    //----------------------------添加关注--------------------------------------
    private void focusPost(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/focus?focusUserId=" +
                    focusUserId +
                    "&userId=" +
                    userId;

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
                client.newCall(request).enqueue(focusCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback focusCallback = new Callback() {
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
        }
    };
    //----------------------------取消关注--------------------------------------
    private void cancelfocusPost(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/focus/cancel?focusUserId=" +
                    focusUserId +
                    "&userId=" +
                    userId;

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
                client.newCall(request).enqueue(cancelfocusCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback cancelfocusCallback = new Callback() {
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
        }
    };
    //----------------------------点赞--------------------------------------
    private void likePost(){
        new Thread(() -> {

            // url路径
            String url =  "http://47.107.52.7:88/member/photo/like?" +
                    "shareId="+shareId+"&" +
                    "userId="+userId;

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
                client.newCall(request).enqueue(likeCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback likeCallback = new Callback() {
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
        }
    };
    //----------------------------取消点赞--------------------------------------
    private void cancellikePost(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/like/cancel?likeId=" +
                    likeId ;

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
                client.newCall(request).enqueue(cancellikeCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback cancellikeCallback = new Callback() {
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
        }
    };

    //----------------------------添加收藏--------------------------------------
    private void collectPost(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/collect?shareId=" +
                    shareId +
                    "&userId=" +
                    userId;

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
                client.newCall(request).enqueue(collectCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback collectCallback = new Callback() {
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
        }
    };



    //----------------------------取消收藏--------------------------------------

    private void cancelcollectPost(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/collect/cancel?collectId=" +
                    collectId;

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
                client.newCall(request).enqueue(cancelcollectCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback cancelcollectCallback = new Callback() {
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

//
//   focus.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//        focusUserId = (String) Datas.get(i).get("pUserId");
//        if (hasFocus == true){
//        cancelfocusPost();
//        }else {
//        focusPost();
//        }
//        }
//        });


//        like.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("MyAdapter1111");
//                System.out.println("MyAdapter.hasLike:"+hasLike);
//                System.out.println("MyAdapter.hasLike.like:"+Like);
//                if (Like ==1){
//                    likeId = (String) Datas.get(i).get("likeId");
//                    System.out.println("MyAdapter.likeId"+likeId);
//                    System.out.println("MyAdapter.userId"+userId);
//                    cancellikePost();
//                }else if(Like == 0){
//                    shareId = (String) Datas.get(i).get("id");
//                    System.out.println("MyAdapter.shareId"+shareId);
//                    likePost();
//                }
//            }
//        });


//        collection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                System.out.println("MyAdapter.hasCollect"+hasCollect);
//                if (hasCollect == true){
//                    collectId = (String) Datas.get(i).get("collectId");
//                    System.out.println("MyAdapter.likeId"+collectId);
//                    System.out.println("MyAdapter.userId"+userId);
//                    cancelcollectPost();
//                }else if(hasCollect == false){
//                    shareId = (String) Datas.get(i).get("id");
//                    System.out.println("MyAdapter.shareId"+shareId);
//                    collectPost();
//                }
//            }
//        });
