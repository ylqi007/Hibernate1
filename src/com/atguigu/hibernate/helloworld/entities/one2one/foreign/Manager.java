package com.atguigu.hibernate.helloworld.entities.one2one.foreign;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Manager {
    private Integer mgrId;
    private String mgrName;
    private Department dept;

    @Override
    public String toString() {
        return "Manager{" +
                "mgrId=" + mgrId +
                ", mgrName='" + mgrName + '\'' +
                ", dept=" + dept +
                '}';
    }
}
