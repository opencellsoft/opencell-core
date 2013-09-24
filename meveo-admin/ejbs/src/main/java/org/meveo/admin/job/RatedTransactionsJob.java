package org.meveo.admin.job;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;

@Startup
@Singleton
public class RatedTransactionsJob implements Job {

	@Resource
	TimerService timerService;

	@Inject
	private ProviderService providerService;
	
	@Inject
	JobExecutionService jobExecutionService;


	@Inject
	private WalletOperationService walletOperationService;
	
	@Inject
	private RatedTransactionService ratedTransactionService;


	private Logger log = Logger.getLogger(RatedTransactionsJob.class.getName());

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, Provider provider) {
		log.info("execute RatedTransactionsJob.");
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		try {
			//FIXME: only for postpaid wallets
			List<WalletOperation> walletOperations = walletOperationService.findByStatus(WalletOperationStatusEnum.OPEN);
			log.info("# walletOperations to convert into rateTransactions:" + walletOperations.size());
			for (WalletOperation walletOperation : walletOperations) {
				try {
					
					RatedTransaction ratedTransaction=new RatedTransaction(walletOperation.getId(), walletOperation.getOperationDate(), walletOperation.getUnitAmountWithoutTax(), walletOperation.getUnitAmountWithTax(), 
							walletOperation.getUnitAmountTax(), walletOperation.getQuantity(), walletOperation.getAmountWithoutTax(), walletOperation.getAmountWithTax(),
							walletOperation.getAmountTax(), RatedTransactionStatusEnum.OPEN,walletOperation.getProvider(),walletOperation.getWallet(),walletOperation.getWallet().getUserAccount().getBillingAccount(),walletOperation.getChargeInstance().getChargeTemplate().getInvoiceSubCategory());
					ratedTransactionService.create(ratedTransaction);
					
					walletOperation.setStatus(WalletOperationStatusEnum.TREATED);
					
					walletOperationService.update(walletOperation);
				} catch (Exception e) {
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		result.close("");
		return result;
	}

	@Override
	public TimerHandle createTimer(ScheduleExpression scheduleExpression, TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		Timer timer = timerService.createCalendarTimer(scheduleExpression, timerConfig);
		return timer.getHandle();
	}

	boolean running = false;

	@Timeout
	public void trigger(Timer timer) {
		TimerInfo info = (TimerInfo) timer.getInfo();
		if (!running && info.isActive()) {
			try {
				running = true;
                Provider provider=providerService.findById(info.getProviderId());
                JobExecutionResult result=execute(info.getParametres(),provider);
                jobExecutionService.persistResult(this, result,info,provider);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				running = false;
			}
		}
	}

	@Override
	public Collection<Timer> getTimers() {
		// TODO Auto-generated method stub
		return timerService.getTimers();
	}

}
