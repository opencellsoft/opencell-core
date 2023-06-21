package org.meveo.service.billing.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceStatusEnum;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.LinkedInvoice;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.meveo.service.payments.impl.AccountOperationService;
import org.meveo.service.payments.impl.MatchingCodeService;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvoiceServiceAutoMatchingTest {
    @InjectMocks
    private InvoiceService invoiceService;
    @Mock
    private EntityManager entityManager;
    @Mock
    private EntityManagerWrapper emWrapper;
    @Mock
    private AccountOperationService accountOperationService;
    @Mock
    private MatchingCodeService matchingCodeService;
    @Mock
    private InvoiceTypeService invoiceTypeService;

    @Before
    public void setUp() {
        when(emWrapper.getEntityManager()).thenReturn(entityManager);
    }

    @Test
    public void autoMatchingInvoiceNominal() {
        // Adj data : Invoice, Type, AO
        InvoiceType typeAdj = buildInvoiceType("ADJ");
        Invoice adjInv = buildInvoice(1L, typeAdj);
        adjInv.setAutoMatching(true);
        AccountOperation adjAo = buildAccountOperation(1L);

        // Original invoice  data : Invoice, Type, AO
        InvoiceType typeCom = buildInvoiceType("COM");
        Invoice originalInv = buildInvoice(-1L, typeCom);
        AccountOperation originalAo = buildAccountOperation(2L);

        LinkedInvoice linkedInvoice = new LinkedInvoice();
        linkedInvoice.setInvoice(originalInv);
        linkedInvoice.setLinkedInvoiceValue(adjInv);

        TypedQuery query = mock(TypedQuery.class);
        when(invoiceService.getEntityManager().createNamedQuery("LinkedInvoice.findBySourceInvoiceByAdjId")).thenReturn(query);
        when(query.setParameter(ArgumentMatchers.anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(linkedInvoice));

        when(invoiceTypeService.getListAdjustementCode()).thenReturn(List.of("ADJ"));
        when(accountOperationService.listByInvoice(linkedInvoice.getInvoice())).thenReturn(List.of(originalAo));
        when(accountOperationService.listByInvoice(linkedInvoice.getLinkedInvoiceValue())).thenReturn(List.of(adjAo));

        try {
            invoiceService.autoMatchingAdjInvoice(adjInv, null);
        } catch (Exception e) {
            Assert.fail("Error during auto matching adjInv : " + e.getMessage());
        }
    }

    @Test
    public void autoMatchingInvoiceWithGeneratedAdjAoNominal() {
        // Adj data : Invoice, Type, AO
        InvoiceType typeAdj = buildInvoiceType("ADJ");
        Invoice adjInv = buildInvoice(1L, typeAdj);
        adjInv.setAutoMatching(true);
        AccountOperation adjAo = buildAccountOperation(1L);

        // Original invoice  data : Invoice, Type, AO
        InvoiceType typeCom = buildInvoiceType("COM");
        Invoice originalInv = buildInvoice(-1L, typeCom);
        AccountOperation originalAo = buildAccountOperation(2L);

        LinkedInvoice linkedInvoice = new LinkedInvoice();
        linkedInvoice.setInvoice(originalInv);
        linkedInvoice.setLinkedInvoiceValue(adjInv);

        TypedQuery query = mock(TypedQuery.class);
        when(invoiceService.getEntityManager().createNamedQuery("LinkedInvoice.findBySourceInvoiceByAdjId")).thenReturn(query);
        when(query.setParameter(ArgumentMatchers.anyString(), any())).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(linkedInvoice));

        when(invoiceTypeService.getListAdjustementCode()).thenReturn(List.of("ADJ"));
        when(accountOperationService.listByInvoice(linkedInvoice.getInvoice())).thenReturn(List.of(originalAo));

        try {
            invoiceService.autoMatchingAdjInvoice(adjInv, adjAo);
        } catch (Exception e) {
            Assert.fail("Error during auto matching adjInv : " + e.getMessage());
        }
    }

    private AccountOperation buildAccountOperation(long id) {
        AccountOperation adjAo = new AccountOperation();
        adjAo.setId(id);
        adjAo.setUnMatchingAmount(BigDecimal.TEN);
        adjAo.setMatchingStatus(MatchingStatusEnum.O);
        adjAo.setCustomerAccount(new CustomerAccount());
        return adjAo;
    }

    private Invoice buildInvoice(long id, InvoiceType typeAdj) {
        Invoice adjInv = new Invoice();
        adjInv.setId(id);
        adjInv.setInvoiceNumber("INV_NUMB1");
        adjInv.setStatus(InvoiceStatusEnum.NEW);
        adjInv.setLastAppliedRate(new BigDecimal("1.12"));
        adjInv.setLastAppliedRateDate(new Date());
        adjInv.setDueDate(new Date());
        adjInv.setAmountTax(BigDecimal.TEN);
        adjInv.setAmountWithTax(new BigDecimal(20));
        adjInv.setAmountWithoutTax(BigDecimal.TEN);
        adjInv.setInvoiceType(typeAdj);
        return adjInv;
    }

    private InvoiceType buildInvoiceType(String ADJ) {
        InvoiceType typeAdj = new InvoiceType();
        typeAdj.setCode(ADJ);
        return typeAdj;
    }
}
