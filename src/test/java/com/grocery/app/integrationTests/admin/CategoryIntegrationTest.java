package com.grocery.app.integrationTests.admin;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.CategoryDTO;
import com.grocery.app.entities.Category;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.repositories.CategoryRepo;
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
public class CategoryIntegrationTest extends AdminServicesTestSupport{

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ModelMapper modelMapper;


    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp(){
        Category category=Category.builder().name("test").build();
        categoryRepo.save(category);
        category=categoryRepo.findById(1L).get();
        this.categoryDTO=modelMapper.map(category,CategoryDTO.class);




    }

    @Test
    @Order(1)
    void givenAuthRequest_whenGetCategorys_thenSuccess(){
        HttpEntity<Void> req=new HttpEntity<>(adminHeader());
        final ResponseEntity<BaseResponse<List<CategoryDTO>>> response = testRestTemplate.exchange("/api/admin/categories", HttpMethod.GET, req, new ParameterizedTypeReference<BaseResponse<List<CategoryDTO>>>() {
        });
        System.out.println(response);
        assert response.getBody() != null;
        assert response.getBody().getData() != null;
        assertThat(response.getBody().getData().size(),is(1));
        assertThat(response.getBody().getData().get(0).getName(),is("test"));

    }

    @Test
    @Order(2)
    void givenAuthRequest_whenCreateCategory_thenSuccess(){

        final ResponseEntity<BaseResponse<CategoryDTO>> errRes = testRestTemplate.exchange("/api/admin/categories/create", HttpMethod.POST, new HttpEntity<>(this.categoryDTO,adminHeader()), new ParameterizedTypeReference<BaseResponse<CategoryDTO>>() {
        });
        assert  errRes.getStatusCode().is4xxClientError();
        assert errRes.getBody().getCode().equals(ResCode.CATEGORY_EXIST.getCode());

        final ResponseEntity<BaseResponse<CategoryDTO>> sucRes = testRestTemplate.exchange("/api/admin/categories/create", HttpMethod.POST, new HttpEntity<>(CategoryDTO.builder().name("test2").build(),adminHeader()), new ParameterizedTypeReference<BaseResponse<CategoryDTO>>() {
        });
        assert sucRes.getBody() != null;
        assert sucRes.getBody().getData() != null;
        assertThat(sucRes.getBody().getData().getName(),is("test2"));

    }

    @Test
    @Order(3)
    void givenAuthRequest_whenUpdateCategory_thenSuccess(){
        CategoryDTO categoryDTO=CategoryDTO.builder().id(2L).name("test").build();
        final ResponseEntity<BaseResponse<CategoryDTO>> errRes = testRestTemplate.exchange("/api/admin/categories/update", HttpMethod.PUT, new HttpEntity<>(categoryDTO,adminHeader()), new ParameterizedTypeReference<BaseResponse<CategoryDTO>>() {
        });
        assert  errRes.getStatusCode().is4xxClientError();
        assert errRes.getBody().getCode().equals(ResCode.CATEGORY_NOT_FOUND.getCode());

        final ResponseEntity<BaseResponse<CategoryDTO>> sucRes = testRestTemplate.exchange("/api/admin/categories/update", HttpMethod.PUT, new HttpEntity<>(CategoryDTO.builder().id(1L).name("test2").build(),adminHeader()), new ParameterizedTypeReference<BaseResponse<CategoryDTO>>() {
        });
        assert sucRes.getBody() != null;
        assert sucRes.getBody().getData() != null;
        assertThat(sucRes.getBody().getData().getName(),is("test2"));
    }

    @Test
    @Order(4)
    void givenAuthRequest_whenDeleteCategory_thenSuccess(){
        final ResponseEntity<BaseResponse<CategoryDTO>> errRes = testRestTemplate.exchange("/api/admin/categories/delete", HttpMethod.DELETE, new HttpEntity<>(2L,adminHeader()), new ParameterizedTypeReference<BaseResponse<CategoryDTO>>() {
        });
        assert  errRes.getStatusCode().is4xxClientError();
        assert errRes.getBody().getCode().equals(ResCode.CATEGORY_NOT_FOUND.getCode());

        final ResponseEntity<BaseResponse<CategoryDTO>> sucRes = testRestTemplate.exchange("/api/admin/categories/delete", HttpMethod.DELETE, new HttpEntity<>(1L,adminHeader()), new ParameterizedTypeReference<BaseResponse<CategoryDTO>>() {
        });
        assert sucRes.getBody() != null;
        assert sucRes.getBody().getData() != null;
        assertThat(sucRes.getBody().getData().getIsDeleted(),is(true));
    }
}
