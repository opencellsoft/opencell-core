package org.meveo.apiv2.accountreceivable;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.apiv2.AcountReceivable.AccountOperationAndSequence;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.meveo.service.payments.impl.PaymentPlanService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class AccountOperationServiceMatchingTest {

    @InjectMocks
    private AccountOperationApiService accountOperationApiService;
    @Mock
    private AccountOperationService accountOperationService;
    @Mock
    private CustomerAccountService customerAccountService;
    @Mock
    private MatchingCodeService matchingCodeService;
    @Mock
    private PaymentPlanService paymentPlanService;

    @Test
    public void matchOperationsNominal() throws Exception {
        AccountOperation ao = buildAo("ABC", 1L);
        TradingCurrency tradingCurrency = new TradingCurrency();
        tradingCurrency.setId(1L);
        ao.setTransactionalCurrency(tradingCurrency);
        ao.setTransactionCategory(OperationCategoryEnum.CREDIT);
        Mockito.when(customerAccountService.findById(any())).thenReturn(ao.getCustomerAccount());
        Mockito.when(accountOperationService.findById(1L)).thenReturn(ao);
        Mockito.doNothing().when(paymentPlanService).toComplete(any());

        List<AccountOperationAndSequence> request = buildRequest(List.of(1L), List.of(0));
        accountOperationApiService.matchOperations(request); // no need to validate content, MatchingResult is mocked

    }

    @Test
    public void matchOperationsExceptionDifferentCustomer() {
        AccountOperation ao1 = buildAo("ABC", 1L);
        AccountOperation ao2 = buildAo("ABC", 2L);
        Mockito.when(accountOperationService.findById(1L)).thenReturn(ao1);
        Mockito.when(accountOperationService.findById(2L)).thenReturn(ao2);

        try {
            List<AccountOperationAndSequence> request = buildRequest(Arrays.asList(1L, 2L), Arrays.asList(0, 1));
            accountOperationApiService.matchOperations(request);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Matching action is failed : AccountOperations passed for matching are linked to different CustomerAccount");
        }

    }

    @Test
    public void matchOperationsExceptionInvalidAoCode() {
        AccountOperation ao1 = buildAo("CRD_SD", 1L);
        AccountOperation ao2 = buildAo("REF_SD", 1L);
        Mockito.when(accountOperationService.findById(1L)).thenReturn(ao1);
        Mockito.when(accountOperationService.findById(2L)).thenReturn(ao2);

        try {
            List<AccountOperationAndSequence> request = buildRequest(Arrays.asList(1L, 2L), Arrays.asList(0, 1));
            accountOperationApiService.matchOperations(request);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertTrue(e.getMessage().startsWith("Matching action is failed :" +
                    " AccountOperations passed for matching contains one of unexpected codes ["));
        }

    }

    @Test
    public void matchOperationsExceptionNoCustomerFound() {
        AccountOperation ao1 = buildAo("ABC", 1L);
        AccountOperation ao2 = buildAo("EFG", 1L);
        Mockito.when(accountOperationService.findById(1L)).thenReturn(ao1);
        Mockito.when(accountOperationService.findById(2L)).thenReturn(ao2);

        try {
            List<AccountOperationAndSequence> request = buildRequest(Arrays.asList(1L, 2L), Arrays.asList(0, 1));
            accountOperationApiService.matchOperations(request);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Matching action is failed : No CustomerAccount found with id 1 for matching");
        }

    }

    private List<AccountOperationAndSequence> buildRequest(List<Long> aoIds, List<Integer> seqs) {
        if (aoIds == null || aoIds.isEmpty() || seqs == null || seqs.isEmpty()) {
            throw new IllegalArgumentException("Test build : Both AoIds/Seqs must not be null and must have same size");
        }

        List<AccountOperationAndSequence> accountOperations = new ArrayList<>();

        for (int i = 0; i < aoIds.size(); i++) {
            final int indice = i;
            AccountOperationAndSequence accountOperationAndSequence = new AccountOperationAndSequence() {
                @Nonnull
                @Override
                public Integer getSequence() {
                    return seqs.get(indice);
                }

                @Nonnull
                @Override
                public Long getId() {
                    return aoIds.get(indice);
                }

                @Nullable
                @Override
                public BigDecimal getAmountToMatch() {
                    return null;
                }
            };

            accountOperations.add(accountOperationAndSequence);
        }

        return accountOperations;
    }

    private AccountOperation buildAo(String aoCode, Long customerId) {
        AccountOperation ao = new AccountOperation();
        ao.setId(1L);
        ao.setCode(aoCode);

        org.meveo.model.payments.CustomerAccount customerAccount = new org.meveo.model.payments.CustomerAccount();
        customerAccount.setId(customerId);

        ao.setCustomerAccount(customerAccount);
        return ao;
    }

    @Test
    public void should_Fill_Converted_Amounts_When_Null() {

        AccountOperation accountOperation = buildAo("ABC", 1L);

        accountOperation.setAmount(new BigDecimal(10));
        accountOperation.setMatchingAmount(new BigDecimal(5));
        accountOperation.setUnMatchingAmount(new BigDecimal(6));
        accountOperation.setTransactionalMatchingAmount(null);
        accountOperation.setTransactionalUnMatchingAmount(null);
        accountOperation.setTransactionalAmount(null);

        Assert.assertEquals(accountOperation.getTransactionalMatchingAmount(),accountOperation.getMatchingAmount());
        Assert.assertEquals(accountOperation.getTransactionalAmount(),accountOperation.getTransactionalAmount());
        Assert.assertEquals(accountOperation.getTransactionalUnMatchingAmount(),accountOperation.getUnMatchingAmount());

    }
}