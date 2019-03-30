package com.example.zjj20181218.icustody.javaBean;

/**
 * Created by Zjj on 2018/12/20.
 */

public class Content {
    private int id;
    private String user_title;
    private String user_content;

    public Content(String user_title, String user_content) {
        this.user_title = user_title;
        this.user_content = user_content;
    }

    public Content(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_title() {
        return user_title;
    }

    public void setUser_title(String user_title) {
        this.user_title = user_title;
    }

    public String getUser_content() {
        return user_content;
    }

    public void setUser_content(String user_content) {
        this.user_content = user_content;
    }
    
}
