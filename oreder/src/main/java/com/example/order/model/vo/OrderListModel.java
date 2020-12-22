package com.example.order.model.vo;

import lombok.Data;

import java.util.List;

/***
 * @author yansong chen
 * @time 2020-12-16 8:21
 * @description: 返回订单概要的数据
 */
@Data
public class OrderListModel {
    private int page;
    private int pageSize;
    private int total;
    private int pages;//总页数
    private List<OrderListModelItem> list=null;
}
