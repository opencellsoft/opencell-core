package org.meveo.admin.action.notification;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.CsvBuilder;
import org.meveo.model.notification.EmailNotification;
import org.meveo.model.notification.InstantMessagingNotification;
import org.meveo.model.notification.Notification;
import org.meveo.model.notification.NotificationHistory;
import org.meveo.model.notification.WebHook;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.NotificationHistoryService;
import org.meveo.service.notification.NotificationService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link NotificationHistory} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class NotificationHistoryBean extends BaseBean<NotificationHistory> {

	private static final long serialVersionUID = -6762628879784107169L;

	@Inject
	private NotificationHistoryService notificationHistoryService;

	@Inject
	private transient ResourceBundle resourceMessages;

	@Inject
	private NotificationService notificationService;
	


	public NotificationHistoryBean() {
		super(NotificationHistory.class);
	}

	@Override
	protected IPersistenceService<NotificationHistory> getPersistenceService() {
		return notificationHistoryService;
	}

	public Map<String, String> getNotificationTypes() {
		Map<String, String> types = new HashMap<String, String>();

		types.put(WebHook.class.getName(),
				resourceMessages.getString("entity.notification.notificationType." + WebHook.class.getName()));
		types.put(EmailNotification.class.getName(),
				resourceMessages.getString("entity.notification.notificationType." + EmailNotification.class.getName()));
		types.put(
				InstantMessagingNotification.class.getName(),
				resourceMessages.getString("entity.notification.notificationType."
						+ InstantMessagingNotification.class.getName()));

		return types;
	}

	/**
	 * DataModel for primefaces lazy loading datatable component.
	 * 
	 * @return LazyDataModel implementation.
	 */
	public LazyDataModel<NotificationHistory> getLazyDataModel(Long notificationId) {
		Notification notification = notificationService.findById(notificationId);
		filters.put("notification", notification);

		return getLazyDataModel(filters, false);
	}


}
