package com.example.order;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;
import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.nio.charset.StandardCharsets;

/***
 * @author yansong chen
 * @time 2020-12-23 16:50
 * @description:
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = OrederApplication.class)
@Transactional
@Slf4j
public class OrderTestFrom6 {
    private String managementGate="localhost:8081";

    //@Value("${public-test.mallgate}")
    private String mallGate="localhost:8081";

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

    /**
     * 管理员登入
     * @param username 用户名
     * @param password 密码
     * @throws Exception parse error
     */
    private String adminLogin(String username, String password) {
        return new JwtHelper().createToken(100L, 7L, 100);
    }
    /**
     * 用户登入
     * @param username 用户名
     * @param password 密码
     * @throws Exception parse error
     */
    private String customerLogin(String username, String password) {
        return new JwtHelper().createToken(2668L, 100l, 100);
    }
    /**
     * 店家修改订单
     */
    @Test
    @Order(11)
    public void shopEditOrder() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/order/shops/7/orders/2203919")
                .header("authorization", token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/order/shops/7/orders/2203919")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"data\": {\n" +
                "    \"id\": 2203919,\n" +
                "    \"message\": \"我愛你\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家修改订单 (无权限)
     */
    @Test
    @Order(12)
    public void shopEditOrderNoRights() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"message\": \"有內鬼终止交易\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/order/shops/7/orders/2203923") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isForbidden() // 不被批准
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 店家修改订单 (不存在订单)
     * @throws Exception
     */
    @Test
    @Order(13)
    public void shopEditOrderNotExist() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        manageClient
                .put()
                .uri("/order/shops/7/orders/2203928") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单 (字段不合法)
     * @throws Exception
     */
    @Test
    @Order(14)
    public void shopEditOrderFieldIllegal() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{}";
        manageClient
                .put()
                .uri("/order/shops/7/orders/2203919") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单 (伪造 JWT)
     * @throws Exception
     */
    @Test
    @Order(15)
    public void shopEditOrderTokenIllegal() throws Exception {
        // depart = 7L

        String body = "{\n" +
                "  \"message\": \"我愛你\"\n" +
                "}";
        manageClient
                .put()
                .uri("/order/shops/7/orders/2203928") // depart=2
                .header("authorization", "12u8789781379127312ui3y1i3")
                .bodyValue(body)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 店家发货 (字段不合法)
     * @throws Exception
     */
    @Test
    @Order(16)
    public void shopDeliverFieldIllegal() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{}";
        manageClient
                .put()
                .uri("/order/shops/7/orders/2203919/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 店家发货
     * @throws Exception
     */
    @Test
    @Order(17)
    public void shopDeliver() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/order/shops/7/orders/2203919/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/order/shops/7/orders/2203919")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"data\": {\n" +
                "    \"id\": 2203919,\n" +
                "    \"state\": 2,\n" +
                "    \"subState\": 24,\n" +
                "    \"shipmentSn\": \"1212121212123\"\n" +
                "  }\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家发货 (发货单号为空)
     * @throws Exception
     */
    @Test
    @Order(18)
    public void shopDeliverAlreadyDelivered() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"\"\n" +
                "}";
        manageClient
                .put()
                .uri("/order/shops/7/orders/2203919/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (状态不允许)
     * @throws Exception
     */
    @Test
    @Order(19)
    public void shopDeliverStateNotAllow() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        manageClient
                .put()
                .uri("/order/shops/7/orders/2203921/deliver")
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (无权限)
     */
    @Test
    @Order(20)
    public void shopDeliverNoRights() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        byte[] responseBytes = manageClient
                .put()
                .uri("/order/shops/7/orders/2203923/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家发货 (不存在订单)
     * @throws Exception
     */
    @Test
    @Order(21)
    public void shopDeliverNotExist() throws Exception {
        // depart = 7L
        String token = adminLogin("shopadmin_No2", "123456");
        String body = "{\n" +
                "  \"freightSn\": \"1212121212123\"\n" +
                "}";
        manageClient
                .put()
                .uri("/order/shops/7/orders/2203928/deliver") // depart=2
                .header("authorization",token)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound() // 未找到
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /*
     * 支付部分
     */



    /**
     * 获取订单概要,伪造token
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(1)
    public void customerGetAllSimpleOrders1() throws Exception {
        byte[] responseString = mallClient.get().uri("/order/orders?page=1&pageSize=5")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 获取订单概要，userid为59的全部
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(2)
    public void customerGetAllSimpleOrders2() throws Exception {
        //userid=59
        //String token=this.login("39634362551", "123456");
        String token = new JwtHelper().createToken(59L, 123456L, 100);
        byte[] responseString = mallClient.get().uri("/order/orders?page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":13,\"pages\":3,\"pageSize\":5,\"page\":1,\"list\":[{\"id\":456,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1174,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1175,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1423,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":1424,\"customerId\":59,\"shopId\":1,\"pid\":null,\"orderType\":0,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]}}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 获取订单概要，根据orderSn,但OrderSn不是自己的,故查不到
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(3)
    public void customerGetAllSimpleOrders3() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/order/orders?orderSn=2016102333120&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":5,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取订单概要，根据状态查询
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(4)
    public void customerGetAllSimpleOrders5() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/order/orders?state=4&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":5,\"page\":1,\"list\":[{\"id\":38057,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":4,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]}}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取订单概要，2022年,时间超限故查不到
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(5)
    public void customerGetAllSimpleOrders6() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/order/orders?beginTime=2022-12-10T19:29:33&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":5,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取订单概要，根据orderSn和时间，订单编号存在，但时间不对，返回空
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(6)
    public void customerGetAllSimpleOrders7() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/order/orders?orderSn=2016102364965&beginTime=2021-12-10T19:29:33&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":5,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取订单概要，根据特定终止时间查询
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(7)
    public void customerGetAllSimpleOrders8() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/order/orders?endTime=2020-11-28T17:48:47&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":7,\"pages\":2,\"pageSize\":5,\"page\":1,\"list\":[{\"id\":38055,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":24,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":38056,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":38057,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":4,\"subState\":null,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":38058,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":24,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null},{\"id\":38059,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 获取订单概要，根据特定起止时间查询
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(8)
    public void customerGetAllSimpleOrders9() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/order/orders?beginTime=2020-11-29T17:48:47&endTime=2020-11-30T17:48:47&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":5,\"page\":1,\"list\":[{\"id\":38064,\"customerId\":58,\"shopId\":1,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":24,\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]},\"errmsg\":\"成功\"}\n";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }


    /**
     * 获取订单概要，查询一个被逻辑删除的订单,返回空
     * @author yujiawei 21620182203533
     * @throws Exception
     */
    @Test
    @Order(9)
    public void customerGetAllSimpleOrders10() throws Exception {
        //userid=58
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.get().uri("/order/orders?orderSn=2016102398984&page=1&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse="{\"errno\":0,\"data\":{\"total\":0,\"pages\":0,\"pageSize\":5,\"page\":1,\"list\":[]},\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 买家修改本人名下订单,伪造token
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/10 17:07
     * modifiedBy
     */
    @Test
    @Order(10)
    public void UsermodifyOrder1() throws Exception {
        byte[] responseString = mallClient.put().uri("/order/orders/{id}", 38058)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家修改本人名下订单,修改成功
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/10 17:07
     * modifiedBy
     */
    //222
    @Test
    @Order(11)
    public void UsermodifyOrder2() throws Exception {
        String orderJson="{\"consignee\": \"adad\",\n" +
                "  \"regionId\": 1,\n" +
                "  \"address\": \"曾厝垵\",\n" +
                "  \"mobile\": \"111\"}";
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.put().uri("/order/orders/438")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/order/orders/438")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{data:{id:438,regionId:1,address:曾厝垵}}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 买家修改本人名下订单,订单已发货，无法修改
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/10 17:07
     * modifiedBy
     */
    @Test
    @Order(12)
    public void UsermodifyOrder3() throws Exception {
        String orderJson="{\"consignee\": \"adad\",\n" +
                "  \"regionId\": 1,\n" +
                "  \"address\": \"曾厝垵\",\n" +
                "  \"mobile\": \"111\"}";
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.put().uri("/order/orders/38058")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家修改本人名下订单,访问的订单id不是自己的
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/10 17:07
     * modifiedBy
     */
    @Test
    @Order(13)
    public void UsermodifyOrder4() throws Exception {
        String orderJson="{\"consignee\": \"adad\",\n" +
                "  \"regionId\": 1,\n" +
                "  \"address\": \"曾厝垵\",\n" +
                "  \"mobile\": \"111\"}";
        //String token=this.login("39634362551", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        byte[] responseString = mallClient.put().uri("/order/orders/38058")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家修改本人名下订单,订单id不存在
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/10 17:07
     * modifiedBy
     */
    @Test
    @Order(14)
    public void UsermodifyOrder5() throws Exception {
        String orderJson="{\"consignee\": \"adad\",\n" +
                "  \"regionId\": 1,\n" +
                "  \"address\": \"曾厝垵\",\n" +
                "  \"mobile\": \"111\"}";
        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.put().uri("/order/orders/99999")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 买家取消，逻辑删除本人名下订单,伪造token
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(15)
    public void UserdeleteOrder1() throws Exception {
        byte[] responseString = mallClient.delete().uri("/order/orders/{id}", 38055)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 买家取消，逻辑删除本人名下订单,取消成功
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    //222333
    @Test
    @Order(16)
    public void UserdeleteOrder2() throws Exception {

        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.delete().uri("/order/orders/38059")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/order/orders/38059")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{data:{id:38059,state:4}}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 买家取消，逻辑删除本人名下订单,逻辑删除成功
     * 逻辑删除之后通过买家的订单查询api就无法查到这个订单，故返回id不存在
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(17)
    //222
    public void UserdeleteOrder3() throws Exception {

        String token=this.login("79734441805", "123456");
        byte[] responseString = mallClient.delete().uri("/order/orders/38059")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/order/orders/38059")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 买家取消，逻辑删除本人名下订单,订单已发货，无法取消
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(18)
    public void UserdeleteOrder4() throws Exception {
        String token = this.login("79734441805", "123456");
        //String token = createTestToken(7L, 2L, 100);
        byte[] responseString = mallClient.delete().uri("/order/orders/{id}", 38055)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家取消，逻辑删除本人名下订单,标记的订单不是他自己的
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(19)
    public void UserdeleteOrder5() throws Exception {
        //String token = createTestToken(8L, 2L, 100);
        String token = new JwtHelper().createToken(1L,100L,100);
        byte[] responseString = mallClient.delete().uri("/order/orders/{id}", 38055)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家取消，逻辑删除本人名下订单,订单id不存在
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(20)
    public void UserdeleteOrder6() throws Exception {
        String token = this.login("79734441805", "123456");
        byte[] responseString = mallClient.delete().uri("/order/orders/{id}", 99999)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 买家标记确认收货,伪造token
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(21)
    public void UserconfirmOrder1() throws Exception {
        String token = this.login("39634362551", "123456");
        byte[] responseString = mallClient.put().uri("/order/orders/{id}/confirm", 38056)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家标记确认收货,收货成功
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(22)
    public void UserconfirmOrder2() throws Exception {
        String token = this.login("79734441805", "123456");
        //String token = createTestToken(7L, 2L, 100);
        byte[] responseString = mallClient.put().uri("/order/orders/{id}/confirm",38061 )
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/order/orders/{id}",38061 )
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{data:{id:38061,state:3}}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 买家标记确认收货,状态不合法
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(23)
    public void UserconfirmOrder3() throws Exception {
        //String token = createTestToken(7L, 2L, 100);
        String token = this.login("79734441805", "123456");
        byte[] responseString = mallClient.put().uri("/order/orders/{id}/confirm", 38056)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家标记确认收货,标记的订单不是他自己的
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(24)
    public void UserconfirmOrder4() throws Exception {
        String token =/* this.login("39634362551", "123456");*/
        new JwtHelper().createToken(59L, 100l, 100);
        //String token = createTestToken(8L, 2L, 100);
        byte[] responseString = mallClient.put().uri("/order/orders/{id}/confirm", 38056)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家标记确认收货,标记的订单id不存在
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(25)
    public void UserconfirmOrder5() throws Exception {
        String token = this.login("79734441805", "123456");
        byte[] responseString = mallClient.put().uri("/order/orders/{id}/confirm", 99999)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家将团购订单转为普通订单,伪造token
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(26)
    public void UserchangeOrderToNormal1() throws Exception {
        byte[] responseString = mallClient.post().uri("/order/orders/{id}/groupon-normal", 12)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家将团购订单转为普通订单,转换成功
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    //2222
    @Order(27)
    public void UserchangeOrderToNormal2() throws Exception {
        String token = this.login("79734441805", "123456");
        //String token = createTestToken(7L, 2L, 100);
        byte[] responseString = mallClient.post().uri("/order/orders/{id}/groupon-normal",38060 )
                .header("authorization", token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = mallClient.get().uri("/order/orders/{id}", 38060)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse ="{data:{id:38060,state:2,subState:21,orderType:0}}";
        JSONAssert.assertEquals(expectedResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 买家将团购订单转为普通订单,状态不合法
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(28)
    public void UserchangeOrderToNormal3() throws Exception {
        String token = this.login("79734441805", "123456");
        byte[] responseString = mallClient.post().uri("/order/orders/{id}/groupon-normal", 38057)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家将团购订单转为普通订单,转换订单不是他自己的
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(29)
    public void UserchangeOrderToNormal4() throws Exception {
        //String token = this.login("39634362551", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        byte[] responseString = mallClient.post().uri("/order/orders/{id}/groupon-normal", 38057)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 买家将团购订单转为普通订单,订单id不存在
     * @author yujiawei 21620182203533
     * createdBy yujiawei 2020/12/3 17:07
     * modifiedBy
     */
    @Test
    @Order(30)
    public void UserchangeOrderToNormal5() throws Exception {
        String token = this.login("79734441805", "123456");
        byte[] responseString = mallClient.post().uri("/order/orders/{id}/groupon-normal", 99999)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
    /**
     * 1
     *
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    @Order(1)
    public void shopUpdateOrderTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{ \"message\": \"test\"}";
        byte[] responseString = manageClient.put().uri("/order/shops/{shopId}/orders/{id}", 123, 40000)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBody();

//        //查询
//        byte[] responseString1 = manageClient.get().uri("/shops/{shopId}/orders/{id}", 123, 40000)
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .returnResult()
//                .getResponseBody();
//        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":40000,\"message\":\"test\"},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString1, StandardCharsets.UTF_8), false);
    }


    /**
     * 2
     * 不是自己店铺的资源
     *
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    @Order(2)
    public void shopUpdateOrderTest2() throws Exception {
        String token = this.login("537300010", "123456");
        String json = "{ \"message\": \"test\"}";
        byte[] responseString = manageClient.put().uri("/order/shops/{shopId}/orders/{id}", 1, 40000)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 3
     * 不存在这个资源
     *
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    @Order(3)
    public void shopUpdateOrderTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        String json = "{ \"message\": \"test\"}";
        byte[] responseString = manageClient.put().uri("/order/shops/{shopId}/orders/{id}", 123, 100000)
                .header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBody();
    }

    /**
     * 4
     * 商铺将支付完成的订单改为发货状态(状态码为21的订单才能修改为发货状态)
     *
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    @Order(4)
    public void shopDeliverOrderTest1() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/order/shops/{shopId}/orders/{id}/deliver", 123, 40000)
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"123456\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBody();

//        //查询
//        byte[] responseString1 = manageClient.get().uri("/shops/{shopId}/orders/{id}", 123, 40000)
//                .header("authorization", token)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
//                .returnResult()
//                .getResponseBody();
//        System.out.println(new String(responseString1, StandardCharsets.UTF_8));
//        String expectedResponse = "{\"errno\":0,\"data\":{\"id\":40000,\"shipmentSn\":\"123456\",\"subState\":24},\"errmsg\":\"成功\"}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString1, StandardCharsets.UTF_8), false);
    }

    /**
     * 5
     * 商铺将支付完成的订单改为发货状态(状态码为21的订单才能修改为发货状态),但订单状态不为21无法完成转化
     *
     * @author 张湘君 24320182203327
     * @date Created in 2020年12月3日20:32:07
     */
    @Test
    @Order(5)
    public void shopDeliverOrderTest2() throws Exception {
        String token = this.login("13088admin", "123456");

        byte[] responseString = manageClient.put().uri("/order/shops/{shopId}/orders/{id}/deliver", 123, 40001)
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"123456\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBody();

    }

    private String login(String userName, String password) throws Exception {
        return new JwtHelper().createToken(58L, 100l, 100);
/*        JSONObject body = new JSONObject();
        body.put("userName", userName);
        body.put("password", password);
        String requireJson = body.toJSONString();

        byte[] ret = manageClient.post().uri("/adminusers/login").bodyValue(requireJson).exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo("成功")
                .returnResult()
                .getResponseBodyContent();
        return JacksonUtil.parseString(new String(ret, "UTF-8"), "data");*/

    }

}
