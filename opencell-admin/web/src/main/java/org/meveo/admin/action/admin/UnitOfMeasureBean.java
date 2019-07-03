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
package org.meveo.admin.action.admin;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.UnitOfMeasure;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.UnitOfMeasureService;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.sql.BatchUpdateException;

/**
 * @author Mounir Bahije
 */

@Named
@ViewScoped
public class UnitOfMeasureBean extends BaseBean<UnitOfMeasure> {

    private static final long serialVersionUID = -2634473401391113093L;

    /**
     * Injected @{link UnitOfMeasure} service. Extends {@link PersistenceService}.
     */
    @Inject
    private UnitOfMeasureService unitOfMeasureService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public UnitOfMeasureBean() {
        super(UnitOfMeasure.class);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<UnitOfMeasure> getPersistenceService() {
        return unitOfMeasureService;
    }

    public void test() throws BatchUpdateException {
        throw new BatchUpdateException();
    }

}