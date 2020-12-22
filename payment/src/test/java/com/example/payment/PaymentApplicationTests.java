package com.example.payment;

//import com.example.payment.result.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.example.payment.dao.RefundDao;
import com.example.payment.service.RefundService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PaymentApplicationTests {

    //@Autowired
    //RefundDao refundDao;
//    @Autowired
//    RefundService refundService;
//    @Test
//    public void getrefundsbyorderidtest()
//    {
//        ReturnObject reo= refundService.getRefundsByAftersaleId(1L);
//        reo.getCode();
//    }
    @Test
    public void refundDaoTest()
    {
        //refundDao.getRefundsByAftersaleId(666666L);
    }
    @Test
    void contextLoads() {
        System.out.println("aa");
        //PayPatternAndName pan=new PayPatternAndName("22","33");
        //T t=new T(1,"aa");
        System.out.println("dd");
    }

}
@Data
@AllArgsConstructor
@JsonIgnoreProperties(value={"hibernateLazyInitializer","handler","fieldHandler"})
class T{
    int a;
    String b;
//    public T(String x,String y)
//    {
//        a=x;
//        b=y;
//    }
}