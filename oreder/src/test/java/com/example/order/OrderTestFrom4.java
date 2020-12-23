package com.example.order;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;

/***
 * @author yansong chen
 * @time 2020-12-23 12:15
 * @description:
 */
public class OrderTestFrom4 {
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

    private String login(String userName, String password) throws Exception {
        return new JwtHelper().createToken(1L, 123456L, 100);
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
        String token = this.login("13088admin", "123456");
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
     * 2 获取商铺订单概要测试2
     * 正常访问本商铺的订单（分页）
     *
     * @throws Exception
     */
    @Test
    @Order(1)
    public void getAllShopOrdersTest2() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.get()
                .uri("/order/shops/3/orders?page=2&pageSize=2")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{" +
                "\"page\":2,\"pageSize\":2,\"total\":26,\"pages\":13,\"list\":[" +
                "{\"id\":240002,\"customerId\":5344,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}," +
                "{\"id\":240003,\"customerId\":2830,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 3 获取商铺订单概要测试3
     * 正常访问本商铺的订单（分页大小）
     *
     * @throws Exception
     */
    @Test
    @Order(2)
    public void getAllShopOrdersTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.get()
                .uri("/order/shops/3/orders?page=2&pageSize=5")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{" +
                "\"page\":2,\"pageSize\":5,\"total\":26,\"pages\":6,\"list\":[" +
                "{\"id\":240005,\"customerId\":5344,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}," +
                "{\"id\":240006,\"customerId\":2830,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}," +
                "{\"id\":240007,\"customerId\":4298,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}," +
                "{\"id\":240008,\"customerId\":5344,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}," +
                "{\"id\":240009,\"customerId\":2830,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 4 获取商铺订单概要测试4
     * 正常访问本商铺的订单（订单号）
     *
     * @throws Exception
     */
    @Test
    @Order(3)
    public void getAllShopOrdersTest4() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.get()
                .uri("/order/shops/3/orders?orderSn=2020121229742&pageSize=1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{" +
                "\"page\":1,\"pageSize\":1,\"total\":1,\"pages\":1,\"list\":[" +
                "{\"id\":240025,\"customerId\":7,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":3,\"subState\":24,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null,\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 5 获取商铺订单概要测试5
     * 访问非本商铺的订单（订单号）
     * 采用店铺ID为1的账号登录
     *
     * @throws Exception
     */
    @Test
    @Order(4)
    public void getAllShopOrdersTest5() throws Exception {
        String token = this.login("537300010", "123456"); //departId=1
        byte[] ret = manageClient.get()
                .uri("/order/shops/1/orders?orderSn=2020112681600&pageSize=1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":1,\"total\":0,\"pages\":0,\"list\":[]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 6 获取商铺订单概要测试6
     * 访问不存在的订单（订单号）
     *
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getAllShopOrdersTest6() throws Exception {
        String token = this.login("537300010", "123456"); //departId=1
        byte[] ret = manageClient.get()
                .uri("/order/shops/1/orders?orderSn=20190712576690000&pageSize=1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":1,\"total\":0,\"pages\":0,\"list\":[]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 7 获取商铺订单概要测试7
     * 正常访问本商铺的订单（顾客id）
     *
     * @throws Exception
     */
    @Test
    @Order(6)
    public void getAllShopOrdersTest7() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.get()
                .uri("/order/shops/3/orders?customerId=7&pageSize=1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{" +
                "\"page\":1,\"pageSize\":1,\"total\":3,\"pages\":3,\"list\":[" +
                "{\"id\":240022,\"customerId\":7,\"shopId\":3," +
                "\"pid\":null,\"orderType\":0,\"state\":2,\"subState\":21,\"gmtCreate\":\"2020-12-10T19:29:33\"," +
                "\"originPrice\":null,\"discountPrice\":null," +
                "\"freightPrice\":null," +
                "\"grouponId\":null,\"presaleId\":null,\"shipmentSn\":null}]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 8 获取商铺订单概要测试8
     * 访问非本商铺的订单（顾客id）
     * （顾客没下过本商铺的订单）
     * 采用店铺ID为1的账号登录
     *
     * @throws Exception
     */
    @Test
    @Order(7)
    public void getAllShopOrdersTest8() throws Exception {
        String token = this.login("537300010", "123456"); //departId=1
        byte[] ret = manageClient.get()
                .uri("/order/shops/1/orders?customerId=734678&pageSize=1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":1,\"total\":0,\"pages\":0,\"list\":[]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 9 获取商铺订单概要测试9
     * 访问非本商铺的订单（顾客id+订单序列号）
     * （订单序列号存在但不属于该顾客）
     * 采用店铺ID为1的账号登录
     *
     * @throws Exception
     */
    @Test
    @Order(8)
    public void getAllShopOrdersTest9() throws Exception {
        String token = this.login("537300010", "123456"); //departId=1
        byte[] ret = manageClient.get()
                .uri("/order/shops/1/orders?customerId=734&orderSn=2020112681600&pageSize=1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":1,\"total\":0,\"pages\":0,\"list\":[]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

    }

    /**
     * 10 获取商铺订单概要测试10
     * 访问非本商铺的订单（顾客id+订单序列号）
     * （订单序列号存在且属于该顾客但不属于本商铺）
     * 采用店铺ID为1的账号登录
     *
     * @throws Exception
     */
    @Test
    @Order(9)
    public void getAllShopOrdersTest10() throws Exception {
        String token = this.login("537300010", "123456"); //departId=1
        byte[] ret = manageClient.get()
                .uri("/order/shops/1/orders?customerId=5344&orderSn=2020121224844&pageSize=1")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":1,\"total\":0,\"pages\":0,\"list\":[]}}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);
    }

    /**
     * 11 获取商铺订单详细内容测试1
     * 访问不存在的订单
     *
     * @throws Exception
     */
    @Test
    @Order(10)
    public void getShopOrderDetailsTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.get()
                .uri("/order/shops/3/orders/66666666")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 12 获取商铺订单详细内容测试2
     * 访问不属于本商铺的订单
     * 采用店铺ID为1的账号登录
     *
     * @throws Exception
     */
    @Test
    @Order(11)
    public void getShopOrderDetailsTest2() throws Exception {
        String token = this.login("537300010", "123456"); //departId=1
        byte[] ret = manageClient.get()
                .uri("/order/shops/1/orders/240000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }
    /**
     * 14 店家留言测试1
     * 访问不存在的订单
     *
     * @throws Exception
     */
    @Test
    @Order(13)
    public void addShopOrderMessageTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.put()
                .uri("/order/shops/3/orders/66666666")
                .header("authorization", token)
                .bodyValue("{\"message\": \"6666\"}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 15 店家留言测试2
     * 访问不属于本商铺的订单
     * 采用店铺ID为1的账号登录
     *
     * @throws Exception
     */
    @Test
    @Order(14)
    public void addShopOrderMessageTest2() throws Exception {
        String token = this.login("537300010", "123456"); //departId=1
        byte[] ret = manageClient.put()
                .uri("/order/shops/1/orders/240000")
                .header("authorization", token)
                .bodyValue("{\"message\": \"6666\"}")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 16 店家留言测试3
     * 访问属于本商铺的订单
     *
     * @throws Exception
     */
    @Test
    @Order(15)
    public void addShopOrderMessageTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.put()
                .uri("/order/shops/3/orders/240020")
                .header("authorization", token)
                .bodyValue("{\"message\": \"6666\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
        String responseString = new String(ret, "UTF-8");

        ret = manageClient.get()
                .uri("/order/shops/3/orders/240020")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.message").isEqualTo("6666")
                .returnResult()
                .getResponseBodyContent();



    }


    /**
     * 17 店家取消订单测试1
     * 访问不存在的订单
     *
     * @throws Exception
     */
    @Test
    @Order(16)
    public void cancelShopOrderTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/order/shops/3/orders/66666666")
                .header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 18 店家取消订单测试2
     * 访问不属于本商铺的订单
     * 采用店铺ID为1的账号登录
     *
     * @throws Exception
     */
    @Test
    @Order(17)
    public void cancelShopOrderTest2() throws Exception {
        String token = this.login("537300010", "123456"); //departId=1
        byte[] ret = manageClient.delete()
                .uri("/order/shops/1/orders/240000")
                .header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 19 店家取消订单测试3
     * 访问属于本商铺的订单
     *
     * @throws Exception
     */
    @Test
    @Order(18)
    public void cancelShopOrderTest3() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/order/shops/3/orders/240021")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();

        String responseString = new String(ret, "UTF-8");

        ret = manageClient.get()
                .uri("/order/shops/3/orders/240021")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.state").isEqualTo(4)
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 20 店家取消订单测试4
     * 访问属于本商铺的订单
     * 但当前订单状态为“已完成”
     *
     * @throws Exception
     */
    @Test
    @Order(19)
    public void cancelShopOrderTest4() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.delete()
                .uri("/order/shops/3/orders/240021")
                .header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 21 店家标记订单发货1
     * 访问不存在的订单
     *
     * @throws Exception
     */
    @Test
    @Order(20)
    public void markShopOrderDeliverTest1() throws Exception {
        String token = this.login("13088admin", "123456");
        byte[] ret = manageClient.put()
                .uri("/order/shops/3/orders/66666666/deliver")
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"1233\"}")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 22 店家标记订单发货2
     * 访问不属于本商铺的订单
     * 采用店铺ID为1的账号登录
     *
     * @throws Exception
     */
    @Test
    @Order(21)
    public void markShopOrderDeliverTest2() throws Exception {
        String token = this.login("537300010", "123456"); //departId=1
        byte[] ret = manageClient.put()
                .uri("/order/shops/1/orders/240000/deliver")
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"1233\"}")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode())
                .returnResult()
                .getResponseBodyContent();

    }

    /**
     * 23 店家标记订单发货3
     * 访问属于本商铺的订单
     * 但当前订单状态并非“付款完成”或“待收货”
     *
     * @throws Exception
     */
    @Test
    @Order(22)
    public void markShopOrderDeliverTest3() throws Exception {
        String token = this.login("13088admin", "123456");
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
        String token = this.login("13088admin", "123456");
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
}
