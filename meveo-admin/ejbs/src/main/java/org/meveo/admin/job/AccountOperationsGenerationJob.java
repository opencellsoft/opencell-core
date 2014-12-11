package org.meveo.admin.job;

import java.math.BigDecimal;
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

import org.meveo.admin.exception.ImportInvoiceException;
import org.meveo.admin.exception.InvoiceExistException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.meveo.services.job.Job;
import org.meveo.services.job.JobExecutionService;
import org.meveo.services.job.TimerEntityService;
import org.slf4j.Logger;

@Startup
@Singleton
public class AccountOperationsGenerationJob implements Job {

	@Resource
	private TimerService timerService;

	@Inject
	private UserService userService;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private OCCTemplateService oCCTemplateService;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private RecordedInvoiceService recordedInvoiceService;

	@Inject
	private Logger log;

	@PostConstruct
	public void init() {
		TimerEntityService.registerJob(this);
	}

	@Override
	public JobExecutionResult execute(String parameter, User currentUser) {
		log.info("execute XMLInvoiceGenerationJob.");

		ParamBean paramBan = ParamBean.getInstance();
		JobExecutionResultImpl result = new JobExecutionResultImpl();
		List<Invoice> invoices = invoiceService
				.getInvoicesWithNoAccountOperation(null);
		Provider providerForHistory = null;
		for (Invoice invoice : invoices) {
			try {

				CustomerAccount customerAccount = null;
				OCCTemplate invoiceTemplate = null;
				RecordedInvoice recordedInvoice = new RecordedInvoice();
				BillingAccount billingAccount = invoice.getBillingAccount();
				if (recordedInvoiceService.isRecordedInvoiceExist(
						invoice.getInvoiceNumber(), invoice.getProvider())) {
					throw new InvoiceExistException("Invoice id"
							+ invoice.getId() + " already exist");
				}
				try {
					customerAccount = invoice.getBillingAccount()
							.getCustomerAccount();
					recordedInvoice.setCustomerAccount(customerAccount);
					recordedInvoice.setProvider(customerAccount.getProvider());
					// set first provider from first customer account
					if (providerForHistory != null) {
						providerForHistory = customerAccount.getProvider();
					}
				} catch (Exception e) {
					throw new ImportInvoiceException(
							"Cannot found customerAccount");
				}

				try {
					invoiceTemplate = oCCTemplateService.findByCode(paramBan
							.getProperty(
									"accountOperationsGenerationJob.occCode",
									"FA_FACT"), customerAccount.getProvider()
							.getCode());
				} catch (Exception e) {
					// TODO message fr|en
					throw new ImportInvoiceException(
							"Cannot found OCC Template for invoice");
				}
				recordedInvoice.setReference(invoice.getInvoiceNumber());
				recordedInvoice
						.setAccountCode(invoiceTemplate.getAccountCode());
				recordedInvoice.setOccCode(invoiceTemplate.getCode());
				recordedInvoice.setOccDescription(invoiceTemplate
						.getDescription());
				recordedInvoice.setTransactionCategory(invoiceTemplate
						.getOccCategory());
				recordedInvoice.setAccountCodeClientSide(invoiceTemplate
						.getAccountCodeClientSide());

				try {
					recordedInvoice.setAmount(invoice.getAmountWithTax());
					recordedInvoice.setUnMatchingAmount(invoice
							.getAmountWithTax());
					recordedInvoice.setMatchingAmount(BigDecimal.ZERO);
				} catch (Exception e) {
					throw new ImportInvoiceException("Error on amountWithTax");
				}
				try {
					recordedInvoice.setAmountWithoutTax(invoice
							.getAmountWithoutTax());
				} catch (Exception e) {
					throw new ImportInvoiceException(
							"Error on amountWithoutTax");
				}
				try {
					recordedInvoice.setNetToPay(invoice.getNetToPay());
				} catch (Exception e) {
					throw new ImportInvoiceException("Error on netToPay");
				}

				try {
					recordedInvoice
							.setDueDate(DateUtils.parseDateWithPattern(
									invoice.getDueDate(),
									paramBan.getProperty(
											"accountOperationsGenerationJob.dateFormat",
											"dd/MM/yyyy")));
				} catch (Exception e) {
					throw new ImportInvoiceException("Error on DueDate");
				}
				try {
					recordedInvoice
							.setInvoiceDate(DateUtils.parseDateWithPattern(
									invoice.getInvoiceDate(),
									paramBan.getProperty(
											"accountOperationsGenerationJob.dateFormat",
											"dd/MM/yyyy")));
					recordedInvoice
							.setTransactionDate(DateUtils.parseDateWithPattern(
									invoice.getInvoiceDate(),
									paramBan.getProperty(
											"accountOperationsGenerationJob.dateFormat",
											"dd/MM/yyyy")));

				} catch (Exception e) {
					throw new ImportInvoiceException("Error on invoiceDate");
				}
				try {
					recordedInvoice.setPaymentMethod(billingAccount
							.getPaymentMethod());
				} catch (Exception e) {
					throw new ImportInvoiceException("Error on paymentMethod");
				}
				try {
					recordedInvoice.setTaxAmount(invoice.getAmountTax());
				} catch (Exception e) {
					throw new ImportInvoiceException("Error on total tax");
				}
				recordedInvoice.setPaymentInfo(billingAccount
						.getBankCoordinates().getIban());
				recordedInvoice.setPaymentInfo1(billingAccount
						.getBankCoordinates().getBankCode());
				recordedInvoice.setPaymentInfo2(billingAccount
						.getBankCoordinates().getBranchCode());
				recordedInvoice.setPaymentInfo3(billingAccount
						.getBankCoordinates().getAccountNumber());
				recordedInvoice.setPaymentInfo4(billingAccount
						.getBankCoordinates().getKey());
				recordedInvoice.setPaymentInfo5(billingAccount
						.getBankCoordinates().getBankName());
				recordedInvoice.setPaymentInfo6(billingAccount
						.getBankCoordinates().getBic());
				recordedInvoice.setBillingAccountName(billingAccount
						.getBankCoordinates().getAccountOwner());
				recordedInvoice.setMatchingStatus(MatchingStatusEnum.O);
				recordedInvoiceService.create(recordedInvoice);
				invoice.setRecordedInvoice(recordedInvoice);
				invoiceService.update(invoice);

			} catch (Exception e) {
				log.error(e.getMessage());
				result.registerError(e.getMessage());
			}
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
		log.info("Cancel " + alltimers.size() + " timers for"
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
