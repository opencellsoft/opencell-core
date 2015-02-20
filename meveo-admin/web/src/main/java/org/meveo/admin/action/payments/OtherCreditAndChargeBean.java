/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.payments;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.OtherCreditAndChargeService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link OtherCreditAndCharge} (extends
 * {@link BaseBean} that provides almost all common methods to handle entities
 * filtering/sorting in datatable, their create, edit, view, delete operations).
 * It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class OtherCreditAndChargeBean extends BaseBean<OtherCreditAndCharge> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link OtherCreditAndCharge} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private OtherCreditAndChargeService otherCreditAndChargeService;

	/**
	 * Injected @{link OCustomerAccountService} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private CustomerAccountService customerAccountService;

	/**
	 * Injected @{link OCCTemplateService} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private OCCTemplateService occTemplateService;

	private ParamBean paramBean = ParamBean.getInstance();

	private CustomerAccount customerAccount;

	private OCCTemplate occTemplate;

	/**
	 * CustomerAccoiunt Id passed as a parameter.
	 */
	@Inject
	@RequestParam
	private Instance<Long> customerAccountId;

	/**
	 * OCCTemplate Id passed as a parameter.
	 */
	@Inject
	@RequestParam
	private Instance<Long> occTemplateId;

	@Inject
	@RequestParam
	private Instance<String> initType = null;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public OtherCreditAndChargeBean() {
		super(OtherCreditAndCharge.class);
	}

	@PostConstruct
	public void init() {

		if (customerAccountId != null && customerAccountId.get() != null) {
			customerAccount = customerAccountService.findById(customerAccountId
					.get());
		}
		if (occTemplateId != null && occTemplateId.get() != null) {
			occTemplate = occTemplateService.findById(occTemplateId.get());
		}
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public OtherCreditAndCharge initEntity() {

		// Initialize a new one from ID or empty
		if (initType == null || initType.get() == null) {
			super.initEntity();

			// Either create a new entity from a user selected template
		} else if ("loadFromTemplate".equals(initType.get())) {
			if (occTemplateId != null && occTemplateId.get() != null) {
				copyFromTemplate(occTemplateService.findById(occTemplateId
						.get()));

			}
			return entity;

			// Create a new entity from a rejectPayment template
		} else if ("loadFromTemplateRejectPayment".equals(initType.get())) {
			String occTemplateRejectPaymentCode = paramBean.getProperty(
					"occ.templateRejectPaymentCode", "IP_PLVT");
			OCCTemplate occ = occTemplateService.findByCode(
					occTemplateRejectPaymentCode, getCurrentProvider()
							.getCode());
			copyFromTemplate(occ);

			// Create a new entity from a paymentCheck template
		} else if ("loadFromTemplatePaymentCheck".equals(initType.get())) {
			String occTemplatePaymentCode = paramBean.getProperty(
					"occ.templatePaymentCheckCode", "RG_CHQ");
			OCCTemplate occ = occTemplateService.findByCode(
					occTemplatePaymentCode, getCurrentProvider().getCode());
			copyFromTemplate(occ);

		}
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation)
			throws BusinessException {
		entity.setUnMatchingAmount(entity.getAmount());
		entity.getCustomerAccount().getAccountOperations().add(entity);

		return super.saveOrUpdate(killConversation);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<OtherCreditAndCharge> getPersistenceService() {
		return otherCreditAndChargeService;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#back()
	 */
	@Override
	public String back() {
		return "customerAccountDetail";
	}

	/**
	 * 
	 * @param customerAccountId
	 * @return
	 */
	public String loadFromTemplatePaymentCheck(Long customerAccountId) {
		return "/pages/payments/accountOperations/accountOperationDetail.xhtml?initType=loadFromTemplatePaymentCheck"
				+ "&edit=true&faces-redirect=true&includeViewParams=true";
	}

	/**
	 * @param customerAccountId
	 * @return
	 */
	public String loadFromTemplateRejectPayment(Long customerAccountId) {
		return "/pages/payments/accountOperations/accountOperationDetail.xhtml?initType=loadFromTemplateRejectPayment"
				+ "&edit=true&faces-redirect=true&includeViewParams=true";

	}

	/**
	 * @param occ
	 * @param customerAccountId
	 */
	private void copyFromTemplate(OCCTemplate occ) {
		entity = new OtherCreditAndCharge();
		entity.setCustomerAccount(customerAccount);
		if (occ != null) {
			entity.setOccCode(occ.getCode());
			entity.setOccDescription(occ.getDescription());
			entity.setAccountCode(occ.getAccountCode());
			entity.setTransactionCategory(occ.getOccCategory());
			entity.setAccountCodeClientSide(occ.getAccountCodeClientSide());
		}

		entity.setMatchingStatus(MatchingStatusEnum.O);
		entity.setDueDate(new Date());
		entity.setTransactionDate(new Date());
	}

	public String loadFromTemplate() {
		return "/pages/payments/accountOperations/accountOperationDetail.xhtml?initType=loadFromTemplate"
				+ "&edit=true&faces-redirect=true&includeViewParams=true&occTemplateId="
				+ occTemplate.getId();
	}

	public void setOccTemplate(OCCTemplate occTemplate) {
		this.occTemplate = occTemplate;
	}

	public OCCTemplate getOccTemplate() {
		return occTemplate;
	}

	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}
}