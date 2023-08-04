package com.lhw.hwapiclinetsdk.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * @ClassName SignUtils
 * @Description TODO
 * @Author Administrator
 * @Date 2023/8/4 12:02
 * @Version 1.0
 */
public class SignUtils {


    /**
     * 签名工具
     * @param hashmap
     * @param secretKey
     * @return
     */
    public static String genSign(String hashmap, String secretKey){
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        String content = hashmap + "." + secretKey;
        return digester.digestHex(content);
    }
}
