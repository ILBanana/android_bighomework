package com.example.picturesharing;

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
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

/**
 * 个人信息界面修改
 */
public class PerInforActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    private CheckBox cBgirl;
    private CheckBox cBboy;
    private CheckBox cBsecrecy;
    //上传头像
    private ImageButton chooseImage;//创建一个用户头像
    private byte[] image;//设置头像
    private Bitmap bm;//选择头像的照片
    //用户信息
    private EditText eTchangename;
    private EditText eTchangecontent;
    private Button bTinfortrue;

    private LinearLayout blank;

    private int sex;
    private String username;
    private String content;

    private String imgUrl;
    private Gson gson = new Gson();
    private File file;

    String userID;
    String avatar;
    String introduce;
    String musername;
//    String imgUrlList;
//    String sex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_per_infor);

        initView();
    }

    private void initView() {
        //找到控件
        cBgirl=findViewById(R.id.sex_girl);
        cBboy=findViewById(R.id.sex_boy);
        cBsecrecy=findViewById(R.id.sex_secrecy);

        eTchangename=findViewById(R.id.change_name);//用户名
        eTchangecontent=findViewById(R.id.change_content);//个人简介
        bTinfortrue=findViewById(R.id.infor_true);//发布按钮

        bTinfortrue=findViewById(R.id.infor_true);
        chooseImage =findViewById(R.id.image_head);//选择头像的形式
        blank=findViewById(R.id.linearLayout);

        cBgirl.setOnCheckedChangeListener(this);
        cBboy.setOnCheckedChangeListener(this);
        cBsecrecy.setOnCheckedChangeListener(this);

        MyApp myApp=(MyApp)this.getApplication();
        userID=myApp.getId();

        musername=myApp.getUsername();


        //点击空白区域收起软键盘
        blank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            //ivPwdSwitch对象调用方法，括号里面是new一个接口当参数传入
            @Override
            public void onClick(View view) {
                //检测是否进行了授权
                if (ContextCompat.checkSelfPermission(PerInforActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 权限还没有授予，进行申请
                    ActivityCompat.requestPermissions(PerInforActivity.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    //打开系统相册
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);//请求标识为1
                }
            }

        });


        bTinfortrue.setOnClickListener(new View.OnClickListener() {//确定修改信息按钮
            @Override
            public void onClick(View view) {
            //判读性别是否已填写，判断用户名是否已修改

                username = eTchangename.getText().toString().trim();
                content = eTchangecontent.getText().toString().trim();
                if (username == null){ username=musername; }
                if (content == null){ content=" "; }
                if (cBgirl.isChecked()){
                    sex=1;
                }else if (cBboy.isChecked()){
                    sex=2;
                }else if (cBsecrecy.isChecked()){
                    sex=0;
                }
                System.out.println("PerInforActivity.username:"+username);
                System.out.println("PerInforActivity.content:"+content);
                System.out.println("PerInforActivity.sex:"+sex);
                System.out.println("PerInforActivity.userID:"+userID);
                upPost();
             //添加这里，添加这里！！！
            }

        });

    }


    /**
     * 复选框只能选择一个
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.sex_girl:
                if (cBgirl.isChecked()){
                    //setChecked(),更改此按钮的选中状态 如果为false,则不能选中该控件
                    cBboy.setChecked(false);
                    cBsecrecy.setChecked(false);
//                    sex="女";
                }
                break;
            case R.id.sex_boy:
                if (cBboy.isChecked()){
                    cBgirl.setChecked(false);
                    cBsecrecy.setChecked(false);
//                    sex="男";
                }
                break;
            case R.id.sex_secrecy:
                if (cBsecrecy.isChecked()){
                    cBgirl.setChecked(false);
                    cBboy.setChecked(false);
//                    sex="未知";
                }
                break;
            default:
                break;
        }
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
        image = baos.toByteArray();//接收读取到的字符，即图片的路径
        Log.d("TAG", "showImage: "+image);
        chooseImage.setImageBitmap(bm);//设置头像

    }

    String imgUrlList;
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
            Type jsonType = new TypeToken<PubInterface.ResponseBody<Object>>(){}.getType();
            // 获取响应体的json串
            String body = response.body().string();
            Log.d("info", body);
            // 解析json串到自己封装的状态
            PubInterface.ResponseBody<Object> dataResponseBody = gson.fromJson(body,jsonType);
            Log.d("info", dataResponseBody.toString());

            Map<String,Object> data =  (Map<String,Object>) dataResponseBody.getData();
            Log.d("info", "onResponse: "+data.get("imageCode"));
            Log.d("info", "onResponse: "+data.get("imageUrlList"));
//            imageCode = (String) data.get("imageCode");
            imgUrlList = (String) data.get("imageUrlList").toString();
            imgUrlList = imgUrlList.substring(1, imgUrlList.length() - 1);
            System.out.println("PerInforActivity.imgUrlList"+imgUrlList);
            turnPost();
        }
    };

    private void turnPost(){
        new Thread(() -> {

            // url路径
            String url = "http://47.107.52.7:88/member/photo/user/update";

            // 请求头
            Headers headers = new Headers.Builder()
                    .add("appId", "3d94d2a8649e48cd9bff4d8e106f2e80")
                    .add("appSecret", "371691c334806b1a04dc2a74117e98de5e7f7")
                    .add("Accept", "application/json, text/plain, */*")
                    .build();

            avatar = imgUrlList.substring(1, imgUrlList.length() - 1);
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("avatar", avatar);
            bodyMap.put("id",userID);
            bodyMap.put("introduce", content);
            bodyMap.put("sex", sex);
            bodyMap.put("username", username);
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
                client.newCall(request).enqueue(trunCallback);
            }catch (NetworkOnMainThreadException ex){
                ex.printStackTrace();
            }
        }).start();
    }

    /**
     * 回调
     */
    private final Callback trunCallback = new Callback() {
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