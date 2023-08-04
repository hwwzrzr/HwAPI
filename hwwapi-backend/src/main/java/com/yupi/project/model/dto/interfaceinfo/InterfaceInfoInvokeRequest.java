package com.yupi.project.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName InterfaceInfoInvokeRequest
 * @Description TODO
 * @Author Administrator
 * @Date 2023/8/4 18:08
 * @Version 1.0
 */
@Data
public class InterfaceInfoInvokeRequest implements Serializable {
    private Long id;
    private String userRequestParams;
    private static final long serialVersionUID = 1L;
}
