package com.example.payment.model.vo;

import cn.edu.xmu.ooad.model.VoObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
public class PayPatternAndNameRetVo implements VoObject {
    String name;
    String payPattern;
    public PayPatternAndNameRetVo( String name,String pattern)
    {
        payPattern=pattern;
        this.name=name;
    }

    @Override
    public Object createVo() {
        return new PayPatternAndNameRetVo(this.name,this.payPattern);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
