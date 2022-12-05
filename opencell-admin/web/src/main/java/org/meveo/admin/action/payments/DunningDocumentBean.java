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
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.dunning.DunningDocument;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.wf.WorkflowHistory;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.generic.wf.WorkflowInstanceHistoryService;
import org.meveo.service.generic.wf.WorkflowInstanceService;
import org.meveo.service.payments.impl.DunningDocumentService;
import org.primefaces.model.LazyDataModel;

import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Standard backing bean for {@link DunningDocument} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 *
 * @author mboukayoua
 */
@Named
@ViewScoped
public class DunningDocumentBean extends BaseBean<DunningDocument> {
	
	/**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public DunningDocumentBean() {
        super(DunningDocument.class);
    }

    /**
     * Injected @{link DunningDocument} service. Extends {@link PersistenceService}
     */
    @Inject
    private DunningDocumentService dunningDocumentService;
    
    /**
     * Method that returns concrete PersistenceService for an entity class backing bean is bound to. That service is then used for operations on concrete entities (eg. save, delete
     * etc).
     *
     * @return Persistence service
     */
    @Override
    protected IPersistenceService<DunningDocument> getPersistenceService() {
        return dunningDocumentService;
    }

    /**
     * get CA's associated dunning documents
     * @param ca
     * @return CA's dunning docs
     */
    public LazyDataModel<DunningDocument> getDunningDocuments(CustomerAccount ca) {
        if (!ca.isTransient()) {
            filters.put("customerAccount", ca);
            return getLazyDataModel();
        } else {
            return null;
        }
    }    
}
