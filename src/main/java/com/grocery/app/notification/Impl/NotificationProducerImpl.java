//package com.grocery.app.notification.Impl;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.grocery.app.notification.NotificationProducer;
//import com.grocery.app.dto.FcmDTO;
//import com.grocery.app.dto.MailDetailsDTO;
//import com.grocery.app.dto.NotiDTO;
//import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.ModelMapper;
//import org.modelmapper.TypeToken;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//
//import java.util.List;
//
//@Component
//@AllArgsConstructor
//@NoArgsConstructor
//@Slf4j
//public class NotificationProducerImpl implements NotificationProducer {
//
//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Override
//    public void    sendMessage(NotiDTO notiDTO) {
//      List<MailDetailsDTO> mailDetailsDTOS=modelMapper.map(notiDTO,new TypeToken<List<MailDetailsDTO>>(){}.getType());
//      FcmDTO fcmDTO=modelMapper.map(notiDTO,FcmDTO.class);
//      if(fcmDTO!=null){
//          sendFcmMessage(fcmDTO);
//      }
//      if(mailDetailsDTOS!=null){
//          mailDetailsDTOS.forEach(this::sendEmailMessage);
//      }
//    }
//
//    private void sendEmailMessage(MailDetailsDTO mailDetailsDTO) {
//        try{
//        String message=objectMapper.writeValueAsString(mailDetailsDTO);
//        rabbitTemplate.convertAndSend("app.exchange", "email", message);
//        }catch (Exception e){
//            log.error("Error sending email message: {}",e.getMessage());
//        }
//    }
//
//    private void sendFcmMessage(FcmDTO fcmDTO){
//        rabbitTemplate.convertAndSend("app.exchange", "fcm", fcmDTO.toString());
//
//    }
//}
