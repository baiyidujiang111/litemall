package com.example.payment.mapper;

import com.example.payment.model.po.PaymentPo;
import com.example.payment.model.po.PaymentPoExample;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PaymentPoMapper {
    int countByExample(PaymentPoExample example);

    int deleteByExample(PaymentPoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(PaymentPo record);

    int insertSelective(PaymentPo record);

    List<PaymentPo> selectByExample(PaymentPoExample example);

    PaymentPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(PaymentPo record);

    int updateByPrimaryKey(PaymentPo record);
    @Select("select distinct state from payment")
    List<Byte> getAllState();
}