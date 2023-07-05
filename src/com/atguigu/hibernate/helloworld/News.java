package com.atguigu.hibernate.helloworld;

import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

@Getter
@Setter
public class News {
    private Integer id;
    private String title;
    private String author;
    private Date date;

    public News() {
    }

    public News(String title, String author, Date date) {
        this.title = title;
        this.author = author;
        this.date = date;
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", date=" + date +
                '}';
    }
}
