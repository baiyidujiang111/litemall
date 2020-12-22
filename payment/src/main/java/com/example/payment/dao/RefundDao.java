package com.example.payment.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.example.payment.mapper.PaymentPoMapper;
import com.example.payment.mapper.RefundPoMapper;
import com.example.payment.model.bo.RefundBo;
import com.example.payment.model.po.RefundPo;
import com.example.payment.model.po.RefundPoExample;
import com.example.payment.model.vo.AmountVo;
import com.example.payment.model.vo.PaymentInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RefundDao {
    @Autowired
    RefundPoMapper refundPoMapper;
    @Autowired
    PaymentPoMapper paymentPoMapper;
    public List<RefundPo> getRefundsByPaymentId(long paymentId)
    {
        RefundPoExample refundExample=new RefundPoExample();
        RefundPoExample.Criteria criteria=refundExample.createCriteria();
        criteria.andPaymentIdEqualTo(paymentId);
        return refundPoMapper.selectByExample(refundExample);
    }

    public ReturnObject createPayment(Long id, PaymentInfoVo vo)
    {
        return null;
    }

    /**
     * @Description: 管理员创建退款信息 todo: mapper
     * @Param:  [paymentId, amountVo]
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: lzn
     * @Date 2020/12/17
     */
    public ReturnObject postRefundsByPayments(Long paymentId, AmountVo amountVo)
    {
        ReturnObject returnObject = null;
        return returnObject;
    }

    /**
     * @Description: 根据订单id查询退款信息
     * @Param:  [orderId]
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: lzn
     * @Date 2020/12/17
     */
    public ReturnObject getRefundsByOrderId(Long orderId)
    {
        RefundPoExample refundPoExample = new RefundPoExample();
        RefundPoExample.Criteria criteria = refundPoExample.createCriteria();
        criteria.andOrderIdEqualTo(orderId);
        List<RefundPo> refundPos = refundPoMapper.selectByExample(refundPoExample);
        List<RefundBo> refundBos = new ArrayList<>(refundPos.size());
        for(RefundPo refundPo:refundPos)
        {
            refundBos.add(new RefundBo(refundPo));
        }
        ReturnObject returnObject = null;
        if(!refundBos.isEmpty())
        {
            returnObject=new ReturnObject(refundBos);
        }
        else
        {
            returnObject=new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return returnObject;
    }

    /**
     * @Description: 根据售后单id查询退款信息
     * @Param:  [orderId]
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: lzn
     * @Date 2020/12/17
     */
    public ReturnObject getRefundsByAftersaleId(Long aftersaleId)
    {
        RefundPoExample refundPoExample = new RefundPoExample();
        RefundPoExample.Criteria criteria = refundPoExample.createCriteria();
        criteria.andOrderIdEqualTo(aftersaleId);
        List<RefundPo> refundPos = refundPoMapper.selectByExample(refundPoExample);
        List<RefundBo> refundBos = new ArrayList<>(refundPos.size());
        for(RefundPo refundPo:refundPos)
        {
            refundBos.add(new RefundBo(refundPo));
        }

        ReturnObject returnObject = null;
        if(!refundBos.isEmpty())
        {
            returnObject= new ReturnObject(refundBos);
        }
        else
        {
            returnObject=new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        return returnObject;
    }
}
