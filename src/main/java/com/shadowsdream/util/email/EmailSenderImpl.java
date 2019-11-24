package com.shadowsdream.util.email;

import com.shadowsdream.util.io.FileReader;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;


public final class EmailSenderImpl implements EmailSender{
    private final String senderName;
    private final String senderPassword;
    private Session session;


    public EmailSenderImpl(String emailPropertiesFile, Properties smtpProperties) throws FileNotFoundException {
        Objects.requireNonNull(emailPropertiesFile, "Argument emailPropertiesFile must not be null");
        Objects.requireNonNull(smtpProperties, "Argument smtpProperties must not be null");

        List<String> info = getSenderInfoFromFile(emailPropertiesFile);
        senderName = info.get(0);
        senderPassword = info.get(1);

        setSession(smtpProperties);
    }

    @Override
    public void sendEmail(String text, String subject, String recipient) throws MessagingException {
        Objects.requireNonNull(text, "Argument text must not be null");
        Objects.requireNonNull(subject, "Argument subject must not be null");
        Objects.requireNonNull(recipient, "Argument recipient must not be null");

        Transport.send(prepareMessage(text, subject, recipient));
    }

    @Override
    public void sendEmail(String text, String subject, String recipient, String attachmentPath)
                                                            throws MessagingException, IOException {
        Objects.requireNonNull(text, "Argument text must not be null");
        Objects.requireNonNull(subject, "Argument subject must not be null");
        Objects.requireNonNull(recipient, "Argument recipient must not be null");
        Objects.requireNonNull(attachmentPath, "Argument attachmentPath must not be null");

        Multipart multipart = new MimeMultipart();
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.attachFile(attachmentPath);
        multipart.addBodyPart(bodyPart);

        Message message = prepareMessage(text, subject, recipient);
        message.setContent(multipart);

        Transport.send(message);
    }

    private Message prepareMessage(String text, String subject, String recipient) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderName));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject(subject);
        message.setText(text);

        return message;
    }

    private List<String> getSenderInfoFromFile(String fileName) throws FileNotFoundException {
        Path filePath = Path.of(fileName);
        if ( !Files.exists(filePath) ) {
            throw new FileNotFoundException("File not found");
        }

        return FileReader.readLinesFromFile(filePath)
                .collect(Collectors.toList());
    }

    private void setSession(Properties smtpProperties) {
        session = Session.getDefaultInstance(smtpProperties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderName, senderPassword);
            }
        });
    }
}
