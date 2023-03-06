package org.meveo.apiv2.cpq;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.not;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import org.assertj.core.api.Condition;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.apiv2.cpq.contracts.ImmutableBillingRuleDto;
import org.meveo.apiv2.cpq.service.CpqContractApiService;
import org.meveo.model.cpq.contract.BillingRule;
import org.meveo.model.cpq.contract.Contract;
import org.meveo.service.cpq.BillingRuleService;
import org.meveo.service.cpq.ContractService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CpqContractApiServiceTest {

	@InjectMocks
	private CpqContractApiService cpqContractApiService;

	@Mock
	private ContractService contractService;

	@Mock
	private BillingRuleService billingRuleService;

	
	@Test
	public void testCreateBillingRuleOK() {

		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();
		doReturn(new Contract()).when(contractService).findByCode("validContractCode");

		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("criteriaEL")
				.invoicedBACodeEL("invoicedBACodeEL")
				.build();

		cpqContractApiService.createBillingRule("validContractCode", data);
	}

	@Test
	public void testCreateBillingRule_FailContractNotFound() {
		
		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("criteriaEL")
				.invoicedBACodeEL("invoicedBACodeEL")
				.build();
		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();
		assertThrows(EntityDoesNotExistsException.class, () -> {			
			cpqContractApiService.createBillingRule("invalidContractCode", data);
		});
	}

	@Test
	public void testCreateBillingRule_FailMissingContractCodeParameter() {

		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("criteriaEL")
				.invoicedBACodeEL("invoicedBACodeEL")
				.build();

		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();
		Exception exception = assertThrows(MissingParameterException.class, () -> {			
			cpqContractApiService.createBillingRule("", data);
		});
		
		assertTrue("Expecting missing [contractCode] parameter", exception.getMessage().contains("contractCode"));
	}

	@Test
	public void testCreateBillingRule_FailMissingCriteriaEL() {

		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("")
				.invoicedBACodeEL("invoicedBACodeEL")
				.build();

		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();
		Exception exception = assertThrows(MissingParameterException.class, () -> {			
			cpqContractApiService.createBillingRule("validContractCode", data);
		});
		
		assertTrue("Expecting missing [criteriaEL] field", exception.getMessage().contains("criteriaEL"));
	}

	@Test
	public void testCreateBillingRule_FailMissingInvoicedBACodeEL() {

		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("criteriaEL")
				.invoicedBACodeEL("")
				.build();

		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();
		Exception exception = assertThrows(MissingParameterException.class, () -> {			
			cpqContractApiService.createBillingRule("validContractCode", data);
		});
		
		assertTrue("Expecting missing [invoicedBACodeEL] field", exception.getMessage().contains("invoicedBACodeEL"));
	}

	@Test
	public void testUpdateBillingRuleOK() {
		
		doReturn(new Contract()).when(contractService).findByCode("validContractCode");
		doReturn(new BillingRule()).when(billingRuleService).findById(anyLong());
		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();

		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("criteriaEL")
				.invoicedBACodeEL("invoicedBACodeEL")
				.build();

		cpqContractApiService.updateBillingRule("validContractCode", 1L, data);
	}

	@Test
	public void testUpdateBillingRule_FailContractNotFound() {

		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();

		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("criteriaEL")
				.invoicedBACodeEL("invoicedBACodeEL")
				.build();

		assertThrows(EntityDoesNotExistsException.class, () -> {			
			cpqContractApiService.updateBillingRule("invalidContractCode", 1L, data);
		});
	}

	@Test
	public void testUpdateBillingRule_FailBillingRuleNotFound() {

		doReturn(new Contract()).when(contractService).findByCode("validContractCode");
		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();

		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("criteriaEL")
				.invoicedBACodeEL("invoicedBACodeEL")
				.build();

		assertThrows(EntityDoesNotExistsException.class, () -> {			
			cpqContractApiService.updateBillingRule("validContractCode", 1L, data);
		});
	}

	@Test
	public void testUpdateBillingRule_FailMissingContractCodeParameter() {

		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();

		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("criteriaEL")
				.invoicedBACodeEL("invoicedBACodeEL")
				.build();

		Exception exception = assertThrows(MissingParameterException.class, () -> {			
			cpqContractApiService.updateBillingRule("", 1L, data);
		});
		
		assertTrue("Expecting missing [contractCode] parameter", exception.getMessage().contains("contractCode"));
	}

	@Test
	public void testUpdateBillingRule_FailMissingCriteriaEL() {

		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();

		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("")
				.invoicedBACodeEL("invoicedBACodeEL")
				.build();

		Exception exception = assertThrows(MissingParameterException.class, () -> {			
			cpqContractApiService.updateBillingRule("validContractCode", 1L, data);
		});
		
		assertTrue("Expecting missing [criteriaEL] field", exception.getMessage().contains("criteriaEL"));
	}

	@Test
	public void testUpdateBillingRule_FailMissingInvoicedBACodeEL() {

		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();

		ImmutableBillingRuleDto data = ImmutableBillingRuleDto.builder()
				.priority(1)
				.criteriaEL("criteriaEL")
				.invoicedBACodeEL("")
				.build();

		Exception exception = assertThrows(MissingParameterException.class, () -> {			
			cpqContractApiService.updateBillingRule("validContractCode", 1L, data);
		});
		
		assertTrue("Expecting missing [invoicedBACodeEL] field", exception.getMessage().contains("invoicedBACodeEL"));
	}

	@Test
	public void testDeleteBillingRuleOK() {
		
		doReturn(new Contract()).when(contractService).findByCode("validContractCode");
		doReturn(new BillingRule()).when(billingRuleService).findById(anyLong());
		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();

		cpqContractApiService.deleteBillingRule("validContractCode", 1L);
	}

	@Test
	public void testDeleteBillingRule_FailContractNotFound() {

		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();
		
		assertThrows(EntityDoesNotExistsException.class, () -> {			
			cpqContractApiService.deleteBillingRule("invalidContractCode", 1L);
		});
	}

	@Test
	public void testDeleteBillingRule_FailBillingRuleNotFound() {

		doReturn(new Contract()).when(contractService).findByCode("validContractCode");
		doReturn(true).when(billingRuleService).isBillingRedirectionRulesEnabled();

		assertThrows(EntityDoesNotExistsException.class, () -> {			
			cpqContractApiService.deleteBillingRule("validContractCode", 0L);
		});
	}
}
