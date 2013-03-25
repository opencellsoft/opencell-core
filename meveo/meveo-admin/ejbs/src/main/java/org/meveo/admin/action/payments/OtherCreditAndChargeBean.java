/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.payments;

import java.util.Date;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.OtherCreditAndChargeService;
import org.meveo.service.payments.local.CustomerAccountServiceLocal;

/**
 * Standard backing bean for {@link OtherCreditAndCharge} (extends
 * {@link BaseBean} that provides almost all common methods to handle entities
 * filtering/sorting in datatable, their create, edit, view, delete operations).
 * It works with Manaty custom JSF components.
 * 
 * @author Ignas
 * @created 2009.10.13
 */
@Named
@ConversationScoped
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
	private CustomerAccountServiceLocal customerAccountService;

	/**
	 * Injected @{link OCCTemplateService} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private OCCTemplateService occTemplateService;

	@Inject
	@CurrentProvider
	private Provider currentProvider;
	
	@Inject
	ParamBean paramBean;

//	TODO - what to inject @Inject SyntheseClientBean.synCustomerAccount or CustomerAccountBean.init()
	private CustomerAccount customerAccount;

	/**
	 * Customer Id passed as a parameter. Used when creating new
	 * OtherCreditAndCharge from otherCreditAndCharge window, so default
	 * customerAccount will be set on newly created OtherCreditAndCharge.
	 */
	@Inject
	@RequestParam
	private Instance<Long> customerAccountId;

	private Long customerAccountIdToSet;

	private OCCTemplate occTemplate;

	private String initType = null;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public OtherCreditAndChargeBean() {
		super(OtherCreditAndCharge.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Produces
	@Named("otherCreditAndCharge")
	public OtherCreditAndCharge init() {

		// Initialize a new one from ID or empty

		if (initType == null) {
			initEntity();

			// Either create a new entity from a user selected template
		} else if (initType.equals("loadFromTemplate")) {
			copyFromTemplate(getOccTemplate(), null);
			return entity;

			// Create a new entity from a rejectPayment template
		} else if (initType.equals("loadFromTemplateRejectPayment")) {
            String occTemplateRejectPaymentCode = paramBean.getProperty("occ.templateRejectPaymentCode");
			OCCTemplate occ = occTemplateService.findByCode(occTemplateRejectPaymentCode,
					currentProvider.getCode());
			customerAccountIdToSet = customerAccountId.get();
			copyFromTemplate(occ, customerAccountId.get());

			// Create a new entity from a paymentCheck template
		} else if (initType.equals("loadFromTemplatePaymentCheck")) {
            String occTemplatePaymentCode = paramBean.getProperty("occ.templatePaymentCheckCode");
			OCCTemplate occ = occTemplateService.findByCode(occTemplatePaymentCode,
					currentProvider.getCode());
			customerAccountIdToSet = customerAccountId.get();
			copyFromTemplate(occ, customerAccountId.get());

		}
		return entity;
	}

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * 
	 * @return
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	@Produces
	@Named("otherCreditAndCharges")
	@ConversationScoped
	public PaginationDataModel<OtherCreditAndCharge> list() {
		return super.list();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	public String saveOrUpdate() {
		entity.setUnMatchingAmount(entity.getAmount());
		entity.getCustomerAccount().getAccountOperations().add(entity);
		return saveOrUpdate();
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
		return "/pages/payments/customerAccounts/customerAccountDetail.xhtml?objectId="
				+ (customerAccountIdToSet != null ? customerAccountIdToSet
						: (customerAccount != null ? customerAccount.getId() : null))
				+ "&edit=false&tab=ops";
	}

	/**
	 * 
	 * @param customerAccountId
	 * @return
	 */
	public String loadFromTemplatePaymentCheck(Long customerAccountId) {
		initType = "loadFromTemplatePaymentCheck";
		return "/pages/payments/accountOperations/accountOperationDetail.xhtml?edit=true";
	}

	/**
	 * @param customerAccountId
	 * @return
	 */
	public String loadFromTemplateRejectPayment(Long customerAccountId) {
		initType = "loadFromTemplateRejectPayment";
		return "/pages/payments/accountOperations/accountOperationDetail.xhtml?edit=true";

	}

	/**
	 * @param occ
	 * @param customerAccountId
	 */
	private void copyFromTemplate(OCCTemplate occ, Long customerAccountId) {
		entity = new OtherCreditAndCharge();
		if (customerAccountId != null) {
			entity.setCustomerAccount(customerAccountService.findById(customerAccountId));
		} else {
			entity.setCustomerAccount(customerAccount);
		}
		entity.setOccCode(occ.getCode());
		entity.setOccDescription(occ.getDescription());
		entity.setAccountCode(occ.getAccountCode());
		entity.setTransactionCategory(occ.getOccCategory());
		entity.setAccountCodeClientSide(occ.getAccountCodeClientSide());
		entity.setMatchingStatus(MatchingStatusEnum.O);
		entity.setDueDate(new Date());
		entity.setTransactionDate(new Date());
	}

	public String loadFromTemplate() {
		initType = "loadFromTemplate";
		return "/pages/payments/accountOperations/accountOperationDetail.xhtml?edit=true";
	}

	public void setOccTemplate(OCCTemplate occTemplate) {
		this.occTemplate = occTemplate;
	}

	public OCCTemplate getOccTemplate() {
		return occTemplate;
	}
}