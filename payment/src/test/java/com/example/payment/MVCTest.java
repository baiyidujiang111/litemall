package com.example.payment;

import cn.edu.xmu.ooad.util.JwtHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @program: oomall
 * @description:
 * @author: alex101
 * @create: 2020-12-22 15:55
 **/
@AutoConfigureMockMvc
@Transactional
@SpringBootTest(classes = PaymentApplication.class)   //标识本类是一个SpringBootTest
public class MVCTest {
    @Autowired
    private MockMvc mvc;

    @Test
    public void setDefaultFreightModel1() throws Exception {
        String token = new JwtHelper().createToken(100L, 100l, 100);
        String responseString = this.mvc.perform(get("/payment/aftersales/1/refunds").header("authorization", token))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn().getResponse().getContentAsString();
    }
}
