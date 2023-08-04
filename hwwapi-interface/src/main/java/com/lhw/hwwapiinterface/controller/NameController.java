package com.lhw.hwwapiinterface.controller;

import com.lhw.hwwapiinterface.model.User;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

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
        // todo 调用次数 + 1 invokeCount
        String result = "POST 用户名字是" + user.getUsername();
        return result;
    }
}
