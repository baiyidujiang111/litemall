package com.example.order.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import com.example.order.model.vo.OrderListModel;
import com.example.order.model.vo.OrderListModelItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/***
 * @author yansong chen
 * @time 2020-12-20 19:41
 * @description:
 */
@Data
public class OrderListBo implements VoObject {

    private int page;
    private int pageSize;
    private int total;
    private int pages;//总页数
    private List<OrderListModelItem> orderListModelItems=new ArrayList<>();

    public OrderListBo()
    {

    }

    @Override
    public Object createVo() {
        OrderListModel orderListModel=new OrderListModel();
        orderListModel.setPage(page);
        orderListModel.setPageSize(pageSize);
        orderListModel.setTotal(total);
        orderListModel.setPages(pages);
        orderListModel.setList(orderListModelItems);
        return orderListModel;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
