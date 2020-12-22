package com.example.payment.mapper;

import com.example.payment.model.po.RefundPo;
import com.example.payment.model.po.RefundPoExample;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface RefundPoMapper {
    int countByExample(RefundPoExample example);

    int deleteByExample(RefundPoExample example);

    int deleteByPrimaryKey(Long id);

    int insert(RefundPo record);

    int insertSelective(RefundPo record);

    List<RefundPo> selectByExample(RefundPoExample example);

    RefundPo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RefundPo record);

    int updateByPrimaryKey(RefundPo record);
}