package com.example.payment.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import com.example.payment.dao.PaymentDao;
import com.example.payment.model.po.PaymentPo;
import com.example.payment.model.vo.PaymentInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {


    @Autowired
    PaymentDao paymentDao;

    /**
     * @Description: 返回所有订单状态
     * @Param: []
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: alex101
     * @Date: 2020/12/16
     */

    public ReturnObject getAllPaymentState() {
        return paymentDao.getAllPaymentsState();
    }


    /**
     * @Description: 返回所有支付方式
     * @Param: []
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: alex101
     * @Date: 2020/12/16
     */
    public ReturnObject getAllPaymentPatterns() {
        return paymentDao.getAllPaymentPatterns();
    }

    /**
     * @Description: 用户创建支付单
     * @Param: [id, vo]
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: alex101
     * @Date: 2020/12/16
     */
    @Transactional
    public ReturnObject createPayment(Long id, PaymentInfoVo vo) {
        return paymentDao.createPayment(id, vo);
    }

    /** 
    * @Description: 根据订单id查询支付信息
    * @Param: [id] 
    * @return: cn.edu.xmu.ooad.util.ReturnObject 
    * @Author: alex101
    * @Date: 2020/12/17 
    */
    public ReturnObject getPaymentsByOrderId(Long id)
    {
        return paymentDao.getPaymentsByOrderId(id);
    }


    /**
     * @Description: 根据售后单id查询支付信息
     * @Param:  [id]
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: lzn
     * @Date 2020/12/17
     */
    public ReturnObject getPaymentsByAftersaleId(Long id) { return paymentDao.getPaymentsByAftersaleId(id); }


    @Transactional
    public ReturnObject createAftersalePayment(Long id, PaymentInfoVo vo) {
        return paymentDao.createAftersalePayment(id, vo);
    }
    @Transactional
    public ReturnObject createPaymentByShop(Long id,Long refundAmount)
    {
        return paymentDao.createPaymentByShop(id,refundAmount);
    }
    @Transactional
    public ReturnObject createAftersalePaymentByShop(Long id,Long refundAmount)
    {
        return paymentDao.createAftersalePaymentByShop(id,refundAmount);
    }

    public ReturnObject getPaymentPoById(Long paymentId)
    {
        return paymentDao.getPaymentById(paymentId);
    }

}
