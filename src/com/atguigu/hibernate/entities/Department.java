package com.atguigu.hibernate.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
public class Department {
    private Integer id;
    private String name;
    private Set<Employee> employees = new HashSet();

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
