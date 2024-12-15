package com.grocery.app.integrationTests.user;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.FridgeDTO;
import com.grocery.app.dto.FridgeItemDTO;
import com.grocery.app.entities.*;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.repositories.FamilyRepo;
import com.grocery.app.repositories.FoodRepo;
import com.grocery.app.repositories.FridgeRepo;
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
public class FridgeControllerIntegrationTest extends ServicesTestSupport {

    private Family fa1;

    private Family fa2;

    private User fmUser;


    @Autowired
    private FamilyRepo familyRepo;

    @Autowired
    private FridgeRepo fridgeRepo;

    @Autowired
    private FoodRepo foodRepo;


    @BeforeAll
    void setUp() {
        fa1= Family.builder().name("test").owner(user).build();
        fa1=familyRepo.save(fa1);

        fmUser= User.builder()
                .firstName("test")
                .lastName("test")
                .username("test1")
                .password(passwordEncoder.encode("123456789"))
                .email("")
                .role(Role.builder().id(102L).name("USER").build())
                .build();
        userRepo.save(fmUser);


        fa2= Family.builder().name("test").owner(fmUser).build();
        fa2=familyRepo.save(fa2);

        Food food=Food.builder().name("hello").description("zoo").build();
        foodRepo.save(food);





    }
//
//    @Test
//    @Order(1)
//    void givenFamily_whenCheckFridge_thenFridgeIsCreated() {
//        assert fa1.getFridge()!=null;
//        assert fa1.getFridge().getName().equals("My Fridge");
//    }
    @Test
    @Order(2)
    void givenFamily_WhenAddItemToFridge_thenItemIsAdded() throws  Exception{
        FridgeItemDTO fridgeItemDTO=FridgeItemDTO.builder().duration(3).quantity(3).food(FridgeItemDTO.FoodFridgeDTO.builder().id(1L).build()).build();
        HttpEntity<String> httpEntity=new HttpEntity<>(objectMapper.writeValueAsString(fridgeItemDTO),getHeader());
        ResponseEntity<BaseResponse<FridgeDTO>> res=testRestTemplate.exchange("/api/family/fridges/add?familyId=1", HttpMethod.PUT,httpEntity,new ParameterizedTypeReference<BaseResponse<FridgeDTO>>(){});

        System.out.println(res);

        Family family=familyRepo.findById(1L).get();
        assertThat(family.getFridge().getFridgeItemList().size(), is(1));


        assertThat(res.getStatusCode(), is(HttpStatus.OK));
        assert res.getBody()!=null;
        assert res.getBody().getData()!=null;
        assert res.getBody().getData().getFridgeItemList().size()==1;

        ResponseEntity<BaseResponse<FridgeItemDTO>> res1=testRestTemplate.exchange("/api/family/fridges/add?familyId=2", HttpMethod.PUT,httpEntity,new ParameterizedTypeReference<BaseResponse<FridgeItemDTO>>(){});
        assertThat(res1.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(res1.getBody().getMessage(), is(ResCode.NOT_OWNER_OF_FAMILY.getMessage()));
    }

    @Test
    @Order(3)
    void givenFamily_WhenGetFridge_thenIsSuccess() throws  Exception{
        ResponseEntity<BaseResponse<FridgeDTO>> res=testRestTemplate.exchange("/api/family/fridges?familyId=1", HttpMethod.GET,new HttpEntity<>(getHeader()),new ParameterizedTypeReference<BaseResponse<FridgeDTO>>(){});
        assertThat(res.getStatusCode(), is(HttpStatus.OK));
        assert res.getBody()!=null;
        assert res.getBody().getData()!=null;
        assert res.getBody().getData().getFridgeItemList().size()==1;

    }

    @Test
    @Order(4)
    void givenFamily_WhenUpdateFridge_thenIsSuccess() throws Exception{
        FridgeItemDTO fridgeItemDTO=FridgeItemDTO.builder().duration(4).quantity(3).id(1L).food(new FridgeItemDTO.FoodFridgeDTO(1L,null,null,null)).build();
        HttpEntity<String> httpEntity=new HttpEntity<>(objectMapper.writeValueAsString(fridgeItemDTO),getHeader());
        ResponseEntity<BaseResponse<FridgeDTO>> res=testRestTemplate.exchange("/api/family/fridges/update?familyId=1", HttpMethod.PUT,httpEntity,new ParameterizedTypeReference<BaseResponse<FridgeDTO>>(){});
        System.out.println(res);
        assertThat(res.getStatusCode(), is(HttpStatus.OK));
        assert res.getBody()!=null;
        assert res.getBody().getData()!=null;
        assert res.getBody().getData().getFridgeItemList().get(0).getDuration().equals(4);
        assert res.getBody().getData().getFridgeItemList().get(0).getQuantity().equals(3);

    }

    @Test
    @Order(5)
    void givenFamily_WhenRemoveItemFromFridge_thenIsSuccess() throws Exception{
        ResponseEntity<BaseResponse<FridgeDTO>> res=testRestTemplate.exchange("/api/family/fridges/remove?familyId=1&itemId=1", HttpMethod.PUT,new HttpEntity<>(getHeader()),new ParameterizedTypeReference<BaseResponse<FridgeDTO>>(){});
        assertThat(res.getStatusCode(), is(HttpStatus.OK));
        assert res.getBody()!=null;
        assert res.getBody().getData()!=null;
        assert res.getBody().getData().getFridgeItemList().size()==0;
    }
}
