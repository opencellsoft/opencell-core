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
	
	public void exportToFile() throws Exception {

		CsvBuilder csv = new CsvBuilder();
		csv.appendValue("Update date");
		csv.appendValue("NotificationType");
		csv.appendValue("Notification");
		csv.appendValue("Retry");
		csv.appendValue("Status");
		csv.appendValue("Result");
		csv.appendValue("Entity code");
		csv.appendValue("Serialized entity");
		csv.startNewLine();
		for (NotificationHistory notificationHistory : notificationHistoryService
				.list()) {
			csv.appendValue(DateUtils.formatDateWithPattern(notificationHistory
					.getAuditable().getUpdated(), "dd/MM/yyyy"));
			csv.appendValue(notificationHistory.getInboundRequest() != null ? notificationHistory
					.getInboundRequest().getCode() : notificationHistory
					.getEntityClassName());
			csv.appendValue(notificationHistory.getNotification().getCode());
			csv.appendValue(notificationHistory.getNbRetry() + "");
			csv.appendValue(notificationHistory.getStatus() + "");
			csv.appendValue(notificationHistory.getResult());
			csv.appendValue(notificationHistory.getEntityCode());
			csv.appendValue(notificationHistory.getSerializedEntity());
			csv.startNewLine();
		}
		InputStream inputStream = new ByteArrayInputStream(csv.toString()
				.getBytes());
		csv.download(inputStream, "NotificationHistories.csv");
	}





}
