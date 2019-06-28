package org.meveo.service.communication.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
public class EmailSender {
	
	private Logger log = LoggerFactory.getLogger(EmailSender.class);

    @Resource(lookup = "java:/MeveoMail")
    private Session mailSession;

    /**
     * Deprecated in v.4.7. Just a method name change. Use send() instead.
     * 
     * @param from email address from which the email is sent
     * @param replyTo email address in wich the reply should be sent.
     * @param to email address in which email is sent to
     * @param cc email address 'cc 
     * @param bcc email address's bcc
     * @param subject email subject
     * @param textContent text content
     * @param htmlContent html content
     * @throws BusinessException business exception.
     */
    @Deprecated
    public void sent(String from, List<String> replyTo, List<String> to, List<String> cc, List<String> bcc, String subject, String textContent, String htmlContent)
            throws BusinessException {
        sent(from, replyTo, to, cc, bcc, subject, textContent, htmlContent, null, null);
    }

    /**
     * Deprecated in v.4.7. Just a method name change. Use send() instead.
     * 
     * @param from email address from which the email is sent
     * @param replyTo email address in wich the reply should be sent.
     * @param to email address in which email is sent to
     * @param subject email subject
     * @param textContent text content
     * @param htmlContent html content
     * @throws BusinessException business exception.
     */
    @Deprecated
    public void sent(String from, List<String> replyTo, List<String> to, String subject, String textContent, String htmlContent) throws BusinessException {
        sent(from, replyTo, to, null, null, subject, textContent, htmlContent, null, null);
    }

    /**
     * Deprecated in v.4.7. Just a method name change. Use send() instead.
     * 
     * @param from email address from which the email is sent
     * @param replyTo email address in wich the reply should be sent.
     * @param to email address in which email is sent to
     * @param cc email address 'cc 
     * @param bcc email address's bcc
     * @param subject email subject
     * @param textContent text content
     * @param htmlContent html content
     * @param attachments list of attached file.
     * @param sendDate date when email is sent.
     * @throws BusinessException business exception.
     */
    @Deprecated
    public void sent(String from, List<String> replyTo, List<String> to, List<String> cc, List<String> bcc, String subject, String textContent, String htmlContent,
            List<File> attachments, Date sendDate) throws BusinessException {

    }

    /**
     * Send an email message
     * 
     * @param from Sender's email address
     * @param replyTo Reply to email addresses
     * @param to Recipient's email addresses
     * @param cc CC recipient's email addresses
     * @param bcc BCC recipient's email addresses
     * @param subject Email subject
     * @param textContent Plain text contents
     * @param htmlContent HTML type contents
     * @throws BusinessException business exception.
     */
    public void send(String from, List<String> replyTo, List<String> to, List<String> cc, List<String> bcc, String subject, String textContent, String htmlContent)
            throws BusinessException {
        send(from, replyTo, to, cc, bcc, subject, textContent, htmlContent, null, null);
    }

    /**
     * Send an email message.
     * 
     * @param from Sender's email address
     * @param replyTo Reply to email addresses
     * @param to Recipient's email addresses
     * @param subject Email subject
     * @param textContent Plain text contents
     * @param htmlContent HTML type contents
     * @throws BusinessException business exception.
     */
    public void send(String from, List<String> replyTo, List<String> to, String subject, String textContent, String htmlContent) throws BusinessException {
        send(from, replyTo, to, null, null, subject, textContent, htmlContent, null, null);
    }

    /**
     * Send an email message.
     * 
     * @param from Sender's email address
     * @param replyTo Reply to email addresses
     * @param to Recipient's email addresses
     * @param cc CC recipient's email addresses
     * @param bcc BCC recipient's email addresses
     * @param subject Email subject
     * @param textContent Plain text contents
     * @param htmlContent HTML type contents
     * @param attachments Email attachments
     * @param sendDate Sending date
     * @throws BusinessException business exception.
     */
    public void send(String from, List<String> replyTo, List<String> to, List<String> cc, List<String> bcc, String subject, String textContent, String htmlContent,
            List<File> attachments, Date sendDate) throws BusinessException {

        try {
            if (to == null || to.isEmpty()) {
                //throw new MissingParameterException(Arrays.asList("addressTo"));
            	log.warn("addressTo is null. Email will not be sent");
            	return;
            }
            MimeMessage msg = new MimeMessage(mailSession);
            if (!StringUtils.isBlank(from)) {
                msg.setFrom(new InternetAddress(from));
            }
            List<InternetAddress> addressTo = new ArrayList<InternetAddress>();
            for (String address : to) {
                addressTo.add(new InternetAddress(address));
            }
            msg.setRecipients(RecipientType.TO, addressTo.toArray(new InternetAddress[addressTo.size()]));
            List<InternetAddress> replytoAddress = new ArrayList<InternetAddress>();
            if (replyTo != null && !replyTo.isEmpty()) {
                for (String address : replyTo) {
                    replytoAddress.add(new InternetAddress(address));
                }
                msg.setReplyTo(replytoAddress.toArray(new InternetAddress[replytoAddress.size()]));
            }
            List<InternetAddress> ccAddress = new ArrayList<InternetAddress>();
            if (cc != null && !cc.isEmpty()) {
                for (String address : cc) {
                    ccAddress.add(new InternetAddress(address));
                }
                msg.setRecipients(RecipientType.CC, ccAddress.toArray(new InternetAddress[ccAddress.size()]));
            }
            List<InternetAddress> bccAddress = new ArrayList<InternetAddress>();
            if (bcc != null && !bcc.isEmpty()) {
                for (String address : bcc) {
                    bccAddress.add(new InternetAddress(address));
                }
                msg.setRecipients(RecipientType.BCC, bccAddress.toArray(new InternetAddress[bccAddress.size()]));
            }
            msg.setSentDate(sendDate == null ? new Date() : sendDate);
            msg.setSubject(subject, "UTF-8");

            BodyPart messageBodyPart = new MimeBodyPart();
            if (!StringUtils.isBlank(htmlContent)) {
                messageBodyPart.setContent(htmlContent, "text/html; charset=UTF-8");
            } else {
                messageBodyPart.setContent(textContent, "text/plain; charset=UTF-8");
            }

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            if (attachments != null && !attachments.isEmpty()) {
                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                for (File file : attachments) {
                    if (file != null) {
                        mimeBodyPart = new MimeBodyPart();
                        DataSource source = new FileDataSource(file);
                        mimeBodyPart.setDataHandler(new DataHandler(source));
                        mimeBodyPart.setFileName(file.getName());
                        multipart.addBodyPart(mimeBodyPart);
                    }
                }

            }
            msg.setContent(multipart);
            Transport.send(msg);
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
    }
}
