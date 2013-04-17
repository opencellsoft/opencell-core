/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.billing;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.billing.WalletInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.WalletService;

/**
 * Standard backing bean for {@link WalletInstance} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create, edit,
 * view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 */
@Named
@ConversationScoped
public class WalletBean extends BaseBean<WalletInstance> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link WalletInstance} service. Extends {@link PersistenceService}. */
    @Inject
    private WalletService walletService;

    /**
     * Customer account Id passed as a parameter. Used when creating new WalletInstance from customer account window, so default customer account will be set on newly created wallet.
     */
    @Inject
    @RequestParam
    private Instance<Long> customerAccountId;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public WalletBean() {
        super(WalletInstance.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public WalletInstance initEntity() {
        super.initEntity();
        if (customerAccountId != null) {
            // wallet.setCustomerAccount(customerAccountService.findById(customerAccountId));
        }
        return entity;
    }


    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<WalletInstance> getPersistenceService() {
        return walletService;
    }

}
