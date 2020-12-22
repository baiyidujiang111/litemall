package com.example.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import com.example.order.model.po.Orders;
import com.example.order.model.vo.NewOrderItem;
import com.example.order.model.vo.OrderDetail;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/***
 * @author yansong chen
 * @time 2020-12-16 20:43
 * @description:
 */
@Data
public class NewOrder implements VoObject {

    private Long id;
    private Long customerId;
    private Long shopId;

    private String orderSn;
    private Long pid;
    private String consignee;
    private Long regionId;
    private String address;
    private String mobile;
    private String message;
    private Byte orderType;
    private Long freightPrice;
    private Long couponId;
    private Long couponActivityId;
    private Long discountPrice;
    private Long originPrice;
    private Long presaleId;
    private Long grouponDiscount;
    private Integer rebateNum;
    private LocalDateTime confirmTime;
    private String shipmentSn;
    private Byte state;
    private Byte substate;
    private Byte beDeleted;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Long grouponId;

    private List<NewOrderItem> list=new ArrayList<>();

    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    public Orders NewOrder()
    {
        Orders orders=new Orders();
        orders.setAddress(address);
        orders.setBeDeleted((byte)beDeleted);
        orders.setConfirmTime(confirmTime);
        orders.setConsignee(consignee);
        orders.setCouponActivityId(couponActivityId);
        orders.setCouponId(couponId);
        orders.setCustomerId(customerId);
        orders.setDiscountPrice(discountPrice);
        orders.setFreightPrice(freightPrice);
        orders.setGmtCreate(gmtCreate);
        orders.setGmtModified(gmtModified);
        orders.setGrouponDiscount(grouponDiscount);
        orders.setGrouponId(grouponId);
        orders.setId(id);
        orders.setMessage(message);
        orders.setMobile(mobile);
        orders.setOrderSn(orderSn);
        orders.setOrderType((byte)orderType);
        orders.setOriginPrice(originPrice);
        orders.setPid(pid);
        orders.setPresaleId(presaleId);
        orders.setRebateNum(rebateNum);
        orders.setRegionId(regionId);
        orders.setShipmentSn(shipmentSn);
        orders.setShopId(shopId);
        orders.setState((byte)state);
        orders.setSubstate((byte)substate);

        return orders;
    }

}
