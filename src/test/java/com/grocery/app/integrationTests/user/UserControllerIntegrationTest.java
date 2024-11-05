package com.grocery.app.integrationTests.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grocery.app.config.constant.AppConstants;
import com.grocery.app.config.constant.ResCode;
import com.grocery.app.dto.DeviceDTO;
import com.grocery.app.dto.UserDetailDTO;
import com.grocery.app.integrationTests.base.ServicesTestSupport;
import com.grocery.app.payloads.loginCredentials.DefaultCredentials;
import com.grocery.app.payloads.responses.AuthResponse;
import com.grocery.app.payloads.responses.BaseResponse;
import com.grocery.app.payloads.users.UpdateUserDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIntegrationTest extends ServicesTestSupport {


    @Test
    @DisplayName("Happy Path Test: Login User")
    @Order(1)
    @Disabled
    void givenDefaultCredential_whenLoginUser_thenReturnUserDTO() throws Exception {

        DefaultCredentials defaultCredentials=DefaultCredentials.builder()
                .username("test")
                .password("123456789")
                .device(DeviceDTO.builder().deviceId("test").deviceType("test").build())
                .build();
        final HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(defaultCredentials),unAuthHeader());

        final ResponseEntity<AuthResponse> response = testRestTemplate.exchange("/api/auth/default-login", HttpMethod.POST, httpEntity, AuthResponse.class);

        assert response.getBody() != null;
        assertThat(response.getBody().getMessage(),is(ResCode.LOGIN_SUCCESSFULLY.getMessage()));
        assertThat(response.getBody().getCode(),is(ResCode.LOGIN_SUCCESSFULLY.getCode()));
        assertThat(response.getBody().getData().getUsername(),is("test"));
        assertThat(response.getBody().getData().getRole().getName(),is("USER"));
    }

    @Test
    @DisplayName("Happy Path Test: get User")
    @Order(2)
    @Disabled
    void givenAuthenticatedRequest_whenGetUser_thenReturnUserDTO() throws Exception {

        final HttpEntity<String> httpEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<BaseResponse<UserDetailDTO>> response = testRestTemplate.exchange("/api/users/profile", HttpMethod.GET, httpEntity, new ParameterizedTypeReference<BaseResponse<UserDetailDTO>>() {
        });

        assert response.getBody() != null;
        assertThat(response.getBody().getMessage(),is(ResCode.GET_USER_SUCCESSFULLY.getMessage()));
        assertThat(response.getBody().getCode(),is(ResCode.GET_USER_SUCCESSFULLY.getCode()));
        assertThat(response.getBody().getData().getUsername(),is("test"));
        assertThat(response.getBody().getData().getRole().getName(),is("USER"));

    }

    @Test
    @DisplayName("Happy Path Test: update User")
    @Order(3)
    @Disabled
    void givenUpdateRequest_whenUpdateUser_thenReturnUserDTO()  throws  Exception {
        UpdateUserDTO userDetailDTO=UpdateUserDTO.builder()
                .firstName("test1")
                .lastName("test2")
                .birthday(new SimpleDateFormat(AppConstants.DATE_FORMAT).parse("1999-01-01"))
                .build();

        final HttpEntity<String> httpEntity = new HttpEntity<>(objectMapper.writeValueAsString(userDetailDTO), getHeader());
        final ResponseEntity<BaseResponse<UserDetailDTO>> response = testRestTemplate.exchange("/api/users/profile/update", HttpMethod.PUT, httpEntity, new ParameterizedTypeReference<BaseResponse<UserDetailDTO>>() {
        });

        assert response.getBody() != null;
        assertThat(response.getBody().getMessage(),is(ResCode.UPDATE_USER_SUCCESSFULLY.getMessage()));
        assertThat(response.getBody().getCode(),is(ResCode.UPDATE_USER_SUCCESSFULLY.getCode()));
        assertThat(response.getBody().getData().getUsername(),is("test"));
        assertThat(response.getBody().getData().getRole().getName(),is("USER"));
        assertThat(response.getBody().getData().getFirstName(),is("test1"));
        assertThat(response.getBody().getData().getLastName(),is("test2"));
    }

    @Test
    @DisplayName("Happy Path Test: lock User")
    @Order(4)
    @Disabled
    public void givenLockRequest_whenLockUser_thenReturnUserDTO() throws Exception {
        final HttpEntity<String> httpEntity = new HttpEntity<>(getHeader());
        final ResponseEntity<BaseResponse<UserDetailDTO>> response = testRestTemplate.exchange("/api/users/profile/lock", HttpMethod.PUT, httpEntity, new ParameterizedTypeReference<BaseResponse<UserDetailDTO>>() {
        });

        assert response.getBody() != null;
        assertThat(response.getBody().getMessage(),is(ResCode.LOCK_USER_SUCCESSFULLY.getMessage()));
        assertThat(response.getBody().getCode(),is(ResCode.LOCK_USER_SUCCESSFULLY.getCode()));
        assertThat(response.getBody().getData().getUsername(),is("test"));
        assertThat(response.getBody().getData().getRole().getName(),is("USER"));
        assertThat(response.getBody().getData().getIsActivated(),is(false));
    }

}
