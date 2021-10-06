package org.meveo.service.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.meveo.commons.utils.ReflectionUtils.getSubclassObjectByDiscriminatorValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.accounting.AccountingOperationAction;
import org.meveo.model.accounting.AccountingPeriod;
import org.meveo.model.accounting.AccountingPeriodForceEnum;
import org.meveo.model.accounting.AccountingPeriodStatusEnum;
import org.meveo.model.accounting.SubAccountingPeriod;
import org.meveo.model.accounting.SubAccountingPeriodStatusEnum;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.AccountOperationRejectionReason;
import org.meveo.model.payments.AccountOperationStatus;
import org.meveo.model.payments.AutomatedPayment;
import org.meveo.model.payments.AutomatedRefund;
import org.meveo.model.payments.OtherCreditAndCharge;
import org.meveo.model.payments.Payment;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.model.payments.Refund;
import org.meveo.model.payments.RejectedPayment;
import org.meveo.model.payments.WriteOff;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.accounting.impl.AccountingPeriodService;
import org.meveo.service.accounting.impl.SubAccountingPeriodService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AccountOperationServiceTest {

	@Spy
	@InjectMocks
	private AccountOperationService accountOperationService;

	@Mock
	private AccountingPeriodService accountingPeriodService;

	@Mock
	private SubAccountingPeriodService subAccountingPeriodService;

	@Test
	public void handleAccountingPeriods_AccountOperation_Without_AccountingPeriod() {

		AccountOperation ao = new AccountOperation();
		Date transactionDate = new Date();
		Date collectionDate = DateUtils.addDaysToDate(transactionDate, 5);
		ao.setCollectionDate(collectionDate);
		ao.setTransactionDate(transactionDate);

		accountOperationService.handleAccountingPeriods(ao);
		assertThat(ao.getReason()).isNull();
		assertThat(ao.getStatus()).isEqualTo(AccountOperationStatus.POSTED);
		assertThat(ao.getAccountingDate()).isEqualToIgnoringHours(ao.getTransactionDate());
	}

	@Test
	public void handleAccountingPeriods_AutomatedPayment_ClosedAccountingPeriod() {

		when(accountingPeriodService.count()).thenReturn(1L);
		AccountingPeriod closedAP = new AccountingPeriod();
		closedAP.setAccountingPeriodStatus(AccountingPeriodStatusEnum.CLOSED);
		when(accountingPeriodService.findByAccountingPeriodYear(any())).thenReturn(closedAP);

		AutomatedPayment payment = (AutomatedPayment) createAccountOperation("AP");

		accountOperationService.handleAccountingPeriods(payment);
		assertThat(payment.getAccountingDate()).isNull();
		assertThat(payment.getStatus()).isEqualTo(AccountOperationStatus.REJECTED);
		assertThat(payment.getReason()).isEqualTo(AccountOperationRejectionReason.CLOSED_PERIOD);
	}

	@Test
	public void handleAccountingPeriods_Payment_OpenAccountingPeriod() {

		when(accountingPeriodService.count()).thenReturn(1L);
		AccountingPeriod closedAP = new AccountingPeriod();
		closedAP.setAccountingPeriodStatus(AccountingPeriodStatusEnum.OPEN);
		when(accountingPeriodService.findByAccountingPeriodYear(any())).thenReturn(closedAP);

		Payment payment = (Payment) createAccountOperation("P");
		payment.setCollectionDate(null);

		accountOperationService.handleAccountingPeriods(payment);
		assertThat(payment.getStatus()).isEqualTo(AccountOperationStatus.POSTED);
		assertThat(payment.getAccountingDate()).isEqualToIgnoringHours(payment.getDueDate());
	}

	@Test
	public void handleAccountingPeriods_RejectedPayment_OpenAccountingPeriod() {

		when(accountingPeriodService.count()).thenReturn(1L);
		AccountingPeriod openAP = new AccountingPeriod();
		openAP.setAccountingPeriodStatus(AccountingPeriodStatusEnum.OPEN);
		when(accountingPeriodService.findByAccountingPeriodYear(any())).thenReturn(openAP);

		RejectedPayment rejectedPayment = (RejectedPayment) createAccountOperation("R");

		accountOperationService.handleAccountingPeriods(rejectedPayment);
		assertThat(rejectedPayment.getStatus()).isEqualTo(AccountOperationStatus.POSTED);
		assertThat(rejectedPayment.getAccountingDate()).isEqualToIgnoringHours(rejectedPayment.getCollectionDate());
	}

	@Test
	public void handleAccountingPeriods_RecordedInvoice_OpenSubAccoutingPeriod() {

		when(accountingPeriodService.count()).thenReturn(1L);
		AccountingPeriod openAP = new AccountingPeriod();
		openAP.setUseSubAccountingCycles(Boolean.TRUE);
		openAP.setAccountingPeriodStatus(AccountingPeriodStatusEnum.OPEN);
		when(accountingPeriodService.findByAccountingPeriodYear(any())).thenReturn(openAP);

		SubAccountingPeriod openSAP = new SubAccountingPeriod();
		openSAP.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.OPEN);
		when(subAccountingPeriodService.findByAccountingPeriod(any(), any())).thenReturn(openSAP);

		RecordedInvoice recordedInvoice = (RecordedInvoice) createAccountOperation("I");

		accountOperationService.handleAccountingPeriods(recordedInvoice);
		assertThat(recordedInvoice.getStatus()).isEqualTo(AccountOperationStatus.POSTED);
		assertThat(recordedInvoice.getAccountingDate()).isEqualTo(recordedInvoice.getTransactionDate());
	}

	@Test
	public void handleAccountingPeriods_WriteOff_Block_Case() {

		when(accountingPeriodService.count()).thenReturn(1L);
		AccountingPeriod openAP = new AccountingPeriod();
		openAP.setUseSubAccountingCycles(Boolean.TRUE);
		openAP.setAccountingPeriodStatus(AccountingPeriodStatusEnum.OPEN);
		openAP.setAccountingOperationAction(AccountingOperationAction.BLOCK);
		when(accountingPeriodService.findByAccountingPeriodYear(any())).thenReturn(openAP);

		SubAccountingPeriod closedSAP = new SubAccountingPeriod();
		closedSAP.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.CLOSED);
		when(subAccountingPeriodService.findByAccountingPeriod(any(), any())).thenReturn(closedSAP);

		WriteOff writeOff = (WriteOff) createAccountOperation("W");

		accountOperationService.handleAccountingPeriods(writeOff);
		assertThat(writeOff.getAccountingDate()).isNull();
		assertThat(writeOff.getStatus()).isEqualTo(AccountOperationStatus.REJECTED);
		assertThat(writeOff.getReason()).isEqualTo(AccountOperationRejectionReason.CLOSED_PERIOD);
	}

	@Test
	public void handleAccountingPeriods_Refund_Force_Case_First_Day() {

		when(accountingPeriodService.count()).thenReturn(1L);
		AccountingPeriod openAP = new AccountingPeriod();
		openAP.setUseSubAccountingCycles(Boolean.TRUE);
		openAP.setAccountingPeriodStatus(AccountingPeriodStatusEnum.OPEN);
		openAP.setAccountingOperationAction(AccountingOperationAction.FORCE);
		openAP.setForceOption(AccountingPeriodForceEnum.FIRST_DAY);
		when(accountingPeriodService.findByAccountingPeriodYear(any())).thenReturn(openAP);

		SubAccountingPeriod closedSAP = new SubAccountingPeriod();
		closedSAP.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.CLOSED);
		when(subAccountingPeriodService.findByAccountingPeriod(any(), any())).thenReturn(closedSAP);

		SubAccountingPeriod openSAP = new SubAccountingPeriod();
		Date startDate = DateUtils.parseDateWithPattern("2021-07-01", DateUtils.DATE_PATTERN);
		openSAP.setStartDate(startDate);
		openSAP.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.OPEN);
		when(subAccountingPeriodService.findLastSubAccountingPeriod()).thenReturn(openSAP);

		Refund refund = (Refund) createAccountOperation("RF");

		accountOperationService.handleAccountingPeriods(refund);
		assertThat(refund.getStatus()).isEqualTo(AccountOperationStatus.POSTED);
		assertThat(refund.getReason()).isEqualTo(AccountOperationRejectionReason.FORCED);
		assertThat(refund.getAccountingDate()).isEqualToIgnoringHours(startDate);
	}

	@Test
	public void handleAccountingPeriods_AutomatedRefund_Force_Case_First_Sunday() {

		when(accountingPeriodService.count()).thenReturn(1L);
		AccountingPeriod openAP = new AccountingPeriod();
		openAP.setUseSubAccountingCycles(Boolean.TRUE);
		openAP.setAccountingPeriodStatus(AccountingPeriodStatusEnum.OPEN);
		openAP.setAccountingOperationAction(AccountingOperationAction.FORCE);
		openAP.setForceOption(AccountingPeriodForceEnum.FIRST_SUNDAY);
		when(accountingPeriodService.findByAccountingPeriodYear(any())).thenReturn(openAP);

		SubAccountingPeriod closedSAP = new SubAccountingPeriod();
		closedSAP.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.CLOSED);
		when(subAccountingPeriodService.findByAccountingPeriod(any(), any())).thenReturn(closedSAP);

		SubAccountingPeriod openSAP = new SubAccountingPeriod();
		Date startDate = DateUtils.parseDateWithPattern("2021-07-01", DateUtils.DATE_PATTERN);
		openSAP.setStartDate(startDate);
		openSAP.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.OPEN);
		when(subAccountingPeriodService.findLastSubAccountingPeriod()).thenReturn(openSAP);

		AutomatedRefund refund = (AutomatedRefund) createAccountOperation("ARF");

		accountOperationService.handleAccountingPeriods(refund);
		assertThat(refund.getStatus()).isEqualTo(AccountOperationStatus.POSTED);
		assertThat(refund.getReason()).isEqualTo(AccountOperationRejectionReason.FORCED);
		assertThat(refund.getAccountingDate()).isEqualToIgnoringHours("2021-07-04");
	}

	@Test
	public void handleAccountingPeriods_OCC_Force_Case_Custom_Day() {

		when(accountingPeriodService.count()).thenReturn(1L);
		AccountingPeriod openAP = new AccountingPeriod();
		openAP.setUseSubAccountingCycles(Boolean.TRUE);
		openAP.setAccountingPeriodStatus(AccountingPeriodStatusEnum.OPEN);
		openAP.setAccountingOperationAction(AccountingOperationAction.FORCE);
		openAP.setForceOption(AccountingPeriodForceEnum.CUSTOM_DAY);
		openAP.setForceCustomDay(31);
		when(accountingPeriodService.findByAccountingPeriodYear(any())).thenReturn(openAP);

		SubAccountingPeriod closedSAP = new SubAccountingPeriod();
		closedSAP.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.CLOSED);
		when(subAccountingPeriodService.findByAccountingPeriod(any(), any())).thenReturn(closedSAP);

		SubAccountingPeriod openSAP = new SubAccountingPeriod();
		Date startDate = DateUtils.parseDateWithPattern("2021-02-01", DateUtils.DATE_PATTERN);
		openSAP.setStartDate(startDate);
		openSAP.setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum.OPEN);
		when(subAccountingPeriodService.findLastSubAccountingPeriod()).thenReturn(openSAP);

		OtherCreditAndCharge refund = (OtherCreditAndCharge) createAccountOperation("OCC");

		accountOperationService.handleAccountingPeriods(refund);
		assertThat(refund.getStatus()).isEqualTo(AccountOperationStatus.POSTED);
		assertThat(refund.getReason()).isEqualTo(AccountOperationRejectionReason.FORCED);
		assertThat(refund.getAccountingDate()).isEqualToIgnoringHours("2021-02-28");
	}

	private AccountOperation createAccountOperation(String Type) {
		Object object = getSubclassObjectByDiscriminatorValue(AccountOperation.class, Type);

		AccountOperation ao = null;
		if (object instanceof AutomatedPayment) {
			ao = (AutomatedPayment) object;
		} else if (object instanceof AutomatedRefund) {
			ao = (AutomatedRefund) object;
		} else if (object instanceof Refund) {
			ao = (Refund) object;
		} else if (object instanceof Payment) {
			ao = (Payment) object;
		} else if (object instanceof WriteOff) {
			ao = (WriteOff) object;
		} else if (object instanceof RecordedInvoice) {
			ao = (RecordedInvoice) object;
		} else if (object instanceof RejectedPayment) {
			ao = (RejectedPayment) object;
		} else if (object instanceof OtherCreditAndCharge) {
			ao = (OtherCreditAndCharge) object;
		}

		Date transactionDate = new Date();
		Date collectionDate = DateUtils.addDaysToDate(transactionDate, 5);
		Date dueDate = DateUtils.addDaysToDate(collectionDate, 1);
		ao.setDueDate(dueDate);
		ao.setCollectionDate(collectionDate);
		ao.setTransactionDate(transactionDate);

		return ao;
	}
}
