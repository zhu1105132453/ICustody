package com.example.zjj20181218.icustody.javaBean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "talk")
public class Talk {
    @DatabaseField
    private int id;
    @DatabaseField
    private int userid;
    @DatabaseField
    private String content;
    @DatabaseField
    private String img;
    @DatabaseField
    private String time;

    public Talk(){

    }

    public Talk(int userid, String content, String img, String time) {
        this.userid = userid;
        this.content = content;
        this.img = img;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Talk{" +
                "id=" + id +
                ", userid=" + userid +
                ", content='" + content + '\'' +
                ", img='" + img + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
