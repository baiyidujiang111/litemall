package com.example.order.model.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/***
 * @author yansong chen
 * @time 2020-12-17 22:34
 * @description:
 */
@Data
public class OrderFreightSn {
    @NotNull
    @Length(min = 1)
    private String freightSn;
}
