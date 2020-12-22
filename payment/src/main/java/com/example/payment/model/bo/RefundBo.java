package com.example.payment.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import com.example.payment.model.po.RefundPo;
import com.example.payment.model.vo.RefundRetVo;
import lombok.Data;

import java.util.Date;

/**
 * @program: core
 * @description: refund bo
 * @author: alex101
 * @create: 2020-12-16 10:23
 **/
@Data
public class RefundBo implements VoObject
{
    private Long id;

    private Long paymentId;

    private Long amount;

    private Long orderId;

    private Long aftersaleId;

    private Byte state;

    private Date gmtCreate;

    private Date gmtModified;

    public RefundBo()
    {

    }

    public RefundBo(RefundPo refundPo)
    {
        id = refundPo.getId();
        paymentId = refundPo.getPaymentId();
        amount = refundPo.getAmount();
        orderId = refundPo.getOrderId();
        aftersaleId = refundPo.getAftersaleId();
        state = refundPo.getState();
        gmtCreate = refundPo.getGmtCreate();
        gmtModified = refundPo.getGmtModified();
    }

    @Override
    public Object createVo() {
        return new RefundRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
