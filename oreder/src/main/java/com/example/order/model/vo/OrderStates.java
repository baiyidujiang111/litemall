package com.example.order.model.vo;

import lombok.Data;

/***
 * @author yansong chen
 * @time 2020-12-21 11:21
 * @description:
 */
@Data
public class OrderStates {
    private int code;
    private String name;

    public OrderStates(int code,String name)
    {
        this.code=code;
        this.name=name;
    }

}
