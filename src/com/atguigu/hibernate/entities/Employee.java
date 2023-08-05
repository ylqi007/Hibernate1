package com.atguigu.hibernate.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Employee {
    private Integer id;
    private String name;
    private float salary;
    private String email;
    private Department department;

    // For testing testFieldQuery2()
    public Employee(float salary, String email, Department department) {
        this.salary = salary;
        this.email = email;
        this.department = department;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name;
    }
}
