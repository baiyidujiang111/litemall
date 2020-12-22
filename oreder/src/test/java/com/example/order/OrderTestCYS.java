package com.example.order;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.example.order.mapper.OrdersMapper;
import com.example.order.model.vo.OrderFreightSn;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/***
 * @author yansong chen
 * @time 2020-12-17 23:46
 * @description:
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = OrederApplication.class)
@Transactional
@Slf4j
public class OrderTestCYS {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrdersMapper ordersMapper;

    @Test
    public void setDefaultFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(1L, 0L, 100);
        String responseString = this.mvc.perform(get("/order/shops/3/orders?pageSize=2")
                .header("authorization", token)
                .contentType("application/json;charset=UTF-8"))
                //.queryParam("orderSn","2016102364965"))
                //.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();

        System.out.println(responseString);
        log.debug(responseString);
    }

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
     * 1 获取商铺订单概要测试1
     * 正常访问本商铺的订单
     *
     * @throws Exception
     */
    @Test
    @Order(0)
    public void getAllShopOrdersTest1() throws Exception {
        //String token = this.login("13088admin", "123456");
        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] ret = manageClient.get()
                .uri("/order/shops/3/orders?pageSize=2")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{" +
                "\"page\":1,\"pageSize\":2,\"total\":26,\"pages\":13,\"list\":[" +
                "{\"id\":240000,\"customerId\":2830,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}," +
                "{\"id\":240001,\"customerId\":4298,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 23 店家标记订单发货3
     * 访问属于本商铺的订单
     * 但当前订单状态并非“付款完成”或“待收货”
     *
     * @throws Exception
     */


    //1
    @Test
    public void markShopOrderDeliverTest5() throws Exception {
        //String token = this.login("13088admin", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        byte[] ret = manageClient.put()
                .uri("/order/shops/3/orders/240025/deliver")
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"1233\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 24 店家标记订单发货4
     * 访问属于本商铺的订单
     * 当前订单状态为“付款完成”或“待收货”
     *
     * @throws Exception
     */
    @Test
    @Order(23)
    public void markShopOrderDeliverTest4() throws Exception {
        //String token = this.login("13088admin", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        byte[] ret = manageClient.put()
                .uri("/order/shops/3/orders/240019/deliver")
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"1233\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");

        ret = manageClient.get()
                .uri("/order/shops/3/orders/240019")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(2)
                .jsonPath("$.data.subState").isEqualTo(24)
                .jsonPath("$.data.shipmentSn").isEqualTo("1233")
                .returnResult()
                .getResponseBodyContent();

    }

    //1
    @Test
    @Order(1)
    public void shopUpdateOrderTest1() throws Exception {
        //String token = this.login("13088admin", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
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

    }

    /**
     * 查找订单的所有状态 1
     */
    @Test
    public void getOrderState() throws Exception{
        //String token = this.userLogin("8606245097", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        byte[] responseString =
                mallClient.get().uri("/order/orders/states")
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
                "            \"name\": \"已取消\",\n" +
                "            \"code\": 4\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"待付款\",\n" +
                "            \"code\": 1\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"待成团\",\n" +
                "            \"code\": 22\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"待支付尾款\",\n" +
                "            \"code\": 12\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"新订单\",\n" +
                "            \"code\": 11\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"未成团\",\n" +
                "            \"code\": 23\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"付款完成\",\n" +
                "            \"code\": 21\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"已完成\",\n" +
                "            \"code\": 3\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"待收货\",\n" +
                "            \"code\": 2\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"已发货\",\n" +
                "            \"code\": 24\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse,new String(responseString, StandardCharsets.UTF_8),false);
    }


    /**
     * 查找订单  查询条件：通过orderSn查找
     */
    @Test
    public void getOrders1() throws Exception {
        //String token = this.userLogin("8606245097", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        byte[] responseString =
                mallClient.get().uri("/order/orders?orderSn=2016102322523&page=1&pageSize=5")
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
     * 查找订单  查询条件：通过beginTime查找  即查找创建时间在beginTime之后的订单
     */
    @Test
    public void getOrders2() throws Exception {
        //String token = this.userLogin("8606245097", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        byte[] responseString =
                mallClient.get().uri("/order/orders?page=1&pageSize=5&beginTime=2020-11-24 18:40:20")
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
     * 查找订单  查询条件：通过endTime查找  即查找创建时间在endTime之前的订单
     */
    @Test
    public void getOrders3() throws Exception {
        //String token = this.userLogin("8606245097", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        byte[] responseString =
                mallClient.get().uri("/order/orders?page=1&endTime=2021-11-23 18:40:20&pageSize=5")
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
     * 查找订单  查询条件：通过beginTime和endTime联合查找  即查找创建时间在beginTime和endTime之间的订单
     */
    @Test
    public void getOrders4() throws Exception {
        //String token = this.userLogin("8606245097", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        byte[] responseString =
                mallClient.get().uri("/order/orders?page=1&endTime=2021-11-23 18:40:20&pageSize=5&beginTime=2020-11-24 18:40:20")
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
     * 查找订单  查询条件：beginTime或endTime格式错误,返回错误码503
     */
    @Test
    public void getOrders6() throws Exception {
        //String token = this.userLogin("8606245097", "123456");
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        byte[] responseString =
                mallClient.get().uri("/order/orders?page=1&pageSize=5&beginTime=2020-11-2418:40:20")
                        .header("authorization", token)
                        .exchange()
                        .expectStatus().isBadRequest()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.FIELD_NOTVALID.getCode())
                        .returnResult()
                        .getResponseBody();
    }
}
