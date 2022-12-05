package org.meveo.apiv2.accountreceivable;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.dto.payment.UnMatchingOperationRequestDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.apiv2.AcountReceivable.UnMatchingAccountOperationDetail;
import org.meveo.model.payments.AccountOperation;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.securityDeposit.impl.SecurityDepositTransactionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import jakarta.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class AccountOperationServiceUnMatchingTest {

    @InjectMocks
    private AccountOperationApiService accountOperationApiService;
    @Mock
    private AccountOperationService accountOperationService;
    @Mock
    private CustomerAccountService customerAccountService;
    @Mock
    private SecurityDepositTransactionService securityDepositTransactionService;

    @Test
    public void validateAndGetAOForUnmatchingNominal() throws Exception {
        AccountOperation ao1 = buildAo("CODE-1", 1L, 1L);
        AccountOperation ao2 = buildAo("CODE-2", 2L, 2L);
        Mockito.when(customerAccountService.findById(1L)).thenReturn(ao1.getCustomerAccount());
        Mockito.when(customerAccountService.findById(2L)).thenReturn(ao2.getCustomerAccount());
        Mockito.when(accountOperationService.findByIds(any())).thenReturn(Arrays.asList(ao1, ao2));
        Mockito.when(securityDepositTransactionService.getSecurityDepositCodesByAoIds(any())).thenReturn(null);

        List<UnMatchingAccountOperationDetail> request = buildRequest(Arrays.asList(1L, 2L));
        List<UnMatchingOperationRequestDto> results = accountOperationApiService.validateAndGetAOForUnmatching(request);

        Assert.assertEquals(results.size(), 2);

        List<Long> expectedAoId = Arrays.asList(1L, 2L);
        List<String> expectedCustomerCode = Arrays.asList("CODE-1", "CODE-2");

        List<Long> resultAoIds = results.stream()
                .map(UnMatchingOperationRequestDto::getAccountOperationId)
                .sorted()
                .collect(Collectors.toList());

        List<String> resultCustomerCodes = results.stream()
                .map(UnMatchingOperationRequestDto::getCustomerAccountCode)
                .sorted()
                .collect(Collectors.toList());

        Assert.assertEquals(expectedAoId, resultAoIds);
        Assert.assertEquals(expectedCustomerCode, resultCustomerCodes);

    }

    @Test
    public void validateAndGetAOForUnmatchingNominalEmptyDepositList() throws Exception {
        AccountOperation ao1 = buildAo("CODE-1", 1L, 1L);
        AccountOperation ao2 = buildAo("CODE-2", 2L, 2L);
        Mockito.when(customerAccountService.findById(1L)).thenReturn(ao1.getCustomerAccount());
        Mockito.when(customerAccountService.findById(2L)).thenReturn(ao2.getCustomerAccount());
        Mockito.when(accountOperationService.findByIds(any())).thenReturn(Arrays.asList(ao1, ao2));
        Mockito.when(securityDepositTransactionService.getSecurityDepositCodesByAoIds(any())).thenReturn(new ArrayList<>());

        List<UnMatchingAccountOperationDetail> request = buildRequest(Arrays.asList(1L, 2L));
        List<UnMatchingOperationRequestDto> results = accountOperationApiService.validateAndGetAOForUnmatching(request);

        Assert.assertEquals(results.size(), 2);

        List<Long> expectedAoId = Arrays.asList(1L, 2L);
        List<String> expectedCustomerCode = Arrays.asList("CODE-1", "CODE-2");

        List<Long> resultAoIds = results.stream()
                .map(UnMatchingOperationRequestDto::getAccountOperationId)
                .sorted()
                .collect(Collectors.toList());

        List<String> resultCustomerCodes = results.stream()
                .map(UnMatchingOperationRequestDto::getCustomerAccountCode)
                .sorted()
                .collect(Collectors.toList());

        Assert.assertEquals(expectedAoId, resultAoIds);
        Assert.assertEquals(expectedCustomerCode, resultCustomerCodes);

    }

    @Test(expected = EntityDoesNotExistsException.class)
    public void validateAndGetAOForUnmatchingExceptionEntityNotFound() {
        Mockito.when(accountOperationService.findByIds(any())).thenReturn(new ArrayList<>());

        List<UnMatchingAccountOperationDetail> request = buildRequest(List.of(1L));
        accountOperationApiService.validateAndGetAOForUnmatching(request);

    }

    @Test
    public void validateAndGetAOForUnmatchingExceptionAoSecurityDeposit() {
        AccountOperation ao1 = buildAo("ABC", 1L, 1L);
        Mockito.when(accountOperationService.findByIds(any())).thenReturn(List.of(ao1));
        Mockito.when(securityDepositTransactionService.getSecurityDepositCodesByAoIds(any())).thenReturn(List.of("A, B, C"));

        try {
            List<UnMatchingAccountOperationDetail> request = buildRequest(List.of(1L));
            accountOperationApiService.validateAndGetAOForUnmatching(request);
            Assert.fail("Exception must be thrown");
        } catch (BusinessApiException e) {
            Assert.assertEquals(e.getMessage(), "Unmatching action is failed : Cannot unmatch AO used by the SecurityDeposit codes: [A, B, C]");
        }

    }

    private List<UnMatchingAccountOperationDetail> buildRequest(List<Long> aoIds) {
        if (aoIds == null || aoIds.isEmpty()) {
            throw new IllegalArgumentException("Test build : AoIds must not be null");
        }

        List<UnMatchingAccountOperationDetail> unMatchingAccountOperationDetails = new ArrayList<>();

        for (int i = 0; i < aoIds.size(); i++) {
            final int indice = i;
            UnMatchingAccountOperationDetail unMatchingAccountOperationDetail = new UnMatchingAccountOperationDetail() {
                @Nonnull
                @Override
                public Long getId() {
                    return aoIds.get(indice);
                }

                @Override
                public List<Long> getMatchingAmountIds() {
                    return null;
                }
            };

            unMatchingAccountOperationDetails.add(unMatchingAccountOperationDetail);
        }

        return unMatchingAccountOperationDetails;
    }

    private AccountOperation buildAo(String aoCode, Long aoId, Long customerId) {
        AccountOperation ao = new AccountOperation();
        ao.setId(aoId);
        ao.setCode(aoCode);

        org.meveo.model.payments.CustomerAccount customerAccount = new org.meveo.model.payments.CustomerAccount();
        customerAccount.setId(customerId);
        customerAccount.setCode(aoCode);

        ao.setCustomerAccount(customerAccount);
        return ao;
    }
}