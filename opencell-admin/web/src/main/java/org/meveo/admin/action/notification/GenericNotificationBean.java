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

package org.meveo.admin.action.notification;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

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
