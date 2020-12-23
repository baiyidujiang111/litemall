package com.example.freight.controller;

import cn.edu.xmu.goods.client.IGoodsService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.*;
import com.alibaba.fastjson.JSON;
import com.example.freight.model.po.FreightModelPo;
import com.example.freight.model.vo.PieceFreightModelInfoVo;
import com.example.freight.model.bo.FreightModelBo;
import com.example.freight.model.vo.FreightModelInfoVo;
import com.example.freight.model.vo.ItemVo;
import com.example.freight.model.vo.WeightFreightModelInfoVo;
import com.example.freight.service.FreightService;
import com.example.freight.service.IFreightModelService;
import com.example.orderservice.OrderServiceDubbo;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import net.bytebuddy.asm.Advice;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: core
 * @description: 运输服务控制器
 * @author: alex101
 * @create: 2020-12-09 16:57
 **/
@RestController
@RequestMapping(value = "/freight", produces = "application/json;charset=UTF-8")
public class FreightController {

    private static final Logger logger = LoggerFactory.getLogger(FreightController.class);

    @Autowired
    HttpServletResponse httpServletResponse;

    @Autowired
    FreightService freightService;


    @DubboReference(version = "0.0.1-SNAPSHOT")
    IGoodsService iGooosService;
    //此处需要有一个删除商品关联的运费模板信息的dubbo服务


    /**
    * @Description: 管理员设置默认运费模板
    * @Param: [shopId, id]
    * @return: java.lang.Object
    * @Author: alex101
    * @Date: 2020/12/9
    */
    @ApiOperation(value = "设置默认运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "shopid", value = "商户ID", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @Audit
    @PostMapping("shops/{shopId}/freightmodels/{id}/default")
    @ResponseBody
    public Object setDefaultFreightModel(@Depart Long jwtShopId,@PathVariable Long shopId, @PathVariable Long id,HttpServletResponse httpServletResponse) {
        if(!shopId.equals(jwtShopId))
        {
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
        }
        logger.debug("setDefaultFreightModel shopid = " + shopId + " id = " + id);
        ReturnObject returnObject = freightService.setDefaultFreightModel(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObject);
        } else {
            if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
            {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return Common.decorateReturnObject(returnObject);
        }

    }

    
    /** 
    * @Description: 返回模板概要
    * @Param: [shopId, id] 
    * @return: java.lang.Object 
    * @Author: alex101
    * @Date: 2020/12/10 
    */
    @ApiOperation(value = "/shops/{shopId}/freightmodels/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "shopId", value = "商户ID", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })

    @Audit
    @GetMapping("/shops/{shopId}/freightmodels/{id}")
    @ResponseBody
    public Object getFreightModelSummary(@PathVariable Long shopId, @PathVariable Long id,HttpServletResponse httpServletResponse) {
        logger.debug("getFreightModelSummary shopId: " + shopId + " id = " + id);
        ReturnObject returnObject = freightService.getFreightModelSummary(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            if (returnObject.getCode() == ResponseCode.RESOURCE_ID_OUTSCOPE)
            {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return Common.decorateReturnObject(returnObject);
        }
    }


    /** 
    * @Description: 增加运费模板
    * @Param: [id, vo] 
    * @return: java.lang.Object 
    * @Author: alex101
    * @Date: 2020/12/11 
    */
    @Audit
    @PostMapping("/shops/{id}/freightmodels")
    public Object addFreightModel(@PathVariable Long id, @RequestBody FreightModelInfoVo vo,HttpServletResponse httpServletResponse) {
        logger.info("set freight model id: " + id + " feightmodel info " + vo);
        ReturnObject returnObject = freightService.addFreightModel(id, vo);
        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }


    /** 
    * @Description: 获取店铺中商品的运费模板
    * @Param: [id, name, page, pageSize] 
    * @return: java.lang.Object 
    * @Author: alex101
    * @Date: 2020/12/12 
    */
    @Audit
    @GetMapping("/shops/{id}/freightmodels")
    public Object getGoodsFreightModels(@PathVariable Long id, @RequestParam(required = false) String name, @RequestParam(required = false)  Integer page, @RequestParam(required = false) Integer pageSize)
    {
        logger.debug("getGoodsFreightModels: page = "+ page +"  pageSize ="+pageSize+" name="+name);
        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;
        ReturnObject<PageInfo<VoObject>> returnObject = freightService.getGoodsFreightModel(id,name,page,pageSize);
        return Common.getPageRetObject(returnObject);
    }

    @Audit
    @PutMapping("/shops/{shopId}/freightmodels/{id}")
    public Object modifyFreightModel(@Depart Long jwtShopId,@PathVariable Long shopId,@PathVariable Long id,@RequestBody FreightModelInfoVo vo,HttpServletResponse httpServletResponse)
    {
        if(!shopId.equals(jwtShopId))
        {
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
        }
        ReturnObject returnObject = freightService.modifyFreightModel(shopId,id,vo);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
            {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return Common.decorateReturnObject(returnObject);
        }
    }

    /** 删除运费模板
    * @Description:
    * @Param: [shopId, id]
    * @return: java.lang.Object
    * @Author: alex101
    * @Date: 2020/12/14
    */
    @Audit
    @DeleteMapping("/shops/{shopId}/freightmodels/{id}")
    public Object deleteFreightModel(@PathVariable Long shopId,@PathVariable Long id,HttpServletResponse httpServletResponse)
    {
        ReturnObject returnObject = freightService.deleteFreightModel(shopId,id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
            {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
    * @Description:  新增重量明细
    * @Param: [shopId, id, vo, result, httpServletResponse]
    * @return: java.lang.Object
    * @Author: alex101
    * @Date: 2020/12/14
    */

    @Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/weightItems")
    public Object addWeightItems(@PathVariable Long shopId, @PathVariable Long id, @RequestBody WeightFreightModelInfoVo vo, BindingResult result, HttpServletResponse httpServletResponse)
    {
        Object o = Common.processFieldErrors(result, httpServletResponse);
        if(o != null){
            return o;
        }
        ReturnObject returnObject = freightService.addWeightItem(shopId,id,vo);
        if (returnObject.getCode() == ResponseCode.OK) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @Audit
    @GetMapping("/shops/{shopId}/freightmodels/{id}/weightItems")
    public Object getWeightItems(@PathVariable Long shopId,@PathVariable Long id)
    {
        ReturnObject returnObject  = freightService.getWeightItem(shopId,id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getListRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    /**
     * @Description: 管理员克隆店铺的运费模板
     * @Param:  [shopId, id]
     * @return: {@link java.lang.Object}
     * @Author: lzn
     * @Date 2020/12/10
     */
    @ApiOperation(value = "/shops/{shopId}/freightmodels/{id}/clone")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization",value = "Token",required = true),
            @ApiImplicitParam(name = "shopId",value ="商户ID",required = true,dataType = "Integer",paramType = "path"),
            @ApiImplicitParam(name = "id",value ="id",required = true,dataType = "Integer",paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })

    @Audit
    @PostMapping("shops/{shopId}/freightmodels/{id}/clone")
    @ResponseBody
    public Object cloneFreightModel(@PathVariable Long shopId, @PathVariable Long id,HttpServletResponse httpServletResponse)
    {
        logger.debug("cloneFreightModel shopId:"+shopId+" id = "+id);
        ReturnObject returnObject = freightService.cloneFreightModel(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK)
        {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObject);
        }
        else
        {
            if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
            {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return Common.decorateReturnObject(returnObject);
        }
    }

    @Audit
    @PostMapping("region/{rid}/price")
    // todo:商品模块拿sku信息
    public Object getFreight(@PathVariable Long rid, @RequestBody List<ItemVo> items,HttpServletResponse httpServletResponse)
    {
        logger.info("compute freight: region id: " +rid + " items = " + items);
        List<Long> freightModelIds = new ArrayList<>();
        List<FreightModelBo> freightModelBoList = new ArrayList<>();
        List<Long> skuWeight = new ArrayList<>();
        for(ItemVo itemVo:items)
        {
            //logger.info(orderServiceDubbo.GetShopIdByOrderId(itemVo.getSkuId())+" here");
            freightModelIds.add(iGooosService.getFreightModelIdBySku(itemVo.getSkuId()));
            skuWeight.add(iGooosService.getGoodWeightBySku(itemVo.getSkuId()));
        }
        logger.info("ready to compute");
        /* 传入每个商品的运费模板id，sku重量，vo信息，抵达区域 */
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        ReturnObject returnObject = freightService.getFreight(freightModelIds,skuWeight,items,rid);
        logger.info(JacksonUtil.toJson(returnObject));
        return Common.getRetObject(returnObject);
    }


    /**
     * @Description: 管理员定义件数模板明细
     * @Param:  [shopId, id, pieceFreightModelInfoVo]
     * @return: {@link java.lang.Object}
     * @Author: lzn
     * @Date 2020/12/14
     */
    @ApiOperation(value = "/shops/{shopId}/freightmodels/{id}/pieceItems")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "shopId", value = "商户ID", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "freightModelInfo", value = "运费模板资料", required = true, dataType = "PieceFreightModelInfoVo", paramType = "body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在"),
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义")
    })
    @Audit
    @PostMapping("shops/{shopId}/freightmodels/{id}/pieceItems")
    @ResponseBody
    public Object setPieceItems(@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody PieceFreightModelInfoVo pieceFreightModelInfoVo, BindingResult result, HttpServletResponse httpServletResponse)
    {
        logger.info("setPieceItems shopId:" + shopId + " id = " + id);
        if (result.hasErrors())
        {
            return Common.processFieldErrors(result, httpServletResponse);
        }
        ReturnObject returnObject = freightService.setPieceItems(shopId, id, pieceFreightModelInfoVo);
        if (returnObject.getCode() == ResponseCode.OK)
        {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
            return Common.getRetObject(returnObject);
        }
        else
        {
            return Common.decorateReturnObject(returnObject);
        }

    }


    /**
     * @Description: 店家或管理员查询件数运费模板明细
     * @Param:  [shopId, id]
     * @return: {@link java.lang.Object}
     * @Author: lzn
     * @Date 2020/12/14
     */
    @ApiOperation(value = "/shops/{shopId}/freightmodels/{id}/pieceItems")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "shopId", value = "商户ID", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在"),
    })
    @Audit
    @GetMapping("shops/{shopId}/freightmodels/{id}/pieceItems")
    @ResponseBody
    public Object getPieceItems(@PathVariable Long shopId, @PathVariable Long id)
    {
        logger.debug("getPieceItems shopId:" + shopId + " id = " + id);
        ReturnObject returnObject = freightService.getPieceItems(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK)
        {
            return Common.getListRetObject(returnObject);
        }
        else
        {
            return Common.decorateReturnObject(returnObject);
        }
    }



    /**
     * @Description: 店家或管理员修改件数模板明细
     * @Param:  [shopId, id, pieceFreightModelInfoVo, result]
     * @return: {@link java.lang.Object}
     * @Author: lzn
     * @Date 2020/12/14
     */
    @ApiOperation(value = "/shops/{shopId}/pieceItems/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "shopId", value = "商户ID", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "freightModelInfo", value = "运费模板资料", required = true, dataType = "PieceFreightModelInfoVo", paramType = "body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在"),
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义")
    })
    @Audit
    @PutMapping("shops/{shopId}/pieceItems/{id}")
    @ResponseBody
    public Object putPieceItems(@Depart Long jwtShopId, @PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody PieceFreightModelInfoVo pieceFreightModelInfoVo, BindingResult result, HttpServletResponse httpServletResponse)
    {
        if(!shopId.equals(jwtShopId))
        {
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
        }

        logger.debug("putPieceItems shopId:" + shopId + " id = " + id);
        if (result.hasErrors())
        {
            return Common.processFieldErrors(result, httpServletResponse);
        }
        ReturnObject returnObject = freightService.putPieceItems(shopId, id, pieceFreightModelInfoVo);
        if (returnObject.getCode() == ResponseCode.OK)
        {
            return Common.getRetObject(returnObject);
        }
        else
        {
            if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
            {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return Common.decorateReturnObject(returnObject);
        }
    }



    /**
     * @Description: 店家或管理员删掉件数运费模板明细
     * @Param:  [shopId, id]
     * @return: {@link java.lang.Object}
     * @Author: lzn
     * @Date 2020/12/14
     */
    @ApiOperation(value = "/shops/{shopId}/pieceItems/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "shopId", value = "商户ID", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在"),
    })
    @Audit
    @DeleteMapping("shops/{shopId}/pieceItems/{id}")
    @ResponseBody
    public Object delPieceItems(@PathVariable Long shopId, @PathVariable Long id)
    {
        logger.info("delPieceItems shopId:" + shopId + " id = " + id);
        ReturnObject returnObject = freightService.delPieceItems(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK)
        {
            return Common.getRetObject(returnObject);
        }
        else
        {
            return Common.decorateReturnObject(returnObject);
        }
    }


    /**
     * @Description: 店家或管理员修改重量模板明细
     * @Param:  [shopId, id, weightFreightModelInfoVo, result, httpServletResponse]
     * @return: {@link Object}
     * @Author: lzn
     * @Date 2020/12/14
     */
    @ApiOperation(value = "/shops/{shopId}/weightItems/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "shopId", value = "商户ID", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "freightModelInfo", value = "运费模板资料", required = true, dataType = "PieceFreightModelInfoVo", paramType = "body")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在"),
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义")
    })
    @Audit
    @PutMapping("shops/{shopId}/weightItems/{id}")
    @ResponseBody
    public Object putWeightItems(@Depart Long jwtShopId,@PathVariable Long shopId, @PathVariable Long id, @Validated @RequestBody WeightFreightModelInfoVo weightFreightModelInfoVo, BindingResult result, HttpServletResponse httpServletResponse)
    {
        logger.info("jwtShopId shopId "+jwtShopId+" "+shopId);
        if(!shopId.equals(jwtShopId))
        {
            return Common.decorateReturnObject(new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE));
        }

        logger.debug("putWeightItems shopId:" + shopId + " id = " + id);
        if (result.hasErrors())
        {
            return Common.processFieldErrors(result, httpServletResponse);
        }
        ReturnObject returnObject = freightService.putWeightItems(shopId, id, weightFreightModelInfoVo);
        if (returnObject.getCode() == ResponseCode.OK)
        {
            return Common.getRetObject(returnObject);
        }
        else
        {
            if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
            {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return Common.decorateReturnObject(returnObject);
        }
    }


    /**
     * @Description: 店家或管理员删掉重量模板明细
     * @Param:  [shopId, id, weightFreightModelInfoVo, result, httpServletResponse]
     * @return: {@link Object}
     * @Author: lzn
     * @Date 2020/12/14
     */
    @ApiOperation(value = "/shops/{shopId}/weightItems/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "shopId", value = "商户ID", required = true, dataType = "Integer", paramType = "path"),
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Integer", paramType = "path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在"),
    })
    @Audit
    @DeleteMapping("shops/{shopId}/weightItems/{id}")
    @ResponseBody
    public Object delWeightItems(@PathVariable Long shopId, @PathVariable Long id,HttpServletResponse httpServletResponse)
    {
        logger.info("DELETE WeightItems shopId:" + shopId + " id = " + id);
        ReturnObject returnObject = freightService.delWeightItems(shopId, id);
        if (returnObject.getCode() == ResponseCode.OK)
        {
            return Common.getRetObject(returnObject);
        }
        else
        {
            if(returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
            {
                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            }
            return Common.decorateReturnObject(returnObject);
        }
    }
}
