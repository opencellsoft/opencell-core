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
package org.meveo.admin.action.billing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperation;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.primefaces.model.LazyDataModel;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Named
@ViewScoped
public class WalletOperationBean extends BaseBean<WalletOperation> {
	private static final long serialVersionUID = 1L;

	/**
     * Injected @{link WalletOperation} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private WalletOperationService walletOperationService;
	
	@Inject
	private TradingCurrencyService tradingCurrencyService;
	
	@Inject
	private RatedTransactionService ratedTransactionService;
   private Map<String, Currency> listCurrency = new HashMap<String, Currency>();

	/**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
	 */
	public WalletOperationBean() {
		super(WalletOperation.class);
	}

	/**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
	 * @return wallet operation.
	 */
	@Produces
	@Named("walletOperation")
	public WalletOperation init() {
		return initEntity();
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<WalletOperation> getPersistenceService() {
		return walletOperationService;
	}

	public Map<String, Currency> getListCurrency() {
		listCurrency.clear();
		if(tradingCurrencyService.list().size()>0 && tradingCurrencyService.list()!=null){
			for(TradingCurrency trading :tradingCurrencyService.list() ){
				listCurrency.put(trading.getCurrency().getCurrencyCode(),trading.getCurrency());
            }
        }
		return listCurrency;
	}
	
	@Override
	public LazyDataModel<WalletOperation> getLazyDataModel() {
		return this.getLazyDataModel(this.filters, this.listFiltered);
	}
	
	@Override
	public LazyDataModel<WalletOperation> getLazyDataModel(Map<String, Object> inputFilters, boolean forceReload) {
		this.filters = inputFilters;
		this.getFilters();
		return this.filterDataModelByBillingRun(forceReload);
	}
	
	@Override
	public Map<String, Object> getFilters() {
		return super.getFilters();
	}
	
	private LazyDataModel<WalletOperation> filterDataModelByBillingRun(boolean forceReload) {
		if (filters.containsKey("billingRun")) {
			BillingRun br = (BillingRun) filters.get("billingRun");
            filters.put("ratedTransaction.billingRun", br);
			filters.remove("billingRun");
		}
		return super.getLazyDataModel(filters, forceReload);
	}

	public void updatedToRerate(WalletOperation walletOperation) {
		try {
			List<Long> walletIdList = new ArrayList<Long>();
			walletIdList.add(walletOperation.getId());
			if (walletOperationService.markToRerateInNewTx(walletIdList, false) > 0) {
				walletOperationService.refresh(walletOperation);
				messages.info(new BundleKey("messages", "update.successful"));
			} else {
				messages.info(new BundleKey("messages", "walletOperation.alreadyBilled"));
			}
		} catch (Exception e) {
			log.error("failed to updated to rerate ", e);
			messages.error(new BundleKey("messages", "update.failed"));
		}
	}
	
	public String massToRerate() {
		try {
			List<Long> walletIdList = null;
			if (getSelectedEntities() != null) {
				walletIdList = new ArrayList<Long>();
				for (WalletOperation wallet : getSelectedEntities()) {
					walletIdList.add(wallet.getId());
				}
			}
			int count = walletOperationService.markToRerateInNewTx(walletIdList, false);
			messages.info(new BundleKey("messages", "walletOperation.updateToRerate"), count);
		} catch (Exception e) {
			log.error("error while updating to rerate", e);
			messages.error(new BundleKey("messages", "update.failed"));
		}
		conversation.end();
		return "walletOperations";
	}
}