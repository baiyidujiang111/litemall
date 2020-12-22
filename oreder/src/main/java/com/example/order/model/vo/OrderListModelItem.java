package com.example.order.model.vo;

import com.example.order.model.po.Orders;
import lombok.Data;

import java.time.LocalDateTime;

/***
 * @author yansong chen
 * @time 2020-12-16 8:28
 * @description: 返回订单概要的数据的List_item
 */
@Data
public class OrderListModelItem {
    private Long id;
    private Long customerId;
    private Long shopId;
    private Long pid;
    private Byte orderType;
    private Byte state;
    private Byte subState;
    private LocalDateTime gmtCreate;
    private Long discountPrice;
    private Long originPrice;
    private Long freightPrice;
    private Long grouponId;
    private Long presaleId;
    private String shipmentSn;

    public OrderListModelItem(Orders orders)
    {
        this.id=orders.getId();
        this.customerId=orders.getCustomerId();
        this.shopId=orders.getShopId();
        this.pid=orders.getPid();
        this.orderType=orders.getOrderType();
        this.state=orders.getState();
        this.subState=orders.getSubstate();
        this.gmtCreate=orders.getGmtCreate();
        this.discountPrice=orders.getDiscountPrice();
        this.originPrice=orders.getOriginPrice();
        this.freightPrice=orders.getFreightPrice();
        this.grouponId=orders.getGrouponId();
        this.presaleId=orders.getPresaleId();
        this.shipmentSn=orders.getShipmentSn();
    }

}
