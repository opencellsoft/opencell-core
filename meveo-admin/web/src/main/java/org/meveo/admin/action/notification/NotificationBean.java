package org.meveo.admin.action.notification;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.NotificationService;

@Named
@ConversationScoped
public class NotificationBean extends BaseBean<Notification>{


	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	NotificationService notificationService;
	

	public NotificationBean(){
		super(Notification.class);
	}
	
	@Override
	protected IPersistenceService<Notification> getPersistenceService() {
		return notificationService;
	}

	protected String getDefaultViewName() {
		return "notifications";
	}

	@Override
	protected String getListViewName() {
		return "notifications";
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
