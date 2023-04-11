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
package org.meveo.admin.action.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.security.Role;
import org.meveo.util.view.LazyDataModelWSize;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@Named

public class UserRoleListBean extends UserRoleBean {

    private static final long serialVersionUID = 3202016025277911165L;

    private LazyDataModel<Role> filteredRoles = null;

    public LazyDataModel<Role> getFilteredLazyDataModel() {

        if (filteredRoles == null) {

            filteredRoles = new LazyDataModelWSize<Role>() {

                private static final long serialVersionUID = 1L;

                @Override
                @SuppressWarnings("rawtypes")
                public List<Role> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map mapfilters) {

                    PaginationConfiguration paginationConfig = new PaginationConfiguration(first, pageSize, filters, null, null);
                    List<Role> roles = userRoleService.list(paginationConfig);
                    setRowCount(roles.size());

                    if (getRowCount() > 0) {

                        return roles.subList(first, roles.size() > first + pageSize ? first + pageSize : roles.size());
                    } else {
                        return new ArrayList<Role>();
                    }
                }
            };
        }

        return filteredRoles;
    }
}