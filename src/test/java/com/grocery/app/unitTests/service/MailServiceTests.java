package com.grocery.app.unitTests.service;

import com.grocery.app.dto.MailDetailsDTO;
import com.grocery.app.services.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class MailServiceTests {

    @Autowired
    private MailService mailService;

    @Test
    public void testSendEmail(){
        // Given
        MailDetailsDTO mailDetailsDTO = MailDetailsDTO.builder().message("I love you").recipient("nguyenhongson159stk@gmail.com").subject("Test").build();

        // When
        mailService.sendEmail(mailDetailsDTO);

        assertThat(mailDetailsDTO).isNotNull();


    }
}
