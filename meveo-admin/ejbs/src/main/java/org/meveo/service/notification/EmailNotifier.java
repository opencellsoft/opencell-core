package org.meveo.service.notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.IEntity;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.NotificationHistoryStatusEnum;
import org.meveo.service.base.ValueExpressionWrapper;
import org.slf4j.Logger;

//TODO : transform that into MDB to correctly handle retries
@Stateless
public class EmailNotifier {

	@Resource(lookup = "java:/MeveoMail")
	private Session mailSession;

	@Inject 
	NotificationHistoryService notificationHistoryService;
	
	@Inject
    private Logger log;
	
	@Asynchronous
	public void sendEmail(EmailNotification notification, IEntity e,Map<String,Object> context){
		MimeMessage msg = new MimeMessage(mailSession);
		try {
			msg.setFrom(new InternetAddress(notification.getEmailFrom()));
			msg.setSentDate(new Date());
			HashMap<Object,Object> userMap = new HashMap<Object, Object>();
			userMap.put("event", e);
			userMap.put("context", context);
			log.debug("event[{}], context[{}]",e,context);
			msg.setSubject((String)ValueExpressionWrapper.evaluateExpression(notification.getSubject(), userMap, String.class));
			if(!StringUtils.isBlank(notification.getHtmlBody())){
				String htmlBody=(String)ValueExpressionWrapper.evaluateExpression(notification.getHtmlBody(), userMap, String.class);
				msg.setContent(htmlBody, "text/html");
			} else {
				String body=(String)ValueExpressionWrapper.evaluateExpression(notification.getBody(), userMap, String.class);
				msg.setContent(body, "text/plain");
			}
			List<InternetAddress> addressTo = new ArrayList<InternetAddress>();
			
			if(!StringUtils.isBlank(notification.getEmailToEl())){
				addressTo.add(new InternetAddress((String)ValueExpressionWrapper.evaluateExpression(notification.getEmailToEl(), userMap, String.class)));
			}
			if(notification.getEmails()!=null){
				for (String address:notification.getEmails()) {
					addressTo.add(new InternetAddress(address));
				}
			}
			msg.setRecipients(RecipientType.TO,addressTo.toArray(new InternetAddress[addressTo.size()]));

			InternetAddress[] replytoAddress = { new InternetAddress(
					notification.getEmailFrom()) };
			msg.setReplyTo(replytoAddress);
			if(!StringUtils.isBlank(notification.getBody())){
				String htmlBody=(String)ValueExpressionWrapper.evaluateExpression(notification.getBody(), userMap, String.class);
				msg.setContent(htmlBody, "text/plain");
			}
			if(!StringUtils.isBlank(notification.getHtmlBody())){
				String htmlBody=(String)ValueExpressionWrapper.evaluateExpression(notification.getHtmlBody(), userMap, String.class);
				msg.setContent(htmlBody, "text/html");
			}
			Transport.send(msg);
			notificationHistoryService.create(notification, e, "", NotificationHistoryStatusEnum.SENT);

		} catch (BusinessException e1) {
			try {
				notificationHistoryService.create(notification, e, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
			} catch (BusinessException e2) {
				log.error("Failed to create notification history business",e);
			}
			
		} catch (AddressException e1) {
			try {
				notificationHistoryService.create(notification, e, e1.getMessage(), NotificationHistoryStatusEnum.FAILED);
			} catch (BusinessException e2) {
				log.error("Failed to create notification history address",e);
			}
		} catch (MessagingException e1) {
			try {
				notificationHistoryService.create(notification, e, e1.getMessage(), NotificationHistoryStatusEnum.TO_RETRY);
			} catch (BusinessException e2) {
				log.error("Failed to create notification history messaging ",e);
			}
		}
	}
}
