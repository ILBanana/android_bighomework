package com.example.picturesharing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

//import cn.edu.cdut.app.adapter.MyFragmentPagerAdapter;
public class homepage extends AppCompatActivity implements View.OnClickListener{

    private RadioGroup  mTabRadioGroup;
    private RadioButton rBhome;
    private RadioButton rBmine;

//    private ViewPager viewPager;
    //Fragment页面
    private HomeActivity homeActivity;
    private MineActivity mineActivity;
    private ImageView iVadd;

    private String hid;
    private String husername;
    private String hpassword;
    private String avatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        initView();
        setMain();
    }

    private void initView() {
        //找到控件
        mTabRadioGroup = findViewById(R.id.bar_bottom);
        rBhome=findViewById(R.id.home_table);
        rBmine=findViewById(R.id.mine_table);
        iVadd=findViewById(R.id.addTo_table);
        //监听事件
        rBhome.setOnClickListener(this);
        rBmine.setOnClickListener(this);
        iVadd.setOnClickListener(this);

        rBhome.setChecked(true);
        //实例化
        homeActivity = new HomeActivity();
        mineActivity = new MineActivity();

        MyApp myApp=(MyApp)this.getApplication();
        hid=myApp.getId();
        husername=myApp.getUsername();
        hpassword=myApp.getPassword();
//        avatar=myApp.getPassword();
        System.out.println("homepage.hid"+hid);
        System.out.println("homepage.hpassword"+hpassword);
        System.out.println("homepage.hpassword"+hpassword);

        Bundle bundle = new Bundle();
        bundle.putString("id", hid);
        bundle.putString("username", husername);
        bundle.putString("password", hpassword);
//        bundle.putString("avatar", avatar);
        homeActivity = new HomeActivity();
        homeActivity.setArguments(bundle);
        mineActivity = new MineActivity();
        mineActivity.setArguments(bundle);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.home_table:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_homepage,homeActivity).addToBackStack(null).commitAllowingStateLoss();
                break;
            case R.id.addTo_table:
                startActivity(new Intent(homepage.this,PubInterface.class));
                break;
            case R.id.mine_table:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_homepage,mineActivity).addToBackStack(null).commitAllowingStateLoss();
                break;

        }
    }

    //用于打开初始页面
    private void setMain() {
        this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_homepage,homeActivity).commit();
    }

}