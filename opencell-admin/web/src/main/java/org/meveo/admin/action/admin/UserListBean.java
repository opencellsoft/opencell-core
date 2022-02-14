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

import org.meveo.model.admin.User;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.index.ElasticClient;
import org.meveo.util.view.ServiceBasedLazyDataModel;
import org.meveo.util.view.LazyDataModelWSize;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import java.util.*;

@Named
@ConversationScoped
public class UserListBean extends UserBean {

	private static final long serialVersionUID = 5761298784298195322L;

	private LazyDataModel<User> filteredUsers = null;	

	public LazyDataModel<User> getFilteredLazyDataModel() {
		
		if (filteredUsers == null) {

			filteredUsers = new ServiceBasedLazyDataModel<User>() {
				private static final long serialVersionUID = 1L;
	
				@Override
				public List<User> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> loadingFilters) {
					
					String userName = filters.get("userName") != null ? (String) filters.get("userName") : null;
					List<User> entities = null;

					if (currentUser.hasRole("marketingManager")) {
						if(userName != null) {
							entities = userService.findUserByRole(userName, "marketingManager", "CUSTOMER_CARE_USER");
						}else {
							entities = userService.listUsersInMM(Arrays.asList("marketingManager", "CUSTOMER_CARE_USER"));
						}
					}else {
						if(userName != null) {
							User user = userService.findByUsernameWithFetch(userName, null);
							if(user != null) {
								entities = new ArrayList<User>();
								entities.add(user);
							}else {
								return new ArrayList<User>();
							}
						}else {
							return super.load(first, pageSize, sortField, sortOrder,  loadingFilters);



						}
					}


					setRowCount(entities.size());
	
					return entities.subList(first, (first + pageSize) > entities.size() ? entities.size() : (first + pageSize));
				}

				@Override
				protected Map<String, Object> getSearchCriteria() {
					return new HashMap<>();
				}

				@Override
				protected IPersistenceService<User> getPersistenceServiceImpl() {
					return userService;
				}

				@Override
				protected ElasticClient getElasticClientImpl() {
					return null;
				}
			};
			
		}
	
		return filteredUsers;
		
		
	}

}