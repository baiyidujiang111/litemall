package com.example.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import com.example.order.model.vo.OrderStates;
import com.example.order.model.vo.OrderStatesListVo;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/***
 * @author yansong chen
 * @time 2020-12-21 11:21
 * @description:
 */
@Data
public class OrderStatesList implements VoObject {
    private int code;
    private String name;

    public OrderStatesList(int code,String name)
    {
        this.code=code;
        this.name=name;
    }

    @Override
    public Object createVo() {
        return new OrderStates(code,name);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
/*    private List<OrderStates> list=new ArrayList<>();

    public OrderStatesList()
    {
        list.add(new OrderStates(1,"待付款"));
        list.add(new OrderStates(2,"待收货"));
        list.add(new OrderStates(3 ,"已完成"));
        list.add(new OrderStates(4,"已取消"));
        list.add(new OrderStates(11,"新订单"));
        list.add(new OrderStates(12,"待支付尾款"));
        list.add(new OrderStates(21,"付款完成"));
        list.add(new OrderStates(22,"待成团"));
        list.add(new OrderStates(23,"未成团"));
        list.add(new OrderStates(24,"已发货"));
    }

    @Override
    public Object createVo() {
        return new OrderStatesListVo(list);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }*/
}
