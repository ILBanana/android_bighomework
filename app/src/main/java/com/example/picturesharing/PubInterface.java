package com.example.picturesharing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PubInterface extends AppCompatActivity {

    private byte[] imagedata;
    private Bitmap bm;
    private ImageView iBpicture;
    private EditText eTcontent;
    private EditText eTtitle;
    private LinearLayout blank;
    private Button bsure;
    private File file;
    private final Gson gson = new Gson();

    String content;
    String title;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pub_interface);

        MyApp myApp=(MyApp)this.getApplication();
        id=myApp.getId();

        initView();

    }

    private void initView() {
        //找到控件
        iBpicture = findViewById(R.id.camera_table);
        eTcontent = findViewById(R.id.share_content);
        eTtitle = findViewById(R.id.share_title);
        bsure = findViewById(R.id.share_true);
        blank = findViewById(R.id.pubinter);

        //点击空白区域收起软键盘
        blank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        iBpicture.setOnClickListener(new View.OnClickListener() {//在切换密码是否可见的图标处，添加一个监听事件
            //ivPwdSwitch对象调用方法，括号里面是new一个接口当参数传入
            @Override
            public void onClick(View view) {
                //检测是否进行了授权
                if (ContextCompat.checkSelfPermission(PubInterface.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // 权限还没有授予，进行申请
                    ActivityCompat.requestPermissions(PubInterface.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    //打开系统相册
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);//请求标识为1
                }
            }

        });

        //发布
        bsure.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View view) {
                content = eTcontent.getText().toString().trim();
                title = eTtitle.getText().toString().trim();
                upPost();
                startActivity(new Intent(PubInterface.this,homepage.class));
            }

        });

    }

    /**
     * 关于图片
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //requestCode是用来标识请求的来源(这里是图片点击事件，标识为1）， resultCode是用来标识返回的数据来自哪一个activity
        //        requestCode:请求码，用于启动子Activity
        //resultCode:子Activity设置的结果码，用于指示操作结果。可以是任何整数值，但通常是resultCode = =
        //RESULT_OK或resultCode==RESULT_CANCELED
        //Data:用于打包返回数据的Intent,可以包括用于表示所选内容的URI。子Activity也可以在返回数据Intent时，添加一些附加消息。

        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();//选择照片
            String[] filePathColumns = {MediaStore.Images.Media.DATA};//获取图片路径

            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();//正确指向第一个位置
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            file = new File(imagePath);
            ArrayList<File> fileList = new ArrayList<File>();//不写会显示空指针
            fileList.add(file);
            c.close();
        }
    }

    private void showImage(String imagePath) {

        bm = BitmapFactory.decodeFile(imagePath);//通过BitmapFactory.decodeFile(imagePath)方法来加载图片
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//字符串输出流
        //三个参数分别是压缩后的图像的格式（png），图像显示的质量（0—100），100表示最高质量，图像处理的输出流（baos）。
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imagedata = baos.toByteArray();//接收读取到的字符，即图片的路径
        iBpicture.setImageBitmap(bm);//设置头像

    }

    String imageCode;
    /**
     * 上传文件post
     */
    private void upPost(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/image/upload";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "3d94d2a8649e48cd9bff4d8e106f2e80")
                    .add("appSecret", "371691c334806b1a04dc2a74117e98de5e7f7")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();


            MediaType MEDIA_TYPE_JSON = MediaType.parse("multipart/form-data");
            RequestBody fileBody = RequestBody.create(MEDIA_TYPE_JSON, file);
            MultipartBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("fileList",file.toString(), fileBody)
                    .build();
            //请求组合创建
            Request request = new Request.Builder()
                    .url(url)
                    // 将请求头加至请求中
                    .headers(headers)
                    .post(requestBody)
                    .build();

            try {
                OkHttpClient client = new OkHttpClient();
                //发起请求，传入callback进行回调
                client.newCall(request).enqueue(upCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }
    /**
     * 回调
     */
    private final Callback upCallback = new Callback() {
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

            Map<String,Object> data =  (Map<String,Object>) dataResponseBody.getData();
            Log.d("info", "onResponse: "+data.get("imageCode"));
            imageCode = (String) data.get("imageCode");
            addPost();
        }
    };

    /**
     * 新增图文分享post
     */
    private void addPost(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/share/add";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "3d94d2a8649e48cd9bff4d8e106f2e80")
                    .add("appSecret", "371691c334806b1a04dc2a74117e98de5e7f7")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            // 请求体
            // PS.用户也可以选择自定义一个实体类，然后使用类似fastjson的工具获取json串
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("content", content);
            bodyMap.put("imageCode", imageCode);
            bodyMap.put("pUserId",id);
            bodyMap.put("title", title);
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
                client.newCall(request).enqueue(addCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback addCallback = new Callback() {
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

            /*-------------------自由编码------------------------*/
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