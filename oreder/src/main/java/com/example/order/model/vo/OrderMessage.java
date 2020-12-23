package com.example.order.model.vo;

import cn.edu.xmu.ooad.annotation.Depart;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/***
 * @author yansong chen
 * @time 2020-12-17 16:02
 * @description:
 */
@Data
public class OrderMessage {
    @NotNull
    @Length(min = 1,max = 100)
    private String message;
}
