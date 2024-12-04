package com.grocery.app.integrationTests.admin;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.UnitDTO;
import com.grocery.app.entities.Category;
import com.grocery.app.entities.Unit;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.repositories.CategoryRepo;
import com.grocery.app.repositories.UnitRepo;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
public class UnitIntegrationTest extends AdminServicesTestSupport {

    @Autowired
    private UnitRepo unitRepo;

    @Autowired
    private ModelMapper modelMapper;


    private UnitDTO unitDTO;

    @BeforeEach
    void setUp(){
        Unit unit=Unit.builder().name("test").build();
        unitRepo.save(unit);
        unit=unitRepo.findById(1L).get();
        this.unitDTO=modelMapper.map(unit,UnitDTO.class);




    }

    @Test
    @Order(1)
    void givenAuthRequest_whenGetUnits_thenSuccess(){
        HttpEntity<Void> req=new HttpEntity<>(adminHeader());
        final ResponseEntity<BaseResponse<List<UnitDTO>>> response = testRestTemplate.exchange("/api/admin/units", HttpMethod.GET, req, new ParameterizedTypeReference<BaseResponse<List<UnitDTO>>>() {
        });
        System.out.println(response);
        assert response.getBody() != null;
        assert response.getBody().getData() != null;
        assertThat(response.getBody().getData().size(),is(1));
        assertThat(response.getBody().getData().get(0).getName(),is("test"));

    }

    @Test
    @Order(2)
    void givenAuthRequest_whenCreateUnit_thenSuccess(){

        final ResponseEntity<BaseResponse<UnitDTO>> errRes = testRestTemplate.exchange("/api/admin/units/create", HttpMethod.POST, new HttpEntity<>(this.unitDTO,adminHeader()), new ParameterizedTypeReference<BaseResponse<UnitDTO>>() {
        });
        assert  errRes.getStatusCode().is4xxClientError();
        assert errRes.getBody().getCode().equals(ResCode.UNIT_EXIST.getCode());

        final ResponseEntity<BaseResponse<UnitDTO>> sucRes = testRestTemplate.exchange("/api/admin/units/create", HttpMethod.POST, new HttpEntity<>(UnitDTO.builder().name("test2").build(),adminHeader()), new ParameterizedTypeReference<BaseResponse<UnitDTO>>() {
        });
        assert sucRes.getBody() != null;
        assert sucRes.getBody().getData() != null;
        assertThat(sucRes.getBody().getData().getName(),is("test2"));

    }

    @Test
    @Order(3)
    void givenAuthRequest_whenUpdateUnit_thenSuccess(){
        UnitDTO unitDTO=UnitDTO.builder().id(2L).name("test").build();
        final ResponseEntity<BaseResponse<UnitDTO>> errRes = testRestTemplate.exchange("/api/admin/units/update", HttpMethod.PUT, new HttpEntity<>(unitDTO,adminHeader()), new ParameterizedTypeReference<BaseResponse<UnitDTO>>() {
        });
        assert  errRes.getStatusCode().is4xxClientError();
        assert errRes.getBody().getCode().equals(ResCode.UNIT_NOT_FOUND.getCode());

        final ResponseEntity<BaseResponse<UnitDTO>> sucRes = testRestTemplate.exchange("/api/admin/units/update", HttpMethod.PUT, new HttpEntity<>(UnitDTO.builder().id(1L).name("test2").build(),adminHeader()), new ParameterizedTypeReference<BaseResponse<UnitDTO>>() {
        });
        assert sucRes.getBody() != null;
        assert sucRes.getBody().getData() != null;
        assertThat(sucRes.getBody().getData().getName(),is("test2"));
    }

    @Test
    @Order(4)
    void givenAuthRequest_whenDeleteUnit_thenSuccess(){
        final ResponseEntity<BaseResponse<UnitDTO>> errRes = testRestTemplate.exchange("/api/admin/units/delete", HttpMethod.DELETE, new HttpEntity<>(2L,adminHeader()), new ParameterizedTypeReference<BaseResponse<UnitDTO>>() {
        });
        assert  errRes.getStatusCode().is4xxClientError();
        assert errRes.getBody().getCode().equals(ResCode.UNIT_NOT_FOUND.getCode());

        final ResponseEntity<BaseResponse<UnitDTO>> sucRes = testRestTemplate.exchange("/api/admin/units/delete", HttpMethod.DELETE, new HttpEntity<>(1L,adminHeader()), new ParameterizedTypeReference<BaseResponse<UnitDTO>>() {
        });
        assert sucRes.getBody() != null;
        assert sucRes.getBody().getData() != null;
        assertThat(sucRes.getBody().getData().getIsDeleted(),is(true));
    }

}
