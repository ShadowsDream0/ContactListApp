package com.shadowsdream.service;

import com.shadowsdream.exception.ServiceException;
import com.shadowsdream.util.email.EmailSender;
import com.shadowsdream.util.email.EmailSenderImpl;
import com.shadowsdream.util.logging.ContactListLogger;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public final class EmailSenderService {
    private static EmailSender emailSender;
    private static EmailSenderService emailSenderService;
    private static ValidatorService validatorService;

    private static final String PATH_TO_EMAIL_PROPERTIES = "/home/shadows-dream/Documents/email-properties.txt";

    private static final String TEXT = "Hi, user!\nThis email was sent by ContactListApp." +
                                        " You can find all of your contacts in the attachment above";
    private static final String SUBJECT = "Exported contacts from ContactListApp";


    private EmailSenderService(){}

    public static EmailSenderService getInstance() throws ServiceException {
        if (emailSenderService == null) {
            try {
                emailSender = new EmailSenderImpl(PATH_TO_EMAIL_PROPERTIES, getProperties());
            } catch (FileNotFoundException e) {
                throw new ServiceException("File with email properties not found");
            }

            validatorService = ValidatorServiceImpl.getInstance();
            emailSenderService = new EmailSenderService();
        }

        return emailSenderService;
    }

    public void sendEmail(String text, String recipient) throws ServiceException {
        Objects.requireNonNull(recipient, "Argument recipient must not be null");

        validatorService.validateEmail(recipient);

        try {
            emailSender.sendEmail(text, SUBJECT, recipient);
        } catch (AddressException ae) {
            throw new ServiceException("recipient address is not valid" + ae.getMessage());
        } catch (MessagingException me) {
            ContactListLogger.getLog().debug("Error occurred during sending email" + me.getMessage() + " " + me.getCause());
            PrettyPrinter.printError("Error occurred during sending email. Exiting...");
            System.exit(1);
        }
    }

    public void sendEmailWithAttachment(String recipient, String attachmentPath) throws ServiceException {
        Objects.requireNonNull(recipient, "Argument recipient must not be null");
        Objects.requireNonNull(attachmentPath, "Argument attachmentPath must not be null");

        validatorService.validateEmail(recipient);

        try {
            emailSender.sendEmail(TEXT, SUBJECT, recipient, attachmentPath);
        } catch (IOException ioe) {
            throw new ServiceException("could not attach file", ioe);
        } catch (AddressException ae) {
            throw new ServiceException("recipient address is not valid", ae);
        } catch (MessagingException me) {
            ContactListLogger.getLog().debug("Error occurred during sending email" + me.getMessage() + " " + me.getCause());
            PrettyPrinter.printError("Error occurred during sending email. Exiting...\n");
            System.exit(1);
        }
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        return properties;
    }
}
