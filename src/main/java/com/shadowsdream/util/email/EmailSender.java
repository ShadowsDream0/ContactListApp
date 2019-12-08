package com.shadowsdream.util.email;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailSender {

    void sendEmail(String text, String subject, String recipient) throws MessagingException;

    void sendEmail(String text, String subject, String recipient, String attachmentPath)
            throws MessagingException, IOException;
}
