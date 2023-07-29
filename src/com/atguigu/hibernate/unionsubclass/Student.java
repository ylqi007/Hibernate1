package com.atguigu.hibernate.unionsubclass;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Student extends Person {
    private String school;
}
