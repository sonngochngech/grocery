package com.grocery.app.services.impl;

import com.grocery.app.factory.MailMessageFactory;
import com.grocery.app.dto.MailDetailsDTO;
import com.grocery.app.services.MailService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private MailMessageFactory mailMessageFactory;

    @Override
    public void sendEmail(MailDetailsDTO mailDetailsDTO) {

        SimpleMailMessage mailMessage=mailMessageFactory.createSimpleMail();

        mailMessage.setTo(mailDetailsDTO.getRecipient());
        mailMessage.setSubject(mailDetailsDTO.getSubject());
        mailMessage.setText(mailDetailsDTO.getMessage());
        javaMailSender.send(mailMessage);
    }

    @Override
    public void sendEmailWithAttachment(MailDetailsDTO mailDetailsDTO) {
        System.out.println("Email sent with attachment: " + mailDetailsDTO);
    }

}
