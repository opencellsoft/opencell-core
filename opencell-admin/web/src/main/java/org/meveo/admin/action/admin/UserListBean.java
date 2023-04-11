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
import org.meveo.model.admin.User;
import org.meveo.util.view.LazyDataModelWSize;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@Named

public class UserListBean extends UserBean {

    private static final long serialVersionUID = 5761298784298195322L;

    private LazyDataModel<User> filteredUsers = null;

    public LazyDataModel<User> getFilteredLazyDataModel() {

        if (filteredUsers == null) {

            filteredUsers = new LazyDataModelWSize<User>() {

                private static final long serialVersionUID = 1L;

                @Override
                @SuppressWarnings("rawtypes")
                public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map mapfilters) {
                    filters.put("firstName", filters.get("name.firstName"));
                    filters.put("lastName", filters.get("name.lastName"));

                    PaginationConfiguration paginationConfig = new PaginationConfiguration(first, pageSize, filters, null, null);
                    setRowCount(Long.valueOf(userService.count(paginationConfig)).intValue());

                    if (getRowCount() > 0) {
                        return userService.list(paginationConfig);
                    } else {
                        return new ArrayList<User>();
                    }

                    // TODO What to do with this logic in KC??
//                    if (currentUser.hasRole("marketingManager")) {
//                      if (userName != null) {
//                          entities = userService.findUserByRole(userName, "marketingManager", "CUSTOMER_CARE_USER");
//                      } else {
//                          entities = userService.listUsersInMM(Arrays.asList("marketingManager", "CUSTOMER_CARE_USER"));
//                      }
//                  }
                }
            };
        }

        return filteredUsers;
    }
}