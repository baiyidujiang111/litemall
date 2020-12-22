package com.example.order.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import com.example.order.dao.OrderModelDao;
import com.example.order.model.bo.NewOrder;
import com.example.order.model.vo.NewOrderVO;
import com.example.order.model.vo.OrderFreightSn;
import com.example.order.model.vo.OrderMessage;
import com.example.order.model.vo.modifyOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * @author yansong chen
 * @time 2020-12-10 16:15
 * @return
 */
@Service
public class OrderService {
    @Autowired
    OrderModelDao orderModeldao;

    /**
    * @Description:
    * @Param: []
    * @return: cn.edu.xmu.ooad.util.ReturnObject
    * @Author: yansong chen
    * @Date: 2020-12-13 1:20
    */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject GetOrderStatus()
    {
        return orderModeldao.GetOrderStatus();
    }


    @Transactional
    public ReturnObject GetListOrder(Long id,String orderSn,Integer state,
                                     String begintime,String endtime,Integer page,Integer pageSize)
    {
        return orderModeldao.GetListOrder(id,orderSn,state,begintime,endtime,page,pageSize);
        //return new ReturnObject(String.valueOf(55));
    }

    @Transactional
    public ReturnObject PostOrder(NewOrder newOrder)
    {
        return orderModeldao.PostNewOrdere(newOrder);
    }

    @Transactional
    public ReturnObject GetOrderDetail(Long id,int order_id)
    {
        return orderModeldao.GetOrderDetail(id,order_id);
    }

    @Transactional
    public ReturnObject DelOrder(Long user_id,int id)
    {
        return orderModeldao.DelOrder(user_id,id);
    }

    @Transactional
    public ReturnObject putOrderIdConfirm(int id)
    {
        return orderModeldao.putOrderIdConfirm(id);
    }

    @Transactional
    public ReturnObject PostGroupon_Normal(Long id)
    {
        return orderModeldao.PostGroupon_Normal(id);
    }

    @Transactional
    public ReturnObject GetShopOrderList(Long authorization, Long shopId,
                                         String orderSn,String beginTime,String endTime,
                                         int page,int pageSize)
    {
        return orderModeldao.GetShopOrderList(authorization,shopId,orderSn,beginTime,endTime,page,pageSize);
    }

    @Transactional
    public ReturnObject PutOrderMessage(Long shopid, Long id, OrderMessage message)
    {
        return orderModeldao.PutOrderMessage(shopid,id,message);
    }

    @Transactional
    public ReturnObject GetShopOrderDetail(Long authorization,int shopid,int id)
    {
        return  orderModeldao.GetShopOrderDetail(authorization,shopid,id);
    }

    @Transactional
    public ReturnObject DelShopOrder(Long shopId,Long id)
    {
        return orderModeldao.DelShopOrder(shopId,id);
    }

    @Transactional
    public ReturnObject putDeliver(Long shopId, Long id, OrderFreightSn orderFreightSn)
    {
        return  orderModeldao.putDeliver(shopId,id,orderFreightSn);
    }

    @Transactional
    public ReturnObject modifyOrder(int id, modifyOrder modifyOrder)
    {
        return orderModeldao.modifyOrder(id,modifyOrder);
    }
}
