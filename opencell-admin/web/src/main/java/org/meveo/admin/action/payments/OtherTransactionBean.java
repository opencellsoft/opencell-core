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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

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