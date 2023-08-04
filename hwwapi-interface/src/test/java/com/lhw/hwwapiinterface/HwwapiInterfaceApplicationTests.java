package com.lhw.hwwapiinterface;

import com.lhw.hwapiclinetsdk.client.HwApiClient;
import com.lhw.hwapiclinetsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class HwwapiInterfaceApplicationTests {

    @Resource
    private HwApiClient hwApiClient;
    @Test
    void contextLoads() {
        User user = new User();
        user.setUsername("liyueyue");
        String liyue = hwApiClient.getUsernameByPost(user);
        System.out.println(liyue);
    }

}
