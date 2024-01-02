package org.meveo.api.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.payments.ImmutableImportRejectionCodeInput;
import org.meveo.apiv2.payments.ImmutablePaymentGatewayInput;
import org.meveo.apiv2.payments.ImmutableRejectionCode;
import org.meveo.apiv2.payments.ImportRejectionCodeInput;
import org.meveo.apiv2.payments.PaymentGatewayInput;
import org.meveo.apiv2.payments.RejectionCode;
import org.meveo.apiv2.payments.RejectionCodesExportResult;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.Journal;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.payments.PaymentRejectionCode;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.JournalService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentGatewayService;
import org.meveo.service.payments.impl.PaymentHistoryService;
import org.meveo.service.payments.impl.PaymentRejectionCodeService;
import org.meveo.service.payments.impl.PaymentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.NotFoundException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class PaymentApiTest {

    static class PaymentApiMock extends PaymentApi {
        @Override
        protected ICustomFieldEntity populateCustomFields(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity, boolean isNewEntity) throws MeveoApiException {
        return null;
    }
    }


    @Rule
    public final ExpectedException expectedException = none();

    @InjectMocks()
    private PaymentApi paymentApi = new PaymentApiMock();

    @Mock
    private CustomerAccountService customerAccountService;

    @Mock
    private OCCTemplateService oCCTemplateService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private JournalService journalService;

    @Mock
    private TradingCurrencyService tradingCurrencyService;

    @Mock
    private Provider applicationProvider;

    @Mock
    private AccountOperationService accountOperationService;

    @Mock
    private PaymentHistoryService paymentHistoryService;

    @Mock
    private PaymentGatewayService paymentGatewayService;

    @Mock
    private PaymentRejectionCodeService paymentRejectionCodeService;


    @Test
    public void createPayment_throwNoExceptionWhenCustomerIsNull() throws UnbalanceAmountException, NoAllOperationUnmatchedException {
        PaymentDto paymentDto = new PaymentDto();

        paymentDto.setAmount(BigDecimal.valueOf(100L));
        paymentDto.setOccTemplateCode("PAY_CHK");
        paymentDto.setReference("123456789");
        paymentDto.setPaymentMethod(PaymentMethodEnum.CHECK);

        doReturn(null).when(customerAccountService).findByCode(any());
        doReturn(new OCCTemplate()).when(oCCTemplateService).findByCode(any());
        doReturn(new Journal()).when(journalService).findByCode(any());
        doNothing().when(paymentService).create(any());
        doNothing().when(accountOperationService).handleAccountingPeriods(any());
        Long id = paymentApi.createPayment(paymentDto);
        Assert.assertNull(id);

    }

    @Test
    public void should_fail_to_create_payment_rejection_code_if_payment_gateway_found() {
        RejectionCode rejectionCode = ImmutableRejectionCode.builder().code("CODE_RC")
                .description("DESCRIPTION")
                .paymentGateway(ImmutableResource.builder().id(1L).build())
                .build();

        when(paymentGatewayService.findById(any())).thenReturn(null);

        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Payment gateway not found");
        paymentApi.createPaymentRejectionCode(rejectionCode);
    }

    @Test
    public void should_update_payment_rejection_code() {
        RejectionCode rejectionCode = ImmutableRejectionCode.builder().code("CODE_RC")
                .description("DESCRIPTION")
                .paymentGateway(ImmutableResource.builder().id(1L).build())
                .build();
        PaymentGateway paymentGateway = new PaymentGateway();
        paymentGateway.setId(1L);
        PaymentRejectionCode entity = new PaymentRejectionCode();
        entity.setCode("CODE_RC");
        entity.setId(1L);

        when(paymentGatewayService.findById(any())).thenReturn(paymentGateway);
        when(paymentRejectionCodeService.findById(any())).thenReturn(entity);
        when(paymentRejectionCodeService.update(any())).thenReturn(entity);

        RejectionCode updatedEntity = paymentApi.updatePaymentRejectionCode(1L, rejectionCode);

        assertTrue(updatedEntity instanceof RejectionCode);
    }

    @Test
    public void should_fail_to_update_payment_rejection_code_if_payment_gateway_not_found() {
        RejectionCode rejectionCode = ImmutableRejectionCode.builder().code("CODE_RC")
                .description("DESCRIPTION")
                .paymentGateway(ImmutableResource.builder().id(1L).build())
                .build();
        PaymentGateway paymentGateway = new PaymentGateway();
        paymentGateway.setId(1L);

        when(paymentGatewayService.findById(any())).thenReturn(paymentGateway);

        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Payment rejection code not found");

        paymentApi.updatePaymentRejectionCode(1L, rejectionCode);
    }

    @Test
    public void should_fail_to_update_payment_rejection_code_if_if_payment_gateway_found() {
        RejectionCode rejectionCode = ImmutableRejectionCode.builder().code("CODE_RC")
                .description("DESCRIPTION")
                .paymentGateway(ImmutableResource.builder().id(1L).build())
                .build();
        PaymentGateway paymentGateway = new PaymentGateway();
        paymentGateway.setId(1L);

        when(paymentGatewayService.findById(any())).thenReturn(null);

        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Payment gateway not found");

        paymentApi.updatePaymentRejectionCode(1L, rejectionCode);
    }

    @Test
    public void should_remove_rejection_code() {
        PaymentRejectionCode entity = new PaymentRejectionCode();
        entity.setCode("CODE_RC");
        entity.setId(1L);

        when(paymentRejectionCodeService.findById(any())).thenReturn(entity);

        paymentApi.removeRejectionCode(1L);

        verify(paymentRejectionCodeService, times(1)).remove(entity);
    }

    @Test
    public void should_export_rejection_code() {
        PaymentRejectionCode entity = new PaymentRejectionCode();
        entity.setCode("CODE_RC");
        entity.setId(1L);

        PaymentGatewayInput paymentGatewayInput = ImmutablePaymentGatewayInput.builder()
                .paymentGateway(ImmutableResource.builder().id(1L).build())
                .build();
        PaymentGateway paymentGateway = new PaymentGateway();
        paymentGateway.setId(1L);
        Map<String, Object> exportResult = new HashMap<>();
        exportResult.put("FILE_PATH", "PATH/AAA");
        exportResult.put("EXPORT_SIZE", 1);
        when(paymentGatewayService.findById(any())).thenReturn(paymentGateway);
        when(paymentRejectionCodeService.export(any())).thenReturn(exportResult);

        RejectionCodesExportResult export = paymentApi.export(paymentGatewayInput);

        assertEquals(1, export.getExportSize().intValue());
    }

    @Test
    public void should_fail_to_export_rejection_code_if_gateway_not_found() {
        PaymentRejectionCode entity = new PaymentRejectionCode();
        entity.setCode("CODE_RC");
        entity.setId(1L);

        PaymentGatewayInput paymentGatewayInput = ImmutablePaymentGatewayInput.builder()
                .paymentGateway(ImmutableResource.builder().id(1L).build())
                .build();
        when(paymentGatewayService.findById(any())).thenReturn(null);

        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Payment gateway not found");

        paymentApi.export(paymentGatewayInput);
    }

    @Test
    public void should_validate_if_encoded_file_is_provided() {
        PaymentRejectionCode entity = new PaymentRejectionCode();
        entity.setCode("CODE_RC");
        entity.setId(1L);

        ImportRejectionCodeInput importRejectionCodeInput = ImmutableImportRejectionCodeInput.builder()
                .base64csv("")
                .build();

        expectedException.expect(BusinessApiException.class);
        expectedException.expectMessage("Encoded file should not be null or empty");

        paymentApi.importRejectionCodes(importRejectionCodeInput);
    }
}
