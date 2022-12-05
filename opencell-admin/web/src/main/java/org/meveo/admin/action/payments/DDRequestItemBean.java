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

import java.util.HashMap;
import java.util.Map;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.payments.DDRequestItem;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.DDRequestItemService;

/**
 * Standard backing bean for {@link DDRequestItem} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 */
@Named
@ViewScoped
public class DDRequestItemBean extends BaseBean<DDRequestItem> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link DDRequestItem} service. Extends {@link PersistenceService} .
     */
    @Inject
    private DDRequestItemService ddRequestItemService;


    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public DDRequestItemBean() {
        super(DDRequestItem.class);
    }

   
    
    

    @Override
    protected IPersistenceService<DDRequestItem> getPersistenceService() {
        return ddRequestItemService;
    }

    public PaginationDataModel<DDRequestItem> getItemsByLot(DDRequestLOT ddRequestLOT) {
        PaginationDataModel<DDRequestItem> items = new PaginationDataModel<DDRequestItem>(ddRequestItemService);
        Map<String, Object> filters2 = new HashMap<String, Object>();
        filters2.put("ddRequestLOT", ddRequestLOT);
        items.addFilters(filters2);
        items.addFetchFields(getListFieldsToFetch());
        items.forceRefresh();
        return items;
    }

}