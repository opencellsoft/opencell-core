/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.communication.MediaEnum;
import org.meveo.model.communication.MessageSenderConfig;
import org.meveo.model.crm.Email;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;

/**
 * Email service implementation.
 */
@Stateless
public class EmailService extends PersistenceService<Email> {

	@Resource(lookup = "java:/MeveoMail")
	private static Session mailSession;

	@Inject
	private static Logger log;

	public void sendEmail(String from, List<String> to, List<String> cc, String subject, String body, List<File> files)
			throws BusinessException {
		log.info("start sendEmail details: from:#0,to:#1,cc:#2,subject:#3,body:#4,files:#5", from, to, cc, subject,
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
			message.setSubject(subject);
			message.setSentDate(new Date());
			MimeBodyPart bodyPart = new MimeBodyPart();

			bodyPart.setText(body);
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
					}
				}
			}
			message.setContent(multipart);
			Transport.send(message);
			log.info("send email(s)");

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new BusinessException("Error: " + e.getMessage() + " when send email to " + to);
		}
		log.info("successfully sendEmail!");
	}

	@SuppressWarnings("unchecked")
	public HashMap<MediaEnum, List<MessageSenderConfig>> getMediaConfig(Provider provider) {
		HashMap<MediaEnum, List<MessageSenderConfig>> result = new HashMap<MediaEnum, List<MessageSenderConfig>>();
		List<MessageSenderConfig> allConfig = (List<MessageSenderConfig>) getEntityManager()
				.createQuery(
						"from " + MessageSenderConfig.class.getSimpleName()
								+ " where provider=:provider and disabled=false").setParameter("provider", provider)
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
