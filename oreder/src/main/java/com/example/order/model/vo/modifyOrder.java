package com.example.order.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/***
 * @author yansong chen
 * @time 2020-12-17 23:13
 * @description:
 */
@Data
public class modifyOrder {
    @NotNull
    private String consignee;
    @NotNull
    private Long regionId;
    @NotNull
    private String address;
    @NotNull
    private String mobile;
}
