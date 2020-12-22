package com.example.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import com.example.order.model.vo.OrderDetail;
import com.fasterxml.jackson.core.sym.NameN;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/***
 * @author yansong chen
 * @time 2020-12-22 15:12
 * @description:
 */
@Data
public class OrderDetailBo implements VoObject {
    private Long id;
    private String orderSn;

    private customer customers;
    private shop shops;

    private Long pid;
    private Byte orderType;
    private Byte state;
    private Byte subState;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private LocalDateTime confirmTime;
    private Long originPrice;
    private Long discountPrice;
    private Long freightPrice;
    private Integer rebateNum;
    private String message;
    private Long regionId;
    private String address;
    private String mobile;
    private String consignee;
    private Long couponId;
    private Long grouponId;
    private Long presaleId;
    private String shipmentSn;

    /*
    private Long couponActivityId;
    private Long grouponDiscount;
    private int beDeleted;*/

    private List<orderitem> orderitems= new ArrayList<>();

    public OrderDetailBo(OrderBo orderBo)
    {
        this.orderSn=orderBo.getOrderSn();
        this.id=orderBo.getId();
        this.pid=orderBo.getPid();
        this.consignee=orderBo.getConsignee();
        this.regionId=orderBo.getRegionId();
        this.address=orderBo.getAddress();
        this.freightPrice=orderBo.getFreightPrice();
        this.couponId=orderBo.getCouponId();
        this.orderType=orderBo.getOrderType();
        this.message=orderBo.getMessage();
        this.mobile=orderBo.getMobile();
        //this.couponActivityId=orderBo.getCouponActivityId();
        this.discountPrice=orderBo.getDiscountPrice();
        this.originPrice=orderBo.getOriginPrice();
        this.presaleId=orderBo.getPresaleId();
        //this.grouponDiscount=orderBo.getGrouponDiscount();
        this.rebateNum=orderBo.getRebateNum();
        this.confirmTime=orderBo.getConfirmTime();
        this.shipmentSn=orderBo.getShipmentSn();
        this.state=orderBo.getState();
        this.subState=orderBo.getSubstate();
        //this.beDeleted=orderBo.getBeDeleted();
        this.gmtCreate=orderBo.getGmtCreate();
        this.gmtModified=orderBo.getGmtModified();
        this.grouponId=orderBo.getGrouponId();
    }

    @Override
    public Object createVo() {
        OrderDetail orderDetail=new OrderDetail();
        orderDetail.setCustomers(customers);
        orderDetail.setAddress(address);
        orderDetail.setConfirmTime(confirmTime);
        orderDetail.setConsignee(consignee);
        orderDetail.setCouponId(couponId);
        orderDetail.setDiscountPrice(discountPrice);
        orderDetail.setFreightPrice(freightPrice);
        orderDetail.setGmtCreate(gmtCreate);
        orderDetail.setGmtModified(gmtModified);
        orderDetail.setGrouponId(grouponId);
        orderDetail.setId(id);
        orderDetail.setMessage(message);
        orderDetail.setMobile(mobile);
        orderDetail.setOrderSn(orderSn);
        orderDetail.setOrderType(orderType);
        orderDetail.setOriginPrice(originPrice);
        orderDetail.setPid(pid);
        orderDetail.setPresaleId(presaleId);
        orderDetail.setRebateNum(rebateNum);
        orderDetail.setRegionId(regionId);
        orderDetail.setShipmentSn(shipmentSn);
        orderDetail.setState(state);
        orderDetail.setSubState(subState);
        orderDetail.setOrderItems(orderitems);
        orderDetail.setShop(shops);

        return orderDetail;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }

    @Data
    public class customer{
        private  Long id;
        private String username;
        private String name;
    }

    @Data
    public class shop{
        private Long id;
        private String name;
        private int state;
        private String gmtCreate;
        private String gmtModified;
    }

    @Data
    public class orderitem{
        private Long orderId;
        private Long goodsSkuId;
        private Integer quantity;
        private Long price;
        private Long discount;
        private String name;
        private Long couponActivityId;
        private Long beShareId;

        public orderitem(OrderItemBo orderItemBo)
        {
            this.beShareId=orderItemBo.getBeShareId();
            this.couponActivityId=orderItemBo.getCouponActivityId();
            this.orderId=orderItemBo.getOrderId();
            this.goodsSkuId=orderItemBo.getGoodsSkuId();
            this.quantity=orderItemBo.getQuantity();
            this.price=orderItemBo.getPrice();
            this.name=orderItemBo.getName();
            this.discount=orderItemBo.getDiscount();
        }

    }
}
