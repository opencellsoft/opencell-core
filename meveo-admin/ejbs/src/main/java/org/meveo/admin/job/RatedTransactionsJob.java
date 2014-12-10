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
package org.meveo.admin.job;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.meveo.model.admin.User;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Startup
@Singleton
public class RatedTransactionsJob implements Job {

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private WalletOperationService walletOperationService;

	@Inject
	private RatedTransactionService ratedTransactionService;

	private Logger log = LoggerFactory.getLogger(RatedTransactionsJob.class
			.getName());

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		log.info("execute RatedTransactionsJob.");

		Provider provider = currentUser.getProvider();
		JobExecutionResultImpl result = new JobExecutionResultImpl();

		try {
			// FIXME: only for postpaid wallets
			List<WalletOperation> walletOperations = walletOperationService
					.findByStatus(WalletOperationStatusEnum.OPEN, provider);
			log.info("WalletOperations to convert into rateTransactions={}",
					walletOperations.size());
			for (WalletOperation walletOperation : walletOperations) {
				try {
					RatedTransaction ratedTransaction = new RatedTransaction(
							walletOperation.getId(),
							walletOperation.getOperationDate(),
							walletOperation.getUnitAmountWithoutTax(),
							walletOperation.getUnitAmountWithTax(),
							walletOperation.getUnitAmountTax(),
							walletOperation.getQuantity(),
							walletOperation.getAmountWithoutTax(),
							walletOperation.getAmountWithTax(),
							walletOperation.getAmountTax(),
							RatedTransactionStatusEnum.OPEN,
							walletOperation.getProvider(),
							walletOperation.getWallet(), walletOperation
									.getWallet().getUserAccount()
									.getBillingAccount(), walletOperation
									.getChargeInstance().getChargeTemplate()
									.getInvoiceSubCategory(),
							walletOperation.getParameter1(),
							walletOperation.getParameter2(),
							walletOperation.getParameter3());
					ratedTransactionService.create(ratedTransaction);

					walletOperation
							.setStatus(WalletOperationStatusEnum.TREATED);

					walletOperationService.update(walletOperation);
				} catch (Exception e) {
					log.error(e.getMessage());
					result.registerError(e.getMessage());
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		result.close("");

		return result;
	}

	@Override
	public Timer createTimer(ScheduleExpression scheduleExpression,
			TimerInfo infos) {
		TimerConfig timerConfig = new TimerConfig();
		timerConfig.setInfo(infos);
		timerConfig.setPersistent(false);

		return timerService
				.createCalendarTimer(scheduleExpression, timerConfig);
	}

	boolean running = false;

	@Timeout
	public void trigger(Timer timer) {
		TimerInfo info = (TimerInfo) timer.getInfo();
		if (!running && info.isActive()) {
			try {
				running = true;
				User currentUser = userService.findById(info.getUserId());
				JobExecutionResult result = execute(info.getParametres(),
						currentUser);
				jobExecutionService.persistResult(this, result, info,
						currentUser);
			} catch (Exception e) {
				log.error(e.getMessage());
			} finally {
				running = false;
			}
		}
	}

	@Override
	public JobExecutionService getJobExecutionService() {
		return jobExecutionService;
	}

	@Override
	public void cleanAllTimers() {
		Collection<Timer> alltimers = timerService.getTimers();
		log.info("cancel " + alltimers.size() + " timers for"
				+ this.getClass().getSimpleName());

		for (Timer timer : alltimers) {
			try {
				timer.cancel();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

}
