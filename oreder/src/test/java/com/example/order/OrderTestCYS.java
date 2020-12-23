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
    String token = new JwtHelper().createToken(1L, 123456L, 100);
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

    @Test
    @Order(1)
    public void shopsShopIdOrdersGet0() throws Exception {
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString=manageClient.get().uri("order/shops/406/orders").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 8,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 10,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 48050,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 11,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48051,\n" +
                "        \"customerId\": 2,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 12,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48052,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 21,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48053,\n" +
                "        \"customerId\": 4,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 22,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48054,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 23,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48055,\n" +
                "        \"customerId\": 7,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 24,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48056,\n" +
                "        \"customerId\": 7,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 3,\n" +
                "        \"subState\": 11,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48057,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 4,\n" +
                "        \"subState\": 11,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家查询商户所有订单 (概要) 输入shopId及page=1 pageSize=3
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(2)
    public void shopsShopIdOrdersGet1() throws Exception {
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString=manageClient.get().uri("/order/shops/406/orders?page=1&pageSize=3").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 8,\n" +
                "    \"pages\": 3,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 48050,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 11,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48051,\n" +
                "        \"customerId\": 2,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 12,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 48052,\n" +
                "        \"customerId\": 1,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 2,\n" +
                "        \"subState\": 21,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家查询商户所有订单 (概要) 查找指定买家且page=1,pageSize=3
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(3)
    public void shopsShopIdOrdersGet2() throws Exception {
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString=manageClient.get().uri("/order/shops/406/orders?customerId=2&page=1&pageSize=3").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": {\n" +
                "    \"total\": 1,\n" +
                "    \"pages\": 1,\n" +
                "    \"pageSize\": 3,\n" +
                "    \"page\": 1,\n" +
                "    \"list\": [\n" +
                "      {\n" +
                "        \"id\": 48051,\n" +
                "        \"customerId\": 2,\n" +
                "        \"shopId\": 406,\n" +
                "        \"pid\": null,\n" +
                "        \"orderType\": 0,\n" +
                "        \"state\": 1,\n" +
                "        \"subState\": 12,\n" +
                "        \"gmtCreate\": \"2020-11-28T17:48:46\",\n" +
                "        \"originPrice\": null,\n" +
                "        \"discountPrice\": null,\n" +
                "        \"freightPrice\": null\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        System.out.println(new String(responseString, StandardCharsets.UTF_8));
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家查询商户所有订单 (概要) 查找指定订单编号
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(4)
    public void shopsShopIdOrdersGet3() throws Exception {
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString=manageClient.get().uri("/order/shops/406/orders?orderSn=2016102363333").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse="{\"errno\":0,\"data\":{\"total\":1,\"pages\":1,\"pageSize\":10,\"page\":1,\"list\":[{\"id\":48052,\"customerId\":1,\"shopId\":406,\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-11-28T17:48:46\",\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null}]},\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）订单id不存在
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(5)
    public void shopsShopIdOrdersIdGet1() throws Exception {
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString=manageClient.get().uri("/order/shops/406/orders/4000000").header("authorization",token).exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）查找非本店铺订单
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(6)
    public void shopsShopIdOrdersIdGet2() throws Exception {
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString=manageClient.get().uri("/order/shops/406/orders/1").header("authorization",token).exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家修改订单留言 成功
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(7)
    public void shopsShopIdOrdersIdPut0() throws Exception {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/48050")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/order/shops/406/orders/48050")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48050,message:test}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 店家修改订单留言 订单id不存在
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(8)
    public void shopsShopIdOrdersIdPut1() throws Exception {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/4000000")
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
     * 店家修改订单留言 订单非本店铺订单
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(9)
    public void shopsShopIdOrdersIdPut2() throws Exception {
        String orderJson="{\n" +
                "  \"message\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/1")
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
     * 店家对订单标记发货 成功
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(10)
    public void postFreights0() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/48052/deliver")
                .header("authorization", token)
                .bodyValue(orderJson)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/order/shops/406/orders/48052")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48052,shipmentSn:test}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);

    }

    /**
     * 店家对订单标记发货 操作的订单id不存在
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(11)
    public void postFreights1() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/4000000/deliver")
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
     * 店家对订单标记发货 操作的订单不是本店铺的
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(12)
    public void postFreights2() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/1/deliver")
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
     * 店家对订单标记发货 订单状态为新订单不满足待发货
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(13)
    public void postFreights3() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/48050/deliver")
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
     * 店家对订单标记发货 订单状态为待支付尾款
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(14)
    public void postFreights4() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/48051/deliver")
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
     * 店家对订单标记发货 订单状态为待成团
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(15)
    public void postFreights5() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/48053/deliver")
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
     * 店家对订单标记发货 订单状态为未成团
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(16)
    public void postFreights6() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/48054/deliver")
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
     * 店家对订单标记发货 订单状态为已发货
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(17)
    public void postFreights7() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/48055/deliver")
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
     * 店家对订单标记发货 订单状态为已完成
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(18)
    public void postFreights8() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/48056/deliver")
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
     * 店家对订单标记发货 订单状态为已取消
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(19)
    public void postFreights9() throws Exception {
        String orderJson="{\n" +
                "  \"freightSn\": \"test\"\n" +
                "}";
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString = manageClient.put().uri("/order/shops/406/orders/48057/deliver")
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
     * 获得订单所有状态
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(20)
    public void getorderState() throws Exception {
        String token = new JwtHelper().createToken(1L, 123456L, 100);

        byte[] responseString=manageClient.get().uri("/order/orders/states").header("authorization",token).exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\n" +
                "  \"errno\": 0,\n" +
                "  \"data\": [\n" +
                "    {\n" +
                "      \"name\": \"待付款\",\n" +
                "      \"code\": 1\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"待收货\",\n" +
                "      \"code\": 2\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"已完成\",\n" +
                "      \"code\": 3\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"已取消\",\n" +
                "      \"code\": 4\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"新订单\",\n" +
                "      \"code\": 11\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"待支付尾款\",\n" +
                "      \"code\": 12\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"付款完成\",\n" +
                "      \"code\": 21\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"待成团\",\n" +
                "      \"code\": 22\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"未成团\",\n" +
                "      \"code\": 23\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"已发货\",\n" +
                "      \"code\": 24\n" +
                "    }\n" +
                "  ],\n" +
                "  \"errmsg\": \"成功\"\n" +
                "}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员取消本店铺订单 订单状态为新订单 成功
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(21)
    public void shopsShopIdOrdersIdDelete0() throws Exception {

        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = manageClient.delete().uri("/order/shops/406/orders/48050")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/order/shops/406/orders/48050")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48050,state:4}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员取消本店铺订单 订单状态为待支付尾款
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(22)
    public void shopsShopIdOrdersIdDelete1() throws Exception {

        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = manageClient.delete().uri("/order/shops/406/orders/48051")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/order/shops/406/orders/48051")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48051,state:4}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员取消本店铺订单 取消的订单id不存在
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(23)
    public void shopsShopIdOrdersIdDelete2() throws Exception {

        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = manageClient.delete().uri("order/shops/406/orders/4000000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单 取消的订单不是本店铺的
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(24)
    public void shopsShopIdOrdersIdDelete3() throws Exception {

        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = manageClient.delete().uri("/order/shops/406/orders/1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单
     * 由于postFreight0修改了订单48052的状态 所以此时该订单的状态已发货，不能取消
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(25)
    public void shopsShopIdOrdersIdDelete4() throws Exception {
        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = manageClient.delete().uri("/order/shops/406/orders/48052")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单 订单状态为待成团
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(26)
    public void shopsShopIdOrdersIdDelete5() throws Exception {

        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = manageClient.delete().uri("/order/shops/406/orders/48053")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        byte[] confirmString = manageClient.get().uri("/order/shops/406/orders/48053")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48053,state:4}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员取消本店铺订单 订单状态为未成团
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(27)
    public void shopsShopIdOrdersIdDelete6() throws Exception {

        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = manageClient.delete().uri("/order/shops/406/orders/48054")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\": 0,\"errmsg\": \"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, StandardCharsets.UTF_8), false);

        byte[] confirmString = manageClient.get().uri("/order/shops/406/orders/48054")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        String expectedConResponse = "{data:{id:48054,state:4}}";

        JSONAssert.assertEquals(expectedConResponse, new String(confirmString, StandardCharsets.UTF_8), false);
    }

    /**
     * 管理员取消本店铺订单 订单状态为已发货
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(28)
    public void shopsShopIdOrdersIdDelete7() throws Exception {

        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = manageClient.delete().uri("/order/shops/406/orders/48055")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单 订单状态为已完成
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(29)
    public void shopsShopIdOrdersIdDelete8() throws Exception {

        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = manageClient.delete().uri("/order/shops/406/orders/48056")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员取消本店铺订单 订单状态为已取消
     * @author ChenYixin 24320182203180
     * @throws Exception
     */
    @Test
    @Order(30)
    public void shopsShopIdOrdersIdDelete9() throws Exception {

        String token = new JwtHelper().createToken(1L, 123456L, 100);
        byte[] responseString = manageClient.delete().uri("/order/shops/406/orders/48057")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();
    }
}
