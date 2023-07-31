package com.atguigu.hibernate.strategy.set;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Customer {
    private Integer customerId;
    private String customerName;
    /**
     * 1. 声明集合类型时，需要使用接口类型，因为hibernate在获取集合类型时，返回的是Hibernate内置的集合类型，而不是Java SE的标准实现。
     * 2. 需要把Set进行初始化，放置发生NPE
     */
    private Set<Order> orders = new HashSet<>();    // 在Test中有customer.getOrders().add(order1);，所以orders必须初始化，否则会发生NPE
}
