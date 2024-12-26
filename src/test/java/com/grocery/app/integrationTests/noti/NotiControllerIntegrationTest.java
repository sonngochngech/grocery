package com.grocery.app.integrationTests.noti;

import com.grocery.app.dto.NotiContentDTO;
import com.grocery.app.dto.NotificationDTO;
import com.grocery.app.entities.User;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.services.NotificationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotiControllerIntegrationTest extends ServicesTestSupport {


    @Autowired
    private NotificationService notificationService;


    private User fmUser1;

    private User  fmUser2;

    private User  fmUser3;



    @BeforeAll
     void setUp() {
        // Given
        this.fmUser1 = addUser("fmUser1");
        this.fmUser2 = addUser("fmUser2");
        this.fmUser3 = addUser("fmUser3");
    }



    @Test
    @Order(2)
    void givenNoti_WhenSaveNoti_ReturnSuccessfully(){
        System.out.println("sdgsdgsđ");
        System.out.println(user);
        NotificationDTO notificationDTO= notificationService.saveNotification(fmUser1.getId(), List.of(user.getId(), fmUser3.getId()), NotiContentDTO.builder().title("test").message("hehe").type("test").externalData("test").build());
        System.out.println("heheee");
        System.out.println(notificationDTO);

    }

    @Test
    @Order(3)
    void givenNoti_WhenGetNoti_ReturnSuccessfully(){
        ResponseEntity<BaseResponse<List<NotificationDTO>>> response = testRestTemplate.exchange("/api/users/notifications", HttpMethod.GET, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<List<NotificationDTO>>>() {});
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        System.out.println(response.getBody().getData());

    }

    @Test
    @Order((4))
    void givenNoti_WhenDeleteNoti_ReturnSuccessfully(){
        ResponseEntity<BaseResponse<Long>> response = testRestTemplate.exchange("/api/users/notifications/1", HttpMethod.DELETE, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<Long>>() {});
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        System.out.println(response.getBody().getData());


    }


}
