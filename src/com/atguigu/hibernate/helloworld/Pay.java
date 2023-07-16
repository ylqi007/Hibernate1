package com.atguigu.hibernate.helloworld;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pay {
    private int monthlyPay;
    private int yearlyPay;
    private int vocationWithPay;

    private Worker worker;  // Optional
}
