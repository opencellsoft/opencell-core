/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.billing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.WalletOperation;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.primefaces.model.LazyDataModel;


@Named
@ViewScoped
public class WalletOperationBean extends BaseBean<WalletOperation> {
	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link WalletOperation} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private WalletOperationService walletOperationService;
	
	@Inject
	private TradingCurrencyService tradingCurrencyService;
	
	@Inject
	private RatedTransactionService ratedTransactionService;
   private Map<String, Currency> listCurrency = new HashMap<String, Currency>(); 


	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public WalletOperationBean() {
		super(WalletOperation.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
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
		if(tradingCurrencyService.list(getCurrentProvider()).size()>0 && tradingCurrencyService.list(getCurrentProvider())!=null){
			for(TradingCurrency trading :tradingCurrencyService.list(getCurrentProvider()) ){
				listCurrency.put(trading.getCurrency().getCurrencyCode(),trading.getCurrency());
				}}
		return listCurrency;
	}
	
	
	
	@Override
	public LazyDataModel<WalletOperation> getLazyDataModel() {
		getFilters();
		
		if (filters.containsKey("chargeInstance")) {
			filters.put("chargeInstance.chargeTemplate", filters.get("chargeInstance")); 
			filters.remove("chargeInstance");
		}
		if (filters.containsKey("wallet")) {
			filters.put("wallet.walletTemplate", filters.get("wallet")); 
			filters.remove("wallet");
		}
		if (filters.containsKey("counter")) {
			filters.put("counter.counterTemplate", filters.get("counter"));
			filters.remove("counter");
		}
		if (filters.containsKey("billingAccount")) {
			filters.put("wallet.userAccount.billingAccount", filters.get("billingAccount"));
			filters.remove("billingAccount");
		}
		if (filters.containsKey("invoiceSubCategory")) {
			filters.put("chargeInstance.chargeTemplate.invoiceSubCategory", filters.get("invoiceSubCategory"));
			filters.remove("invoiceSubCategory");
		}
		if (filters.containsKey("offerTemplate")) {
			filters.put("priceplan.offerTemplate", filters.get("offerTemplate"));
			filters.remove("offerTemplate");
		}
		if (filters.containsKey("billingRun")) {
			List<Long> walletOperationIds=new ArrayList<Long>();
			BillingRun br=(BillingRun)filters.get("billingRun");
			List<RatedTransaction> ListRated=ratedTransactionService.getRatedTransactionsByBillingRun(br);
			if(ListRated.size()>0 && !ListRated.isEmpty()){
			for(RatedTransaction rated : ListRated){
				walletOperationIds.add(rated.getWalletOperationId());
			} 
			   StringBuffer wpIds=new StringBuffer();
			   String sep="";
			   for(Long ids:walletOperationIds){
			    wpIds.append(sep);
			    wpIds.append(ids.toString());
			    sep=",";
			   }
			   filters.put("inList-id", wpIds);
			  }
			else{
				return null;
			}
			 filters.remove("billingRun");
		}
	
		return super.getLazyDataModel();
	}
	
	

	public void updatedToRerate(WalletOperation walletOperation) {
		 try{
			  List<Long> walletIdList=new ArrayList<Long>();
			  walletIdList.add(walletOperation.getId());
			  if(walletOperationService.updateToRerate(walletIdList)>0){
				  walletOperationService.refresh(walletOperation);
			      messages.info(new BundleKey("messages","update.successful"));
			  }else{
			 messages.info(new BundleKey("messages","walletOperation.alreadyBilled"));
			 }
			  }catch (Exception e) {
			   log.error("failed to updated to rerate ",e); 
			    messages.error(new BundleKey("messages","update.failed"));
			  }}
	
	public String massToRerate() {
		try{
		List<Long> walletIdList=null;
		if (getSelectedEntities() != null) {
			walletIdList=new ArrayList<Long>();
			for (WalletOperation wallet : getSelectedEntities()) {
				walletIdList.add(wallet.getId());	
			}}
			int count=walletOperationService.updateToRerate(walletIdList); 
			messages.info(new BundleKey("messages", "walletOperation.updateToRerate"),count);
		}catch (Exception e) {
			log.error("error while updating to rerate",e);
			messages.error(new BundleKey("messages","update.failed"));
		}
		conversation.end();
		return "walletOperations";
	}

} 

