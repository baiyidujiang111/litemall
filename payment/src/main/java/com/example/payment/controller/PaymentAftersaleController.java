package com.example.payment.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.oomall.other.service.IAftersaleService;
import com.example.orderservice.OrderServiceDubbo;
import com.example.payment.dao.PaymentDao;
import com.example.payment.dao.RefundDao;
import com.example.payment.model.bo.RefundBo;
import com.example.payment.model.vo.PaymentInfoVo;
import com.example.payment.service.PaymentService;
import com.example.payment.service.RefundService;
import io.swagger.annotations.*;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/payment/aftersales",produces = "application/json;charset=UTF-8")
public class PaymentAftersaleController {




    @Autowired
    RefundService refundService;
    @Autowired
    PaymentService paymentService;
    @Autowired
    HttpServletResponse httpServletResponse;

    @DubboReference(registry = "other")
    IAftersaleService iAftersaleService;

    @PostMapping("{id}/payments")
    @Audit
    public Object createPaymentsForRefund(@PathVariable Long id, @RequestBody PaymentInfoVo vo)
    {

        return Common.getRetObject(paymentService.createAftersalePayment(id,vo));
    }


    /**
     * @Description: 买家查询自己的支付信息(售后单) todo:还没完成dubbo调用
     * @Param:  [aftersaleId]
     * @return: java.lang.Object
     * @Author: lzn
     * @Date 2020/12/17
     */
    @ApiOperation(value = "买家查询自己的支付信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "id",value = "支付单id",required = true,dataType = "Integer",paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("{id}/payments")
    @Audit
    public Object getPaymentByAftersaleId(@PathVariable("id")Long aftersaleId,@LoginUser Long userid)
    {
        /*先校验一下该aftersaleId是不是本用户自己的*/
        Long checkedUserId = iAftersaleService.findUserIdbyAftersaleId(aftersaleId).getData();
        if(checkedUserId==null)
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            return Common.decorateReturnObject(returnObject);
        }
        if(!userid.equals(checkedUserId))
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            return Common.decorateReturnObject(returnObject);
        }
        /* 若正常，接着处理 */
        ReturnObject returnObject = paymentService.getPaymentsByAftersaleId(aftersaleId);
        if(returnObject.getCode()==ResponseCode.OK)
        {
            return Common.getListRetObject(returnObject);
        }else {
            return Common.decorateReturnObject(returnObject);
        }
    }



    /**
     * @Description: 买家查询自己的退款信息 todo:还没完成dubbo调用
     * @Param:  [aftersaleId, userId]
     * @return: java.lang.Object
     * @Author: lzn
     * @Date 2020/12/17
     */
    @GetMapping("{id}/refunds")
    @Audit
    public Object getRefundByAftersaleId(@PathVariable("id") long aftersaleId,@LoginUser Long userId)
    {
        /*先校验一下该aftersaleId是不是本用户自己的*/
        Long checkId = iAftersaleService.findUserIdbyAftersaleId(aftersaleId).getData();
        if(checkId==null)
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
            return Common.decorateReturnObject(returnObject);
        }
        if(!userId.equals(checkId))
        {
            ReturnObject returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
            return Common.decorateReturnObject(returnObject);
        }


        /* 若正常，接着处理 */
        /*得到refund*/
        ReturnObject returnObject = refundService.getRefundsByAftersaleId(aftersaleId);
        if(returnObject.getCode()==ResponseCode.OK)
        {
            return Common.getListRetObject(returnObject);
        }else {
            return Common.decorateReturnObject(returnObject);
        }
    }

//    @GetMapping("{id}/payments")
//    public Object getAftersaleByAftersaleId(@PathVariable("id")long aftersaleId)
//    {
//        return ResponseUtil.ok(paymentDao.getPaymentsByAftersaleId(aftersaleId));
//    }
//    @GetMapping("{id}/refunds")
//    //在payment表中通过aftersaleid找id（paymentid），然后在refund表通过paymentid找refund
//    public Object getRefundsByAftersaleId(@PathVariable("id")long aftersaleId)
//    {
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
