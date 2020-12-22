package com.example.payment;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.example.payment.PaymentApplication;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

/**
 * @program:
 * @despciption:
 * @author: lzn
 * @create: 2020/12/22 10:47
 */

@AutoConfigureMockMvc
@Transactional
@SpringBootTest(classes = PaymentApplication.class)   //标识本类是一个SpringBootTest
@Slf4j
public class publicPaymentControllerTest
{
    @Value("${public-test.managementgate}")
    private String managementGate="localhost:8081";

    @Value("${public-test.mallgate}")
    private String mallGate="localhost:8081";

    private WebTestClient manageClient;

    private WebTestClient mallClient;


    @BeforeEach
    public void setUp(){
        System.out.println("in setup");

        this.manageClient = WebTestClient.bindToServer()
                .baseUrl("http://"+managementGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();

        this.mallClient = WebTestClient.bindToServer()
                .baseUrl("http://"+mallGate)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8")
                .build();
    }

    /**
     * 通过aftersaleId查找refund 成功
     */
    @Test
    public void getRefundTest1() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                mallClient.get().uri("/payment/aftersales/{id}/refunds",1)
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
    public void getRefundTest2() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                mallClient.get().uri("/payment/aftersales/{id}/refunds",666666)
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
    public void getRefundTest3() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                mallClient.get().uri("/payment/aftersales/{id}/refunds",295)
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
    public void getRefundTest4() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                mallClient.get().uri("/payment/orders/{id}/refunds",1)
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
    public void getRefundTest5() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                mallClient.get().uri("/payment/orders/{id}/refunds",666666)
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
    public void getRefundTest6() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                mallClient.get().uri("/payment/orders/{id}/refunds",2)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过aftersaleId和shopId查找refund  通过aftersaleId找shopId 返回的shopId与路径上的shopId不符
     */
    @Test
    public void getRefundTest7() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/refunds",666666,1)
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
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/refunds",1,1)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBody();

    }

    /**
     * 通过aftersaleId和shopId查找refund  找不到aftersaleId
     */
    @Test
    public void getRefundTest9() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/refunds",1,666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 通过orderId和shopId查找refund  通过orderId找shopId 返回的shopId与路径上的shopId不符
     */
    @Test
    public void getRefundTest10() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                manageClient.get().uri("/payment/shops/{shopId}/orders/{id}/refunds",666666,1)
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
    public void getRefundTest11() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                manageClient.get().uri("/payment/shops/{shopId}/orders/{id}/refunds",1,1)
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
     */
    @Test
    public void getRefundTest12() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                manageClient.get().uri("/payment/shops/{shopId}/orders/{id}/refunds",1,666666)
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                        .returnResult()
                        .getResponseBody();
    }

    /**
     * 查找支付单的所有状态
     */
    @Test
    public void getPaymentState() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString =
                mallClient.get().uri("/payment/payments/states")
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBody();

        String expectedResponse = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"name\": \"未支付\",\n" +
                "            \"code\": 0\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"已支付\",\n" +
                "            \"code\": 1\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"支付失败\",\n" +
                "            \"code\": 2\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }

    /**
     *
     * 获得支付单的所有状态，获取成功
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月15日20:32:07
     */
    @Test
    public void getAllPaymentsStatesTest() throws Exception{
        byte[] responseString=mallClient.get().uri("/payment/payments/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\"errno\":0,\"data\":[{\"name\":\"未支付\",\"code\":0},{\"name\":\"已支付\",\"code\":1},{\"name\":\"支付失败\",\"code\":2}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }


    /**
     *
     * 获得支付渠道，目前只返回002 模拟支付渠道，001返点支付
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月15日20:32:07
     */
    @Test
    public void getPaymentPatternsTset1() throws Exception{
        byte[] responseString=mallClient.get().uri("/payment/payments/patterns")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBody();

        String expectedResponse="{\"errno\":0,\"data\":[{\"name\":\"返点支付\",\"payPattern\":\"001\"},{\"name\":\"模拟支付渠道\",\"payPattern\":\"002\"}],\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }

    /**
     * 获取支付的所有可能状态
     * @throws Exception
     */
    @Test
    public void getPaymentStatusTest() throws Exception {
        byte[] responseString = mallClient.get().uri("/payment/payments/states")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"code\":0,\"name\":\"未支付\"},{\"code\":1,\"name\":\"已支付\"},{\"code\":2,\"name\":\"支付失败\"}]}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 获取所有可行的支付渠道
     * @throws Exception
     */
    @Test
    public void getPaymentPatternTest() throws Exception {
        byte[] responseString = mallClient.get().uri("/payment/payments/patterns")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBody();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":[{\"payPattern\":\"001\",\"name\":\"返点支付\"},{\"payPattern\":\"002\",\"name\":\"模拟支付渠道\"}]}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }

    /**
     * 查找订单(id=1)的支付信息, 成功
     * @throws Exception
     */
    @Test
    public void getOrderPaymentTest1() throws Exception {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString = mallClient.get().uri("/payment/orders/{id}/payments",1L)
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
     * 查找订单(id=12343210)的支付信息, 不存在该资源id
     * @throws Exception
     */
    @Test
    public void getOrderPaymentTest2() throws Exception {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString = mallClient.get().uri("/payment/orders/{id}/payments",12343210L)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }

    /** todo:增加todo
     * 查找订单(id=16405)的支付信息, 操作资源id不属于操作者
     * @throws Exception
     */
    @Test
    public void getOrderPaymentTest3() throws Exception {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString = mallClient.get().uri("/payment/orders/{id}/payments",16405L)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 查找售后(id=1)的支付信息, 成功
     * @throws Exception
     */
    @Test
    public void getAfterSalePaymentTest1() throws Exception {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString = mallClient.get().uri("/payment/aftersales/{id}/payments",1L)
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
     * 查找售后(id=12343210)的支付信息, 不存在该资源id
     * @throws Exception
     */
    @Test
    public void getAfterSalePaymentTest2() throws Exception {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString = mallClient.get().uri("/payment/aftersales/{id}/payments",12343210L)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 查找售后(id=54)的支付信息, 操作资源id不属于操作者
     * @throws Exception
     */
    @Test
    public void getAfterSalePaymentTest3() throws Exception {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString = mallClient.get().uri("/payment/aftersales/{id}/payments",54L)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 店家获取店内订单的支付单
     * @throws Exception
     */
    @Test
    // todo 修改 json 字段, 标准api 中为 aftersaleId 而非 afterSaleId
    public void shopGetOrderPayment() throws Exception {
        // depart = 7L
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseBytes = manageClient
                .get()
                .uri("/payment/shops/7/orders/2203919/payments")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        assert responseBytes != null;
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 1398040,\n" +
                "            \"orderId\": 2203919,\n" +
                "            \"amount\": 25535,\n" +
                "            \"actualAmount\": 25535,\n" +
                "            \"paymentPattern\": \"002\",\n" +
                "            \"aftersaleId\": null\n" +
                "        }\n" +
                "    ],\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString, responseString, false);
    }

    /**
     * 店家获取店内订单的支付单 (无权限)
     * @throws Exception
     */
    @Test
    public void shopGetOrderPaymentNoAuth() throws Exception {
        // depart = 7L
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseBytes = manageClient
                .get()
                .uri("/payment/shops/7/orders/2203923/payments")
                .header("authorization", token)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家获取店内订单的支付单 (无此订单号)
     * @throws Exception
     */
    @Test
    public void shopGetOrderPaymentNoOrder() throws Exception {
        // depart = 7L
        String token = new JwtHelper().createToken(100L, 100l, 100);;
        byte[] responseBytes = manageClient
                .get()
                .uri("/payment/shops/7/orders/2203928/payments")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家获取本人名下的支付单
     * @throws Exception
     */
    @Test
    // todo 修改 json 字段, 标准api 中为 aftersaleId 而非 afterSaleId
    public void customerGetOrderPayment() throws Exception {
        // userId = 2668
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseBytes = mallClient
                .get()
                .uri("/payment/orders/2203920/payments")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        assert responseBytes != null;
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        String expectedString = "{\n" +
                "    \"errno\": 0,\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"id\": 1398041,\n" +
                "            \"orderId\": 2203920,\n" +
                "            \"amount\": 66800,\n" +
                "            \"actualAmount\": 66800,\n" +
                "            \"paymentPattern\": \"002\",\n" +
                "            \"aftersaleId\": null\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 1398042,\n" +
                "            \"orderId\": 2203920,\n" +
                "            \"amount\": 200,\n" +
                "            \"actualAmount\": 200,\n" +
                "            \"paymentPattern\": \"001\",\n" +
                "            \"aftersaleId\": null\n" +
                "        }\n" +
                "    ],\n" +
                "    \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedString, responseString, false);
    }

    /**
     * 买家获订单的支付单 (无权限)
     * @throws Exception
     */
    @Test
    public void customerGetOrderPaymentNoAuth() throws Exception {
        // userId = 2668
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseBytes = mallClient
                .get()
                .uri("/payment/orders/2203924/payments")
                .header("authorization", token)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家获取订单的支付单 (无此订单号)
     * @throws Exception
     */
    @Test
    public void customerGetOrderPaymentNoOrder() throws Exception {
        // userId = 2668
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseBytes = mallClient
                .get()
                .uri("/payment/orders/2203928/payments")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 买家为订单支付 (订单状态禁止)
     * @throws Exception
     */
    @Test
    public void createPaymentNotAllow() throws Exception {
        // userId = 2668
        String token = new JwtHelper().createToken(100L, 100l, 100);
        String body = "{\n" +
                "  \"price\": 9,\n" +
                "  \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseBytes = mallClient
                .post()
                .uri("/payment/orders/2203919/payments")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家为订单支付
     * @throws Exception
     */
    @Test
    public void createPayment() throws Exception {
        // userId = 2668
        String token = new JwtHelper().createToken(100L, 100l, 100);
        String body = "{\n" +
                "  \"price\": 63800,\n" +
                "  \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseBytes = mallClient
                .post()
                .uri("/payment/orders/2203922/payments")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 买家为订单支付 (无此订单)
     * @throws Exception
     */
    @Test
    public void createPaymentNoOrder() throws Exception {
        // userId = 2668
        String token = new JwtHelper().createToken(100L, 100l, 100);
        String body = "{\n" +
                "  \"price\": 63800,\n" +
                "  \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] responseBytes = mallClient
                .post()
                .uri("/payment/orders/2203928/payments")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家为订单支付 (非本人订单)
     * @throws Exception
     */
    @Test
    public void createPaymentNotPersonal() throws Exception {
        // userId = 2668
        String token = new JwtHelper().createToken(100L, 100l, 100);
        String body = "{\n" +
                "  \"price\": 63800,\n" +
                "  \"paymentPattern\": \"002\"\n" +
                "}";
        mallClient
                .post()
                .uri("/payment/orders/2203925/payments")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectBody().jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家为订单支付 (超额支付)
     * @throws Exception
     */
    @Test
    public void createPaymentOverflow() throws Exception {
        // userId = 7768
        String token = new JwtHelper().createToken(100L, 100l, 100);
        String body = "{\n" +
                "  \"price\": 99999999,\n" +
                "  \"paymentPattern\": \"002\"\n" +
                "}";
        byte[] response = mallClient
                .post()
                .uri("/payment/orders/2203925/payments")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectBody().jsonPath("$.errno").exists()
                .returnResult().getResponseBody();

        assert response != null;
        String responseStr = new String(response, StandardCharsets.UTF_8);
        String notExpectedErrNo = "\"errno\": 0";
        JSONAssert.assertNotEquals(notExpectedErrNo, responseStr, false);
    }

    /**
     * 管理员查询售后单的支付信息，失败
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId3() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/payments",57101,47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }


    /**
     * 管理员查询售后单的支付信息，失败
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId4() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/payments",57101,47001)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBody();
    }



    /**
     * 管理员查询售后单的支付信息，失败
     * @author 洪晓杰
     */
    @Test
    public void getPaymentByAftersaleId5() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/aftersales/{id}/payments",57010,47004)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }




    /**
     * 买家查询自己的支付信息，success
     * @author 洪晓杰
     */
    @Test
    public void userQueryPaymentTest() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString=mallClient.get().uri("/payment/orders/{id}/payments",47123)
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
     * 买家查询自己的支付信息，失败
     * @author 洪晓杰
     */
    @Test
    public void userQueryPaymentTest2() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString=mallClient.get().uri("/payment/orders/{id}/payments",48230)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }




    /**
     * 管理员查询订单的支付信息，失败
     * @author 洪晓杰
     */
    @Test
    public void queryPaymentTest3() throws Exception{
        String token = new JwtHelper().createToken(100L, 100l, 100);
        byte[] responseString=manageClient.get().uri("/payment/shops/{shopId}/orders/{id}/payments",77777,47123)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBody();

    }
}

