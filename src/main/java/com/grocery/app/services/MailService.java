package com.grocery.app.services;

import com.grocery.app.dto.MailDetailsDTO;

public interface MailService {
    public void sendEmail(MailDetailsDTO mailDetailsDTO);
    public void sendEmailWithAttachment(MailDetailsDTO mailDetailsDTO);
}
