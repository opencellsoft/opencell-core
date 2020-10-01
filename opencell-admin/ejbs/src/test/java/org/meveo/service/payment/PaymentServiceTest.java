package org.meveo.service.payment;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.Payment;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.PaymentService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private AccountOperationService accountOperationService;

    @Test
    public void test_set_sum_and_orders_num() {
        List<Long> aoIdsToPay = List.of(1L, 2L, 3L);
        AccountOperation firstAccOperation = createAccountOperation("1", 2, 18);
        when(accountOperationService.findById(1L)).thenReturn(firstAccOperation);
        AccountOperation secondAccOperation = createAccountOperation("2", 3, 47);
        when(accountOperationService.findById(2L)).thenReturn(secondAccOperation);
        AccountOperation accOperationWithoutNumber = createAccountOperation(null, 5, 74);
        when(accountOperationService.findById(3L)).thenReturn(accOperationWithoutNumber);



        Payment payment = new Payment();
        paymentService.setSumAndOrdersNumber(payment, aoIdsToPay);

        assertThat(payment.getTaxAmount()).isEqualTo(valueOf(10));
        assertThat(payment.getAmountWithoutTax()).isEqualTo(valueOf(139));
        assertThat(payment.getOrderNumber()).isEqualTo("1|2|");
    }

    private AccountOperation createAccountOperation(String orderNumber, int taxAmount, int amountWithoutTax) {
        AccountOperation t = new AccountOperation();
        t.setOrderNumber(orderNumber);
        t.setTaxAmount(valueOf(taxAmount));
        t.setAmountWithoutTax(valueOf(amountWithoutTax));
        return t;
    }
}
