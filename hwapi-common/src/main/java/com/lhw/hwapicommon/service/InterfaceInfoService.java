package com.lhw.hwapicommon.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.lhw.hwapicommon.model.entity.InterfaceInfo;

/**
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);
}
