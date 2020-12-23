package com.example.freight.controller.publicTest;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.freight.FreightApplication;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @program: oomall
 * @description:
 * @author: alex101
 * @create: 2020-12-21 12:07
 **/
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(classes = FreightApplication.class)   //标识本类是一个SpringBootTest
@Slf4j
public class PublicFreightControllerTest {

    @Value("${public-test.managementgate}")
    private String managementGate="localhost:8080";

    @Value("${public-test.mallgate}")
    private String mallGate="localhost:8080";

    private WebTestClient manageClient;

    private WebTestClient mallClient;


    @Before
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




    @Autowired
    private MockMvc mvc;

    @Test
    public void changeFreightModel1() throws Exception
    {
        String token = new JwtHelper().createToken(100L, 295l, 100);
        System.out.println(mallGate);
        String freightJson = "{\"name\": \"freightModeTest\",\"unit\": 90}";
        byte[] responseString =
                manageClient.put().uri("/freight/shops/{shopId}/freightmodels/{id}",295,88888)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }


    /**
     * 修改运费模板  todo:路径上的shopId与Token中解析出来的不符 这应该是网关做的，暂时测试不过
     * @throws Exception
     */
    @Test
    public void changeFreightModel2() throws Exception
    {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        String freightJson = "{\"name\": \"freightModel\",\"unit\": 100}";
        byte[] responseString =
                manageClient.put().uri("/freight/shops/{shopId}/freightmodels/{id}",295,88888)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();

    }

    /**
     * 修改运费模板  运费模板名重复
     * @throws Exception
     */
    @Test
    public void changeFreightModel3() throws Exception
    {
        String token = new JwtHelper().createToken(100L, 295l, 100);
        String freightJson = "{\"name\": \"freightModel2\",\"unit\": 100}";
        byte[] responseString =
                manageClient.put().uri("/freight/shops/{shopId}/freightmodels/{id}",295,99999)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.FREIGHTNAME_SAME.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }


    /**
     * 修改件数运费模板 运费模板中该地区已经定义  region已存在
     * @throws Exception
     */
    @Test
    public void changePieceFreightModel1() throws Exception
    {

        String token = new JwtHelper().createToken(100L, 295l, 100);
        String freightJson = "{\n" +
                "    \"firstItems\": 60,\n" +
                "    \"firstItemsPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33,\n" +
                "    \"regionId\": 111\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/freight/shops/{shopId}/pieceItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_SAME.getCode())
                        .returnResult()
                        .getResponseBodyContent();

    }

    /**
     * 修改件数运费模板 todo:路径上的shopId与Token中解析出来的不符 网关负责的
     * @throws Exception
     */
    @Test
    public void changePieceFreightModel2() throws Exception
    {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        String freightJson = "{\n" +
                "    \"firstItems\": 60,\n" +
                "    \"firstItemsPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("freight/shops/{shopId}/pieceItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();
    }

    @Test
    public void changePieceFreightModel3() throws Exception
    {
        String token = new JwtHelper().createToken(100L, 295l, 100);
        String freightJson = "{\n" +
                "    \"firstItems\": 60,\n" +
                "    \"firstItemsPrice\": 22,\n" +
                "    \"additionalItems\": 11,\n" +
                "    \"additionalItemsPrice\": 33\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/freight/shops/{shopId}/pieceItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBodyContent();

    }

    /**
     * 修改重量运费模板 运费模板中该地区已经定义  region已存在
     * @throws Exception
     */
    @Test
    public void changeWeightFreightModel1() throws Exception
    {
        String token = new JwtHelper().createToken(100L, 295l, 100);
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391,\n" +
                "    \"regionId\": 111\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/freight/shops/{shopId}/weightItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.REGION_SAME.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }


    /**
     * 重量运费模板修改成功
     * @throws Exception
     */
    @Test
    public void changeWeightFreightModel2() throws Exception
    {
        String token = new JwtHelper().createToken(100L, 295l, 100);
        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/freight/shops/{shopId}/weightItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody()
                        .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                        .returnResult()
                        .getResponseBodyContent();
    }

    /**
     * todo:网关负责
     * 修改重量运费模板 路径上的shopId与Token中解析出来的不符
     * @throws Exception
     */
    @Test
    public void changeWeightFreightModel3() throws Exception
    {
        String token = new JwtHelper().createToken(100L, 100l, 100);

        String freightJson = "{\n" +
                "    \"firstWeightFreight\": 519,\n" +
                "    \"tenPrice\": 391\n" +
                "}";
        byte[] responseString =
                manageClient.put().uri("/freight/shops/{shopId}/weightItems/{id}",295,55555)
                        .header("authorization",token)
                        .bodyValue(freightJson)
                        .exchange()
                        .expectStatus().isForbidden()
                        .expectBody()
                        .returnResult()
                        .getResponseBodyContent();
    }

    /**
     * 计算运费1
     * todo:需要到商品模块才能拿信息
     * @throws Exception
     */

    @Test
    public void calculateFreight1() throws Exception {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        String json = "[{\"count\":6,\"skuId\":10000},{\"count\":2,\"skuId\":10001},{\"count\":1,\"skuId\":10002}]";
        byte[] responseString = mallClient.post().uri("/region/2/price").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"data\":1794}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 计算运费不可达
     * todo:没有dubbo服务
     * @throws Exception
     */
    @Test
    public void calculateFreight3() throws Exception {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        String json = "[{\"count\":1,\"skuId\":10000},{\"count\":1,\"skuId\":10001},{\"count\":1,\"skuId\":10002}]";
        byte[] responseString = mallClient.post().uri("/region/20001/price").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":805}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }
    /**
     * 计算运费2
     * todo:没有dubbo服务
     * @throws Exception
     */
    @Test
    public void calculateFreight4() throws Exception {

        String token = null;
        String json = "[{\"count\":1,\"skuId\":10000},{\"count\":1,\"skuId\":10001},{\"count\":1,\"skuId\":10002}]";
        byte[] responseString = mallClient.post().uri("/region/2/price").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":0,\"data\":522}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /** todo:token处理，需要网关
     * 店家或管理员为商铺定义默认运费模板，失败
     * @author 洪晓杰
     */
    @Test
    public void setupDefaultModelTest1() throws Exception{
        String token = null;

        byte[] responseString = manageClient.post().uri("/freight/shops/{shopId}/freightmodels/{id}/default",47012,47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 管理员定义管理员定义重量模板明细，success
     * @author 洪晓杰
     */
    @Test
    @Order(15)
    public void insertWeightFreightModelTest4() throws Exception{
        String token = new JwtHelper().createToken(200L, 100l, 50);
        String weightFreightModelJson = "{\n" +
                "  \"abovePrice\": 0,\n" +
                "  \"fiftyPrice\": 0,\n" +
                "  \"firstWeight\": 0,\n" +
                "  \"firstWeightFreight\": 0,\n" +
                "  \"hundredPrice\": 0,\n" +
                "  \"regionId\": 0,\n" +
                "  \"tenPrice\": 0,\n" +
                "  \"trihunPrice\": 0\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/freight/shops/{shopId}/freightmodels/{id}/weightItems",47002,47012)
                .header("authorization", token)
                .bodyValue(weightFreightModelJson)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 店家或管理员为商铺定义默认运费模板，失败
     * @author 洪晓杰
     */
    @Test
    public void insertPieceFreightModelTest6() throws Exception{
        String token = null;
        String pieceFreightModelJson = "{\n" +
                "  \"additionalItem\": 0,\n" +
                "  \"additionalItemsPrice\": 0,\n" +
                "  \"firstItem\": 0,\n" +
                "  \"firstItemsPrice\": 0,\n" +
                "  \"regionId\": 0\n" +
                "}";

        byte[] responseString = manageClient.post().uri("/freight/shops/{shopId}/freightmodels/{id}/default",47012,47011)
                .header("authorization", token)
                .bodyValue(pieceFreightModelJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * todo:需要网关
     * 店家或管理员为商铺定义默认运费模板，失败
     * @author 洪晓杰
     */
    @Test
    public void insertPieceFreightModelTest7() throws Exception{
        String token = null;
        String pieceFreightModelJson = "{\n" +
                "  \"additionalItem\": 0,\n" +
                "  \"additionalItemsPrice\": 0,\n" +
                "  \"firstItem\": 0,\n" +
                "  \"firstItemsPrice\": 0,\n" +
                "  \"regionId\": 0\n" +
                "}";

        byte[] responseString = manageClient.post().uri("/freight/shops/{shopId}/freightmodels/{id}/default",47012,47011)
                .header("authorization", token)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }


    /** todo:需要网关
     * 管理员定义件数模板明细，失败
     * @author 洪晓杰
     */
    @Test
    public void insertPieceFreightModelTest8()  throws Exception{
        String token = null;
        String pieceFreightModelJson = "{\n" +
                "  \"additionalItem\": 0,\n" +
                "  \"additionalItemsPrice\": 0,\n" +
                "  \"firstItem\": 0,\n" +
                "  \"firstItemsPrice\": 0,\n" +
                "  \"regionId\": 0\n" +
                "}";
        byte[] responseString = manageClient.post().uri("/freight/shops/{shopId}/freightmodels/{id}/pieceItems",47012,47011)
                .header("authorization", token)
                .bodyValue(pieceFreightModelJson)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.AUTH_NEED_LOGIN.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /*
    todo:需要网关
     */
    @Test
    @Order(1)
    public void calculateFreight() throws Exception {
        String token = new JwtHelper().createToken(200L, 100l, 50);
        String json = "[{\"count\":1,\"skuId\":1275}]";
        byte[] responseString = mallClient.post().uri("/region/201/price").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").isEqualTo(18)
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 计算运费
     *
     * @throws Exception 不可达
     */
    @Test
    @Order(2)
    public void calculateFreight2() throws Exception {

        String token = new JwtHelper().createToken(200L, 100l, 50);
        String json = "[{\"count\":1,\"skuId\":1275}]";
        byte[] responseString = mallClient.post().uri("/region/54101/price").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":805}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    @Test
    @Order(3)
    public void getFreightModelSummary() throws Exception {
        String token = new JwtHelper().createToken(200L, 100l, 50);

        byte[] responseString = manageClient.get().uri("/freight/shops/1/freightmodels/200").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 测试获取模板概要功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    @Order(5)
    public void getFreightModelSummary2() throws Exception {
        String token = new JwtHelper().createToken(200L, 100l, 50);

        byte[] responseString = manageClient.get().uri("/freight/shops/1/freightmodels/13").header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 测试获取模板概要功能
     * 成功
     *
     * @throws Exception
     */
    @Test
    @Order(4)
    public void getFreightModelSummary1() throws Exception {
        String token = new JwtHelper().createToken(200L, 100l, 50);

        byte[] responseString = manageClient.get().uri("/freight/shops/1/freightmodels/9").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

//        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }


    /**
     * 测试获取运费模板功能
     * 全部获取
     *
     * @throws Exception
     */
    @Test
    @Order(6)
    public void getFreightModels() throws Exception {
        String token = new JwtHelper().createToken(200L, 100l, 50);
        byte[] responseString = manageClient.get().uri("/freight/shops/1/freightmodels").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data.list").isArray()
                .returnResult()
                .getResponseBodyContent();

//        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\",\"data\":{\"page\":1,\"pageSize\":10,\"total\":6,\"pages\":1,\"list\":[{\"id\":9,\"name\":\"测试模板\",\"type\":0,\"unit\":500,\"defaultModel\":true,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":10,\"name\":\"测试模板2\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":11,\"name\":\"测试模板3\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":12,\"name\":\"测试模板4\",\"type\":0,\"unit\":500,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":22,\"name\":\"ight model/100g\",\"type\":0,\"unit\":100,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"},{\"id\":23,\"name\":\"piece model/2\",\"type\":1,\"unit\":2,\"defaultModel\":false,\"gmtCreate\":\"2020-12-02T20:33:08\",\"gmtModified\":\"2020-12-02T20:33:08\"}]}}";
//        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);
    }


    /**
     * 测试克隆模板功能
     * 资源不存在
     *
     * @throws Exception
     */
    @Test
    @Order(10)
    public void cloneFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 100l, 50);
        //String token = adminLogin("537300010", "123456");
        byte[] responseString = manageClient.post().uri("/freight/shops/1/freightmodels/200/clone").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试克隆模板功能
     * 成功
     *
 t     * @throws Exception
     */
    @Test
    @Order(11)
    public void cloneFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(200L, 100l, 50);
        byte[] responseString = manageClient.post().uri("/freight/shops/1/freightmodels/9/clone").header("authorization", token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

        String temp = new String(responseString, "UTF-8");
        int startIndex = temp.indexOf("id");
        int endIndex = temp.indexOf("name");
        String id = temp.substring(startIndex + 4, endIndex - 2);

        byte[] queryResponseString = manageClient.get().uri("/freight/shops/1/freightmodels/" + id).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        JSONAssert.assertEquals(new String(queryResponseString, "UTF-8"), new String(responseString, "UTF-8"), true);
    }

    /**
     * 测试克隆模板功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    @Order(12)
    public void cloneFreightModel2() throws Exception {
        String token = new JwtHelper().createToken(200L, 100l, 50);
        byte[] responseString = manageClient.post().uri("/freight/shops/1/freightmodels/13/clone").header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试定义默认模板功能
     * 操作资源不存在
     *
     * @throws Exception
     */
    @Test
    @Order(13)
    public void defineDefaultFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        byte[] responseString = manageClient.post().uri("/freight/shops/1/freightmodels/200/default").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试定义默认模板功能
     * 成功
     *
     * @throws Exception
     */
    @Test
    @Order(14)
    public void defineDefaultFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        byte[] responseString = manageClient.post().uri("/freight/shops/1/freightmodels/22/default").header("authorization", token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

        byte[] queryResponseString = manageClient.get().uri("/freight/shops/1/freightmodels/22").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        JSONAssert.assertEquals(expectedResponse, new String(queryResponseString, "UTF-8"), false);
    }



    /**
     * 测试定义默认模板功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    @Order(15)
    public void defineDefaultFreightModel2() throws Exception {
        String token = new JwtHelper().createToken(200L, 100l, 50);
        byte[] responseString = manageClient.post().uri("/freight/shops/1/freightmodels/13/default").header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }
    /**
     * 测试定义模板功能
     *
     * @throws Exception
     */
    @Test
    @Order(16)
    public void defineFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        String json = "{\"name\":\"测试名\",\"type\":0,\"unit\":500}";

        byte[] responseString = manageClient.post().uri("/freight/shops/1/freightmodels").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();


        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

        String temp = new String(responseString, "UTF-8");
        int startIndex = temp.indexOf("id");
        int endIndex = temp.indexOf("name");
        String id = temp.substring(startIndex + 4, endIndex - 2);

        byte[] queryResponseString = manageClient.get().uri("/freight/shops/1/freightmodels/" + id).header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        JSONAssert.assertEquals(new String(queryResponseString, "UTF-8"), new String(responseString, "UTF-8"), true);
    }

    /**
     * 测试定义模板功能
     * 模板名重复
     *
     * @throws Exception
     */
    @Test
    @Order(17)
    public void defineFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        String json = "{\"name\":\"测试模板5\",\"type\":0,\"unit\":500}";
        byte[] responseString = manageClient.post().uri("/freight/shops/1/freightmodels").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":802}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试修改模板功能
     * 操作的资源id不存在
     *
     * @throws Exception
     */
    @Test
    @Order(18)
    public void modifyFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        String json = "{\"name\":\"测试名\",\"unit\":500}";
        byte[] responseString = manageClient.put().uri("/freight/shops/1/freightmodels/200").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试修改模板功能
     * 运费模板名重复
     *
     * @throws Exception
     */
    @Test
    @Order(19)
    public void modifyFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        String json = "{\"name\":\"测试模板3\",\"unit\":550}";

        byte[] responseString = manageClient.put().uri("/freight/shops/1/freightmodels/12").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":802}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试修改模板功能
     * 成功
     *
     * @throws Exception
     */
    @Test
    @Order(20)
    public void modifyFreightModel2() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        String json = "{\"name\":\"模板修改测试名\",\"unit\":550}";
        byte[] responseString = manageClient.put().uri("/freight/shops/1/freightmodels/9").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

        byte[] queryResponseString = manageClient.get().uri("/freight/shops/1/freightmodels/9").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();

        JSONAssert.assertEquals(expectedResponse, new String(queryResponseString, "UTF-8"), false);
    }

    /**
     * 测试修改模板功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    @Order(21)
    public void modifyFreightModel3() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        String json = "{\"name\":\"模板修改测试名\",\"unit\":550}";
        byte[] responseString = manageClient.put().uri("/freight/shops/1/freightmodels/13").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 测试删除模板功能
     * 操作的资源id不存在
     *
     * @throws Exception
     */
    @Test
    @Order(22)
    public void deleteFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        byte[] responseString = manageClient.delete().uri("/freight/shops/1/freightmodels/200").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 测试删除模板功能
     * 成功
     *
     * @throws Exception
     */
    @Test
    @Order(23)
    public void deleteFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        byte[] responseString = manageClient.delete().uri("/freight/shops/1/freightmodels/10").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

        byte[] queryResponseString = manageClient.get().uri("/freight/shops/1/freightmodels/10").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.RESOURCE_ID_NOTEXIST.getMessage())
                .returnResult()
                .getResponseBodyContent();

        expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(queryResponseString, "UTF-8"), false);

    }

    /**
     * 测试删除模板功能
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     */
    @Test
    @Order(24)
    public void deleteFreightModel2() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        byte[] responseString = manageClient.delete().uri("/freight/shops/1/freightmodels/13").header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 测试删除重量运费模板明细
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     * @author zhibin lan
     */
    @Test
    @Order(25)
    public void deleteWeightFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        byte[] responseString = manageClient.delete().uri("/freight/shops/1/weightItems/209").header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }
    /**
     * 测试删除重量运费模板明细
     * 操作的资源id不存在
     *
     * @throws Exception
     * @author zhibin lan
     */
    @Test
    @Order(26)
    public void deleteWeightFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        byte[] responseString = manageClient.delete().uri("/freight/shops/1/weightItems/351351").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 测试删除重量运费模板明细
     * 成功
     *
     * @throws Exception
     * @author zhibin lan
     */
    @Test
    @Order(27)
    public void deleteWeightFreightModel2() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        byte[] responseString = manageClient.delete().uri("/freight/shops/1/weightItems/200").header("authorization", token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), true);

    }

    /**
     * 测试修改重量运费模板明细
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     * @author zhibin lan
     */
    @Test
    @Order(28)
    public void modifyWeightFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        String json = "{\"abovePrice\":30,\"fiftyPrice\":14,\"firstWeight\":3,\"firstWeightPrice\":10,\"hundredPrice\":16,\"regionId\":210,\"tenPrice\":12,\"trihunPrice\":18}";
        byte[] responseString = manageClient.put().uri("/freight/shops/1/weightItems/209").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试修改重量运费模板明细
     * 操作的资源id不存在
     *
     * @throws Exception
     * @author zhibin lan
     */
    @Test
    @Order(29)
    public void modifyWeightFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        String json = "{\"abovePrice\":30,\"fiftyPrice\":14,\"firstWeight\":3,\"firstWeightPrice\":10,\"hundredPrice\":16,\"regionId\":210,\"tenPrice\":12,\"trihunPrice\":18}";
        byte[] responseString = manageClient.put().uri("/freight/shops/1/weightItems/55113").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试修改重量运费模板明细
     * 运费模板中该地区已经定义
     *
     * @throws Exception
     * @author zhibin lan
     */
    @Test
    @Order(30)
    public void modifyWeightFreightModel2() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        String json = "{\"abovePrice\":30,\"fiftyPrice\":14,\"firstWeight\":3,\"firstWeightPrice\":10,\"hundredPrice\":16,\"regionId\":202,\"tenPrice\":12,\"trihunPrice\":18}";
        byte[] responseString = manageClient.put().uri("/freight/shops/1/weightItems/201").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":803}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试修改计件运费模板明细
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     * @author zhibin lan
     */
    @Test
    @Order(31)
    public void modifyPieceFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        String json = "{\"additionalItemPrice\":16,\"additionalItems\":2,\"firstItem\":3,\"firstItemPrice\":12,\"regionId\":210}";
        byte[] responseString = manageClient.put().uri("/freight/shops/1/pieceItems/209").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }


    /**
     * 测试修改计件运费模板明细
     * 操作的资源id不存在
     *
     * @throws Exception
     * @author zhibin lan
     */
    @Test
    @Order(32)
    public void modifyPieceFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        String json = "{\"additionalItemPrice\":16,\"additionalItems\":2,\"firstItem\":3,\"firstItemPrice\":12,\"regionId\":210}";
        byte[] responseString = manageClient.put().uri("/freight/shops/1/pieceItems/35112").header("authorization", token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);
    }

    /**
     * 测试删除计件运费模板明细
     * 操作的资源id不是自己的对象
     *
     * @throws Exception
     * @author zhibin lan
     */
    @Test
    @Order(33)
    public void deletePieceFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        byte[] responseString = manageClient.delete().uri("/freight/shops/1/pieceItems/209").header("authorization", token)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":505}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    /**
     * 测试删除计件运费模板明细
     * 操作的资源id不存在
     *
     * @throws Exception
     * @author zhibin lan
     */
    @Test
    @Order(34)
    public void deletePieceFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        byte[] responseString = manageClient.delete().uri("/freight/shops/1/pieceItems/10000").header("authorization", token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        String expectedResponse = "{\"errno\":504}";
        JSONAssert.assertEquals(expectedResponse, new String(responseString, "UTF-8"), false);

    }

    @Test
    @Order(1)
    public void defFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        String json = "{\"name\":\"LH运费模板1\",\"type\":0,\"unit\":500}";
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";

        // 新建运费模板
        byte[] responseString = manageClient
                .post()
                .uri("/freight/shops/7/freightmodels")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);
        JSONAssert.assertEquals(expectedResponse, defString, false);
    }

    @Test
    @Order(2)
    public void defDuplicateFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        String json = "{\"name\":\"LH重复定义测试模板\",\"type\":0,\"unit\":500}";
        Integer expectErrNo = 802;

        // 尝试新建运费模板
        byte[] responseString = manageClient
                .post()
                .uri("/freight/shops/7/freightmodels")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);
        JSONObject responseObj = JSONObject.parseObject(defString);
        Integer errNo = responseObj.getInteger("errno");
        String errMsg = responseObj.getString("errmsg");

        // errno
        Assert.isTrue(expectErrNo.equals(errNo), "errno 不是 " + expectErrNo + " 而是 " + errNo + " errMsg 是 " + errMsg);
    }


    /**
     * 定义 (重量) 运费模板 (即使 type 不同也存在模板名重复)
     * @throws Exception
     */
    @Test
    @Order(3)
    public void defDuplicateDifferentTypeFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        String json = "{\"name\":\"LH重复定义测试模板\",\"type\":1,\"unit\":500}";
        Integer expectErrNo = 802;

        // 尝试新建运费模板
        byte[] responseString = manageClient
                .post()
                .uri("/freight/shops/7/freightmodels")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);
        JSONObject responseObj = JSONObject.parseObject(defString);
        Integer errNo = responseObj.getInteger("errno");
        String errMsg = responseObj.getString("errmsg");

        // errno
        Assert.isTrue(expectErrNo.equals(errNo), "errno 不是 " + expectErrNo + " 而是 " + errNo + " errMsg 是 " + errMsg);
    }

    /**
     * 定义件数运费模板明细 (地区码为 200)
     * @throws Exception
     */
    @Test
    @Order(4)
    public void defPieceFreightModelRule() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        String json = "{\n" +
                "  \"regionId\": 200,\n" +
                "  \"firstItem\": 2,\n" +
                "  \"firstItemPrice\": 10000,\n" +
                "  \"additionalItems\": 2,\n" +
                "  \"additionalItemsPrice\": 5000\n" +
                "}";

        // 新建运费模板明细
        manageClient
                .post()
                .uri("/freight/shops/7/freightmodels/284811/pieceItems")
                .header("authorization",token)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 克隆运费模板 (件数)
     * @throws Exception
     */
    @Test
    @Order(5)
    public void cloneFreightModellh() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        byte[] responseBytes = manageClient
                .post()
                .uri("/freight/shops/7/freightmodels/284811/clone")
                .header("authorization",token)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseBytes != null;
        String responseString = new String(responseBytes, StandardCharsets.UTF_8);
        String expectedResponse = "{\"errno\":0,\"errmsg\":\"成功\"}";
        JSONAssert.assertEquals(expectedResponse, responseString, false);

        // 获取定义的运费模板
        JSONObject queryResponse = JSONObject.parseObject(responseString);
        JSONObject clonedModel = queryResponse
                .getJSONObject("data");
        Long clonedFmId = clonedModel.getLong("id");

        // 查询定义的运费模板能否查出来

        byte[] queryResponseString = manageClient
                .get()
                .uri("/freight/shops/7/freightmodels/" + clonedFmId)
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .jsonPath("$.errmsg").isEqualTo(ResponseCode.OK.getMessage())
                .jsonPath("$.data").exists()
                .returnResult()
                .getResponseBodyContent();
    }


    /**
     * 克隆运费模板 (找不到源)
     * @throws Exception
     */
    @Test
    @Order(6)
    public void cloneFreightModelNotFound() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        manageClient
                .post()
                .uri("/freight/shops/7/freightmodels/2643126963/clone")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.errno").isEqualTo("504")
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 查询商家的运费模板 (无分页)
     * @throws Exception
     */
    @Test
    @Order(8)
    public void findFreightModels() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);

        // 新建运费模板
        byte[] responseString = manageClient
                .get()
                .uri("/freight/shops/7/freightmodels")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();

        assert responseString != null;
        String defString = new String(responseString, StandardCharsets.UTF_8);

        JSONObject response = JSONObject.parseObject(defString);

        Assert.isTrue(response.getString("errmsg").equals("成功"), "查询不成功");
        JSONArray modelArr = response
                .getJSONObject("data")
                .getJSONArray("list");

        boolean firstChecked = false;
        boolean secondChecked = false;
        for (int i = 0; i < modelArr.size(); i++) {
            JSONObject model = modelArr.getJSONObject(i);
            if (model.getLong("id") == 284810L) {
                firstChecked = true;
            } else if (model.getLong("id").equals(284811L)) {
                secondChecked = true;
            }
        }
        Assert.isTrue(firstChecked, "第一个运费模板没有找到，id=" + 284810L);
        Assert.isTrue(secondChecked, "克隆的运费模板没有找到，id=" + 284811L);
    }

    /**
     * 删除运费模板 (克隆源)
     * @throws Exception
     */
    @Test
    @Order(9)
    public void delFreightModel() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        manageClient
                .delete()
                .uri("/freight/shops/7/freightmodels/284811")
                .header("authorization",token)
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.errno").isEqualTo(ResponseCode.OK.getCode())
                .returnResult()
                .getResponseBodyContent();
    }

    /**
     * 删除运费模板 (找不到)
     * @throws Exception
     */
    @Test
    @Order(10)
    public void delFreightModelNotFound() throws Exception {
        String token = new JwtHelper().createToken(200L, 1l, 50);
        manageClient
                .delete()
                .uri("/freight/shops/7/freightmodels/5646241156151")
                .header("authorization",token)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .returnResult()
                .getResponseBodyContent();
    }
















}
