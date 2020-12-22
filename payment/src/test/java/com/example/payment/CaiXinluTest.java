package com.example.payment;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/**
 * @author Cai Xinlu  24320182203165
 * @date 2020-12-14 17:28
 */

@SpringBootTest(classes = PaymentApplicationTests.class)   //标识本类是一个SpringBootTest
@Slf4j
public class CaiXinluTest {
    @Value("${public-test.managementgate}")
    private String managementGate;

    @Value("${public-test.mallgate}")
    private String mallGate;

    private WebTestClient manageClient;

    private WebTestClient mallClient;

    @BeforeEach
    public void setUp() {
        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://" + managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://" + mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    private String adminLogin(String userName, String password) throws Exception {
        return "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE2MDkwNjU1ODZPIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjotMiwiZXhwIjozNzU1NTY0NDYyLCJ1c2VySWQiOjEsImlhdCI6MTYwODA4MDgxNX0.N_IyvgpKM6272QLHwQi9pjK2FWf2flLcTynww2XruQM";
    }

    private String userLogin(String userName, String password) throws Exception {
        return "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0aGlzIGlzIGEgdG9rZW4iLCJhdWQiOiJNSU5JQVBQIiwidG9rZW5JZCI6IjIwMjAxMjE2MDkwNjU1ODZPIiwiaXNzIjoiT09BRCIsImRlcGFydElkIjotMiwiZXhwIjozNzU1NTY0NDYyLCJ1c2VySWQiOjEsImlhdCI6MTYwODA4MDgxNX0.N_IyvgpKM6272QLHwQi9pjK2FWf2flLcTynww2XruQM";
    }

    /**
     * 通过aftersaleId查找refund 成功
     */
    //成功
    @Test
    public void getRefundTest1() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("payment/aftersales/{id}/refunds", 1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .jsonPath("$.data").exists()
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过aftersaleId查找refund  找不到路径上的aftersaleId
     */
    @Test
    public void getRefundTest2() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("payment/aftersales/{id}/refunds", 666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过aftersaleId查找refund  orderId不属于Token解析出来的userId
     */
    @Test
    public void getRefundTest3() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        System.out.println(token);
        byte[] responseString =
                mallClient.get().uri("payment/aftersales/{id}/refunds", 295)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过orderId查找refund  成功
     */
    @Test
    public void getRefundTest4() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("payment/orders/{id}/refunds", 1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .jsonPath("$.data").exists()
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过orderId查找refund  找不到路径上的orderId
     */
    @Test
    public void getRefundTest5() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("payment/orders/{id}/refunds", 666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();

    }


    /**
     * 通过orderId查找refund  orderId不属于Token解析出来的userId
     */
    @Test
    public void getRefundTest6() throws Exception {
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("payment/orders/{id}/refunds", 2)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * todo:需要dubbo
     * 通过aftersaleId和shopId查找refund  通过aftersaleId找shopId 返回的shopId与路径上的shopId不符
     */
    @Test
    public void getRefundTest7() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString =
                manageClient.get().uri("payment/shops/{shopId}/aftersales/{id}/refunds", 666666, 1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过aftersaleId和shopId查找refund  成功
     */
    @Test
    public void getRefundTest8() throws Exception{
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =
                manageClient.get().uri("payment/shops/{shopId}/aftersales/{id}/refunds",1,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBody();

    }
//    @Test
//    public void getRefundTest8() throws Exception {
//        String token = this.adminLogin("13088admin", "123456");
//        byte[] responseString =
//                manageClient.get().uri("payment/shops/{shopId}/aftersales/{id}/refunds",  1,1)
//                        .header("authorization", token)
//                        .exchange()
//                        .expectStatus().isOk()
//                        .expectBody()
//                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                        .returnResult()
//                        .getResponseBody();
//
//    }

    @Test
    public void getRefundTest110() throws Exception{
        String token = this.userLogin("8606245097", "123456");
        byte[] responseString =
                mallClient.get().uri("payment/aftersales/{id}/refunds",1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .jsonPath("$.data").exists()
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过aftersaleId和shopId查找refund  找不到aftersaleId
     */
    @Test
    public void getRefundTest9() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =
                manageClient.get().uri("payment/shops/{shopId}/aftersales/{id}/refunds", 1, 666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     *
     * 通过orderId和shopId查找refund  通过orderId找shopId 返回的shopId与路径上的shopId不符
     */
    @Test
    public void getRefundTest10() throws Exception {
        String token = this.adminLogin("537300010", "123456");
        byte[] responseString =
                manageClient.get().uri("payment/shops/{shopId}/orders/{id}/refunds", 666666, 1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过orderId和shopId查找refund  成功
     */
    @Test
    public void getRefundTest11() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =
                manageClient.get().uri("payment/shops/{shopId}/orders/{id}/refunds", 1, 1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过orderId和shopId查找refund  找不到orderId
     * 没有errno
     */
    @Test
    public void getRefundTest12() throws Exception {
        String token = this.adminLogin("13088admin", "123456");
        byte[] responseString =
                manageClient.get().uri("payment/shops/{shopId}/orders/{id}/refunds", 1, 666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();
    }
}
