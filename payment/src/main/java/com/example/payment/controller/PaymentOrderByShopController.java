package com.example.payment.controller;

import cn.edu.xmu.goods.client.IGoodsService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.service.IAftersaleService;
import com.example.orderservice.OrderServiceDubbo;
import com.example.payment.PaymentApplication;
import com.example.payment.dao.PaymentDao;
import com.example.payment.dao.RefundDao;
import com.example.payment.model.bo.PaymentBo;
import com.example.payment.model.po.PaymentPo;
import com.example.payment.model.vo.AmountVo;
import com.example.payment.model.vo.PaymentInfoVo;
import com.example.payment.service.PaymentService;
import com.example.payment.service.RefundService;
import com.mysql.cj.jdbc.jmx.LoadBalanceConnectionGroupManager;
import io.swagger.annotations.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/shops",produces = "application/json;charset=UTF-8")
public class PaymentOrderByShopController {

    @Autowired
    PaymentService paymentService;
    @Autowired
    RefundService refundService;
    @Autowired
    HttpServletResponse httpServletResponse;

    @DubboReference(registry = "order")
    OrderServiceDubbo orderServiceDubbo;

    @DubboReference(registry = "other")
    IAftersaleService iAftersaleService;


    private static final Logger logger = LoggerFactory.getLogger(PaymentOrderByShopController.class);

    /** 
    * @Description: 管理员查询订单的支付信息，todo:缺少dubbo远程调用，调用order
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
        Long checkShopId = orderServiceDubbo.GetShopIdByOrderId(orderId);
        if(checkShopId==null)
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            return Common.decorateReturnObject(returnObject);

        }

        /*验权限*/
        if(!checkShopId.equals(shopId))
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            return Common.decorateReturnObject(returnObject);
        }

        /* 与传入的shopId一致才可放行 */
        ReturnObject returnObject = paymentService.getPaymentsByOrderId(orderId);
        if(returnObject.getCode()==ResponseCode.OK)
        {
            return Common.getListRetObject(returnObject);
        }else {
            return Common.decorateReturnObject(returnObject);
        }
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
                                                    @PathVariable("id")long aftersaleId) {
        /* 先根据aftersalfeId查出shopId */
        /* 与传入的shopId一致才可放行 */

        ReturnObject userId = iAftersaleService.findUserIdbyAftersaleId(aftersaleId);
        if(userId.getData()==null)
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            return Common.decorateReturnObject(returnObject);
        }

        ReturnObject returnObject = paymentService.getPaymentsByAftersaleId(aftersaleId);
        logger.info(JacksonUtil.toJson(returnObject));
        if (returnObject.getCode() == ResponseCode.OK)
        {
            PaymentBo paymentBo = (PaymentBo)returnObject.getData();
            Long orderId = paymentBo.getOrderId();
            //根据orderId查出shopid
            //todo:检查shop和路径上的shop
            return Common.getRetObject(returnObject);
        }
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
        logger.info("in get getRefundsByOrderIdAndShopId shopId: "+shopId +" order ID "+orderId);
        /* 先根据orderId查出shopId */
        Long checkShopId = orderServiceDubbo.GetShopIdByOrderId(orderId);
        /* 如果这个id是空的话，返回*/
        if(checkShopId==null)
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            return Common.decorateReturnObject(returnObject);
        }
        /* 权限校验 */
        if(!checkShopId.equals(shopId))
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            return Common.decorateReturnObject(returnObject);
        }
        /* 与传入的shopId一致才可放行 */
        ReturnObject returnObject = refundService.getRefundsByOrderId(orderId);
        if(returnObject.getCode() == ResponseCode.OK)
        {
            logger.info(JacksonUtil.toJson(returnObject));
            return Common.getListRetObject(returnObject);
        }
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
    @Audit
    @GetMapping("{shopId}/aftersales/{id}/refunds")
    public Object getRefundsByAftersaleIdAndShopId(@PathVariable("shopId") Long shopId, @PathVariable("id") Long aftersaleId, @LoginUser Long userid)
    {
        /* 先根据aftersaleId查出shopId */
        ReturnObject<Long> checkedShopId = iAftersaleService.findShopIdbyAftersaleId(aftersaleId);
        //缺少一个根据orderItemId查userId的

        /* 判空 */
        if(checkedShopId.getData()==null)
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            return Common.decorateReturnObject(returnObject);
        }




        /* 验证权限 */

        if(!shopId.equals(checkedShopId.getData()))
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            return Common.decorateReturnObject(returnObject);
        }


        /* 与传入的shopId一致才可放行 */
        ReturnObject returnObject = refundService.getRefundsByAftersaleId(aftersaleId);
        if(returnObject.getCode() == ResponseCode.OK)
        {
            return Common.getListRetObject(returnObject);
        }else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @PostMapping("{shopId}/payments/{id}/refund")
    public Object createPaymentsForRefundByShop(@PathVariable long shopId,
                                                @PathVariable("id") long paymentId,
                                                @RequestBody AmountVo vo)
    {

        PaymentBo paymentBo = (PaymentBo) paymentService.getPaymentPoById(paymentId).getData();
        if(paymentBo!=null && paymentBo.getOrderId()!=null)//说明这个payment对应着一个order
        {
            //通过order，根据orderid查shopid
            long orderidByPayment=paymentBo.getOrderId();
            long shopidByOrderService=orderServiceDubbo.GetShopIdByOrderId(orderidByPayment);
            if(shopId!=shopidByOrderService)
            {
                return Common.decorateReturnObject(new ReturnObject());
            }
            return Common.getRetObject(paymentService.createPaymentByShop(paymentId,vo.getAmount()));
        }
        else if(paymentBo.getAftersaleId()!=null)//说明这个payment对应着一个aftersale
        {
            Long aftersaleidbypayment=paymentBo.getAftersaleId();
            Long aftersaleidByAftersaleService=paymentBo.getAftersaleId();
            if(!aftersaleidbypayment.equals(aftersaleidByAftersaleService))
            {
                return Common.decorateReturnObject(new ReturnObject());
            }
            return Common.getRetObject(paymentService.createAftersalePaymentByShop(paymentId,vo.getAmount()));
        }
        else //这个支付单出了问题
        {
            return Common.decorateReturnObject(new ReturnObject());

        }
    }

}
