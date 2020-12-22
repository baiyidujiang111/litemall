package com.example.payment.service;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;

import java.util.ArrayList;
import java.util.List;

public class Commonconvert {
    public static Object getListRetObject(ReturnObject<List> returnObject) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:
                List objs = returnObject.getData();
                if (objs != null){
                    List<Object> ret = new ArrayList<>(objs.size());
                    for (Object data : objs) {
                        if (data instanceof VoObject) {
                            ret.add(((VoObject)data).createVo());
                        }
                    }
                    return ResponseUtil.ok(ret);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }
}
