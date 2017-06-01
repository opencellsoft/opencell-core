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

@Stateless
public class EmailSender {

	@Resource(lookup = "java:/MeveoMail")
	private Session mailSession;

	public void sent(String from, List<String> replyTo, List<String> to, String subject, String textContent, String htmlContent) throws BusinessException{
		sent(from, replyTo, to, null, null, subject, textContent, htmlContent, null, null);
	}

	public void sent(String from, List<String> replyTo, List<String> to, List<String> cc, List<String> bcc, String subject, String textContent, String htmlContent, List<File> attachments, Date sendDate) throws BusinessException {
		try{
			if (to == null || to.isEmpty()) {
				throw new MissingParameterException(Arrays.asList("addressTo"));
			}
			MimeMessage msg = new MimeMessage(mailSession);
			if(!StringUtils.isBlank(from)){
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
			msg.setSubject(subject);
			if (!StringUtils.isBlank(htmlContent)) {
				msg.setContent(htmlContent, "text/html");
			} else {
				msg.setContent(textContent, "text/plain");
			}
			if(attachments != null && !attachments.isEmpty()){
				MimeBodyPart messageBodyPart = new MimeBodyPart();
				Multipart multipart = new MimeMultipart();
				for(File file : attachments ){
					if(file != null){
						messageBodyPart = new MimeBodyPart();
						DataSource source = new FileDataSource(file);
						messageBodyPart.setDataHandler(new DataHandler(source));
						messageBodyPart.setFileName(file.getName());
						multipart.addBodyPart(messageBodyPart);
					}
				}
				msg.setContent(multipart);
			}
			Transport.send(msg);
		}catch(Exception e){
			throw new BusinessException(e.getMessage());
		}
	}
}
