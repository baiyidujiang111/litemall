package cn.edu.xmu.oomall.other.service;

import cn.edu.xmu.ooad.util.ReturnObject;


import java.util.List;

/**
 * 广告服务调用接口
 *
 * @author cxr
 * @date 2020/12/14 19:57
 * @version 1.0
 */
public interface IAdvertiseService {

    /**
     * @author cxr
     * 将时段下的广告时段置为0
     * @param id 时段id
     * @return returnObject
     */
    ReturnObject deleteTimeSegmentAdvertisements(Long id);

}
