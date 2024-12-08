package com.grocery.app.integrationTests.user;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.InvitationDTO;
import com.grocery.app.dto.UserDTO;
import com.grocery.app.dto.family.FamilyDTO;
import com.grocery.app.dto.family.FamilyDetailDTO;
import com.grocery.app.entities.*;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.responses.ErrorResponse;
import com.grocery.app.repositories.FamilyRepo;
import com.grocery.app.repositories.InvitationRepo;
import org.hibernate.Hibernate;
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
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private InvitationRepo  invitationRepo;

    private User fmUser;

    private User user3;

    private Family fa1;

    private Family fa2;

    private Family fa3;

    @BeforeEach
    void setUp() {
        fmUser= addUser("test1");
        this.user3= addUser("test2");

        FamilyMember fm1 = FamilyMember.builder().user(fmUser).build();
        FamilyMember fm2 = FamilyMember.builder().user(user).build();

        fa1= Family.builder().name("test").owner(user).familyMembers(List.of(fm1)).build();
        fa2= Family.builder().name("test").owner(fmUser).familyMembers(List.of(fm2)).build();
        fa3= Family.builder().name("test").owner(fmUser).build();

        fm1.setFamily(fa1);
        fm2.setFamily(fa2);


        fa1=familyRepo.save(fa1);
        fa2=familyRepo.save(fa2);
        fa3=familyRepo.save(fa3);
    }



    @Test
    @Order(0)
    void givenUser_whenGetFamilies_thenReturnFamilyDTOList() throws Exception {
        ResponseEntity<BaseResponse<List<FamilyDTO>>> response = testRestTemplate.exchange("/api/users/families", HttpMethod.GET, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<List<FamilyDTO>>>() {});
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody().getData().size(), is(2));
    }
//
//    @Test
//    @Order(1)
//    @Disabled
//    void giveUser_whenCreateFamily_thenReturnFamilyDTO() throws Exception {
//        // Given
//        FamilyDTO familyDTO = FamilyDTO.builder().name("test")
//                .owner(modelMapper.map(user, UserDTO.class))
//                .build();
//        HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(familyDTO), getHeader());
//
//        ResponseEntity<BaseResponse<FamilyDTO>> response = testRestTemplate.exchange("/api/users/families/create", HttpMethod.POST, httpEntity, new ParameterizedTypeReference<BaseResponse<FamilyDTO>>() {});
//
//        assertThat(response.getStatusCode(), is(CREATED));
//    }
//
//    @Test
//    @Order(2)
//    void givenFamily_WhenAddFamilyMember_thenReturnFamilyDTO()throws Exception{
//
//    }
//
//    @Test
//    @Order(3)
//    @Disabled
//    void giveFamilyId_whenGetFamily_thenReturnFamilyDetailDTO() throws Exception {
//        // Expected Success
//        ResponseEntity<BaseResponse<FamilyDetailDTO>> response = testRestTemplate.exchange("/api/users/families/"+fa1.getId(), HttpMethod.GET, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<FamilyDetailDTO>>() {});
//        assertThat(response.getStatusCode(), is(HttpStatus.OK));
//
//        // Expected Success if sender is a member of the family
//        ResponseEntity<BaseResponse<FamilyDetailDTO>> res1 = testRestTemplate.exchange("/api/users/families/"+fa2.getId(), HttpMethod.GET, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<FamilyDetailDTO>>() {});
//        System.out.println(res1);
//        assertThat(res1.getStatusCode(), is(HttpStatus.OK));
//
//
//        // Expected Fail if sender is not the owner
//        Family family = Family.builder().name("test").owner(fmUser).familyMembers(null).build();
//        family=familyRepo.save(family);
//        ResponseEntity<ErrorResponse> response1 = testRestTemplate.exchange("/api/users/families/"+family.getId(), HttpMethod.GET, new HttpEntity<>(getHeader()), ErrorResponse.class);
//        assertThat(response1.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//
//    }
//
//    @Test
//    @Order(4)
//    @Disabled
//    void givenFamilyId_whenDeleteFamily_thenReturnFamilyDTO() throws Exception {
//        // Expected Success
//        ResponseEntity<BaseResponse<FamilyDTO>> response = testRestTemplate.exchange("/api/users/families/"+fa1.getId()+"/delete", HttpMethod.DELETE, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<FamilyDTO>>() {});
//        assertThat(response.getStatusCode(), is(HttpStatus.OK));
//        assertThat(response.getBody().getData().getIsDeleted(), is(true));
//
//        // Expected Fail if sender is not the owner
//        ResponseEntity<ErrorResponse> response1 = testRestTemplate.exchange("/api/users/families/"+fa2.getId()+"/delete", HttpMethod.DELETE, new HttpEntity<>(getHeader()), ErrorResponse.class);
//        System.out.println("2:"+ response1);
//        assertThat(response1.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//    }
//
//    @Test
//    @Order(5)
//    @Disabled
//    void givenFamilyIdAndUserId_whenRemoveFamilyMember_thenReturnFamilyDetailDTO() throws Exception {
//        // Expected Success
//        int size = fa1.getFamilyMembers().size();
//        final String url1 = "/api/users/families/" + fa1.getId() + "/delete-member?userId=" + fmUser.getId();
//        ResponseEntity<BaseResponse<FamilyDetailDTO>> response = testRestTemplate.exchange(url1, HttpMethod.DELETE, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<FamilyDetailDTO>>() {});
//        assertThat(response.getStatusCode(), is(HttpStatus.OK));
//        assertThat(response.getBody().getData().getMembers().size(), is(size-1));
//
//        // Expected Fail if sender is not the owner
//        ResponseEntity<ErrorResponse> response1 = testRestTemplate.exchange("/api/users/families/"+fa2.getId()+"/delete-member?userId="+user.getId(), HttpMethod.DELETE, new HttpEntity<>(getHeader()), ErrorResponse.class);
//        assertThat(response1.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//    }
//
//    @Test
//    @Order(5)
//    @Disabled
//    void givenFamilyId_whenLeaveFromFamilyMember_thenReturnFamilyDetailDTO() throws Exception {
//        // Expected Success
//        int size = fa2.getFamilyMembers().size();
//        final String url1 = "/api/users/families/" + fa2.getId() + "/leave";
//        ResponseEntity<BaseResponse<FamilyDetailDTO>> response1 = testRestTemplate.exchange(url1, HttpMethod.PUT, new HttpEntity<>(getHeader()), new ParameterizedTypeReference<BaseResponse<FamilyDetailDTO>>() {});
//        assertThat(response1.getStatusCode(), is(HttpStatus.OK));
//        assertThat(response1.getBody().getData().getMembers().size(), is(size-1));
//
//        ResponseEntity<ErrorResponse> response3 = testRestTemplate.exchange(url1, HttpMethod.PUT, new HttpEntity<>(getHeader()), ErrorResponse.class);
//        assertThat(response3.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//
//        final String url2 = "/api/users/families/" + fa1.getId() + "/leave";
//        ResponseEntity<ErrorResponse> response2 = testRestTemplate.exchange(url2, HttpMethod.PUT, new HttpEntity<>(getHeader()), ErrorResponse.class);
//        assertThat(response2.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//
//
//
//
//    }

//    @Test
//    @Order(6)
//    void givenFamily_WhenInviteMember_THENISSUCCESS() throws Exception{
//        HttpEntity<String> httpEntity=new HttpEntity<>(getHeader());
//        ResponseEntity<BaseResponse<InvitationDTO>> res=testRestTemplate.exchange("/api/users/families/1/members/invite?username=test2", HttpMethod.PUT,httpEntity,new ParameterizedTypeReference<BaseResponse<InvitationDTO>>(){});
//        System.out.println(res);
//        assertThat(res.getStatusCode(), is(HttpStatus.OK));
//        assert res.getBody()!=null;
//        assert res.getBody().getData()!=null;
//        assert res.getBody().getData().getStatus().equals("PENDING");
//        assertThat(res.getBody().getData().getUser().getId(),is(user3.getId()));
//        assertThat(res.getBody().getData().getFamily().getId(),is(fa1.getId()));
//
//        ResponseEntity<BaseResponse<InvitationDTO>> res1=testRestTemplate.exchange("/api/users/families/2/members/invite?username=test2", HttpMethod.PUT,httpEntity,new ParameterizedTypeReference<BaseResponse<InvitationDTO>>(){});
//        assertThat(res1.getStatusCode(), is(HttpStatus.BAD_REQUEST));
//        assertThat(res1.getBody().getMessage(),is(ResCode.NOT_OWNER_OF_FAMILY.getMessage()));
//    }

//    @Test
//    @Order(7)
//    void givenFamily_WhenAcceptInvitation_THENISSUCCESS() throws Exception{
//        Invitation in1=Invitation.builder().family(fa3).user(user).build();
//        in1=invitationRepo.save(in1);
//        HttpEntity<String> httpEntity=new HttpEntity<>(getHeader());
//        ResponseEntity<BaseResponse<InvitationDTO>> res=testRestTemplate.exchange("/api/users/families/3/members/response-invitation?invitationId="+in1.getId()+"&isAccepted=true", HttpMethod.PUT,httpEntity,new ParameterizedTypeReference<BaseResponse<InvitationDTO>>(){});
//
//
//        assertThat(res.getStatusCode(), is(HttpStatus.OK));
//        assert res.getBody()!=null;
//        assert res.getBody().getData()!=null;
//        assert res.getBody().getData().getStatus().equals("ACCEPTED");
//        assertThat(res.getBody().getData().getUser().getId(),is(user.getId()));
//        assertThat(res.getBody().getData().getFamily().getId(),is(fa3.getId()));
//
//        ResponseEntity<BaseResponse<FamilyDetailDTO>> res1=testRestTemplate.exchange("/api/users/families/3", HttpMethod.GET,httpEntity,new ParameterizedTypeReference<BaseResponse<FamilyDetailDTO>>(){});
//        assertThat(res1.getStatusCode(), is(HttpStatus.OK));
//    }

//    @Test
//    @Order(8)
//    void givenFamily_WhenRejectInvitation_THENISSUCCESS() throws Exception{
//        Invitation in1=Invitation.builder().family(fa3).user(user).build();
//        in1=invitationRepo.save(in1);
//        HttpEntity<String> httpEntity=new HttpEntity<>(getHeader());
//        ResponseEntity<BaseResponse<InvitationDTO>> res=testRestTemplate.exchange("/api/users/families/3/members/response-invitation?invitationId="+in1.getId()+"&isAccepted=false", HttpMethod.PUT,httpEntity,new ParameterizedTypeReference<BaseResponse<InvitationDTO>>(){});
//
//
//        assertThat(res.getStatusCode(), is(HttpStatus.OK));
//        assert res.getBody()!=null;
//        assert res.getBody().getData()!=null;
//        assert res.getBody().getData().getStatus().equals("REJECTED");
//        assertThat(res.getBody().getData().getUser().getId(),is(user.getId()));
//        assertThat(res.getBody().getData().getFamily().getId(),is(fa3.getId()));
//
//
//    }














}
