package com.example.freight.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import com.example.freight.model.po.PieceFreightModelPo;
import com.example.freight.model.vo.OrderItemComputeVo;
import com.example.freight.model.vo.PieceFreightModelRetVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: core
 * @description: PieceFreightModelBo
 * @author: alex101
 * @create: 2020-12-11 09:21
 **/
@Data
public class PieceFreightModelBo implements VoObject, FreightModelDetail
{

    private Long id;
    private Long regionId;
    private Long freightModelId;
    private int firstItem;
    private long firstItemPrice;
    private int additionalItems;
    private long additionalItemsPrice;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    public PieceFreightModelBo(PieceFreightModelPo pieceFreightModelPo)
    {
        id = pieceFreightModelPo.getId();
        regionId = pieceFreightModelPo.getRegionId();
        freightModelId = pieceFreightModelPo.getFreightModelId();
        firstItem = pieceFreightModelPo.getFirstItems();
        firstItemPrice = pieceFreightModelPo.getFirstItemsPrice();
        additionalItems = pieceFreightModelPo.getAdditionalItems();
        additionalItemsPrice = pieceFreightModelPo.getAdditionalItemsPrice();
        gmtCreate = pieceFreightModelPo.getGmtCreate();
        gmtModified = pieceFreightModelPo.getGmtModified();
    }

    @Override
    public Object createVo()
    {
        return new PieceFreightModelRetVo(this);
    }

    @Override
    public Object createSimpleVo()
    {
        return null;
    }

    @Override
    public Long getFreight(Long weight,Long count)
    {
        Long freight = firstItemPrice;
        if(count > firstItem)
            freight += additionalItemsPrice*((int)((count - firstItem)/additionalItems) + 1);
        return freight;
    }

}
