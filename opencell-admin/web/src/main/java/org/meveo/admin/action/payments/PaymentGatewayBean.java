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
package org.meveo.admin.action.payments;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentGatewayRumSequence;
import org.meveo.model.payments.PaymentGatewayTypeEnum;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.PaymentGatewayRumSequenceService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.util.PaymentGatewayClass;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Standard backing bean for {@link PaymentGateway} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class PaymentGatewayBean extends CustomFieldBean<PaymentGateway> {
    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link PaymentGateway} service. Extends {@link PersistenceService}.
     */
    @Inject
    private PaymentGatewayService paymentGatewayService;

    @Inject
    private PaymentGatewayRumSequenceService paymentGatewayRumSequenceService;

    private PaymentGatewayRumSequence selectedRumSequence;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public PaymentGatewayBean() {
        super(PaymentGateway.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     *
     * @return payment gateway.
     */
    @Override
    public PaymentGateway initEntity() {
        super.initEntity();
        if (entity.getId() == null) {
            entity.setType(PaymentGatewayTypeEnum.CUSTOM);
        }
        return entity;
    }

    @Override
    public void search() {
        getFilters();
        if (!filters.containsKey("disabled")) {
            filters.put("disabled", false);
        }
        super.search();
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return getBackViewSave();
        }
        return null;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<PaymentGateway> getPersistenceService() {
        return paymentGatewayService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    public List<PaymentMethodEnum> getAllowedPaymentMethods() {
        return appProvider.getPaymentMethods();
    }

    /**
     * Autocomplete method for implementationClassName filter field - search classes with @PaymentGatewayClass annotation
     *
     * @param query A partial class name (including a package)
     * @return A list of classnames
     */
    @SuppressWarnings({"rawtypes"})
    public List<String> autocompleteClassNames(String query) {

        List<Class> classes;
        try {
            classes = ReflectionUtils.getClasses("org.meveo.service.payments");
        } catch (Exception e) {
            log.error("Failed to get a list of classes for service payments  package", e);
            return new ArrayList<>();
        }

        String queryLc = query.toLowerCase();
        List<String> classNames = new ArrayList<>();
        for (Class clazz : classes) {
            if (clazz.getName().toLowerCase().contains(queryLc) && (clazz.isAnnotationPresent(PaymentGatewayClass.class))) {
                classNames.add(clazz.getName());
            }
        }

        Collections.sort(classNames);
        return classNames;
    }

    public void newRumSequence() {
        selectedRumSequence = new PaymentGatewayRumSequence();
        selectedRumSequence.setPaymentGateway(entity);
    }

    public void selectRumSequence(PaymentGatewayRumSequence rumSequence) {
        this.selectedRumSequence = rumSequence;
    }

    public void deleteRumSequence(PaymentGatewayRumSequence rumSequence) throws BusinessException {
        paymentGatewayRumSequenceService.remove(rumSequence);
        entity = paymentGatewayService.refreshOrRetrieve(entity);
    }

    public void resetRumSequence() {
        selectedRumSequence = null;
    }

    public void saveOrUpdateRumSequence() throws BusinessException {
        if (selectedRumSequence.isTransient()) {
            paymentGatewayRumSequenceService.create(selectedRumSequence);
        } else {
            paymentGatewayRumSequenceService.update(selectedRumSequence);
        }

        entity = paymentGatewayService.refreshOrRetrieve(entity);
        resetRumSequence();
    }

    public PaymentGatewayRumSequence getSelectedRumSequence() {
        return selectedRumSequence;
    }

    public void setSelectedRumSequence(PaymentGatewayRumSequence selectedRumSequence) {
        this.selectedRumSequence = selectedRumSequence;
    }

    public List<PaymentGatewayRumSequence> getSingleListRumSequences() {
        return entity.getRumSequence() == null ? new ArrayList<>() : Collections.singletonList(entity.getRumSequence());
    }
}
