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
package org.meveo.admin.action.quote;

import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.order.OrderListBean;
import org.meveo.model.admin.User;
import org.meveo.service.admin.impl.UserService;

@Named
@ConversationScoped
public class QuoteListBean extends QuoteBean {

    private static final long serialVersionUID = 1954649215739728918L;

    @Inject
    private UserService userService;
    
    private boolean showMyQuotesOnly = true;

    public boolean isShowMyQuotesOnly() {
        return showMyQuotesOnly;
    }

    public void setShowMyQuotesOnly(boolean showMyQuotesOnly) {
        this.showMyQuotesOnly = showMyQuotesOnly;
    }

    /**
     * Add additional criteria for searching by my orders for administrationManagement only
     */
    @Override
    protected Map<String, Object> supplementSearchCriteria(Map<String, Object> searchCriteria) {

        boolean isAdmin = currentUser.hasRole("administrationManagement");
        if (isAdmin && showMyQuotesOnly) {
            User user = userService.findByUsername(currentUser.getSubject());
            searchCriteria.put(OrderListBean.SEARCH_USER_GROUP, user.getUserLevel());
        }

        return searchCriteria;
    }
}