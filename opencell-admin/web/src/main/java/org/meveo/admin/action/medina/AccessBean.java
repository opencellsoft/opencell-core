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
package org.meveo.admin.action.medina;

import jakarta.ejb.EJB;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.service.medina.impl.CDRParsingService;
import org.omnifaces.cdi.Param;

/**
 * Standard backing bean for {@link Access} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Named
@ViewScoped
public class AccessBean extends CustomFieldBean<Access> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link PriceCode} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private AccessService accessService;

	@Inject
	private SubscriptionService subscriptionService;

	@EJB
	private CDRParsingService cdrParsingService;

	@Inject
	@Param
	private Long subscriptionId;

	private Subscription selectedSubscription;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public AccessBean() {
		super(Access.class);
	}

	@Override
	public Access initEntity() {
		super.initEntity();

		log.debug("AccesBean initEntity id={}", entity.getId());
		if (subscriptionId != null && entity.isTransient()) {
			Subscription subscription = subscriptionService.findById(subscriptionId);
			entity.setStartDate(subscription.getSubscriptionDate());
			entity.setSubscription(subscription);
		}

		return entity;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Access> getPersistenceService() {
		return accessService;
	}

	@Override
	protected String getListViewName() {
		return "access";
	}

	public Subscription getSelectedSubscription() {
		return selectedSubscription;
	}

	public void setSelectedSubscription(Subscription selectedSubscription) {
		this.selectedSubscription = selectedSubscription;
	}

    @ActionMethod
	public String saveOrUpdateInSubscription() throws BusinessException {
		if (subscriptionId != null) {
			Subscription subscription = subscriptionService.findById(subscriptionId);
			entity.setSubscription(subscription);
		}

		saveOrUpdate(false);

		return "";
	}
	
    @Override
    @ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		Subscription subscription = subscriptionService.retrieveIfNotManaged(entity.getSubscription());
		entity.setSubscription(subscription);

		if (accessService.isDuplicateAndOverlaps(entity)) {
			messages.error(new BundleKey("messages", "access.duplicate"));
			FacesContext.getCurrentInstance().validationFailed();
			return null;
		}

		
        String outcome = super.saveOrUpdate(killConversation);
//        log.debug("outcome when save in access detail {} and conversation status {}",outcome,killConversation);

        if (outcome == null) {
            return getEditViewName(); // "/pages/medina/access/accessDetail.xhtml?edit=true&accessId=" + entity.getId() + "&faces-redirect=true";
        }
        
        return outcome;
	}
	
	public void resetEntity() {
		entity = new Access();

		if (subscriptionId != null && entity.isTransient()) {
			Subscription subscription = subscriptionService.findById(subscriptionId);
			entity.setStartDate(subscription.getSubscriptionDate());
			entity.setSubscription(subscription);
		}
	}
	
	public Long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(Long subscriptionId) {
	}

}