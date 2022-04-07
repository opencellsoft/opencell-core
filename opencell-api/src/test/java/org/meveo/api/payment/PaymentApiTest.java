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
import org.meveo.model.payments.OCCTemplate;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.meveo.service.payments.impl.OCCTemplateService;
import org.meveo.service.payments.impl.PaymentHistoryService;
import org.meveo.service.payments.impl.PaymentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentApiTest {

    class PaymentApiMock extends PaymentApi {
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
    private PaymentHistoryService paymentHistoryService;




    @Test
    public void createPayment_throwNoExceptionWhenCustomerIsNull() throws UnbalanceAmountException, NoAllOperationUnmatchedException {
        PaymentDto paymentDto = new PaymentDto();

        paymentDto.setAmount(BigDecimal.valueOf(100L));
        paymentDto.setOccTemplateCode("PAY_CHK");
        paymentDto.setReference("123456789");
        paymentDto.setPaymentMethod(PaymentMethodEnum.CHECK);

        doReturn(null).when(customerAccountService).findByCode(any());
        doReturn(new OCCTemplate()).when(oCCTemplateService).findByCode(any());
        doNothing().when(paymentService).create(any());
        doNothing().when(paymentHistoryService).addHistory(any(), any(), any(), any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any());


        Long id = paymentApi.createPayment(paymentDto);
        Assert.assertNull(id);

    }


}
