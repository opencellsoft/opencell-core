/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.service.crm.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.communication.MediaEnum;
import org.meveo.model.communication.MessageSenderConfig;
import org.meveo.model.crm.Email;
import org.meveo.service.base.PersistenceService;

/**
 * Email service implementation.
 */
@Stateless
public class EmailService extends PersistenceService<Email> {

	@Resource(lookup = "java:/MeveoMail")
	private Session mailSession;

	public void sendEmail(String from, List<String> to, List<String> cc,List<String> replytoAddress,String subject, String body)
			throws BusinessException {
	      MimeMessage msg = new MimeMessage(mailSession);
	        try {
	            msg.setFrom(new InternetAddress(from));
	            msg.setSentDate(new Date());
	            msg.setSubject(subject);
	            if (body.indexOf("<html")>=0) {
	                msg.setContent(body, "text/html");
	            } else {
	                msg.setContent(body, "text/plain");
	            }
	            List<InternetAddress> addressTo = new ArrayList<InternetAddress>();
	            for (int i = 0; i < to.size(); i++) {
	            	addressTo.add(new InternetAddress(to.get(i)));
				}
	            msg.setRecipients(RecipientType.TO, addressTo.toArray(new InternetAddress[addressTo.size()]));

	            if(replytoAddress!=null){
	            	List<InternetAddress> replyTo = new ArrayList<InternetAddress>();
	            	for (int i = 0; i < to.size(); i++) {
	            		replyTo.add(new InternetAddress(replytoAddress.get(i)));
					}
	            	msg.setReplyTo(replyTo.toArray(new InternetAddress[replyTo.size()]));
	            }

	            Transport.send(msg);
	        } catch(AddressException  e){
				e.printStackTrace();
				throw new BusinessException("invalid email address",e);
	        } catch (MessagingException e) {
				e.printStackTrace();
				throw new BusinessException("error sending email",e);
			}
	}

	public void sendEmail(String from, List<String> to, List<String> cc, String subject, String body, List<File> files)
			throws BusinessException {
		log.info("start sendEmail details: from:{},to:{},cc:{},subject:{},body:{},files:{}", from, to, cc, subject,
				body, files);
		MimeMessage message = new MimeMessage(mailSession);
		if (to == null || to.size() == 0) {
			log.info("null to emails");
			return;
		}
		InternetAddress[] toAddress = new InternetAddress[to.size()];

		try {
			for (int i = 0; i < to.size(); i++) {
				toAddress[i] = new InternetAddress(to.get(i));
			}
			message.setRecipients(RecipientType.TO, toAddress);

			if (cc != null && cc.size() > 0) {
				InternetAddress[] ccAddress = new InternetAddress[cc.size()];
				for (int j = 0; j < cc.size(); j++) {
					ccAddress[j] = new InternetAddress(cc.get(j));
				}
				message.setRecipients(RecipientType.CC, ccAddress);
			}
			message.setFrom(new InternetAddress(from));
			message.setSubject(subject, "UTF-8");
			message.setSentDate(new Date());
			MimeBodyPart bodyPart = new MimeBodyPart();

			bodyPart.setText(body, "UTF-8");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(bodyPart);
			int index = 0;
			if (files != null) {
				for (File file : files) {
					MimeBodyPart attached = null;
					if (file.exists()) {
						attached = new MimeBodyPart();
						FileDataSource fds = new FileDataSource(file);
						attached.setDataHandler(new DataHandler(fds));
						attached.setFileName(file.getName());
						multipart.addBodyPart(attached, index);
						index++;
						log.debug("added file "+file.getName()+" to email body part");
					}
				}
			}
			message.setContent(multipart);
			Transport.send(message);
			log.debug("sent email");

		} catch (Exception e) {
			e.printStackTrace();
			throw new BusinessException("Error: " + e.getMessage() + " when send email to " + to);
		}
		log.info("successfully sendEmail!");
	}

	@SuppressWarnings("unchecked")
	public HashMap<MediaEnum, List<MessageSenderConfig>> getMediaConfig() {
		HashMap<MediaEnum, List<MessageSenderConfig>> result = new HashMap<MediaEnum, List<MessageSenderConfig>>();
		List<MessageSenderConfig> allConfig = (List<MessageSenderConfig>) getEntityManager()
				.createQuery(
						"from " + MessageSenderConfig.class.getSimpleName()
								+ " where disabled=false")
				.getResultList();
		if (allConfig != null && allConfig.size() > 0) {
			for (MessageSenderConfig config : allConfig) {
				if (result.containsKey(config.getMedia())) {
					result.get(config.getMedia()).add(config);
				} else {
					List<MessageSenderConfig> mediaConfigs = new ArrayList<MessageSenderConfig>();
					mediaConfigs.add(config);
					result.put(config.getMedia(), mediaConfigs);
				}
			}
		}
		return result;
	}
}
