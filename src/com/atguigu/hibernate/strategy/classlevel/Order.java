package com.atguigu.hibernate.strategy.classlevel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    private Integer orderId;
    private String orderName;
    private Customer customer;
}
