package com.example.payment.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.example.payment.dao.PaymentDao;
import com.example.payment.dao.RefundDao;
import com.example.payment.model.vo.AmountVo;
import com.example.payment.service.PaymentService;
import com.example.payment.service.RefundService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "payment/shops",produces = "application/json;charset=UTF-8")
public class PaymentOrderByShopController {

    @Autowired
    PaymentService paymentService;
    @Autowired
    RefundService refundService;
    @Autowired
    HttpServletResponse httpServletResponse;

    
    /** 
    * @Description: 管理员查询订单的支付信息，todo:缺少dubbo远程调用
    * @Param: [shopId, orderId] 
    * @return: java.lang.Object 
    * @Author: alex101
    * @Date: 2020/12/17 
    */
    @ApiOperation(value = "管理员查询订单的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "id",value = "订单id",required = true,dataType = "Integer",paramType = "path"),
            @ApiImplicitParam(name = "shopId",value = "店铺id",required = true,dataType = "Integer",paramType = "path"),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("{shopId}/orders/{id}/payments")
    //比如要找id为9的订单，这个订单的shopid是2，那么只有当访问路径中的shopid为2的时候才能访问，否则会提示无权访问
    public Object getPaymentsByOrderIdAndShopId(@PathVariable("shopId") long shopId,
                                                @PathVariable("id") long orderId)
    {
        /* 先根据orderId查出shopId */
        /* 与传入的shopId一致才可放行 */
        ReturnObject returnObject = new ReturnObject(paymentService.getPaymentsByOrderId(orderId));
        return Common.getRetObject(returnObject);
    }



    /**
     * @Description: 管理员查询售后单的支付信息，todo:缺少dubbo远程调用
     * @Param:  [shopId, aftersaleId]
     * @return: java.lang.Object
     * @Author: lzn
     * @Date 2020/12/17
     */
    @ApiOperation(value = "管理员查询售后单的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "id",value = "售后单id",required = true,dataType = "Integer",paramType = "path"),
            @ApiImplicitParam(name = "shopId",value = "店铺id",required = true,dataType = "Integer",paramType = "path"),

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("{shopId}/aftersales/{id}/payments")
    public Object getPaymentsByAftersaleIdAndShopId(@PathVariable("shopId")long shopId,
                                                    @PathVariable("id")long aftersaleId)
    {
        /* 先根据aftersalfeId查出shopId */
        /* 与传入的shopId一致才可放行 */
        ReturnObject returnObject = new ReturnObject(paymentService.getPaymentsByAftersaleId(aftersaleId));
        if(returnObject.getCode() == ResponseCode.OK)
            return Common.getRetObject(returnObject);
        else
        {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * @Description: 管理员创建退款信息 todo：mapper
     * @Param:  [shopId, paymentId, amountVo]
     * @return: java.lang.Object
     * @Author: lzn
     * @Date 2020/12/17
     */
    @Audit
    @GetMapping("{shopId}/payments/{id}/refunds")
    public Object postRefundsByPayments(@PathVariable("shopId") Long shopId, @PathVariable("id") Long paymentId, @RequestBody AmountVo amountVo)
    {
        ReturnObject returnObject = new ReturnObject(refundService.postRefundsByPayments(shopId, paymentId, amountVo));
        if(returnObject.getCode() == ResponseCode.OK)
            return Common.getRetObject(returnObject);
        else
        {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * @Description: 管理员查询订单的退款信息 todo:缺少dubbo远程调用
     * @Param:  [shopId, orderId]
     * @return: java.lang.Object
     * @Author: lzn
     * @Date 2020/12/17
     */
    @Audit
    @GetMapping("{shopId}/orders/{id}/refunds")
    public Object getRefundsByOrderIdAndShopId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long orderId)
    {
        /* 先根据orderId查出shopId */
        /* 与传入的shopId一致才可放行 */
        ReturnObject returnObject = new ReturnObject(refundService.getRefundsByOrderId(orderId));
        if(returnObject.getCode() == ResponseCode.OK)
            return Common.getRetObject(returnObject);
        else
        {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * @Description: 管理员查询售后单的退款信息 todo:缺少dubbo远程调用
     * @Param:  [shopId, aftersaleId]
     * @return: java.lang.Object
     * @Author: lzn
     * @Date 2020/12/17
     */
    @GetMapping("{shopId}/aftersales/{id}/refunds")
    public Object getRefundsByAftersaleIdAndShopId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long aftersaleId)
    {
        /* 先根据aftersaleId查出shopId */
        /* 与传入的shopId一致才可放行 */
        //ReturnObject returnObject = refundService.getRefundsByAftersaleId(aftersaleId);
//        if(returnObject.getCode() == ResponseCode.OK)
//        {
//            System.out.println("ok");
//            return Common.getListRetObject(returnObject);
//        }


//        if(returnObject.getCode()!=ResponseCode.RESOURCE_ID_NOTEXIST)
//        {
//
//            return Common.getListRetObject(returnObject);
//        }
//
//        else
//        {
//            System.out.println("fail");
//            return Common.decorateReturnObject(returnObject);
//        }
        System.out.println("ccccc");
        ReturnObject returnObject = refundService.getRefundsByAftersaleId(aftersaleId);
        System.out.println(returnObject.getCode());
        System.out.println("ad"+(returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST));
        if(returnObject.getCode()!=ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            System.out.println("dddddd");
            System.out.println("为空吗1"+(returnObject.getData()==null));
            System.out.println("为空吗2"+(returnObject.getData()==""));
            System.out.println("为空吗2"+(returnObject.getData()));
            return Common.getListRetObject(returnObject);
        }


        httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
        System.out.println(returnObject);
        System.out.println("到这一步");
        return Common.getNullRetObj(returnObject, httpServletResponse);
    }


//    @GetMapping("{shopId}/orders/{id}/refund")
//    //比如要找id为9的订单的退款信息，这个订单的shopid是2，那么只有当访问路径中的shopid为2的时候才能访问，否则会提示无权访问
//    public Object getRefundsByOrderIdAndShopId(@PathVariable("shopId") long shopId,
//                                               @PathVariable("id") long orderId)
//    {
//        Orders order=ordersDao.getOrderById(orderId);
//        if(order.getShopId()!=shopId)
//        {
//            return ResponseUtil.fail(ResponseCode.RESOURCE_ID_OUTSCOPE);
//        }
//        List<Payment> paymentList=paymentDao.getPaymentsByOrderId(orderId);
//        List<Refund> refundList=new ArrayList<>();
//        for(Payment p:paymentList)
//        {
//            List<Refund> temp=refundDao.getRefundsByPaymentId(p.getId());
//            refundList.addAll(temp);
//        }
//        return ResponseUtil.ok(refundList);
//    }
//
//
//
//    @GetMapping("{shopId}/aftersales/{id}/payments")
//    public Object getPaymentsByAftersaleIdAndShopId(@PathVariable("shopId")long shopId,
//                                                    @PathVariable("id")long aftersaleId)
//    {
//        //调其他模块的关于aftersale表的方法，要通过aftersale表中的一个id找aftersale的shopid
//        long shopIdGettingFromAfterSale=shopId;
//        if(shopId!=shopIdGettingFromAfterSale)
//        {
//            return ResponseUtil.fail(ResponseCode.RESOURCE_ID_OUTSCOPE);//无权访问
//        }
//        return ResponseUtil.ok(paymentDao.getPaymentsByAftersaleId(aftersaleId));
//    }
//
//    @GetMapping("{shopId}/aftersales/{id}/refund")
//    public Object getRefundsAboutAftersalesByOrderIdAndShopId(@PathVariable("shopId") long shopId,
//                                                              @PathVariable("id")long aftersaleId)
//    {
//        //调其他模块的关于aftersale表的方法，要通过aftersale表中的一个id找aftersale的shopid
//        long shopIdGettingFromAfterSale=shopId;//模拟找aftersaleid所对应的shopid
//        if(shopId!=shopIdGettingFromAfterSale)
//        {
//            return ResponseUtil.fail(ResponseCode.RESOURCE_ID_OUTSCOPE);//无权访问
//        }
//        List<Payment>paymentList=paymentDao.getPaymentsByAftersaleId(aftersaleId);
//        List<Refund>refundList=new ArrayList<>();
//        for(Payment p:paymentList)
//        {
//            List<Refund> temp=refundDao.getRefundsByPaymentId(p.getId());
//            refundList.addAll(temp);
//        }
//        return ResponseUtil.ok(refundList);
//    }
}
