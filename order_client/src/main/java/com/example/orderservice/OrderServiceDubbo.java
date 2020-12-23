package com.example.orderservice;

/***
 * @author yansong chen
 * @time 2020-12-21 10:35
 * @description:
 */
public interface OrderServiceDubbo {
    Long GetShopIdByOrderId(Long id);

    Long GetUserIdByOrderId(Long id);

    Long GetUserIdByOrder_Item_Id(Long item_id);

    Byte GetStateByOrder_id(Long id);

    void SetState2_Substate12(Long id);

    Long GetTotalPriceByOrderId(Long id);
}
