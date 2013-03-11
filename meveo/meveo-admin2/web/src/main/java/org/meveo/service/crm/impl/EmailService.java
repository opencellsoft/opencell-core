/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Log;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.communication.MediaEnum;
import org.meveo.model.communication.MessageSenderConfig;
import org.meveo.model.crm.Email;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.crm.local.EmailServiceLocal;


/**
 * Email service implementation.
 * 
 * @author Gediminas Ubartas
 * @created 2010.10.05
 */
@Stateless
@Name("emailService")
@AutoCreate
public class EmailService extends PersistenceService<Email> implements EmailServiceLocal {

	@Resource(mappedName = "java:/Mail")
	private static Session mailSession;
	
	@Logger
	private static Log log;

	public void sendEmail(String from, List<String> to,List<String> cc, String subject, String body,List<File> files) throws BusinessException{
		log.info("start sendEmail details: from:#0,to:#1,cc:#2,subject:#3,body:#4,files:#5", from,to,cc,subject,body,files);
		MimeMessage message = new MimeMessage(mailSession);
		if(to==null||to.size()==0){
			log.info("null to emails");
			return;
		}
		InternetAddress[] toAddress=new InternetAddress[to.size()];
		
		try {
			for(int i=0;i<to.size();i++){
				toAddress[i]=new InternetAddress(to.get(i));
			}
			message.setRecipients(RecipientType.TO, toAddress);
			
			if(cc!=null&&cc.size()>0){
				InternetAddress[] ccAddress=new InternetAddress[cc.size()];
				for(int j=0;j<cc.size();j++){
					ccAddress[j]=new InternetAddress(cc.get(j));
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
			int index=0;
			if(files!=null){
			for(File file:files){
				MimeBodyPart attached=null;
				if(file.exists()){
					attached=new MimeBodyPart();
					FileDataSource fds=new FileDataSource(file);
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
			e.printStackTrace();
			throw new BusinessException("Error: " + e.getMessage()
					+ " when send email to " + to);
		}
		log.info("successfully sendEmail!");
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<MediaEnum, List<MessageSenderConfig>> getMediaConfig(Provider provider) {
		HashMap<MediaEnum, List<MessageSenderConfig>> result = new HashMap<MediaEnum, List<MessageSenderConfig>>();
		List<MessageSenderConfig> allConfig = (List<MessageSenderConfig>)em.createQuery("from "+MessageSenderConfig.class.getSimpleName()+" where provider=:provider and disabled=false")
		.setParameter("provider", provider).getResultList();
		if(allConfig!=null && allConfig.size()>0){
			for(MessageSenderConfig config :  allConfig){
				if(result.containsKey(config.getMedia())){
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
