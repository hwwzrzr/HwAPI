package com.lhw.hwapiclinetsdk;

import com.lhw.hwapiclinetsdk.client.HwApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName HwApiClientAutoCongig
 * @Description TODO
 * @Author Administrator
 * @Date 2023/8/4 13:23
 * @Version 1.0
 */
@Configuration
@ConfigurationProperties("hwapi.client")
@Data
@ComponentScan
public class HwApiClientAutoCongig {
    private String accessKey;
    private String secretKey;

    @Bean
    public HwApiClient hwApiClient(){
        return new HwApiClient(accessKey, secretKey);
    }
}
