package org.meveo.admin.action.notification;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.InstantMessagingNotificationService;

@Named
@ConversationScoped
public class InstantMessagingNotificationBean extends BaseBean<InstantMessagingNotification>{


	private static final long serialVersionUID = 6473465285480945644L;

	@Inject
	InstantMessagingNotificationService imNotificationService;
	

	public InstantMessagingNotificationBean(){
		super(InstantMessagingNotification.class);
	}
	
	@Override
	protected IPersistenceService<InstantMessagingNotification> getPersistenceService() {
		return imNotificationService;
	}

	protected String getDefaultViewName() {
		return "instantMessagingNotifications";
	}

	@Override
	protected String getListViewName() {
		return "instantMessagingNotifications";
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
