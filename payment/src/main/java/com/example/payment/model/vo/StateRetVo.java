package com.example.payment.model.vo;

import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

/**
 * @program: core
 * @description: statevo
 * @author: alex101
 * @create: 2020-12-16 10:10
 **/
@Data
public class StateRetVo implements VoObject {
    String name;
    Byte code;

    public StateRetVo(Byte code) {
        if(code==null)
        {
            this.code = code;
            this.name="error";
        }else {
            this.code = code;
            switch (code)
            {
                case 0:
                    name="未支付";
                    break;
                case 1:
                    name = "已支付";
                    break;
                case 2:
                    name = "支付失败";
                    break;
                default:
                    name = "error state";
                    break;
            }
        }

    }

    @Override
    public Object createVo() {
        return new StateRetVo(this.code);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}

