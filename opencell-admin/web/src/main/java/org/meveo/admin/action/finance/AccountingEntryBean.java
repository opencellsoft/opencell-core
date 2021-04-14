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
package org.meveo.admin.action.finance;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.finance.AccountingEntry;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.finance.AccountingWritingService;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Standard backing bean for {@link AccountingEntry} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 *
 * @author mboukayoua
 */
@Named
@ViewScoped
public class AccountingEntryBean extends BaseBean<AccountingEntry> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link {@link AccountingEntry} service. Extends {@link PersistenceService}
     */
    @Inject
    private AccountingWritingService accountingEntryService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public AccountingEntryBean() {
        super(AccountingEntry.class);
    }

    /**
     * Method that returns concrete PersistenceService for an entity class backing bean is bound to.
     * That service is then used for operations on concrete entities (eg. save, delete
     * etc).
     *
     * @return Persistence service
     */
    @Override
    protected IPersistenceService<AccountingEntry> getPersistenceService() {
        return accountingEntryService;
    }
}
