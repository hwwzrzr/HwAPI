package com.yupi.project.service.impl.inner;

import com.lhw.hwapicommon.service.InnerUserInterfaceInfoService;
import com.yupi.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * @ClassName InnerUserInterfaceInfoServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2023/8/7 11:26
 * @Version 1.0
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        boolean invokeCount = userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
        return invokeCount;
    }
}
