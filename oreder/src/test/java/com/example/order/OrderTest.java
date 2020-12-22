package com.example.order;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import com.example.order.model.vo.OrderFreightSn;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/***
 * @author yansong chen
 * @time 2020-12-14 15:03
 * @return
 */
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = OrderTest.class)
class OrderTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void  test() throws Exception
    {

    }


    @Test
    public void statustest() throws Exception
    {
        String token = new JwtHelper().createToken(100L, 100l,100);
        String responseString = this.mvc.perform(get("/orders/states").header("authorization", token))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);
        /*String expectedResponse = "{" +
                "\"errno\":0," +
                " \"data\":{" +
                " \"id\":2," +
                " \"name\":\"3SP3CG\"," +
                "\"type\":0," +
                "\"unit\":15," +
                "\"default\":0," +
                "\"gmtCreate\":\"2020-12-10 09:33:28\"," +
                "\"gmtModified\":\"2020-12-10 09:33:28\"" +
                "}," + "\"errmsg\":\"成功\"" +
                "}";
        JSONAssert.assertEquals(expectedResponse, responseString, true);

        String requireJson = JacksonUtil.toJson(vo);
        String response = this.mvc.perform(post("/privilege/privileges/login")
                .contentType("application/json;charset=UTF-8")
                .content(requireJson)).andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.errno").value(ResponseCode.OK.getCode()))
                .andExpect(jsonPath("$.errmsg").value("成功"))
                .andReturn().getResponse().getContentAsString();
        return  JacksonUtil.parseString(response, "data");*/
    }

    @org.junit.jupiter.api.Test
    public void markShopOrderDeliverTest3() throws Exception {
        //String token = this.login("13088admin", "123456");
        OrderFreightSn orderFreightSn=new OrderFreightSn();
        orderFreightSn.setFreightSn("dsd");
        String PieceFreightModelJson = JacksonUtil.toJson(orderFreightSn);
        String token = new JwtHelper().createToken(7L, 123456L, 100);
        String responseString = this.mvc.perform(put("/order/shops/3/orders/240025/deliver")
                        .header("authorization", token)
                //.queryParam("shopId","3L")
                //.queryParam("id","240025L")
                /*.content(PieceFreightModelJson)*/)
                //.queryParam("orderSn","2016102364965"))
                //.andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.errno").value(ResponseCode.RESOURCE_ID_OUTSCOPE.getCode()))
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
        System.out.println(responseString);

      /*  byte[] ret = manageClient.put()
                .uri("/order/shops/3/orders/240025/deliver")
                .header("authorization", token)
                .bodyValue("{\"freightSn\": \"1233\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.errno").isEqualTo(ResponseCode.ORDER_STATENOTALLOW.getCode())
                .returnResult()
                .getResponseBodyContent();*/

    }
}
