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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jakarta.enterprise.inject.Produces;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.admin.Seller;
import org.meveo.model.payments.OtherTransaction;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.OtherTransactionService;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link OtherTransaction} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author hznibar
 * @lastModifiedVersion 5.4.0
 */
@Named
@ViewScoped
public class OtherTransactionBean extends CustomFieldBean<OtherTransaction> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link OtherTransaction} service. Extends {@link PersistenceService}.
     */
    @Inject
    private OtherTransactionService otherTransactionService;

    @Inject
    protected MatchingCodeService matchingCodeService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public OtherTransactionBean() {
        super(OtherTransaction.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return account operation
     */
    @Produces
    @Named("otherTransaction")
    public OtherTransaction init() {
        return initEntity();
    }

    @Override
    protected IPersistenceService<OtherTransaction> getPersistenceService() {
        return otherTransactionService;
    }

    public String getDate() {
        return (new Date()).toString();
    }
    
    public LazyDataModel<OtherTransaction> getOtherTransactions(Seller seller) {
        if (!seller.isTransient() && seller.getGeneralLedger() != null && !seller.getGeneralLedger().isTransient()) {
            filters.put("generalLedger", seller.getGeneralLedger());
            return getLazyDataModel();
        } else {
            return null;
        }
    }

    @Override
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("generalLedger");
    }

    @Override
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("generalLedger");
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        super.saveOrUpdate(killConversation);
        return null;
    }
}