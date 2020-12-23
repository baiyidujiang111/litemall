package com.example.freight.naiveTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.freight.FreightApplication;
import com.example.freight.dao.FreightModelDao;
import com.example.freight.mapper.FreightModelMapper;
import com.example.freight.model.bo.FreightModelBo;
import com.example.freight.model.po.FreightModelPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * @program: oomall
 * @description:
 * @author: alex101
 * @create: 2020-12-24 01:15
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FreightApplication.class)   //标识本类是一个SpringBootTest
public class naiveTest {
    @Autowired
    FreightModelMapper freightModelMapper;

    @Autowired
    FreightModelDao freightModelDao;

    @Test
    public void selectDefaultModel() throws Exception
    {
        QueryWrapper<FreightModelPo> freightModelPoQueryWrapper = new QueryWrapper<FreightModelPo>().eq("default_model",1);
        ArrayList<FreightModelPo> freightModelPos;
        freightModelPos = (ArrayList<FreightModelPo>) freightModelMapper.selectList(freightModelPoQueryWrapper);
    }
}
