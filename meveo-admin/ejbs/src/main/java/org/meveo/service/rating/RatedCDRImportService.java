package org.meveo.service.rating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.mediation.Access;
import org.meveo.model.rating.RatedCDR;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.medina.impl.AccessService;
import org.meveo.util.MeveoCacheContainerProvider;
import org.slf4j.Logger;

@Stateless
public class RatedCDRImportService extends PersistenceService<RatedTransaction> {

	

	@Inject
	private AccessService accessService;

	@Inject
	private Logger log;
	
	@Inject
	MeveoCacheContainerProvider meveoCacheContainerProvider;
	

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		EntityManager em = getEntityManager();

		if(meveoCacheContainerProvider.getUsageChargeTemplateCache().isEmpty()){
			List<UsageChargeTemplate> chargeTemplates = em.createQuery(
					"From " + UsageChargeTemplate.class.getSimpleName())
					.getResultList();
			if (chargeTemplates != null & chargeTemplates.size() > 0) {
				for (UsageChargeTemplate chargeTemplate : chargeTemplates) {
					if (!meveoCacheContainerProvider.getUsageChargeTemplateCache().containsKey(chargeTemplate.getProvider().getCode())) {
						meveoCacheContainerProvider.getUsageChargeTemplateCache().put(chargeTemplate.getProvider().getCode(), new HashMap<String, UsageChargeTemplate>());
					}
					Map<String, UsageChargeTemplate> chargeTemplatesMap = meveoCacheContainerProvider.getUsageChargeTemplateCache().get(chargeTemplate.getProvider()
							.getCode());
					
					if(!chargeTemplatesMap.containsKey(chargeTemplate.getCode())){
						chargeTemplatesMap.put(chargeTemplate.getCode(), chargeTemplate);
					}
				}	
			}
			log.info("loaded " + chargeTemplates.size()
					+ " usage charge template in cache.");

		}
		

		System.gc();

	}

	@Asynchronous
	public Future<List<RatedCDR>> asyncCreateRatedTransactions(
			List<RatedCDR> cdrList) throws BusinessException {
		List<RatedCDR> result = createRatedTransactions(cdrList);

		return new AsyncResult<List<RatedCDR>>(result);
	}

	// return the CDR inof error, with cdr.error set, if not return null;
	public List<RatedCDR> createRatedTransactions(List<RatedCDR> cdrList)
			throws BusinessException {
		ArrayList<RatedCDR> result = new ArrayList<RatedCDR>();
		for (RatedCDR cdr : cdrList) {
			RatedCDR errorCDR = createRatedTransaction(cdr);
			if (errorCDR != null) {
				result.add(errorCDR);
			}
		}

		return result;
	}

	// return the CDR inof error, with cdr.error set, if not return null;
	public RatedCDR createRatedTransaction(RatedCDR cdr)
			throws BusinessException {
		RatedCDR result = cdr;
		UsageChargeTemplate chargeTemplate=null;
		if(meveoCacheContainerProvider.getUsageChargeTemplateCache().containsKey(cdr.getProvider().getCode())){
			Map<String, UsageChargeTemplate> usageChargeTemplates=meveoCacheContainerProvider.getUsageChargeTemplateCache().get(cdr.getProvider().getCode());
			if(usageChargeTemplates!=null && usageChargeTemplates.containsKey(cdr.getServiceCode())){
				chargeTemplate = usageChargeTemplates.get(cdr.getServiceCode());
			}else{
				result.setError("Charge template not found");
				return result;
			}
		}else{
			result.setError("Charge template provider not found");
			return result;
		}

		String userId = cdr.getUserCode();
		List<Access> accesses = null;

		if (meveoCacheContainerProvider.getAccessCache().containsKey(userId)) {
			accesses = meveoCacheContainerProvider.getAccessCache().get(userId);
		} else {
			accesses = accessService.findByUserID(userId);
			if (accesses.size() == 0) {
				result.setError("no acces found for the userCode");
				return result;
			}
			meveoCacheContainerProvider.getAccessCache().put(userId, accesses);
		}

		for (Access accessPoint : accesses) {
			if ((accessPoint.getStartDate() == null || accessPoint
					.getStartDate().getTime() <= cdr.getDate().getTime())
					&& (accessPoint.getEndDate() == null || accessPoint
							.getEndDate().getTime() > cdr.getDate().getTime())) {
				RatedTransaction transaction = new RatedTransaction();
				transaction.setWalletOperationId(cdr.getDate().getTime());
				cdr.fillRatedTransaction(transaction);
				transaction.setStatus(RatedTransactionStatusEnum.OPEN);
				UserAccount userAccount = accessPoint.getSubscription()
						.getUserAccount();
				BillingAccount billingAccount = userAccount.getBillingAccount();
				transaction.setWallet(userAccount.getWallet());
				transaction.setBillingAccount(billingAccount);
				transaction.setInvoiceSubCategory(chargeTemplate
						.getInvoiceSubCategory());
				transaction.setProvider(cdr.getProvider());
				create(transaction);
				return null;
			}
		}

		result.setError("no acces found for the date");

		return result;
	}
}
