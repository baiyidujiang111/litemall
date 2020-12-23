package com.example.freight.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.freight.controller.FreightController;
import com.example.freight.mapper.FreightModelMapper;
import com.example.freight.mapper.PieceFreightModelMapper;
import com.example.freight.mapper.WeightFreightModelMapper;
import com.example.freight.model.bo.FreightModelBo;
import com.example.freight.model.bo.FreightModelDetail;
import com.example.freight.model.bo.PieceFreightModelBo;
import com.example.freight.model.bo.WeightFreightModelBo;
import com.example.freight.model.po.FreightModelPo;
import com.example.freight.model.po.PieceFreightModelPo;
import com.example.freight.model.po.WeightFreightModelPo;
import com.example.freight.model.vo.FreightModelInfoVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @program: core
 * @description: FreightModelDao
 * @author: alex101
 * @create: 2020-12-09 18:01
 **/
@Component
public class FreightModelDao {

    private static final int WEIGHT_TPYE = 0;
    private static final int PEICE_TPYE = 1;
    private static final Logger logger = LoggerFactory.getLogger(FreightController.class);

    @Autowired
    private FreightModelMapper freightModelMapper;

    @Autowired
    private WeightFreightModelMapper weightFreightModelMapper;

    @Autowired
    private PieceFreightModelMapper pieceFreightModelMapper;

    /**
     * @Description: 设置默认运费模板
     * @Param: [shopid, id]
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: alex101
     * @Date: 2020/12/9
     */
    public ReturnObject setDefaultFreightModel(Long shopId, Long id) {

        ReturnObject returnObject;
        FreightModelPo freightModelPo = freightModelMapper.selectById(id);
        if(freightModelPo==null)
        {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            logger.error("set 505");
            logger.error("not found freightModel shopid = "+shopId+" id = "+id);
        }else if(!freightModelPo.getShopId().equals(shopId)) {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            logger.error("freightModel shop Id:"+freightModelPo.getShopId()+" not equal to path shop Id:"+shopId);
        }else if(freightModelPo.getType()==PEICE_TPYE) {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            logger.error("can't set piece type model to default "+freightModelPo.getId());
        } else{
            /*将原始的默认模板取消*/
            UpdateWrapper<FreightModelPo> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("default_model",true).eq("shop_id",shopId).set("default_model",false).set("gmt_modified",LocalDateTime.now());
            freightModelMapper.update(null,updateWrapper);

            // 设置新默认模板
            freightModelPo.setDefaultModel(true);

            //修改时间
            freightModelPo.setGmtModified(LocalDateTime.now());

            freightModelMapper.updateById(freightModelPo);
            returnObject = new ReturnObject<>(ResponseCode.OK);
            logger.info("found freightModel");
        }
        return returnObject;
    }


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
        } else {
            FreightModelBo freightModelBo = new FreightModelBo(freightModelPo);
            returnObject = new ReturnObject<>(freightModelBo);
        }
        return returnObject;
    }

    /**
    * @Description: 增加运费模板
    * @Param:
    * @return:
    * @Author: alex101
    * @Date: 2020/12/11
    */
    public ReturnObject addFreightModel(Long id, FreightModelInfoVo vo)
    {
        ReturnObject returnObject;
        FreightModelBo freightModelBo = new FreightModelBo();
        freightModelBo.setShopId(id);
        freightModelBo.setType(vo.getType());
        freightModelBo.setUnit(vo.getUnit());
        freightModelBo.setName(vo.getName());
        FreightModelPo freightModelPo = (FreightModelPo) freightModelBo.createPo();
        QueryWrapper<FreightModelPo> queryWrapper = new QueryWrapper<FreightModelPo>();
        queryWrapper.eq("name",freightModelPo.getName());
        int count = freightModelMapper.selectCount(queryWrapper);
        if(count>0){
            logger.info("same freight name "+freightModelPo.getName());
            returnObject = new ReturnObject(ResponseCode.FREIGHTNAME_SAME);
        }else {
            freightModelMapper.insert(freightModelPo);

            FreightModelPo freightModelPo1= freightModelMapper.selectOne(queryWrapper);
            FreightModelBo freightModelBo1 = new FreightModelBo(freightModelPo1);
            returnObject = new ReturnObject<>(freightModelBo1);
        }
        return returnObject;
    }


    /**
    * @Description: 分页查询商店中的所有运费模板
    * @Param: [id, name, page, pageSize]
    * @return: cn.edu.xmu.ooad.util.ReturnObject<com.github.pagehelper.PageInfo<cn.edu.xmu.ooad.model.VoObject>>
    * @Author: alex101
    * @Date: 2020/12/14
    */
    public ReturnObject<PageInfo<VoObject>> getGoodsFreightModel(Long id,String name,int page,int pageSize)
    {
        List<FreightModelPo> freightModelPos;
        QueryWrapper<FreightModelPo>  queryWrapper = new QueryWrapper<FreightModelPo>().eq("shop_id",id);
        if(name!=null) {
            queryWrapper = queryWrapper.eq("name",name);
        }
        PageHelper.startPage(page,pageSize);
        freightModelPos = freightModelMapper.selectList(queryWrapper);

        List<VoObject> ret = new ArrayList<>(freightModelPos.size());
        for(FreightModelPo po:freightModelPos)
        {
            ret.add(new FreightModelBo(po));
        }

        PageInfo<FreightModelPo> freightModelPoPageInfo = PageInfo.of(freightModelPos);
        PageInfo<VoObject> voObjectPageInfo = new PageInfo<>(ret);

        voObjectPageInfo.setPages(freightModelPoPageInfo.getPages());
        voObjectPageInfo.setPageNum(freightModelPoPageInfo.getPageNum());
        voObjectPageInfo.setPageSize(freightModelPoPageInfo.getPageSize());
        voObjectPageInfo.setTotal(freightModelPoPageInfo.getTotal());
        return new ReturnObject<>(voObjectPageInfo);
    }

    /**
     * @Description: 修改运费模板
     * @Param: [shopId, id, freightModelInfoVo]
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: alex101
     * @Date: 2020/12/14
     */
    public ReturnObject modifyFreightModel(Long shopId, Long id, FreightModelInfoVo freightModelInfoVo) {
        ReturnObject returnObject;
        FreightModelPo freightModelPo = freightModelMapper.selectById(id);
        if(freightModelPo==null) {
            logger.error("not found freightModel shopid = " + shopId + " id = " + id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }else if(!freightModelPo.getShopId().equals(shopId)){
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            logger.error("freightModel shop Id:" + freightModelPo.getShopId() + " not equal to path shop Id:" + shopId);
        }else {
            QueryWrapper<FreightModelPo> queryWrapper = new QueryWrapper<FreightModelPo>().eq("name", freightModelInfoVo.getName());
            int cnt = freightModelMapper.selectCount(queryWrapper);
            if(cnt>0)
            {
                logger.error("same name = " + shopId + " id = " + id +" name = "+freightModelInfoVo.getName());
                returnObject = new ReturnObject<>(ResponseCode.FREIGHTNAME_SAME);
                return returnObject;
            }
            freightModelPo.setGmtModified(LocalDateTime.now());
            freightModelPo.setUnit(freightModelInfoVo.getUnit());
            freightModelPo.setName(freightModelInfoVo.getName());
            freightModelMapper.updateById(freightModelPo);
            returnObject = new ReturnObject<>(ResponseCode.OK);
        }

        return returnObject;
    }

    /**
     * @Description: 删除运费模板
     * @Param: [shopId, id]
     * @return: cn.edu.xmu.ooad.util.ReturnObject
     * @Author: alex101
     * @Date: 2020/12/14
     */
    public ReturnObject deleteFreightModel(Long shopId, Long id) {
        ReturnObject returnObject;
        FreightModelPo freightModelPo = freightModelMapper.selectById(id);
        if(freightModelPo==null)
        {
            logger.error("not found freightModel shopid = " + shopId + " id = " + id);
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }else if(!freightModelPo.getShopId().equals(shopId)){
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            logger.error("freightModel shop Id:" + freightModelPo.getShopId() + " not equal to path shop Id:" + shopId);
        }else {
            freightModelMapper.deleteById(id);
            returnObject = new ReturnObject<>(ResponseCode.OK);
        }
        return returnObject;

    }


    /**
     * @Description:管理员克隆店铺的运费模板
     * @Param:  [shopId, id]
     * @return: {@link cn.edu.xmu.ooad.util.ReturnObject}
     * @Author: lzn
     * @Date 2020/12/10
     */
    public ReturnObject cloneFreightModel(Long shopId, Long id)
    {
        ReturnObject returnObject;
        FreightModelPo freightModelPo = freightModelMapper.selectById(id);
        if (freightModelPo == null)
        {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            logger.error("not found freightModel shopId = " + shopId + " id = " + id);
        }
        else if (!freightModelPo.getShopId().equals(shopId))
        {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            logger.error("not found freightModel shopId = " + shopId + " id = " + id);
        }
        else
        {
            FreightModelPo freightModelPo2 = freightModelPo.objectClone();
            freightModelPo2.setGmtCreate(LocalDateTime.now().withNano(0));
            freightModelPo2.setGmtModified(LocalDateTime.now().withNano(0));
            logger.info("get gmtcreate time :"+freightModelPo2.getGmtCreate());
            logger.info("get modified time :"+freightModelPo2.getGmtModified());
            freightModelPo2.setName(freightModelPo.getName() + UUID.randomUUID());
            freightModelMapper.insert(freightModelPo2);
            FreightModelBo freightModelBo = new FreightModelBo(freightModelPo2);
            returnObject = new ReturnObject<>(freightModelBo);
            logger.info("found freightModel to be cloned");
        }
        return returnObject;
    }

    public FreightModelBo getDefaultFreightModel()
    {
        QueryWrapper<FreightModelPo> freightModelPoQueryWrapper = new QueryWrapper<FreightModelPo>().eq("default_model",1);
        ArrayList<FreightModelPo> freightModelPos;
        freightModelPos = (ArrayList<FreightModelPo>) freightModelMapper.selectList(freightModelPoQueryWrapper);
        if(freightModelPos!=null)
        {
            return new FreightModelBo(freightModelPos.get(0));
        }else {
            return null;
        }
    }

    public FreightModelDetail getFreightOrderDetail(Long rid,int type,Long freightModelId)
    {
        FreightModelDetail freightModelDetail = null;
        if(type==0)
        {
            QueryWrapper queryWrapper = new QueryWrapper<WeightFreightModelPo>().eq("region_id",rid).eq("freight_model_id",freightModelId);
            WeightFreightModelBo weightFreightModelBo = new WeightFreightModelBo(weightFreightModelMapper.selectOne(queryWrapper)) ;
            freightModelDetail =  weightFreightModelBo;
        }else if(type==1)
        {
            QueryWrapper queryWrapper = new QueryWrapper<PieceFreightModelPo>().eq("region_id",rid).eq("freight_model_id",freightModelId);
            PieceFreightModelBo pieceFreightModelBo = new PieceFreightModelBo(pieceFreightModelMapper.selectOne(queryWrapper)) ;
            freightModelDetail =  pieceFreightModelBo;
        }
        return freightModelDetail;
    }


    /**
     * @Description: (查看id与shopId是否对应)
     * @Param:  [shopId, id]
     * @return: {@link cn.edu.xmu.ooad.util.ReturnObject}
     * @Author: lzn
     * @Date 2020/12/14
     */
    public ReturnObject examIdScope(Long shopId, Long id)
    {
        ReturnObject returnObject = new ReturnObject<>(ResponseCode.OK);
        FreightModelPo freightModelPo = freightModelMapper.selectById(id);
        if (freightModelPo == null)
        {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            logger.error("not found freightModel shopId = " + shopId + " id = " + id);
        }
        else if (!freightModelPo.getShopId().equals(shopId))
        {
            returnObject = new ReturnObject<>(ResponseCode.RESOURCE_ID_OUTSCOPE);
            logger.error("freightModel shop Id:" + freightModelPo.getShopId() + " not equal to path shop Id:" + shopId);
        }
        return returnObject;
    }

    /**
    * @Description:
    * @Param: [freightId]
    * @return: com.example.freight.model.bo.FreightModelBo
    * @Author: alex101
    * @Date: 2020/12/23
    */
    public FreightModelBo getFreightModelById(Long freightId)
    {
        FreightModelPo freightModelPo = freightModelMapper.selectById(freightId);
        if(freightModelPo==null)
        {
            return null;
        }
        return new FreightModelBo(freightModelPo);
    }

}
