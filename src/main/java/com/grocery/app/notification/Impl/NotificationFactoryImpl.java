package com.grocery.app.notification.Impl;

import com.grocery.app.dto.MeanDTO;
import com.grocery.app.dto.NotiContentDTO;
import com.grocery.app.dto.NotiDTO;
import com.grocery.app.notification.NotificationFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@AllArgsConstructor
@Component
public class NotificationFactoryImpl implements NotificationFactory {

    public NotiDTO VerifyCodeNoti(String email,String code){
        MeanDTO meanDTO= MeanDTO.builder()
                .emails(Set.of(email))
                .build();

        NotiContentDTO content= NotiContentDTO.builder()
                                .title("Verify Code")
                                .message("Your verify code is: "+code)
                                 .build();

        NotiDTO notiDTO= NotiDTO.builder()
                .notiContentDTO(content)
                .meanDTO(meanDTO)
                .build();

        return notiDTO;
    }


}
