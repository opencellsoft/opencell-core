package org.meveo.admin.action.notification;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.UpdateMapTypeFieldBean;
import org.meveo.model.notification.Notification;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.notification.GenericNotificationService;

/**
 * @author Tyshan Shi
 *
 **/
@Named
@ViewScoped
public class GenericNotificationBean extends UpdateMapTypeFieldBean<Notification> {

	private static final long serialVersionUID = 1L;
	@Inject
	private GenericNotificationService genericNotificationService;
	

	public GenericNotificationBean() {
		super(Notification.class);
	}
	
	@Override
	protected IPersistenceService<Notification> getPersistenceService() {
		return genericNotificationService;
	}
}
