package com.grocery.app.integrationTests.noti;

import com.grocery.app.dto.*;
import com.grocery.app.entities.User;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.services.NotificationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NotiIntegrationTest extends ServicesTestSupport {

//    public String token="ExponentPushToken[pql4QAEXkY_6W8lH8aWE7R]";

//    @Autowired
//    private NotificationProducer notificationProducer;
//
//    @Autowired
//    private NotificationFactory notificationFactory;

    @Autowired
    private NotificationService notificationService;

    private User  fmUser1;
    private User  fmUser2;
    private User  fmUser3;

    @BeforeEach
    public void setup() {
        // Given
        this.fmUser1 = addUser("fmUser1");
        this.fmUser2 = addUser("fmUser2");
        this.fmUser3 = addUser("fmUser3");
    }

//    @Test
//    @Order(1)
//    void TestSendNoti(){
//       NotificationDTO notificationDTO= notificationService.saveNotification(user.getId(), List.of(fmUser1.getId(), fmUser2.getId()), NotiContentDTO.builder().title("test").message("hehe").type("test").externalData("test").build());
//        assertThat(notificationDTO.getDevices().size(), is(2));
//        System.out.println(notificationDTO);
//    }


    @Test
    @Order(2)
    void TestSaveNoti(){
        System.out.println("sdgsdgsđ");
        System.out.println(user);
        NotificationDTO notificationDTO= notificationService.saveNotification(user.getId(), List.of(fmUser1.getId(), fmUser2.getId()), NotiContentDTO.builder().title("test").message("hehe").type("test").externalData("test").build());
        assertThat(notificationDTO.getDevices().size(), is(2));
        System.out.println(notificationDTO);



    }





}
