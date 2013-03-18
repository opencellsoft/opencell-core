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

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.billing.Wallet;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.local.WalletServiceLocal;

/**
 * Standard backing bean for {@link Wallet} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 */
@Named
//TODO: @Scope(ScopeType.CONVERSATION)
public class WalletBean extends BaseBean<Wallet> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link Wallet} service. Extends {@link PersistenceService}. */
    @Inject
    private WalletServiceLocal walletService;

    /**
     * Customer account Id passed as a parameter. Used when creating new Wallet
     * from customer account window, so default customer account will be set on
     * newly created wallet.
     */
    //TODO: @RequestParameter
    private Long customerAccountId;

    /**
     * Constructor. Invokes super constructor and provides class type of this
     * bean for {@link BaseBean}.
     */
    public WalletBean() {
        super(Wallet.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity
     * from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    /*TODO: @Factory("wallet")
    @Begin(nested = true)*/
    @Produces
	@Named("wallet")
    public Wallet init() {
        initEntity();
        if (customerAccountId != null) {
            // wallet.setCustomerAccount(customerAccountService.findById(customerAccountId));
        }
        return entity;
    }

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    //@Out(value = "wallets", required = false)
    @Produces
	@Named("wallets")
    protected PaginationDataModel<Wallet> getDataModel() {
        return entities;
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes
     * BaseBean.list() method that handles all data model loading. Overriding is
     * needed only to put factory name on it.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    /*TODO: @Begin(join = true)
    @Factory("wallets")*/
    @Produces
	@Named("wallets")
    public void list() {
        super.list();
    }

    /**
     * Conversation is ended and user is redirected from edit to his previous
     * window.
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
     */
    //TODO: @End(beforeRedirect = true, root=false)
    public String saveOrUpdate() {
        return saveOrUpdate(entity);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Wallet> getPersistenceService() {
        return walletService;
    }

}
