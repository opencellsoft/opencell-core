package org.meveo.admin.action.notification;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.notification.EmailNotification;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.EmailNotificationService;

@Named
@ConversationScoped
public class EmailNotificationBean extends BaseBean<EmailNotification> {

	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	private EmailNotificationService emailNotificationService;

	public EmailNotificationBean() {
		super(EmailNotification.class);
	}

	@Override
	protected IPersistenceService<EmailNotification> getPersistenceService() {
		return emailNotificationService;
	}

	protected String getDefaultViewName() {
		return "emailNotifications";
	}

	@Override
	protected String getListViewName() {
		return "emailNotifications";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

}
