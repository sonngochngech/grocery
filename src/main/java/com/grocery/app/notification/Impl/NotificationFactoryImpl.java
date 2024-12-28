package com.grocery.app.notification.Impl;

import com.grocery.app.dto.MeanDTO;
import com.grocery.app.dto.NotiContentDTO;
import com.grocery.app.dto.NotiDTO;
import com.grocery.app.dto.NotificationDTO;
import com.grocery.app.notification.NotificationFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    public NotiDTO sendExpriedNoti(String email,String content){
        MeanDTO meanDTO= MeanDTO.builder()
                .emails(Set.of(email))
                .build();

        NotiContentDTO notiContentDTO= NotiContentDTO.builder()
                .title("Đồ ăn hết hạn")
                .message(content)
                .build();

        NotiDTO notiDTO= NotiDTO.builder()
                .notiContentDTO(notiContentDTO)
                .meanDTO(meanDTO)
                .build();

        return notiDTO;
    }

    public NotiDTO sendTaskAlert(String email,String content){
        MeanDTO meanDTO= MeanDTO.builder()
                .emails(Set.of(email))
                .build();

        NotiContentDTO notiContentDTO= NotiContentDTO.builder()
                .title("Thông báo nhiệm vụ cho bạn")
                .message(content)
                .build();

        NotiDTO notiDTO= NotiDTO.builder()
                .notiContentDTO(notiContentDTO)
                .meanDTO(meanDTO)
                .build();

        return notiDTO;
    }

    public NotiDTO sendInvitationNoti(String email,String content){
        MeanDTO meanDTO= MeanDTO.builder()
                .emails(Set.of(email))
                .build();

        NotiContentDTO notiContentDTO= NotiContentDTO.builder()
                .title("Mời tham gia gia đình")
                .message(content)
                .build();

        NotiDTO notiDTO= NotiDTO.builder()
                .notiContentDTO(notiContentDTO)
                .meanDTO(meanDTO)
                .build();

        return notiDTO;
    }


    @Override
    public NotiDTO sendNotification(NotificationDTO notificationDTO){
        MeanDTO meanDTO= MeanDTO.builder()
                .devices(new HashSet<>(notificationDTO.getDevices()))
                .emails(new HashSet<>())
                .build();

        NotiContentDTO notiContentDTO= NotiContentDTO.builder()
                .title(notificationDTO.getTitle())
                .message(notificationDTO.getMessage())
                .externalData(notificationDTO.getExternalData())
                .type(notificationDTO.getType())
                .build();

        return  NotiDTO.builder()
                .notiContentDTO(notiContentDTO)
                .meanDTO(meanDTO)
                .build();

    }




}
