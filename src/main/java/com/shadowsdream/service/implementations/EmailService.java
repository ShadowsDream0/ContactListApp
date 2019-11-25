package com.shadowsdream.service.implementations;

import com.shadowsdream.exception.ServiceException;

public interface EmailService {

    void sendEmail(String text, String recipient) throws ServiceException;

    void sendEmailWithAttachment(String recipient, String attachmentPath) throws ServiceException;

}
