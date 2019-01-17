/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.payments;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.PaymentVentilation;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.OtherTransactionGeneralService;
import org.meveo.service.payments.impl.PaymentVentilationService;
import org.omnifaces.cdi.Param;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link PaymentVentilation} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class PaymentVentilationBean extends BaseBean<PaymentVentilation> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link MatchingAmount} service. Extends {@link PersistenceService}
     */
    @Inject
    private PaymentVentilationService paymentVentilationService;

    @Inject
    private OtherTransactionGeneralService otherTransactionGeneralService;

    @Inject
    @Param
    private Long otgId;

    @Inject
    @Param
    private Long backEntityId;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public PaymentVentilationBean() {
        super(PaymentVentilation.class);
    }

    @PostConstruct
    public void init() {
        if (otgId != null) {
            getEntity().setOriginalOT(otherTransactionGeneralService.findById(otgId));
        }
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<PaymentVentilation> getPersistenceService() {
        return paymentVentilationService;
    }

    @ActionMethod
    public String unventilate(PaymentVentilation paymentVentilation) {
        AccountOperation ao = entity.getAccountOperation();

        if (ao != null && ao.getMatchingStatus() != MatchingStatusEnum.O) {
            log.error("Unauthorized unventilation!");
            messages.error(new BundleKey("messages", "paymentVentilation.unauthorized"));
        } else {
            try {
                paymentVentilationService.unventilatePayment(paymentVentilation);
                messages.info(new BundleKey("messages", "update.successful"));
            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
                messages.error(new BundleKey("messages", "error.unexpected"));
            }
        }
        
        return "/pages/payments/otherTransactions/ventilateTransaction.xhtml?otgId=" + paymentVentilation.getOriginalOT().getId()
                + "&edit=true&backView=backToSellerFromOT&backEntityId=" + backEntityId + "&faces-redirect=true";
    }

    public String ventilate() throws BusinessException {
        BigDecimal ventilationAmout = entity.getVentilationAmount();
        BigDecimal unventilatedAmount = entity.getOriginalOT().getUnMatchingAmount();
        if (ventilationAmout.compareTo(BigDecimal.ZERO) <= 0 || unventilatedAmount.compareTo(ventilationAmout) < 0) {
            log.error("Ventilation amount is not valid");
            messages.error(new BundleKey("messages", "paymentVentilation.invalidAmount"));
        } else {
            try {
                paymentVentilationService.ventilatePayment(entity);
                messages.info(new BundleKey("messages", "save.successful"));
            } catch (BusinessException e) {
                log.error(e.getMessage(), e);
                messages.error(new BundleKey("messages", "error.unexpected"));
            }
        }
        
        return "/pages/payments/otherTransactions/ventilateTransaction.xhtml?otgId=" + entity.getOriginalOT().getId() + "&edit=true&backView=backToSellerFromOT&backEntityId="
                + backEntityId + "&faces-redirect=true";

    }

    public LazyDataModel<PaymentVentilation> getPaymentVentilations() throws BusinessException {
        if (entity != null && entity.getOriginalOT() != null && !entity.getOriginalOT().isTransient()) {
            filters.put("originalOT", entity.getOriginalOT());
            return getLazyDataModel();
        } else {
            return null;
        }
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("originalOT");
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("originalOT");
    }
}