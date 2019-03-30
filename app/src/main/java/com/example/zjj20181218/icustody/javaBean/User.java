package com.example.zjj20181218.icustody.javaBean;

/**
 * Created by Zjj on 2018/12/18.
 */

public class User {
    private String id;
    private String username;
    private String password;
    private String email;
    private String name;
    private String birth;
    private String gender;
    private String thumb;
    private String type;//登录or注册 数据库查询表

    public User(String email, String username, String password, String type) {
        this.username = email;
        this.username = username;
        this.password = password;
        this.type = type;
    }

    public User(String email, String password, String type) {
        this.username = email;
        this.password = password;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
