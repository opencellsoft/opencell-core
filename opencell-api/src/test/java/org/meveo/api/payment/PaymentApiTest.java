package org.meveo.api.payment;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.NoAllOperationUnmatchedException;
import org.meveo.admin.exception.UnbalanceAmountException;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.payment.PaymentDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.Journal;
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.JournalService;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentService;
import org.meveo.util.ApplicationProvider;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class PaymentApiTest {

    static class PaymentApiMock extends PaymentApi {
        @Override
        protected ICustomFieldEntity populateCustomFields(CustomFieldsDto customFieldsDto, ICustomFieldEntity entity, boolean isNewEntity) throws MeveoApiException {
        return null;
    }
    }


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


}
