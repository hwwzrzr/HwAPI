## 业务流程
管理系统模块
![](https://cdn.nlark.com/yuque/0/2023/jpeg/32679998/1691063148184-f279f0a2-4187-4204-9ce5-a17392485b1c.jpeg)
系统架构图
![](https://cdn.nlark.com/yuque/0/2023/jpeg/32679998/1691064360685-2881337c-bedf-46d4-a6bf-56658b6aaa3f.jpeg)
## 开发流程
### 1、搭建好管理系统
（其实User部分并没有管理员权限，，，所有的用户都能全部操作,但是接口部分是存在权限的）
普通用户模块（获取当前登录用户、用户登录、用户注册、用户退出、更新用户）
管理员模块（增加用户、删除用户、根据id查询用户、查询所有用户、分页查询用户）
初步设计接口模块：
普通用户：增加接口(不合理)、根据Id查询接口、分页查看接口
管理员：删除接口、查看所有接口信息、更新接口
基于redis实现共享session登录（spring-session-data-redis）
### 2、构思好抽取的common：
实体（User、InterfaceInfo、UserInterfaceInfo-用户调用接口统计）、枚举类、vo、service
### 3、开发接口项目
### 4、调用接口项目
可以使用HttpClient、RestTemplate、第三方库（Hutool）去写一个客户端，这样就能不通过浏览器的方式去调用接口
```java
public String getUsernameByPost(User user, HttpServletRequest request){
        String json = JSONUtil.toJsonStr(user);
        HttpResponse httpResponse = HttpRequest.post("https://localhost:8123/api/name/user")
                .addHeaders(getHeaderMap(json))
                .body(json)
                .execute();
        System.out.println(httpResponse.getStatus());
        String body = httpResponse.body();
        System.out.println(body);
        return body;
    }
```
### 5、API签名认证
1、本质：签发签名、校验签名（复杂、无序、无规律）
2、为什么需要：保证接口调用的安全性，不能让任意用户随意调用
3、实现：
accessKey：调用的标识
secretKey：秘钥
ak\sk都是无状态的
在用户表中增添ak、sk字段
4、直接在http请求中传递ak、sk会被拦截，不安全，所以需要设计签名生成算法，不在Http请求中传递sk、通过签名生成算法生成一个API签名，将签名传递、客户端通过一样的参数和签名生成算法去计算签名用于比对。
5、防重放：
加nonce随机数、只能用一次，服务端要保存用过的随机数
加timestamp时间戳，校验时间戳是否过期。
（可以用时间戳配合随机数使用，时间戳范围内的随机数会过期，这样服务端的压力会减小）
6、设计API签名认证算法：
参数：1、ak	2、用户请求参数	3、nonce随机数	4、timestamp时间戳
sign签名生成算法

暂时将API签名认证的校验逻辑写在API接口处，引入网关在网关中统一做API签名认证
```java
public static String genSign(String hashmap, String secretKey){
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        String content = hashmap + "." + secretKey;
        return digester.digestHex(content);
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
```
```java
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
        String signService = SignUtils.genSign(accessKey + nonce + body + timestamp, "liyue");
        if(!sign.equals(signService)){
            throw new RuntimeException("API校验不通过，无权限");
        }
```
### 6、SDK开发
为什么需要Starter：
开发者如果要调用接口，就得自己写httpclient调用、签名算法之类的工作，麻烦。理想情况就是开发者只需要关心调用哪些接口、传递哪些参数，就跟调用自己写的代码一样简单。所以在开发者引入SDK后，只需要在application.yml中写好配置就能直接使用。（涉及Springboot自动配置的原理）

spring-boot-configuration-processor:自动生成配置的代码提示
### 7、开发管理员接口功能、用户接口功能
管理员：
发布：校验接口是否存在、判断该接口是否可以调用、修改接口数据库中的状态字段为1
下线接口：校验接口是否存在、修改接口数据库中的状态字段为0
用户：
1、浏览接口、查看接口文档、
2、申请签名ak\sk（用户注册的时候生成唯一的签名）
```java
        	// 3. 分配 accessKey, secretKey
            String accessKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(5));
            String secretKey = DigestUtil.md5Hex(SALT + userAccount + RandomUtil.randomNumbers(8));
```
3、在线调试接口
流程：
a、前端将用户输入的请求参数和要测试的接口id发给平台后端(封装一个专门的请求参数对象)
补充接口对象的请求参数字段；
b、在调用前做一些校验
c、平台后端去调用接口项目
### 8、接口调用次数统计
#### 用户每次调用接口成功+1
#### 给用户分配或者用户自主申请接口调用次数

业务流程：
1、设计库表：用户和接口之间多对多的关系--》用户接口关系表
```sql
-- 用户调用接口关系表
create table if not exists yuapi.`user_interface_info`
(
    `id` bigint not null auto_increment comment '主键' primary key,
    `userId` bigint not null comment '调用用户 id',
    `interfaceInfoId` bigint not null comment '接口 id',
    `totalNum` int default 0 not null comment '总调用次数',
    `leftNum` int default 0 not null comment '剩余调用次数',
    `status` int default 0 not null comment '0-正常，1-禁用',
    `createTime` datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    `updateTime` datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    `isDelete` tinyint default 0 not null comment '是否删除(0-未删, 1-已删)'
) comment '用户调用接口关系';
```
2、给用户接口表设置基本的CRUD（增加、删除、修改用户调用接口关系、根据id查询、查询全部、分页查询），但是都需要管理员权限
3、开发用户调用接口次数+1的功能（service层）
总调用次数+1，剩余调用次数-1
**Todo：在并发情况下，需要加一个锁（分布式锁）**
4、问题：
a、因为我们的系统会提供SDK，便于用户自己下载部署去调用接口，所以在系统后端统计接口调用次数可能不完整。
b、在接口端进行统计，需要接口开发者自己去添加统计代码，增加接口开发者方的工作量；
c、使用AOP添加：
优点：独立于接口，在每个接口调用后统计次数+1
缺点：还是存在于单个项目中， 独开发者还是需要引入切面的包。
d、通过网关对所有的接口项目进行统一统计！
### 9、引入网关
#### 网关的作用（在本项目中用到的部分）
1、路由（转发请求到接口项目中）
2、统一鉴权（ak、sk）
~~3、跨域~~
~~4、缓存~~
5、流量染色
6、访问控制
7、统一业务处理（每次请求接口后，接口调用次数+1）
8、发布控制
~~9、负载均衡~~
10，接口保护
a 限制请求
b 信息脱敏
c 服务降级、服务熔断
d 限流
e 超时时间
11、 统一日志
~~12、统一文档~~
#### 网关的应用场景以实现
#### 业务逻辑
1、用户发送请求到API网关
2、请求日志
3、黑白名单（访问控制）
4、用户鉴权（ak、sk）
5、请求的接口是否存在
6、请求转发、调用模拟接口
7、响应日志
8、调用成功，请求次数+1
9、调用失败，返回规范的错误码

### 10、引入RPC
网关项目比较纯净，没有引入操作数据库的包，可以调用后端系统中的数据库方法
#### 调用其他项目的方法：
1、复制代码和依赖、环境
2、HTTP请求（提供一个接口、供其他项目调用）
3、RPC
4、把公共的代码打个jar包，其他项目去引用（客户端SDK）
#### HTTP请求怎么调用：
提供方开发一个接口（地址、请求方法、参数、返回值）
调用方使用HTTPClient之类的代码包去发生HTTP请求
#### RPC
作用：像调用本地方法一样调用远程方法, 对开发者更透明
#### 整合：
backend项目作为服务提供者：
1、去数据库中查询ak、sk是否已经分配给用户，并获取ak、sk  **getInvokeUser**
2、从数据库中查询模拟接口是否存在，以及请求方法是否匹配，校验参数  **getInterfaceInfo**
3、调用成功，接口次数+1 **invokeCount方法**
##### 公共服务：目的是让方法、实体类、在多个项目之间复用

### 11、开发统计分析

### 12、接口计费调用


