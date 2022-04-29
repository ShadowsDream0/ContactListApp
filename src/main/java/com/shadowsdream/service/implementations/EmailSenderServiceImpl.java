package com.shadowsdream.service.implementations;

import com.shadowsdream.exception.ServiceException;
import com.shadowsdream.service.EmailService;
import com.shadowsdream.service.PrettyPrinter;
import com.shadowsdream.service.ValidatorService;
import com.shadowsdream.util.PropertyLoader;
import com.shadowsdream.util.email.EmailSender;
import com.shadowsdream.util.email.EmailSenderImpl;
import com.shadowsdream.util.logging.ContactListLogger;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public final class EmailSenderServiceImpl implements EmailService {

    private static EmailSender emailSender;
    private static EmailSenderServiceImpl emailSenderServiceImpl;
    private static ValidatorService validatorService;

    private static final String PATH_TO_CREDENTIALS = "/home/shadows-dream/Documents/credentials.txt";

    private static final String TEXT = "Hi, user!\nThis email was sent by ContactListApp." +
                                        " Your contacts were exported successfully\n";
    private static final String SUBJECT = "Exported contacts from ContactListApp";


    private EmailSenderServiceImpl(){}


    public static EmailSenderServiceImpl getInstance() throws ServiceException {
        if (emailSenderServiceImpl == null) {
            try {
                emailSender = new EmailSenderImpl(PATH_TO_CREDENTIALS, getProperties());
            } catch (FileNotFoundException e) {
                throw new ServiceException("File with email properties not found");
            }

            validatorService = ValidatorServiceImpl.getInstance();
            emailSenderServiceImpl = new EmailSenderServiceImpl();
        }

        return emailSenderServiceImpl;
    }


    public void sendEmail(String text, String recipient) throws ServiceException {
        Objects.requireNonNull(recipient, "Argument recipient must not be null");

        validatorService.validateEmail(recipient);

        try {
            emailSender.sendEmail(TEXT + text, SUBJECT, recipient);
        } catch (AddressException ae) {
            throw new ServiceException("recipient address is not valid" + ae.getMessage());
        } catch (MessagingException me) {
            ContactListLogger.getLog().debug("Error occurred during sending email " + me.getMessage() + " " + me.getCause());
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
        Properties smtpProperties = null;

        try {
            smtpProperties = PropertyLoader.getSmtpProperties();
        } catch (IOException e) {
            ContactListLogger.getLog().debug("Error occurred during loading smtp properties from file " + e.getMessage() + " " + e.getCause());
            PrettyPrinter.printError("Application was not configured properly for sending email. Exiting...\n");
            System.exit(1);
        }

        return smtpProperties;
    }
}
