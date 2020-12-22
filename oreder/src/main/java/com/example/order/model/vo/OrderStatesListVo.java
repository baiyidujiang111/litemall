package com.example.order.model.vo;

import com.example.order.model.bo.OrderStatesList;
import lombok.Data;

import java.util.List;

/***
 * @author yansong chen
 * @time 2020-12-21 11:41
 * @description:
 */
@Data
public class OrderStatesListVo {
    private List<OrderStates> list;

    public OrderStatesListVo(List<OrderStates> list1)
    {
        this.list=list1;
    }

}
