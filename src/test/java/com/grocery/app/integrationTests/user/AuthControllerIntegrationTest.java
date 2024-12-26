package com.grocery.app.integrationTests.user;

import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthControllerIntegrationTest extends ServicesTestSupport {

    @Test
    @Order(1)
    void givenEmail_whenSendEmail_thenSuccess(){
        String email="nguyenhongson159stk@gmail.com";
        HttpEntity<String> httpEntity=new HttpEntity<>(email,unAuthHeader());
        ResponseEntity<BaseResponse<String>> res=testRestTemplate.exchange("/api/auth/get-verify-code", HttpMethod.POST,httpEntity,new ParameterizedTypeReference<BaseResponse<String>>(){});
        System.out.println(res);
        assert res.getBody() != null;
        assert res.getBody().getData() != null;


    }


}
