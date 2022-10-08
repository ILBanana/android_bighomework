package com.example.picturesharing;

public class User {
    private String m_id;
    private String username;
    private String password;
    public User(){ }
    public void setId(String id){m_id = id;}
    public String getId() {
        return m_id;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setPassword(String password){ this.password = password; }
    public String getPassword() {
        return password;
    }
}
