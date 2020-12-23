package com.example.order.service.impl;

import com.example.order.dao.OrderModelDao;
import com.example.orderservice.OrderServiceDubbo;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/***
 * @author yansong chen
 * @time 2020-12-21 1:18
 * @description:
 */
@DubboService
public class OrderServiceImpl implements OrderServiceDubbo {
    @Autowired
    private OrderModelDao orderModelDao;

    @Override
    public Long GetShopIdByOrderId(Long id) {
        return orderModelDao.GetShopIdByOrderId(id);
    }

    @Override
    public Long GetUserIdByOrderId(Long id) {
        return orderModelDao.GetUserIdByOrderId(id);
    }

    @Override
    public Long GetUserIdByOrder_Item_Id(Long item_id) {
        return orderModelDao.GetUserIdByOrder_Item_Id(item_id);
    }

    @Override
    public Byte GetStateByOrder_id(Long id)
    {
        return orderModelDao.GetStateByOrder_id(id);
    }

    @Override
    public void SetState2_Substate12(Long id) {
        orderModelDao.SetState2_Substate12(id);
    }

    @Override
    public Long GetTotalPriceByOrderId(Long id) {
        return orderModelDao.GetTotalPriceByOrderId(id);
    }


}
