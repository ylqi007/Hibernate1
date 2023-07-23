package com.atguigu.hibernate.helloworld.entities.one2one.primary;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Department {
    private Integer deptId;
    private String deptName;

    private Manager manager;

    @Override
    public String toString() {
        return "Department{" +
                "deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                ", manager=" + manager +
                '}';
    }
}
