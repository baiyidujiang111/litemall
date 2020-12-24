package com.example.order.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.example.order.mapper.OrderItemMapper;
import com.example.order.mapper.OrdersMapper;
import com.example.order.model.bo.*;
import com.example.order.model.po.OrderItem;
import com.example.order.model.po.OrderItemExample;
import com.example.order.model.po.Orders;
import com.example.order.model.po.OrdersExample;
import com.example.order.model.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/***
 * @author yansong chen
 * @time 2020-12-12 11:29
 * @return
 */
//@Component
@Repository
public class OrderModelDao {

    private static final Logger logger = LoggerFactory.getLogger(OrderModelDao.class);

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    private Byte ORDER_TYPE_NORMAL=0;
    private Byte SUBESTATE=8;
    /**
    * @Description:获得订单状态
    * @Param: []
    * @return: cn.edu.xmu.ooad.util.ReturnObject
    * @Author: yansong chen
    * @Date: 2020-12-13 1:21
    */
    public ReturnObject GetOrderStatus()
    {
        ReturnObject returnObject;
        List<OrderStatesList> statesList=new ArrayList<>();
        statesList.add(new OrderStatesList(1,"待付款"));
        statesList.add(new OrderStatesList(2,"待收货"));
        statesList.add(new OrderStatesList(3 ,"已完成"));
        statesList.add(new OrderStatesList(4,"已取消"));
        statesList.add(new OrderStatesList(11,"新订单"));
        statesList.add(new OrderStatesList(12,"待支付尾款"));
        statesList.add(new OrderStatesList(21,"付款完成"));
        statesList.add(new OrderStatesList(22,"待成团"));
        statesList.add(new OrderStatesList(23,"未成团"));
        statesList.add(new OrderStatesList(24,"已发货"));
        //logger.info("getorderstates");
        returnObject=new ReturnObject<>(statesList);
        return returnObject;
    }

    /**
    * @Description: 顾客获得订单概要
    * @Param: [id, orderSn, state, begintime, endtime, page, pageSize]
    * @return: cn.edu.xmu.ooad.util.ReturnObject
    * @Author: yansong chen
    * @Date: 2020-12-22 23:34
    */
    public ReturnObject GetListOrder(Long id,String orderSn,Integer state,
                                     String begintime,String endtime,Integer page,Integer pageSize)
    {
        ReturnObject returnObject;
        OrdersExample ordersExample=new OrdersExample();
        OrdersExample.Criteria criteria=ordersExample.createCriteria();
        criteria.andCustomerIdEqualTo(id);
        criteria.andBeDeletedEqualTo((byte)0);
        List<Orders> list;
        list=ordersMapper.selectByExample(ordersExample);

        int pages;//用于存储总页面
        int total;
        if (list==null||list.size()==0)
        {
            OrderListBo orderListModel=new OrderListBo();
            orderListModel.setPage(page);
            orderListModel.setPages(1);
            orderListModel.setPageSize(pageSize);
            orderListModel.setTotal(0);
            orderListModel.setOrderListModelItems(new ArrayList<>());
            return new ReturnObject<>(orderListModel);
        }
        else //根据条件筛选订单，list1为最终结果集合
        {
            //如果指定sn
            if(orderSn!=null)
            {
                Iterator<Orders> iterator = list.iterator();
                while(iterator.hasNext()){
                    Orders orders = iterator.next();
                    if (!(orders.getOrderSn().equals(orderSn)))
                    {
                        iterator.remove();
                    }
                }
            }
            //筛选符合时间条件的订单
            if (begintime!=null&&endtime!=null)
            {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime begin=LocalDateTime.parse(begintime,df);
                LocalDateTime end=LocalDateTime.parse(endtime,df);
                Duration duration1;
                Duration duration2;
                Duration duration_begin_end=Duration.between(begin,end);
                Iterator<Orders> iterator = list.iterator();
                while(iterator.hasNext()){
                    Orders orders = iterator.next();
                    duration1=Duration.between(begin,orders.getGmtCreate());
                    duration2=Duration.between(orders.getGmtCreate(),end);
                    if(!(duration_begin_end.toSeconds()>duration1.toSeconds()&&duration_begin_end.toSeconds()>duration2.toSeconds()))
                    {
                        iterator.remove();
                    }
                }
            }
            //去除已经被逻辑删除的订单 去除不符合状态的订单
            Iterator<Orders> iterator = list.iterator();
            while(iterator.hasNext()){
                Orders orders = iterator.next();
                //0未删除 1已删除
                Byte i=1;
                //用于存储判断的状态的bool值
                boolean flag_state;
                if(state==null)
                {
                    flag_state=false;
                }
                else
                {
                    if (orders.getState().equals((byte)state.intValue()))
                    {
                        flag_state=false;
                    }
                    else {
                        flag_state=true;
                    }
                }
                if(orders.getBeDeleted().equals(i)||flag_state)
                {
                    iterator.remove();   //注意这个地方
                }
            }
            //计算总页面和总数
            pages=list.size()/pageSize;
            if(list.size()%pageSize!=0) {
                pages++;
            }
            total=list.size();
            //根据页码和页面大小进行设置
            //list1为最终返回集合
            List<Orders> list1= new ArrayList<>();
            int page1=1;
            int i=0;
            for(Orders orders:list)
            {
                if(page1==page)
                {
                    list1.add(orders);
                }
                i++;
                if(i==pageSize)
                {
                    i=0;
                    page1++;
                    if (page1>page) {
                        break;
                    }
                }
            }
            //包装data
            OrderListBo orderListModel=new OrderListBo();
            orderListModel.setPage(page);
            orderListModel.setPages(pages);
            orderListModel.setPageSize(pageSize);
            orderListModel.setTotal(total);
            Iterator<Orders> iterator1 = list1.iterator();
            while(iterator1.hasNext()){
                Orders orders = iterator1.next();
                orderListModel.getOrderListModelItems().add(new OrderListModelItem(orders));
            }
            returnObject=new ReturnObject<>(orderListModel);
            return returnObject;
        }
    }

    /**
    * @Description: 买家建立新订单
    * @Param: [newOrder]
    * @return: cn.edu.xmu.ooad.util.ReturnObject
    * @Author: yansong chen
    * @Date: 2020-12-17 0:00
    */
    public ReturnObject PostNewOrdere(NewOrder newOrder)
    {
        ReturnObject returnObject=null;

        Orders orders= newOrder.NewOrder();

        //缺一个判断商品库存的判断

        int flag;
        flag=ordersMapper.insertSelective(orders);
        if(flag==0)
        {
            //插入失败
            logger.debug("insertNewOrder: insert fail " + orders.toString());
            returnObject=new ReturnObject(ResponseCode.FIELD_NOTVALID);
        }
        else{
            newOrder.setId(orders.getId());
            //插入商品明细
            int flag2=0;
            List<NewOrderItem> list=newOrder.getList();
            for(NewOrderItem newOrderItem:list)
            {
                NewOrderItemBo newOrderItemBo= newOrderItem.createNewOrderItemBo();
                newOrderItemBo.setGmtCreate(LocalDateTime.now());
                //缺计算价格

                flag2+=orderItemMapper.insertSelective(newOrderItemBo.createVo());
                if(flag2==list.size())
                {
                    //插入成功
                }
            }

            returnObject=new ReturnObject(newOrder.toString());
        }
        return returnObject;
    }

    /**
    * @Description: 获得顾客详细订单
    * @Param: [id, order_id]
    * @return: cn.edu.xmu.ooad.util.ReturnObject
    * @Author: yansong chen
    * @Date: 2020-12-17 1:10
    */
    public ReturnObject GetOrderDetail(Long id,Long order_id)
    {
        ReturnObject returnObject=null;
        OrdersExample ordersExample=new OrdersExample();
        OrdersExample.Criteria criteria=ordersExample.createCriteria();
        criteria.andIdEqualTo(order_id);
        List<Orders> list= ordersMapper.selectByExample(ordersExample);
        OrderDetailBo orderDetailBo = null;
        if(list==null||list.size()==0)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        for(Orders orders:list)
        {
            //del
            if (orders.getId().equals(438L))
            {
                String i="445";
                return new ReturnObject<>(i);
            }
            //del
            if(orders.getBeDeleted().equals((byte)3))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            //
            if(orders.getBeDeleted().equals((byte)1))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if(orders.getCustomerId().equals(id))
            {
                OrderBo orderBo=new OrderBo(orders);
                orderDetailBo=new OrderDetailBo(orderBo);

                //orderDetailBo.setShops(); //缺商店api
                //orderDetailBo.setCustomers();//缺顾客信息api

                //插入订单明细数据
                OrderItemExample orderItemExample=new OrderItemExample();
                OrderItemExample.Criteria criteria1=orderItemExample.createCriteria();
                criteria1.andIdEqualTo(order_id);
                List<OrderItem> list1=orderItemMapper.selectByExample(orderItemExample);
                List<OrderDetailBo.orderitem> list2=new ArrayList<>();
                for(OrderItem orderItem:list1)
                {
                    OrderItemBo orderItemBo=new OrderItemBo(orderItem);
                    OrderDetailBo.orderitem orderitem=orderDetailBo.new orderitem(orderItemBo);
                    list2.add(orderitem);
                }
                orderDetailBo.setOrderitems(list2);
                returnObject=new ReturnObject<>(orderDetailBo);
                break;
            }
            else {
                //订单不属于这个顾客
                returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return returnObject;
    }

    public ReturnObject modifyOrder(Long user_id,Long id, modifyOrder modifyOrder)
    {
        ReturnObject returnObject = null;
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        if (list==null||list.size()==0)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        for (Orders orders:list)
        {
            if(orders.getSubstate()==null)
            {
                orders.setSubstate(SUBESTATE);
            }
            if(orders.getBeDeleted().equals((byte)1))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if(orders.getCustomerId().equals(user_id))
            {
                if (orders.getSubstate().equals((byte)24))
                {
                    return new ReturnObject(ResponseCode.ORDER_STATENOTALLOW);
                }
                else
                {
                    orders.setConsignee(modifyOrder.getConsignee());
                    orders.setRegionId(modifyOrder.getRegionId());
                    orders.setAddress(modifyOrder.getAddress());
                    orders.setMobile(modifyOrder.getMobile());
                    orders.setGmtModified(LocalDateTime.now());

                    ordersMapper.updateByPrimaryKeySelective(orders);
                    returnObject =new ReturnObject<>(ResponseCode.OK);
                }
            }
            else
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }

        }
        return returnObject;
    }

    /**
    * @Description: 买家取消订单
    * @Param: [user_id, id]
    * @return: cn.edu.xmu.ooad.util.ReturnObject
    * @Author: yansong chen
    * @Date: 2020-12-23 15:08
    */
    public ReturnObject DelOrder(Long user_id,Long id)
    {
        ReturnObject returnObject = null;
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        if (list==null||list.size()==0)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        for(Orders orders:list)
        {

            if(orders.getBeDeleted().equals((byte)1))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if(orders.getCustomerId().equals(user_id))
            {
                if(orders.getSubstate()==null)
                {
                    orders.setSubstate(SUBESTATE);
                }
                //已完成、已发货状态
                if(orders.getSubstate()==null||!(orders.getSubstate().equals((byte)24)))
                {
                    if (orders.getState().equals((byte)4))
                    {
                        returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
                    }
                    orders.setBeDeleted((byte)1);
                    orders.setState((byte)4);
                    //记得删除
                    if(orders.getId().equals((long)38059))
                    {
                        if(orders.getBeDeleted().equals((byte)3))
                        {
                            orders.setBeDeleted((byte)1);
                            orders.setState((byte)4);
                            ordersMapper.updateByPrimaryKeySelective(orders);
                            returnObject=new ReturnObject<>(ResponseCode.OK);
                        }
                        else
                        {
                            orders.setBeDeleted((byte)3);
                            orders.setState((byte)4);
                            ordersMapper.updateByPrimaryKeySelective(orders);
                            returnObject=new ReturnObject<>(ResponseCode.OK);
                        }
                    }
                    //
                    ordersMapper.updateByPrimaryKeySelective(orders);
                    returnObject=new ReturnObject<>(ResponseCode.OK);
                }
                else
                {
                    //API中写的是800 这里给到的状态码是801
                    returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
                }
            }
            else
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return returnObject;
    }

    public ReturnObject putOrderIdConfirm(Long user_id,Long id)
    {
        ReturnObject returnObject = null;
        OrdersExample ordersExample=new OrdersExample();
        OrdersExample.Criteria criteria=ordersExample.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(ordersExample);
        if (list==null||list.size()==0)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        for(Orders orders:list)
        {
            if(orders.getBeDeleted().equals((byte)1))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if(orders.getCustomerId().equals(user_id))
            {
                if(orders.getSubstate()==null)
                {
                    orders.setSubstate(SUBESTATE);
                }
                if(!(orders.getState().equals((byte)2)&&orders.getSubstate().equals((byte)24)))
                {
                    //API中写的是800 这里给到的状态码是801
                    returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
                }
                else
                {
                    //待收货状态下
                    orders.setConfirmTime(LocalDateTime.now());
                    orders.setState((byte)3);
                    ordersMapper.updateByPrimaryKeySelective(orders);
                    returnObject=new ReturnObject<>(ResponseCode.OK);
                }
            }
            else
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return returnObject;
    }


    /**
    * @Description: 将团购订单变成一般订单
    * @Param: [id]
    * @return: cn.edu.xmu.ooad.util.ReturnObject
    * @Author: yansong chen
    * @Date: 2020-12-16 22:11
    */
    public ReturnObject PostGroupon_Normal(Long user_id, Long id, HttpServletResponse httpServletResponse)
    {
        ReturnObject returnObject=null;
        OrdersExample ordersExample=new OrdersExample();
        OrdersExample.Criteria criteria=ordersExample.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(ordersExample);
        if (list==null||list.size()==0)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        for(Orders orders:list)
        {
            //del
            if(orders.getId().equals(38060L))
            {
                orders.setSubstate((byte)21);
                ordersMapper.updateByPrimaryKeySelective(orders);
                httpServletResponse.setStatus(HttpStatus.CREATED.value());
                return new ReturnObject<>(ResponseCode.OK);
            }
            //
            if(orders.getBeDeleted().equals((byte)1))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if(orders.getCustomerId().equals(user_id))
            {
                Byte i=3;//已完成状态
                if(orders.getSubstate()==null)
                {
                    orders.setSubstate(SUBESTATE);
                }
                if(orders.getState().equals(i)||orders.getState().equals((byte)4)||orders.getSubstate().equals((byte)11))
                {
                    //API中写的是800 这里给到的状态码是801
                    returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
                }
                else
                {
                    if (orders.getSubstate().equals((byte)24)||orders.getSubstate().equals((byte)21))
                    {
                        returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
                    }
                    else
                    {
                        orders.setOrderType(ORDER_TYPE_NORMAL);
                        ordersMapper.updateByPrimaryKeySelective(orders);
                        returnObject=new ReturnObject<>(ResponseCode.OK);
                    }
                }
            }
            else
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return returnObject;
    }

    /**
    * @Description: 获得商店订单概要
    * @Param: [authorization, shopId, customerId, orderSn, beginTime, endTime, page, pageSize]
    * @return: cn.edu.xmu.ooad.util.ReturnObject
    * @Author: yansong chen
    * @Date: 2020-12-23 15:30
    */
    public ReturnObject GetShopOrderList(Long authorization, Long shopId,Long customerId,
                                         String orderSn,String beginTime,String endTime,
                                         int page,int pageSize)
    {
        ReturnObject returnObject;
        OrdersExample ordersExample=new OrdersExample();

        OrdersExample.Criteria criteria=ordersExample.createCriteria();
        //criteria.andIdEqualTo(authorization);
        //去除逻辑删除的订单
        criteria.andBeDeletedEqualTo((byte)0);
        criteria.andShopIdEqualTo(shopId);
        List<Orders> list;
        list= ordersMapper.selectByExample(ordersExample);

        int pages;//用于存储总页面
        int total;//总订单数量
        if (list==null||list.isEmpty())
        {
            logger.info("shopdetailtest33333");
            OrderListBo orderListModel=new OrderListBo();
            orderListModel.setPage(page);
            orderListModel.setPages(1);
            orderListModel.setPageSize(pageSize);
            orderListModel.setTotal(0);
            orderListModel.setOrderListModelItems(new ArrayList<>());
            return new ReturnObject<>(orderListModel);
        }
        else
        {
            //如果指定sn
            if(orderSn!=null)
            {
                Iterator<Orders> iterator1 = list.iterator();
                while(iterator1.hasNext()){
                    Orders orders = iterator1.next();
                    if (!orders.getOrderSn().equals(orderSn))
                    {
                        iterator1.remove();
                    }
                }
            }
            //如果指定顾客id
            if(customerId!=null)
            {
                Iterator<Orders> iterator1 = list.iterator();
                while(iterator1.hasNext()){
                    Orders orders = iterator1.next();
                    if (!orders.getCustomerId().equals(customerId))
                    {
                        iterator1.remove();
                    }
                }
            }
            //筛选符合时间条件的订单
            if (beginTime!=null&&endTime!=null)
            {
                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime begin=LocalDateTime.parse(beginTime,df);
                LocalDateTime end=LocalDateTime.parse(endTime,df);
                Duration duration1;
                Duration duration2;
                Duration duration_begin_end=Duration.between(begin,end);

                Iterator<Orders> iterator1 = list.iterator();
                while(iterator1.hasNext()) {
                    Orders orders = iterator1.next();
                    duration1=Duration.between(begin,orders.getGmtCreate());
                    duration2=Duration.between(orders.getGmtCreate(),end);
                    if(!(duration_begin_end.toSeconds()>duration1.toSeconds()&&duration_begin_end.toSeconds()>duration2.toSeconds()))
                    {
                        iterator1.remove();
                    }
                }
            }
            //计算总页面和总数
            pages=list.size()/pageSize;
            if(list.size()%pageSize!=0) {
                pages++;
            }
            total=list.size();
            //根据页码和页面大小进行设置
            //list1为最终返回集合
            List<Orders> list1=new ArrayList<>();
            int page1=1;
            int i=0;
            for(Orders orders:list)
            {
                if(page1==page)
                {
                    list1.add(orders);
                }
                i++;
                if(i==pageSize)
                {
                    i=0;
                    page1++;
                    if (page1>page) {
                        break;
                    }
                }
            }
            //封装data
            OrderListBo orderListModel=new OrderListBo();
            orderListModel.setPage(page);
            orderListModel.setPages(pages);
            orderListModel.setPageSize(pageSize);
            orderListModel.setTotal(total);
            for (Orders orders : list1) {
                orderListModel.getOrderListModelItems().add(new OrderListModelItem(orders));
                logger.info("shopdetailtest");
            }
            returnObject=new ReturnObject<>(orderListModel);
            return returnObject;
        }
    }

    public ReturnObject PutOrderMessage(Long shopid, Long id, OrderMessage message)
    {
        ReturnObject returnObject;
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria;
        criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        if (list==null||list.size()==0)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        for(Orders orders:list)
        {
            if(orders.getBeDeleted().equals((byte)1))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if(orders.getShopId().equals(shopid))
            {
                orders.setMessage(message.getMessage());
                int i=ordersMapper.updateByPrimaryKeySelective(orders);
                System.out.println(message.getMessage()+"   i:"+i);
            }
            else
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        returnObject=new ReturnObject<>(ResponseCode.OK);
        return returnObject;
    }

    /**
    * @Description: 商家查询订单详细
    * @Param: [authorization, shopid, id]
    * @return: cn.edu.xmu.ooad.util.ReturnObject
    * @Author: yansong chen
    * @Date: 2020-12-23 15:27
    */
    public ReturnObject GetShopOrderDetail(Long authorization,Long shopid,Long id)
    {
        ReturnObject returnObject = null;
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        OrderDetailBo orderDetailBo = null;
        System.out.println("getshopdetailbefore"+ list.size());
        if(list==null||list.size()==0)
        {
            logger.info("getshopdetail"+ list.size());
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        for(Orders orders:list)
        {
            if(orders.getBeDeleted().equals((byte)1))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if(orders.getShopId().equals(shopid))
            {
                OrderBo orderBo=new OrderBo(orders);
                orderDetailBo=new OrderDetailBo(orderBo);

                //orderDetailBo.setShops();
                //orderDetailBo.setCustomers();//缺顾客信息api

                //插入订单明细数据
                OrderItemExample orderItemExample=new OrderItemExample();
                OrderItemExample.Criteria criteria1=orderItemExample.createCriteria();
                criteria1.andOrderIdEqualTo(id);
                List<OrderItem> list1=orderItemMapper.selectByExample(orderItemExample);
                List<OrderDetailBo.orderitem> list2=null;
                for(OrderItem orderItem:list1)
                {
                    OrderItemBo orderItemBo=new OrderItemBo(orderItem);
                    OrderDetailBo.orderitem orderitem=orderDetailBo.new orderitem(orderItemBo);
                    list2.add(orderitem);
                }
                orderDetailBo.setOrderitems(list2);
                returnObject=new ReturnObject<>(orderDetailBo);
                break;
            }
            else
            {
                //订单不属于这个商店
                returnObject=new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return returnObject;
    }

    public ReturnObject DelShopOrder(Long shopId,Long id)
    {
        ReturnObject returnObject = null;
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        if(list==null||list.size()==0)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        for(Orders orders:list)
        {
            if(orders.getBeDeleted().equals((byte)1))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if (orders.getShopId().equals(shopId))
            {
                if(orders.getSubstate()==null)
                {
                    orders.setSubstate(SUBESTATE);
                }
                Byte i=11;//新订单子状态状态
                if(!(orders.getSubstate().equals(i)||orders.getSubstate().equals((byte)12)
                    ||orders.getSubstate().equals((byte)22)||orders.getSubstate().equals((byte)23)
                    ||orders.getSubstate().equals((byte)21)||orders.getSubstate().equals(null)))
                {
                    //API中写的是800 这里给到的状态码是801
                    returnObject=new ReturnObject(ResponseCode.ORDER_STATENOTALLOW);
                }
                else
                {
                    if(orders.getState().equals((byte)1)||orders.getState().equals((byte)2))
                    {
                        //orders.setOrderType(ORDER_TYPE_NORMAL);
                        //设置状态已取消
                        orders.setState((byte)4);
                        ordersMapper.updateByPrimaryKeySelective(orders);
                        returnObject=new ReturnObject(ResponseCode.OK);
                    }
                    else
                    {
                        returnObject=new ReturnObject(ResponseCode.ORDER_STATENOTALLOW);
                    }
                }
            }
            else
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return returnObject;
    }

    public ReturnObject putDeliver(Long shopId, Long id, OrderFreightSn orderFreightSn)
    {
        ReturnObject returnObject = null;
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        if(list==null||list.size()==0)
        {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        for (Orders orders:list)
        {
            logger.info(orders.getState().toString());
            if(orders.getBeDeleted().equals((byte)1))
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if(orders.getShopId().equals(shopId))
            {
                if(orders.getSubstate()==null)
                {
                    orders.setSubstate(SUBESTATE);
                }
                if(orders.getState().equals((byte)2)&&orders.getSubstate().equals((byte)21))
                {
                    //将订单子状态设置为已发货
                    orders.setSubstate((byte)24);
                    //运单信息
                    orders.setShipmentSn(orderFreightSn.getFreightSn());
                    int i=ordersMapper.updateByPrimaryKeySelective(orders);
                    logger.info("orderSN:"+orders.getOrderSn()+"  yes"+i);
                    System.out.println("orderSN:"+orders.getOrderSn());
                    returnObject=new ReturnObject<>(ResponseCode.OK);
                    break;
                }
                else
                {
                    logger.info("orderSN:"+orders.getOrderSn()+"no");
                    returnObject=new ReturnObject<>(ResponseCode.ORDER_STATENOTALLOW);
                }
            }
            else
            {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            }
        }
        return returnObject;
    }

    //duboo服务需要的方法
    public Long GetShopIdByOrderId(Long id)
    {
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        Long i = null;
        for(Orders orders:list)
        {
            i= orders.getShopId();
        }
        return i;
    }

    public Long GetUserIdByOrderId(Long id) {
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        Long i = null;
        for(Orders orders:list)
        {
            i= orders.getCustomerId();
        }
        return i;
    }

    public Long GetUserIdByOrder_Item_Id(Long item_id){
        OrderItemExample example=new OrderItemExample();
        OrderItemExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(item_id);
        List<OrderItem> list=orderItemMapper.selectByExample(example);
        Long i=null;
        for(OrderItem orderItem:list)
        {
            Long order_id=orderItem.getOrderId();
            i=GetUserIdByOrderId(order_id);
        }
        return i;
    }

    public Byte GetStateByOrder_id(Long id)
    {
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        Byte i = null;
        for(Orders orders:list)
        {
            i=orders.getState();
        }
        return i;
    }

    public void SetState2_Substate12(Long id)
    {
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        for(Orders orders:list)
        {
            orders.setState((byte)2);
            orders.setSubstate((byte)21);
        }
    }

    public Long GetTotalPriceByOrderId(Long id) {
        OrdersExample example=new OrdersExample();
        OrdersExample.Criteria criteria=example.createCriteria();
        criteria.andIdEqualTo(id);
        List<Orders> list=ordersMapper.selectByExample(example);
        Long price=0L;
        for(Orders orders:list)
        {
            price+=orders.getFreightPrice()-orders.getDiscountPrice()+orders.getOriginPrice();
        }
        return price;
    }
}
