package com.example.picturesharing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
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

public class MineActivity extends Fragment {
    private final Gson gson = new Gson();

    private View minepage;
    private RelativeLayout ProfileCard;
    private LinearLayout Minelist;
    private LinearLayout Collectionlist;
    private LinearLayout Likelist;
    private LinearLayout Sharelist;
    private LinearLayout Morelist;


    private ImageView iVportrait;//头像
    private TextView tTname;//用户名
    private ImageView iVseximage;//性别展示
    private TextView tTcontent;//个人介绍
    private TextView quit;//退出登录


    private ImageView mImgView;
    public static String username;
    public static Object sex;
    public static String introduce;
    public static String avatar;//头像
    String url = "https://guet-lab.oss-cn-hangzhou.aliyuncs.com/api/2022/10/01/7254eeb0-933b-4f3f-8ae9-3132eacc16b6.jpg" ;
    String musername;
    String mpassword;
    String id;
    String savatar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        minepage = inflater.inflate(R.layout.activity_mine,container,false);

        ProfileCard=minepage.findViewById(R.id.relativeLayout_mine);
        Minelist=minepage.findViewById(R.id.mine_list);
        Collectionlist=minepage.findViewById(R.id.collection_list);
        Likelist=minepage.findViewById(R.id.like_list);
        Sharelist=minepage.findViewById(R.id.share_list);
        Morelist=minepage.findViewById(R.id.more_list);
        quit=minepage.findViewById(R.id.quittv);

        iVportrait=minepage.findViewById(R.id.portrait);
        tTname=minepage.findViewById(R.id.show_name);
        iVseximage=minepage.findViewById(R.id.sex_image);
        tTcontent=minepage.findViewById(R.id.show_content);


        id = getArguments().getString("id");
        musername = getArguments().getString("username");
        mpassword = getArguments().getString("password");
//        savatar = getArguments().getString("password");
        System.out.println("info.id:"+id);
        System.out.println("info.musername"+musername);
        System.out.println("info.mpassword"+mpassword);
        ProfileCard.setOnClickListener(new View.OnClickListener(){//跳转到修改信息的页面
            @Override
            public void onClick(View v){
                startActivity(new Intent(getActivity(), PerInforActivity.class));
            }
        });

        Minelist.setOnClickListener(new View.OnClickListener(){//跳转到修改信息的页面
            @Override
            public void onClick(View v){
                startActivity(new Intent(getActivity(),ShmineActivity.class));
            }
        });

        Collectionlist.setOnClickListener(new View.OnClickListener(){//跳转到修改信息的页面
            @Override
            public void onClick(View v){
                startActivity(new Intent(getActivity(), shcollectActivity.class));
            }
        });
        Likelist.setOnClickListener(new View.OnClickListener(){//跳转到修改信息的页面
            @Override
            public void onClick(View v){
                startActivity(new Intent(getActivity(), ShlikeActivity.class));
            }
        });
        Sharelist.setOnClickListener(new View.OnClickListener(){//跳转到修改信息的页面
            @Override
            public void onClick(View v){
                startActivity(new Intent(getActivity(), ShfollowActivity.class));
            }
        });

        Morelist.setOnClickListener(new View.OnClickListener(){//跳转到修改信息的页面
            @Override
            public void onClick(View v){
                startActivity(new Intent(getActivity(), NoThingActivity.class));
            }
        });
        quit.setOnClickListener(new View.OnClickListener(){//跳转到修改信息的页面
            @Override
            public void onClick(View v){
                Intent intent=new Intent(getActivity(),MainActivity.class);
                startActivity(intent);
                System.exit(0);//将活动销毁，只剩下一个登陆界面的活动
            }
        });


//        Glide.with(this).load(savatar).into(iVportrait);
        initView();
                return minepage;
    }

    private void action(String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Bitmap bmp = getURLimage(url);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bmp;
                System.out.println("000");
                handle.sendMessage(msg);

            }
        }).start();
    }
    //加载图片
    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(1000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }


    private Handler handle = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    System.out.println("111");
                    Bitmap bmp=(Bitmap)msg.obj;
                    iVportrait.setImageBitmap(bmp);
                    break;
            }
        };
    };


    private void initView() {
        post();
    }

    public void post(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/user/login?" +
                    "password=" +
                    musername+
                    "&username=" +
                    mpassword;

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
//            Log.d("info", body);
            // 解析json串到自己封装的状态
            ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
//            Log.d("info", dataResponseBody.toString());

            Map<String,String> data = (Map<String,String>)dataResponseBody.getData();
            String msg = dataResponseBody.getMsg();
            Log.d("info", "onResponse: "+msg);


            username = data.get("username");
            introduce = (String) data.get("introduce");
            avatar = (String) data.get("avatar");
            System.out.println("MineActivity.username:"+username);
            System.out.println("MineActivity.introduce:"+introduce);

//            String imgUrlList = records.get(i).get("imageUrlList").toString();
//            avatar = avatar.substring(1, avatar.length() - 1);
            if (username!=null){
                tTname.post(new Runnable() {
                    @Override
                    public void run() {
                        tTname.setText(username);

                    }
                });
            }
            if (introduce!=null){
                tTcontent.post(new Runnable() {
                    @Override
                    public void run() {
                            tTcontent.setText(introduce);

                    }
                });
//                System.out.println("MineActivity.introduce:noooo");
            }else {
                System.out.println("MineActivity.introduce:noooo");
            }

            if (avatar!=null){
                iVportrait.post(new Runnable() {
                    @Override
                    public void run() {
                        action(avatar);
                    }
                });
            }

            if (data.get("sex") == null){
                Log.d("sex","no....");
            }else {
                sex = data.get("sex");
                System.out.println("sex:"+sex);
                if (sex.toString().equals("1.0")){
                    System.out.println("女");
                    iVseximage.post(new Runnable() {
                        @Override
                        public void run() {
                            iVseximage.setImageResource(R.drawable.sex_girl);
                        }
                    });
                }else if (sex.toString().equals("2.0")){
                    iVseximage.post(new Runnable() {
                        @Override
                        public void run() {
                            iVseximage.setImageResource(R.drawable.sex_boy);
                        }
                    });
                }else {
                    iVseximage.post(new Runnable() {
                        @Override
                        public void run() {
                            iVseximage.setImageResource(R.drawable.sex_no);
                        }
                    });
                }

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