package com.grocery.app.integrationTests.user;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileIntegrationTest extends ServicesTestSupport {
    @Value("classpath:static/test-image/cat.jpg")
    private Resource resource;

    @BeforeEach
    void setUp() {
    }

    @Test
    @Order(1)
    void GivenImage_WhenUpload_ThenReturnSuccess() throws IOException {
        // Given


        System.out.println(resource.getFile().getAbsolutePath());
        MultiValueMap<String,Object> body=new LinkedMultiValueMap<>();
        // When
        body.add("file", new FileSystemResource(resource.getFile()));
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, getFormDataHeader());
        ResponseEntity<BaseResponse<String>> res=testRestTemplate.exchange("/api/upload/avatar", HttpMethod.POST, requestEntity, new ParameterizedTypeReference<BaseResponse<String>>() {
        });
        // Then
        assertThat(res.getStatusCode(), is(HttpStatus.OK));
        assertThat(res.getBody().getCode(), is(ResCode.UPLOAD_AVATAR_SUCCESSFULLY.getCode()));

    }

}
