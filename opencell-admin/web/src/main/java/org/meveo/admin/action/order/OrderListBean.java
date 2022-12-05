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
package org.meveo.admin.action.order;

import java.util.Map;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Named;

@Named
@ConversationScoped
public class OrderListBean extends OrderBean {

    private static final long serialVersionUID = 6301829745333803753L;
    public static final String SEARCH_USER_GROUP = "routedToUserGroup";
    
    private boolean showMyOrdersOnly = true;

    public boolean isShowMyOrdersOnly() {
        return showMyOrdersOnly;
    }

    public void setShowMyOrdersOnly(boolean showMyOrdersOnly) {
        this.showMyOrdersOnly = showMyOrdersOnly;
    }

    /**
     * Add additional criteria for searching by my orders for administrationManagement only
     */
    @Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        boolean isAdmin = currentUser.hasRoles("administrationVisualization", "administrationManagement");
        if (isAdmin && showMyOrdersOnly) {
            searchCriteria.put(SEARCH_USER_GROUP, currentUser.getUserGroup());
        }

        return searchCriteria;
    }
}