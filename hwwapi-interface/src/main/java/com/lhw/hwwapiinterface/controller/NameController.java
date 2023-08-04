package com.lhw.hwwapiinterface.controller;

import cn.hutool.core.util.RandomUtil;
import com.lhw.hwapiclinetsdk.model.User;
import com.lhw.hwapiclinetsdk.utils.SignUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

/**
 * @ClassName InterfaceController
 * @Description TODO
 * @Author Administrator
 * @Date 2023/8/4 8:46
 * @Version 1.0
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getNameByGet(String name, HttpServletRequest request) {
        System.out.println(request.getHeader("hwAPI"));
        return "GET 你的名字是" + name;
    }

    @PostMapping("/post")
    public String getNameByPostAndUrl(@RequestParam String name) {
        return "POST 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUsernameByPost(@RequestBody User user, HttpServletRequest request){
        //API签名校验功能
       // TODO: 迁移到网关中去
        String accessKey = request.getHeader("accessKey");
        String nonce = request.getHeader("nonce");
        String body = request.getHeader("body");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        // todo 校验上述参数,实际情况ak要求数据库中查询
        if(!accessKey.equals("liyue")){
            throw new RuntimeException("API校验不通过，无权限");
        }
        Long currentTime = System.currentTimeMillis() / 1000;
        final Long FIVE_MINUTES = 60 * 5L;
        if ((currentTime - Long.parseLong(timestamp)) >= FIVE_MINUTES) {
            throw new RuntimeException("API校验不通过，无权限");
        }
        // todo 实际情况secretKey要求数据库中查询
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        hashMap.put("nonce", nonce);
        hashMap.put("body", body);
        hashMap.put("timestamp", timestamp);
        String signService = SignUtils.genSign(hashMap.toString(), "liyue");
        if(!sign.equals(signService)){
            throw new RuntimeException("API校验不通过，无权限");
        }
        // todo 调用次数 + 1 invokeCount
        String result = "POST 用户名字是" + user.getUsername();
        return result;
    }
}
