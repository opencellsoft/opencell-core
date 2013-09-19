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

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
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

/**
 * Standard backing bean for {@link OtherCreditAndCharge} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Ignas
 * @created 2009.10.13
 */
@Named
@ViewScoped
public class OtherCreditAndChargeBean extends BaseBean<OtherCreditAndCharge> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link OtherCreditAndCharge} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OtherCreditAndChargeService otherCreditAndChargeService;

    /**
     * Injected @{link OCustomerAccountService} service. Extends {@link PersistenceService}.
     */
    @Inject
    private CustomerAccountService customerAccountService;

    /**
     * Injected @{link OCCTemplateService} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OCCTemplateService occTemplateService;

    @Inject
    ParamBean paramBean;

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
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public OtherCreditAndChargeBean() {
        super(OtherCreditAndCharge.class);
    }

    @PostConstruct
    public void init() {

        if (customerAccountId != null && customerAccountId.get() != null) {
            customerAccount = customerAccountService.findById(customerAccountId.get());
        }
        if (occTemplateId != null && occTemplateId.get() != null) {
            occTemplate = occTemplateService.findById(occTemplateId.get());
        }
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
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
            copyFromTemplate(getOccTemplate());
            return entity;

            // Create a new entity from a rejectPayment template
        } else if ("loadFromTemplateRejectPayment".equals(initType.get())) {
            String occTemplateRejectPaymentCode = paramBean.getProperty("occ.templateRejectPaymentCode");
            OCCTemplate occ = occTemplateService.findByCode(occTemplateRejectPaymentCode, getCurrentProvider().getCode());
            copyFromTemplate(occ);

            // Create a new entity from a paymentCheck template
        } else if ("loadFromTemplatePaymentCheck".equals(initType.get())) {
            String occTemplatePaymentCode = paramBean.getProperty("occ.templatePaymentCheckCode");
            OCCTemplate occ = occTemplateService.findByCode(occTemplatePaymentCode, getCurrentProvider().getCode());
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
    public String saveOrUpdate(boolean killConversation) {
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
        return "/pages/payments/accountOperations/accountOperationDetail.xhtml?initType=loadFromTemplate"
                + "&edit=true&faces-redirect=true&includeViewParams=true";
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