package com.example.payment.dao;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.example.payment.mapper.PaymentPoMapper;
import com.example.payment.model.bo.PaymentBo;
import com.example.payment.model.po.PaymentPo;
import com.example.payment.model.po.PaymentPoExample;
import com.example.payment.model.vo.PayPatternAndNameRetVo;
import com.example.payment.model.vo.PaymentInfoVo;
import com.example.payment.model.vo.StateRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PaymentDao {
    @Autowired
    PaymentPoMapper paymentPoMapper;

    public List<PaymentPo> getAllPayments()
    {
        return paymentPoMapper.selectByExample(null);
    }

    public ReturnObject getPaymentsByOrderId(long orderId)
    {
        ReturnObject returnObject;
        PaymentPoExample paymentExample=new PaymentPoExample();
        PaymentPoExample.Criteria criteria=paymentExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<PaymentPo> paymentPos = paymentPoMapper.selectByExample(paymentExample);
        List<PaymentBo> paymentBos = new ArrayList<>(paymentPos.size());
        if(paymentPos.isEmpty())
        {
            returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }else {
            for(PaymentPo paymentPo:paymentPos)
            {
                paymentBos.add(new PaymentBo(paymentPo));
            }
            returnObject= new ReturnObject(paymentBos);
        }

        return returnObject;
    }
    public ReturnObject getPaymentsByAftersaleId(long aftersaleId)
    {
        ReturnObject returnObject;
        PaymentPoExample paymentExample=new PaymentPoExample();
        PaymentPoExample.Criteria criteria=paymentExample.createCriteria();
        criteria.andAftersaleIdEqualTo(aftersaleId);
        List<PaymentPo> paymentPos = paymentPoMapper.selectByExample(paymentExample);
        List<PaymentBo> paymentBos = new ArrayList<>(paymentPos.size());
        System.out.println(paymentPos.size());;
        if(paymentPos.isEmpty())
        {
            returnObject = new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }else{

            for(PaymentPo paymentPo:paymentPos)
            {
                paymentBos.add(new PaymentBo(paymentPo));
            }
            returnObject = new ReturnObject(paymentBos);
        }
        return returnObject;
    }

    public ReturnObject getAllPaymentsState()
    {
        ReturnObject returnObject = null;
        /* 手写SQL查询订单状态 */
        List<StateRetVo> stateRetVos = new ArrayList<>(3);
        stateRetVos.add(new StateRetVo(Byte.valueOf("0")));
        stateRetVos.add(new StateRetVo(Byte.valueOf("1")));
        stateRetVos.add(new StateRetVo(Byte.valueOf("2")));
        returnObject = new ReturnObject(stateRetVos);
        return returnObject;
    }

    public ReturnObject getAllPaymentPatterns()
    {
        List<PayPatternAndNameRetVo> payPatternAndNameRetVos = new ArrayList<>(2);
        /* 目前只返回两种订单状态 */
        payPatternAndNameRetVos.add(new PayPatternAndNameRetVo("返点支付","001"));
        payPatternAndNameRetVos.add(new PayPatternAndNameRetVo("模拟支付渠道","002"));
        return new ReturnObject(payPatternAndNameRetVos);
    }

    /** 
    * @Description: 创建支付单，todo 
    * @Param: [id, vo] 
    * @return: cn.edu.xmu.ooad.util.ReturnObject 
    * @Author: alex101
    * @Date: 2020/12/16 
    */
    public ReturnObject createPayment(Long id, PaymentInfoVo vo)
    {
        ReturnObject returnObject=null;
        PaymentBo paymentBo = new PaymentBo();
        paymentBo.setPaymentPattern(vo.getPaymentPattern());
        paymentBo.setOrderId(id);
        /* 初始设置支付成功 */
        paymentBo.setState((byte)1 );
        paymentBo.setAftersaleId(null);
        //paymentBo.
        return null;
    }


    /** 
    * @Description: 根据id查询支付信息 
    * @Param: [id] 
    * @return: cn.edu.xmu.ooad.util.ReturnObject 
    * @Author: alex101
    * @Date: 2020/12/17 
    */
    public ReturnObject getPaymentById(Long id)
    {
        ReturnObject returnObject=null;
        PaymentBo paymentBo = new PaymentBo(paymentPoMapper.selectByPrimaryKey(id));
        returnObject = new ReturnObject(paymentBo);
        return returnObject;
    }


    /**
     * @Description: 根据paymentId查询shopId todo: mapper
     * @Param:  [paymentId]
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: lzn
     * @Date 2020/12/17
     */
    public ReturnObject getShopIdByPaymentId(Long paymentId)
    {
        ReturnObject returnObject = null;
        return returnObject;
    }

}
