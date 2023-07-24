package com.atguigu.hibernate.helloworld.entities.n2nbi;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Item {
    private Integer id;
    private String name;
    private Set<Category> categories = new HashSet<>();
}
