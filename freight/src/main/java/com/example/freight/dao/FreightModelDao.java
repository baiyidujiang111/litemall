package com.example.freight.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.freight.controller.FreightController;
import com.example.freight.mapper.FreightModelMapper;
import com.example.freight.model.po.FreightModelPo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: core
 * @description: FreightModelDao
 * @author: alex101
 * @create: 2020-12-09 18:01
 **/
@Component
public class FreightModelDao {

    private static final Logger logger = LoggerFactory.getLogger(FreightController.class);

    @Autowired
    private FreightModelMapper freightModelMapper;

    /*
    /** 
    * @Description: 设置默认运费模板 
    * @Param: [shopid, id] 
    * @return: cn.edu.xmu.ooad.util.ReturnObject 
    * @Author: alex101
    * @Date: 2020/12/9 
    */
    @Transactional
    public ReturnObject setDefaultFreightModel(Long shopId,Long id)
    {

        ReturnObject returnObject;
        FreightModelPo freightModelPo = freightModelMapper.selectById(id);
        if(freightModelPo==null)
        {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            logger.error("not found freightModel shopid = "+shopId+" id = "+id);
        }else if(!freightModelPo.getShopId().equals(shopId))
        {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            logger.error("freightModel shop Id:"+freightModelPo.getShopId()+" not equal to path shop Id:"+shopId);
        }else {
            /*将原始的默认模板取消*/
            UpdateWrapper<FreightModelPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("default_model",true).set("default_model",false).set("gmt_modified",LocalDateTime.now());
            freightModelMapper.update(null,updateWrapper);

            /**设置新默认模板**/
            freightModelPo.setDefaultModel(true);

            freightModelPo.setGmtModified(LocalDateTime.now());//修改时间
            freightModelMapper.updateById(freightModelPo);
            returnObject = new ReturnObject<>(ResponseCode.OK);
            logger.info("found freightModel");
        }
        return returnObject;
    }

    /*
    /** 
    * @Description: 返回模板概要 
    * @Param: [shopId, id] 
    * @return: cn.edu.xmu.ooad.util.ReturnObject 
    * @Author: alex101
    * @Date: 2020/12/10 
    */
    public ReturnObject getFreightModelSummary(Long shopId,Long id)
    {
        ReturnObject returnObject;
        FreightModelPo freightModelPo = freightModelMapper.selectById(id);
        if(freightModelPo==null)
        {
            logger.error("not found freightModel shopid = "+shopId+" id = "+id);
            returnObject =  new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }else if(!freightModelPo.getShopId().equals(shopId)) {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            logger.error("freightModel shop Id:" + freightModelPo.getShopId() + " not equal to path shop Id:" + shopId);
        }else {
            returnObject = new ReturnObject<>(freightModelPo);
        }
        return returnObject;


    }

}
