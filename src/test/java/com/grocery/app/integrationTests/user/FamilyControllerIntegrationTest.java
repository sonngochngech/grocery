package com.grocery.app.integrationTests.user;

import com.grocery.app.dto.UserDTO;
import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.dto.family.FamilyDetailDTO;
import com.grocery.app.entities.Family;
import com.grocery.app.entities.FamilyMember;
import com.grocery.app.entities.Role;
import com.grocery.app.entities.User;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ErrorResponse;
import com.grocery.app.repositories.FamilyRepo;
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
import static org.springframework.http.HttpStatus.CREATED;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FamilyControllerIntegrationTest extends ServicesTestSupport {

    @Autowired
    private FamilyRepo familyRepo;

    private User fmUser;

    private Family fa1;

    private Family fa2;

    @BeforeEach
    void setUp() {
        fmUser= User.builder()
                .firstName("test")
                .lastName("test")
                .username("test1")
                .password(passwordEncoder.encode("123456789"))
                .email("")
                .role(Role.builder().id(102L).name("USER").build())
                .build();
        userRepo.save(fmUser);

        FamilyMember fm1 = FamilyMember.builder().user(fmUser).build();
        FamilyMember fm2 = FamilyMember.builder().user(user).build();

        fa1= Family.builder().name("test").owner(user).familyMembers(List.of(fm1)).build();
        fa2= Family.builder().name("test").owner(fmUser).familyMembers(List.of(fm2)).build();

        fm1.setFamily(fa1);
        fm2.setFamily(fa2);

        fa1=familyRepo.save(fa1);
        fa2=familyRepo.save(fa2);
    }

    @Test
    @Order(0)
    void givenUser_whenGetFamilies_thenReturnFamilyDTOList() throws Exception {
        ResponseEntity<BaseResponse<List<FamilyDTO>>> response = testRestTemplate.exchange("/api/users/families", HttpMethod.GET, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<List<FamilyDTO>>>() {});
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().size(), is(2));
    }

    @Test
    @Order(1)
    @Disabled
    void giveUser_whenCreateFamily_thenReturnFamilyDTO() throws Exception {
        // Given
        FamilyDTO familyDTO = FamilyDTO.builder().name("test")
                .owner(modelMapper.map(user, UserDTO.class))
                .build();
        HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(familyDTO), getHeader());

        ResponseEntity<BaseResponse<FamilyDTO>> response = testRestTemplate.exchange("/api/users/families/create", HttpMethod.POST, httpEntity, new ParameterizedTypeReference<BaseResponse<FamilyDTO>>() {});

        assertThat(response.getStatusCode(), is(CREATED));
    }

    @Test
    @Order(2)
    @Disabled
    void giveFamilyId_whenGetFamily_thenReturnFamilyDetailDTO() throws Exception {
        // Expected Success
        ResponseEntity<BaseResponse<FamilyDetailDTO>> response = testRestTemplate.exchange("/api/users/families/"+fa1.getId(), HttpMethod.GET, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<FamilyDetailDTO>>() {});
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        // Expected Success if sender is a member of the family
        ResponseEntity<BaseResponse<FamilyDetailDTO>> res1 = testRestTemplate.exchange("/api/users/families/"+fa2.getId(), HttpMethod.GET, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<FamilyDetailDTO>>() {});
        System.out.println(res1);
        assertThat(res1.getStatusCode(), is(HttpStatus.OK));


        // Expected Fail if sender is not the owner
        Family family = Family.builder().name("test").owner(fmUser).familyMembers(null).build();
        family=familyRepo.save(family);
        ResponseEntity<ErrorResponse> response1 = testRestTemplate.exchange("/api/users/families/"+family.getId(), HttpMethod.GET, new HttpEntity<>(getHeader()), ErrorResponse.class);
        assertThat(response1.getStatusCode(), is(HttpStatus.BAD_REQUEST));

    }

    @Test
    @Order(3)
    @Disabled
    void givenFamilyId_whenDeleteFamily_thenReturnFamilyDTO() throws Exception {
        // Expected Success
        ResponseEntity<BaseResponse<FamilyDTO>> response = testRestTemplate.exchange("/api/users/families/"+fa1.getId()+"/delete", HttpMethod.DELETE, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<FamilyDTO>>() {});
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getIsDeleted(), is(true));

        // Expected Fail if sender is not the owner
        ResponseEntity<ErrorResponse> response1 = testRestTemplate.exchange("/api/users/families/"+fa2.getId()+"/delete", HttpMethod.DELETE, new HttpEntity<>(getHeader()), ErrorResponse.class);
        System.out.println("2:"+ response1);
        assertThat(response1.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    @Order(4)
    @Disabled
    void givenFamilyIdAndUserId_whenRemoveFamilyMember_thenReturnFamilyDetailDTO() throws Exception {
        // Expected Success
        int size = fa1.getFamilyMembers().size();
        final String url1 = "/api/users/families/" + fa1.getId() + "/delete-member?userId=" + fmUser.getId();
        ResponseEntity<BaseResponse<FamilyDetailDTO>> response = testRestTemplate.exchange(url1, HttpMethod.DELETE, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<FamilyDetailDTO>>() {});
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().getMembers().size(), is(size-1));

        // Expected Fail if sender is not the owner
        final String url2= "/api/users/families/" + fa2.getId() + "/delete-member?userId=" + user.getId();
        ResponseEntity<ErrorResponse> response1 = testRestTemplate.exchange("/api/users/families/"+fa2.getId()+"/delete-member?userId="+user.getId(), HttpMethod.DELETE, new HttpEntity<>(getHeader()), ErrorResponse.class);
        assertThat(response1.getStatusCode(), is(HttpStatus.BAD_REQUEST));
    }

    @Test
    @Order(5)
    @Disabled
    void givenFamilyId_whenLeaveFromFamilyMember_thenReturnFamilyDetailDTO() throws Exception {
        // Expected Success
        int size = fa2.getFamilyMembers().size();
        final String url1 = "/api/users/families/" + fa2.getId() + "/leave";
        ResponseEntity<BaseResponse<FamilyDetailDTO>> response1 = testRestTemplate.exchange(url1, HttpMethod.PUT, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<FamilyDetailDTO>>() {});
        assertThat(response1.getStatusCode(), is(HttpStatus.OK));
        assertThat(response1.getBody().getData().getMembers().size(), is(size-1));

        ResponseEntity<ErrorResponse> response3 = testRestTemplate.exchange(url1, HttpMethod.PUT, new HttpEntity<>(getHeader()), ErrorResponse.class);
        assertThat(response3.getStatusCode(), is(HttpStatus.BAD_REQUEST));

        final String url2 = "/api/users/families/" + fa1.getId() + "/leave";
        ResponseEntity<ErrorResponse> response2 = testRestTemplate.exchange(url2, HttpMethod.PUT, new HttpEntity<>(getHeader()), ErrorResponse.class);
        assertThat(response2.getStatusCode(), is(HttpStatus.BAD_REQUEST));




    }














}
