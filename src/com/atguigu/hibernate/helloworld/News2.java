package com.atguigu.hibernate.helloworld;

import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;
import java.util.Date;

@Getter
@Setter
public class News2 {
    private Integer id;
    private String title;
    private String author;
    private Date date;
    private String content; // 大文本
    private Blob image;     // 二进制数据

    public News2() {}

    public News2(String title, String author, Date date) {
        this.title = title;
        this.author = author;
        this.date = date;
    }

    @Override
    public String toString() {
        return "News2{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", date=" + date +
                ", content='" + content + '\'' +
                ", image=" + image +
                '}';
    }
}
