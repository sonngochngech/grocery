package com.grocery.app.factory;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class MailMessageFactory {

    @Autowired
    private JavaMailSender javaMailSender;

    public SimpleMailMessage createSimpleMail(){
        return new SimpleMailMessage();
    }


}
