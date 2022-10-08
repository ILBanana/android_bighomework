package com.example.picturesharing;

import android.app.Application;

/**
 * 存储用户信息
 */
public class MyApp extends Application {
    private String m_id;
    private String username;
    private String password;
    private String avatar;
    private String introduce;
    private String sex;
    private String shareId;
    private String userId;
    public void setId(String id){ m_id = id; }
    public String getId() { return m_id; }

    public void setUsername(String username){ this.username = username; }
    public String getUsername() { return username; }

    public void setPassword(String password){ this.password = password; }
    public String getPassword() { return password; }

    public void setAvatar(String avatar){ this.avatar = avatar; }
    public String getAvatar() { return avatar; }

    public void setIntroduce(String introduce){ this.introduce = introduce; }
    public String getIntroduce() { return introduce; }

    public void setSex(String sex){ this.sex = sex; }
    public String getSex() { return sex; }

    public void setShareId(String shareId){ this.shareId = shareId; }
    public String getShareId() { return shareId; }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

    }

}
