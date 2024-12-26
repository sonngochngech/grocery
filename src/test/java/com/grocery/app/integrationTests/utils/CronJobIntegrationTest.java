package com.grocery.app.integrationTests.utils;


import com.grocery.app.repositories.FridgeItemRepo;
import com.grocery.app.services.FridgeService;
import com.grocery.app.utils.CronJob;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CronJobIntegrationTest {


    @Autowired
    private CronJob cronJob;


    @Autowired
    private FridgeItemRepo fridgeItemRepo;


    @Test
    @Transactional
    void testGetNotis(){
//        cronJob.sendExpriedFridgeItemsNoti();
        System.out.println(fridgeItemRepo.findAllById(List.of(1L)));

    }
}
