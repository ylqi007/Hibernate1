package com.atguigu.hibernate.helloworld.entities.n2n;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Category {
    private Integer id;
    private String name;
    private Set<Item> items = new HashSet<>();
}
