package com.example.freight.model.bo;

import com.example.freight.model.vo.OrderItemComputeVo;


/**
* compute freight
*@Description:  订单详情计算运费方法
* @Param:  Weight:int
* @Param:Count:int
* @return:  freight:Long
* @Author: alex101
* @Date: 2020/12/14
*/
public interface FreightModelDetail {

    /**
    * @Description:  计算运费
    * @Param: weight,count
    * @return: java.lang.Long
    * @Author: alex101
    * @Date: 2020/12/14
    */
    Long getFreight(Long weight, Long count);
}
