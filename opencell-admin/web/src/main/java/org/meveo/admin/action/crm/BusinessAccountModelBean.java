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

package org.meveo.admin.action.crm;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.admin.module.GenericModuleBean;
import org.meveo.model.crm.BusinessAccountModel;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.BusinessAccountModelService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class BusinessAccountModelBean extends GenericModuleBean<BusinessAccountModel> {

    private static final long serialVersionUID = -3508425903046756219L;

    @Inject
    private BusinessAccountModelService businessAccountModelService;

    public BusinessAccountModelBean() {
        super(BusinessAccountModel.class);
    }

    @Override
    protected IPersistenceService<BusinessAccountModel> getPersistenceService() {
        return businessAccountModelService;
    }
}