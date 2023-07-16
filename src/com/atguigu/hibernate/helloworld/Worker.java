package com.atguigu.hibernate.helloworld;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Worker {
    private Integer id;
    private String name;
    private Pay pay;

    public Worker() {}

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", pay=" + pay +
                '}';
    }
}
