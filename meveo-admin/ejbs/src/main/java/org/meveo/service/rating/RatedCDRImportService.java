package org.meveo.service.rating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.AsyncResult;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.RatedCDR;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.medina.impl.AccessService;

@Stateless
public class RatedCDRImportService extends PersistenceService<RatedTransaction>{

	static HashMap<String,UsageChargeTemplate> chargeCache;
	static HashMap<String,List<Access>> accessCache=new HashMap<String, List<Access>>();
	
	@Inject
	AccessService accessService;
	
	private Logger log = Logger.getLogger(RatedCDRImportService.class.getName());
	
	public void init(){
	   EntityManager em=getEntityManager();
	   chargeCache=new HashMap<String,UsageChargeTemplate>();
	  
	   @SuppressWarnings("unchecked")
	   List<UsageChargeTemplate> chargeTemplates =  em.createQuery("From "+UsageChargeTemplate.class.getSimpleName()).getResultList();
	   for(UsageChargeTemplate chargeTemplate:chargeTemplates){
		  chargeCache.put(chargeTemplate.getCode(),chargeTemplate);
	   }
	   log.info("loaded "+chargeTemplates.size()+" usage charge template in cache.");

	   accessCache=new HashMap<String, List<Access>>();
	   
	   System.gc();
	   
	}
	
	@Asynchronous
	public Future<List<RatedCDR>> asyncCreateRatedTransactions(List<RatedCDR> cdrList){
		List<RatedCDR> result=createRatedTransactions(cdrList);
		return new AsyncResult<List<RatedCDR>>(result);
	}
	
	//return the CDR inof error, with cdr.error set, if not return null;
	public List<RatedCDR> createRatedTransactions(List<RatedCDR> cdrList){
		ArrayList<RatedCDR> result=new ArrayList<RatedCDR>();
		for(RatedCDR cdr:cdrList){
			RatedCDR errorCDR=createRatedTransaction(cdr); 
			if(errorCDR!=null){
				result.add(errorCDR);
			}
		}
		return result;
	}
	
	//return the CDR inof error, with cdr.error set, if not return null;
	public RatedCDR createRatedTransaction(RatedCDR cdr){
		RatedCDR result=cdr;
		UsageChargeTemplate chargeTemplate;
		if(chargeCache.containsKey(cdr.getServiceCode())){
			chargeTemplate=chargeCache.get(cdr.getServiceCode());
		} else {
			result.setError("charge template not found");
			return result;
		}
		String userId = cdr.getUserCode();
		List<Access> accesses=null;
		if(accessCache.containsKey(userId)){
			accesses=accessCache.get(userId);
		}else {
			accesses = accessService.findByUserID(userId);
			if(accesses.size()==0){
				result.setError("no acces found for the userCode");
				return result;
			}
			accessCache.put(userId, accesses);
		}
		for(Access accessPoint:accesses){
			if((accessPoint.getStartDate()==null ||accessPoint.getStartDate().getTime()<=cdr.getDate().getTime())
					&& (accessPoint.getEndDate()==null || accessPoint.getEndDate().getTime()>cdr.getDate().getTime())	){
				RatedTransaction transaction = new RatedTransaction();
				transaction.setWalletOperationId(cdr.getDate().getTime());
				cdr.fillRatedTransaction(transaction);
				transaction.setStatus(RatedTransactionStatusEnum.OPEN);
				UserAccount userAccount=accessPoint.getSubscription().getUserAccount();
				BillingAccount billingAccount=userAccount.getBillingAccount();
				transaction.setWallet(userAccount.getWallet());
				transaction.setBillingAccount(billingAccount);
				transaction.setInvoiceSubCategory(chargeTemplate.getInvoiceSubCategory());
				transaction.setProvider(cdr.getProvider());
				create(transaction);
				return null;
			}
		}
		result.setError("no acces found for the date");
		
		return result;
	}
}
