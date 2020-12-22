package com.example.payment.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.example.orderservice.OrderServiceDubbo;
import com.example.payment.dao.PaymentDao;
import com.example.payment.dao.RefundDao;
import com.example.payment.model.vo.PaymentInfoVo;
import com.example.payment.service.PaymentService;
import com.example.payment.service.RefundService;
import io.swagger.annotations.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/payment/orders",produces = "application/json;charset=UTF-8")
public class PaymentOrderController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentOrderController.class);


    @DubboReference
    OrderServiceDubbo orderServiceDubbo;

    @Autowired
    PaymentService paymentService;

    @Autowired
    RefundService refundService;

    @Autowired
    HttpServletResponse httpServletResponse;

    /**
    * @Description:  用户创建支付单，todo:暂无思路
    * @Param: [id, vo]
    * @return: java.lang.Object
    * @Author: alex101
    * @Date: 2020/12/16
    */
    @ApiOperation(value = "买家为订单创建支付单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "id",value = "订单id",required = true,dataType = "Integer",paramType = "path"),
            @ApiImplicitParam(name ="body",value = "支付信息",required = true,dataType = "PaymentInfoVo",paramType = "body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @PostMapping("{id}/payments")
    public Object createPayment(@PathVariable Long id, @RequestBody PaymentInfoVo vo)
    {

        /* 待完成 还没想好怎么做 */
        return paymentService.createPayment(id,vo);
    }

    /** 
    * @Description: 买家查询自己的支付信息 todo:还没完成dubbo调用
    * @Param: [id, userId] 
    * @return: java.lang.Object 
    * @Author: alex101
    * @Date: 2020/12/17 
    */
    @ApiOperation(value = "买家查询自己的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "id",value = "订单id",required = true,dataType = "Integer",paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })

    @GetMapping("{id}/payments")
    @Audit
    public Object getPaymentByOrderId(@PathVariable("id") long id,@LoginUser Long userId)
    {

        /*先校验一下该orderid是不是本用户自己的*/
        Long checkID = orderServiceDubbo.GetUserIdByOrderId(id);
        if(checkID==null)
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return Common.getNullRetObj(returnObject,httpServletResponse);
        }
        /*检验权限*/
        if(!checkID.equals(userId))
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            return Common.decorateReturnObject(returnObject);
        }
            /* 若正常，接着处理 */

        ReturnObject returnObject = paymentService.getPaymentsByOrderId(id);
        if(returnObject.getCode()==ResponseCode.OK)
        {
            return Common.getListRetObject(returnObject);
        }else{
            return Common.decorateReturnObject(returnObject);
        }

    }

    /**
     * @Description: 买家查询自己的退款信息 todo:还没完成dubbo调用
     * @Param:  [orderId, userId]
     * @return: java.lang.Object
     * @Author: lzn
     * @Date 2020/12/17
     */
    @GetMapping("{id}/refunds")
    @Audit
    public Object getRefundByOrderId(@PathVariable("id") long orderId,@LoginUser Long userId)
    {

        /*根据orderId获取UserId*/
        logger.info("in get refund by order id: "+orderId+" userID: "+userId);
        Long checkID = orderServiceDubbo.GetUserIdByOrderId(orderId);
        logger.info("checked ID "+checkID);

        /* 若路径上的资源ID不存在，返回404 */
        if(checkID==null)
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return Common.getNullRetObj(returnObject,httpServletResponse);
        }

        /*校验权限*/
        if(!userId.equals(checkID))
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            return Common.decorateReturnObject(returnObject);
        }

        /* 若正常，接着处理 */
        ReturnObject returnObject = refundService.getRefundsByOrderId(orderId);
        if(returnObject.getCode()== ResponseCode.OK)
        {
            return Common.getListRetObject(returnObject);
        }
        return Common.decorateReturnObject(returnObject);

    }








//    @GetMapping("{id}/refunds")
//    public Object getRefundByOrderId(@PathVariable("id") long orderId)
//    {
//
//        List<Payment>paymentList=paymentDao.getPaymentsByOrderId(orderId);
//        List<Refund>refundList=new ArrayList<>();
//        for(Payment p:paymentList)
//        {
//            List<Refund> temp=refundDao.getRefundsByPaymentId(p.getId());
//            refundList.addAll(temp);
//        }
//        return ResponseUtil.ok(refundList);
//    }
}
