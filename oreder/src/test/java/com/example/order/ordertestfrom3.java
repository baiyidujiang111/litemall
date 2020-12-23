package com.example.order;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

/***
 * @author yansong chen
 * @time 2020-12-23 11:49
 * @description:
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = OrederApplication.class)
@Transactional
@Slf4j
public class ordertestfrom3 {

    //@Value("${public-test.managementgate}")
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
     * 买家修改本人名下订单，success
     * @author 洪晓杰
     */
    @Test
    @Order(4)
    public void updateOrderForCustomer2()throws Exception{
        //String token = userLogin("44357456028", "123456");
        String token = new JwtHelper().createToken(8763L, 123456L, 100);
        String orderVoJson = "{\n" +
                "  \"address\": \"string\",\n" +
                "  \"consignee\": \"string\",\n" +
                "  \"mobile\": \"string\",\n" +
                "  \"regionId\": 0\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/order/orders/{id}/",47008)
                .header("authorization", token)
                .bodyValue(orderVoJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 买家修改本人名下订单，失败：操作的资源id不存在
     * @author 洪晓杰
     */
    @Test
    @Order(3)
    public void updateOrderForCustomer4()throws Exception{
        //String token = userLogin("44357456028", "123456");
        String token = new JwtHelper().createToken(8763L, 123456L, 100);
        String orderVoJson = "{\n" +
                "  \"address\": \"string\",\n" +
                "  \"consignee\": \"string\",\n" +
                "  \"mobile\": \"string\",\n" +
                "  \"regionId\": 0\n" +
                "}";

        byte[] responseString = mallClient.put().uri("/order/orders/{id}/",47127)
                .header("authorization", token)
                .bodyValue(orderVoJson)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 买家标记确认收货，失败：ordersId所属customerId不一致，则无法修改
     * @author 洪晓杰
     */
    @Test
    @Order(5)
    public void updateOrderStateToConfirm()throws Exception{
        //String token = userLogin("2728932539", "123456");
        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = mallClient.put().uri("/order/orders/{id}/confirm",47123)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 买家标记确认收货，success
     * @author 洪晓杰
     */
    @Test
    @Order(8)
    public void updateOrderStateToConfirm3()throws Exception{
        //String token = userLogin("44357456028", "123456");
        String token = new JwtHelper().createToken(8763L, 123456L, 100);
        byte[] responseString = mallClient.put().uri("/order/orders/{id}/confirm",47010)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 买家取消，逻辑删除本人名下订单，失败：ordersId与所属customerId不一致，则无法修改
     * @author 洪晓杰
     */
    @Test
    @Order(9)
    public void updateOdersForLogicDelete2()throws Exception{
        //注意再改成登录的时候要修改userId，让其不一致
        //String token = userLogin("2728932539", "123456");
        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = mallClient.delete().uri("/order/orders/{id}/",47007)
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 买家取消，逻辑删除本人名下订单，失败：操作的资源id不存在
     * @author 洪晓杰
     */
    @Test
    @Order(11)
    public void updateOdersForLogicDelete4()throws Exception{
        //String token = userLogin("44357456028", "123456");
        String token = new JwtHelper().createToken(8763L, 123456L, 100);
        byte[] responseString = mallClient.delete().uri("/order/orders/{id}/",47367)
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 买家取消，逻辑删除本人名下订单，成功
     * @author 洪晓杰
     */
    @Test
    @Order(12)
    public void updateOdersForLogicDelete()throws Exception{
        //String token = userLogin("44357456028", "123456");
        String token = new JwtHelper().createToken(8763L, 123456L, 100);
        byte[] responseString = mallClient.delete().uri("/order/orders/{id}/",47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse ="{\n" +
                "  \"errno\": 0,\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), true);
    }
}
