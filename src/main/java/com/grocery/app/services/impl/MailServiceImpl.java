package com.grocery.app.services.impl;

import com.grocery.app.config.constant.ResCode;
import com.grocery.app.exceptions.ServiceException;
import com.grocery.app.factory.MailMessageFactory;
import com.grocery.app.dto.MailDetailsDTO;
import com.grocery.app.services.MailService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

//    @Value("${spring.mail.username}")
    private String from="maybaymautrang@gmail.com";


    @Autowired
    private MailMessageFactory mailMessageFactory;

    @Override
    public void sendEmail(MailDetailsDTO mailDetailsDTO) {
        try{
            SimpleMailMessage mailMessage=mailMessageFactory.createSimpleMail();
            mailMessage.setFrom(from);
            mailMessage.setTo(mailDetailsDTO.getRecipient());
            mailMessage.setSubject(mailDetailsDTO.getSubject());
            mailMessage.setText(mailDetailsDTO.getMessage());
            javaMailSender.send(mailMessage);

        }catch (Exception e){
            e.printStackTrace();
            throw new ServiceException(ResCode.MAIL_SEND_ERROR.getMessage(), ResCode.MAIL_SEND_ERROR.getCode());
        }


    }

    @Override
    public void sendEmailWithAttachment(MailDetailsDTO mailDetailsDTO) {
        System.out.println("Email sent with attachment: " + mailDetailsDTO);
    }

}
