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

package org.meveo.admin.action.tax;

import java.io.Serializable;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.DatePeriod;
import org.meveo.model.tax.TaxMapping;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.tax.TaxMappingService;

/**
 * Back bean implementation for GUI details page for Tax mapping
 * 
 * @author Andrius Karpavicius
 *
 */
@Named
@ViewScoped
public class TaxMappingBean extends BaseBean<TaxMapping> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link TaxMapping} service. Extends {@link PersistenceService}.
     */
    @Inject
    private TaxMappingService entityService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public TaxMappingBean() {
        super(TaxMapping.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<TaxMapping> getPersistenceService() {
        return entityService;
    }

    @Override
    public TaxMapping initEntity() {

        super.initEntity();
        if (entity.getValid() == null) {
            entity.setValid(new DatePeriod());
        }
        return entity;
    }
}