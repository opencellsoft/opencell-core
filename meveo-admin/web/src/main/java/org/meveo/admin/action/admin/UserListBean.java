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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.meveo.model.admin.User;
import org.meveo.util.view.LazyDataModelWSize;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@Named
@ConversationScoped
public class UserListBean extends UserBean {

	private static final long serialVersionUID = 5761298784298195322L;

	private LazyDataModel<User> filteredUsers = null;	

	public LazyDataModel<User> getFilteredLazyDataModel() {
		if (currentUser.hasRole("marketingManager")) {
			if (filteredUsers != null) {
				return filteredUsers;
			}

			filteredUsers = new LazyDataModelWSize<User>() {
				private static final long serialVersionUID = 1L;

				@Override
				public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> loadingFilters) {

					List<User> entities = null;
					entities = userService.listUsersInMM(Arrays.asList("marketingManager", "CUSTOMER_CARE_USER"));
					setRowCount(entities.size());

					return entities.subList(first, (first + pageSize) > entities.size() ? entities.size() : (first + pageSize));
				}
			};

			return filteredUsers;
		}

		return super.getLazyDataModel();
	}

}