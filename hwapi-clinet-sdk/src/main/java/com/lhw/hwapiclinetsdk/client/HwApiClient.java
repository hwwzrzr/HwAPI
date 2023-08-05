package com.lhw.hwapiclinetsdk.client;


import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.lhw.hwapiclinetsdk.model.User;
import com.lhw.hwapiclinetsdk.utils.SignUtils;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName HwApiClient
 * @Description TODO
 * @Author Administrator
 * @Date 2023/8/4 9:37
 * @Version 1.0
 */
public class HwApiClient {
    private String accessKey;
    private String secretKey;

    private static final String GATEWAY_HOST = "http://localhost:9527";

    public HwApiClient(String accessKey, String secretKey){
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "liyue");
        String result3= HttpUtil.get(GATEWAY_HOST + "/api/name/get", paramMap);
        return result3;
    }

    public String getNameByPost(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.post(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    private Map<String, String> getHeaderMap(String body){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
        String sign = SignUtils.genSign(hashMap.toString(), secretKey);
        hashMap.put("sign", sign);
        return hashMap;
    }

    public String getUsernameByPost(User user){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + "/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String body = httpResponse.body();
        System.out.println(body);
        return body;
    }
}
