package com.example.order.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.example.order.model.bo.NewOrder;
import com.example.order.model.vo.NewOrderVO;
import com.example.order.model.vo.OrderFreightSn;
import com.example.order.model.vo.OrderMessage;
import com.example.order.model.vo.modifyOrder;
import com.example.order.service.OrderService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/***
 * @author yansong chen
 * @time 2020-12-10 0:02
 * @return
 */

@RestController
@RequestMapping(value = "/order",produces = "application/json;charset=UTF-8")
public class Ordercontroller {

    private static final Logger logger = LoggerFactory.getLogger(Ordercontroller.class);

    @Autowired
    OrderService orderService;

/*    @Autowired
    private HttpServletResponse httpServletResponse;*/
    /**
    * @Description:
    * @Param: [re]
    * @return: java.lang.Object
    * @Author: yansong chen
    * @Date: 2020-12-10 1:26
    */
    @ApiOperation(value = "获得订单状态")
    @ApiImplicitParams({
            //@ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    //@Audit
    @GetMapping("/orders/states")
    @ResponseBody
    public Object getOrderStatus()
    {
        ReturnObject returnObject=orderService.GetOrderStatus();

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getListRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "买家名下的订单概要")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(name = "orderSn",value ="订单编号",dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "state",value ="订单状态",dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "beginTime",value ="开始时间",dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "endTime",value ="结束时间",dataType = "String",paramType = "query"),
            @ApiImplicitParam(name = "page",value ="页码",dataType = "Integer",paramType = "query"),
            @ApiImplicitParam(name = "pageSize",value ="每页数目",dataType = "Integer",paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/orders")
    @ResponseBody
    public Object getOrderList(@LoginUser @ApiIgnore Long id,
                               String orderSn, Integer state,
                               @RequestParam(required = false,defaultValue = "1900-01-01 00:00:00") String beginTime,
                               @RequestParam(required = false,defaultValue = "2100-01-01 00:00:00") String endTime,
                               @RequestParam(required = false,defaultValue = "1") Integer page,
                               @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                               HttpServletResponse httpServletResponse)
    {
        String format = "yyyy-MM-dd HH:mm:ss";
        boolean t1=true,t2 = true;
        DateTimeFormatter ldt = DateTimeFormatter.ofPattern(format.replace("y", "u")).withResolverStyle(ResolverStyle.STRICT);
        try {
            if(beginTime!=null)
            {
                beginTime=beginTime.replaceAll("[a-zA-Z]"," ");
                t1=LocalDate.parse(beginTime, ldt)==null?false:true;
            }
            if(endTime!=null)
            {
                endTime=endTime.replaceAll("[a-zA-Z]"," ");
                t2=LocalDate.parse(endTime, ldt)==null?false:true;
            }
        } catch (Exception e) {
            t1=false;
            t2=false;
        }
        if(!(t1&&t2))
        {
            ReturnObject returnObject  = new ReturnObject(ResponseCode.FIELD_NOTVALID);
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return Common.decorateReturnObject(returnObject);
            //return Common.processFieldErrors(result, httpServletResponse);
        }

        ReturnObject returnObject;
        returnObject=orderService.GetListOrder(id,orderSn,state,beginTime,endTime,page,pageSize);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(returnObject);
        }
        else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "买家申请创建订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body",dataType = "object",name = "orderInfo",value = "新订单信息")
    })
    @ApiResponses({
            @ApiResponse(code = 900,message = "商品库存不足"),
            @ApiResponse(code = 0,message = "成功")
    })
    @Audit
    @PostMapping("/orders")
    public Object postOrder(@LoginUser Long authorization, @RequestBody NewOrderVO newOrderVO,
                            BindingResult bindingResult,HttpServletResponse httpServletResponse)
    {

        //校验前端数据
        Object returnObject1 = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject1) {
            logger.debug("validate fail");
            return returnObject1;
        }

        //订单概要
        NewOrder newOrder=newOrderVO.createNewOrder();
        newOrder.setCustomerId(authorization);
        newOrder.setGmtCreate(LocalDateTime.now());

        ReturnObject returnObject;
        returnObject=orderService.PostOrder(newOrder);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "查询订单的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @GetMapping("/orders/{id}")
    @ResponseBody
    public Object GetOrderDetail(@LoginUser Long authorization, @PathVariable("id") Long id,HttpServletResponse httpServletResponse)
    {
        logger.debug("User_id:"+authorization+" Order_id:"+id);
        ReturnObject returnObject=orderService.GetOrderDetail(authorization,id);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(returnObject);
        }else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "买家修改本人名下的订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id", value = "订单id", required = true),
            @ApiImplicitParam(paramType = "body",dataType = "Object",name = "body", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @PutMapping("/orders/{id}")
    public Object modifyOrder(@LoginUser Long authorization,
                              @PathVariable Long id,
                              @Validated @RequestBody modifyOrder order,
                              BindingResult result,HttpServletResponse httpServletResponse)
    {
        if(result.hasErrors())
        {
            return Common.processFieldErrors(result, httpServletResponse);
        }

        ReturnObject returnObject=orderService.modifyOrder(authorization,id,order);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(returnObject);
        }
        else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "买家标记确认收货")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=800,message = "订单状态禁止"),
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @PutMapping("/orders/{id}/confirm")
    public Object putOrderIdConfirm(@LoginUser Long authorization,@PathVariable Long id,HttpServletResponse httpServletResponse)
    {
        ReturnObject returnObject=orderService.putOrderIdConfirm(authorization,id);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(returnObject);
        }
        else {
            return Common.decorateReturnObject(returnObject);
        }
    }


    @ApiOperation(value = "买家取消，逻辑删除名下订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=800,message = "订单状态禁止"),
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @DeleteMapping("/orders/{id}")
    public Object DelOrder(@LoginUser Long authorization,@PathVariable Long id,HttpServletResponse httpServletResponse)
    {
        ReturnObject returnObject=orderService.DelOrder(authorization,id);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(returnObject);
        }
        else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "买家可以把团购订单转为普通订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code=800,message = "订单状态禁止"),
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @PostMapping("/orders/{id}/groupon-normal")
    public Object PostGroupon_Normal(@LoginUser Long use_id,@PathVariable Long id,HttpServletResponse httpServletResponse)
    {
        ReturnObject returnObject=orderService.PostGroupon_Normal(use_id,id);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(returnObject);
        }
        else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "店家查询商户所有订单")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header",dataType = "String",name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "shopId", value = "商户Id", required = true),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "customerId",value = "查询的购买者用户id"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "orderSn",value = "按订单Sn查询"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "beginTime",value = "开始时间"),
            @ApiImplicitParam(paramType = "query",dataType = "String",name = "endTime",value = "结束时间"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "page",value = "页码"),
            @ApiImplicitParam(paramType = "query",dataType = "Integer",name = "pageSize",value = "页码大小"),
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @GetMapping("/shops/{shopId}/orders")
    @ResponseBody
    public Object GetShopOrderList(@LoginUser Long authorization,
                                   @PathVariable("shopId") Long shopId,
                                   Long customerId,String orderSn,
                                   @RequestParam(required = false,defaultValue = "1900-01-01 00:00:00") String beginTime,
                                   @RequestParam(required = false,defaultValue = "2100-01-01 00:00:00") String endTime,
                                   @RequestParam(required = false,defaultValue = "1")int page,
                                   @RequestParam(required = false,defaultValue = "10")int pageSize,
                                   HttpServletResponse httpServletResponse)
    {
        String format = "yyyy-MM-dd HH:mm:ss";
        boolean t1=true,t2 = true;
        DateTimeFormatter ldt = DateTimeFormatter.ofPattern(format.replace("y", "u")).withResolverStyle(ResolverStyle.STRICT);
        try {
            if(beginTime!=null)
            {
                beginTime=beginTime.replaceAll("[a-zA-Z]"," ");
                t1=LocalDate.parse(beginTime, ldt)==null?false:true;
            }
            if(endTime!=null)
            {
                endTime=endTime.replaceAll("[a-zA-Z]"," ");
                t2=LocalDate.parse(endTime, ldt)==null?false:true;
            }
        } catch (Exception e) {
            t1=false;
            t2=false;
        }
        if(!(t1&&t2))
        {
            ReturnObject returnObject  = new ReturnObject(ResponseCode.FIELD_NOTVALID);
            httpServletResponse.setStatus(HttpStatus.BAD_REQUEST.value());
            return Common.decorateReturnObject(returnObject);
            //return Common.processFieldErrors(result, httpServletResponse);
        }

        ReturnObject returnObject=orderService.GetShopOrderList(authorization,shopId,customerId,orderSn,beginTime,endTime,page,pageSize);
        logger.info("get shop detail "+returnObject.getCode());
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(returnObject);
        }
        else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "店家修改订单留言")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商户Id", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id",value = "订单ID",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "Obeject",name = "message",value = "操作字符",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/orders/{id}")
    @ResponseBody
    public Object PutOrderMessage(@LoginUser Long authorization,
                                  @PathVariable("shopId") Long shopId,
                                  @PathVariable("id") Long id,
                                  @RequestBody @Validated OrderMessage message,
                                  BindingResult result,
                                  HttpServletResponse httpServletResponse)
    {
        if(result.hasErrors())
        {
            return Common.processFieldErrors(result, httpServletResponse);
        }
        ReturnObject returnObject=orderService.PutOrderMessage(shopId,id,message);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(returnObject);
        }
        else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "店家查询店内订单的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商户Id", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id",value = "订单ID",required = true)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @GetMapping("/shops/{shopId}/orders/{id}")
    @ResponseBody
    public Object GetShopOrderDetail(@LoginUser Long authorization,
                                     @PathVariable Long shopId,
                                     @PathVariable Long id,
                                     HttpServletResponse httpServletResponse)
    {
        ReturnObject returnObject=orderService.GetShopOrderDetail(authorization,shopId,id);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(returnObject);
        }
        else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "店家查询店内订单的详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商户Id", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id",value = "订单ID",required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 800,message = "成功"),
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/orders/{id}")
    public Object DelShopOrder(@LoginUser Long authorization,
                               @PathVariable Long shopId,
                               @PathVariable Long id,
                               HttpServletResponse httpServletResponse)
    {
        ReturnObject returnObject=orderService.DelShopOrder(shopId,id);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_NOTEXIST)
        {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
            return Common.decorateReturnObject(returnObject);
        }
        else if (returnObject.getCode()==ResponseCode.RESOURCE_ID_OUTSCOPE)
        {
            httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
            return Common.decorateReturnObject(returnObject);
        }
        else {
            return Common.decorateReturnObject(returnObject);
        }
    }

    @ApiOperation(value = "店家对订单标记发货")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "shopId", value = "商户Id", required = true),
            @ApiImplicitParam(paramType = "path",dataType = "Integer",name = "id",value = "订单ID",required = true),
            @ApiImplicitParam(paramType = "body",dataType = "Obeject",name = "frightSn",value = "指定发货讯息"/*,required = true*/)
    })
    @ApiResponses({
            @ApiResponse(code=0,message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/orders/{id}/deliver")
    @ResponseBody
    public Object putDeliver(@PathVariable("shopId") Long shopId,
                             @PathVariable("id") Long id,
                             @RequestBody @Validated OrderFreightSn orderFreightSn,
                             BindingResult result,HttpServletResponse httpServletResponse)
    {
        if(result.hasErrors())
        {
            return Common.processFieldErrors(result, httpServletResponse);
        }

        logger.info("putDeliver shopId:" + shopId + " id = " + id+"  SN:"+orderFreightSn.getFreightSn());
        ReturnObject returnObject=orderService.putDeliver(shopId,id,orderFreightSn);

        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }
    }
}
