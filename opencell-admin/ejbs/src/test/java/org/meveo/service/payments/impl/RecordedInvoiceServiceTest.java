package org.meveo.service.payments.impl;

import static java.math.BigDecimal.TEN;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Date.from;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.rules.ExpectedException.none;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.DISPUTED;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.PAID;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.PENDING;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.PPAID;
import static org.meveo.model.billing.InvoicePaymentStatusEnum.UNPAID;
import static org.meveo.model.billing.InvoiceStatusEnum.VALIDATED;
import static org.meveo.model.payments.MatchingStatusEnum.I;
import static org.meveo.model.payments.MatchingStatusEnum.L;
import static org.meveo.model.payments.MatchingStatusEnum.O;
import static org.meveo.model.payments.MatchingStatusEnum.P;
import static org.meveo.model.payments.OperationCategoryEnum.CREDIT;
import static org.meveo.model.payments.OperationCategoryEnum.DEBIT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.Auditable;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoicePaymentStatusEnum;
import org.meveo.model.payments.OperationCategoryEnum;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.security.MeveoUser;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.enterprise.event.Event;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class RecordedInvoiceServiceTest {

    @Rule
    public ExpectedException expectedException = none();

    private static final String LITIGATION_REASON = "LITIGATION_REASON";

    @InjectMocks
    private RecordedInvoiceService recordedInvoiceService;

    @Mock
    private MeveoUser currentUser;

    @Mock
    private Event<BaseEntity> entityUpdatedEventProducer;

    @Mock
    private CustomFieldInstanceService customFieldInstanceService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private EntityManagerWrapper emWrapper;

    @Before
    public void setUp() {
        when(entityManager.contains(any())).thenReturn(true);
        when(emWrapper.getEntityManager()).thenReturn(entityManager);
    }

    @Test
    public void shouldSetLitigationReason() {
        RecordedInvoice recordedInvoice = createRecordedInvoice(true, DEBIT, PPAID);

        when(entityManager.find(any(), anyLong())).thenReturn(recordedInvoice);
        when(recordedInvoiceService.findById(recordedInvoice.getId())).thenReturn(recordedInvoice);

        RecordedInvoice disputedRecordedInvoice =
                recordedInvoiceService.setLitigation(recordedInvoice, LITIGATION_REASON);

        assertEquals(LITIGATION_REASON, disputedRecordedInvoice.getLitigationReason());
        assertEquals(I, disputedRecordedInvoice.getMatchingStatus());
        assertEquals(DISPUTED, disputedRecordedInvoice.getInvoice().getPaymentStatus());
    }

    @Test
    public void shouldFailsIfAONotFound() {
        expectedException.expectMessage("Account operation not found");
        expectedException.expect(BusinessException.class);

        recordedInvoiceService.setLitigation(null, LITIGATION_REASON);
    }

    @Test
    public void shouldFailsIfAOHasNoInvoice() {
        RecordedInvoice recordedInvoice = createRecordedInvoice(false, DEBIT, PENDING);

        when(entityManager.find(any(), anyLong())).thenReturn(recordedInvoice);
        when(recordedInvoiceService.findById(recordedInvoice.getId())).thenReturn(recordedInvoice);

        expectedException.expectMessage("No invoice associated to account operation");
        expectedException.expect(BusinessException.class);

        recordedInvoiceService.setLitigation(recordedInvoice, LITIGATION_REASON);
    }

    @Test
    public void shouldFailsIfTransactionCategoryNotDEBIT() {
        RecordedInvoice recordedInvoice = createRecordedInvoice(true, CREDIT, PENDING);

        when(entityManager.find(any(), anyLong())).thenReturn(recordedInvoice);
        when(recordedInvoiceService.findById(recordedInvoice.getId())).thenReturn(recordedInvoice);

        expectedException.expectMessage("Account operation transaction category should be DEBIT");
        expectedException.expect(BusinessException.class);

        recordedInvoiceService.setLitigation(recordedInvoice, LITIGATION_REASON);
    }

    @Test
    public void shouldFailsIfInvoicePaymentStatusNotInPossibleStatus() {
        RecordedInvoice recordedInvoice = createRecordedInvoice(true, DEBIT, PAID);

        when(entityManager.find(any(), anyLong())).thenReturn(recordedInvoice);
        when(recordedInvoiceService.findById(recordedInvoice.getId())).thenReturn(recordedInvoice);

        expectedException.expectMessage("Invoice payment status should be in (PENDING, PPAID, UNPAID, DISPUTED)");
        expectedException.expect(BusinessException.class);

        recordedInvoiceService.setLitigation(recordedInvoice, LITIGATION_REASON);
    }

    @Test
    public void shouldRemoveLitigationStatusAndSetPaymentStatusToPaid() {
        Date dueDate = from(Instant.parse("2023-12-31T00:00:00.000Z"));
        RecordedInvoice recordedInvoice = createLitigatedAO(true, dueDate, ZERO);

        when(entityManager.find(any(), anyLong())).thenReturn(recordedInvoice);
        when(recordedInvoiceService.findById(recordedInvoice.getId())).thenReturn(recordedInvoice);

        RecordedInvoice result = recordedInvoiceService.removeLitigation(recordedInvoice, LITIGATION_REASON);

        assertNotNull(result);
        assertEquals(L, result.getMatchingStatus());
        assertEquals(LITIGATION_REASON, result.getLitigationReason());
        assertEquals(PAID, result.getInvoice().getPaymentStatus());
    }

    @Test
    public void shouldRemoveLitigationStatusAndSetPaymentStatusToPPaid() {
        Date dueDate = from(Instant.parse("2023-12-31T00:00:00.000Z"));
        RecordedInvoice recordedInvoice = createLitigatedAO(false, dueDate, valueOf(5l));

        when(entityManager.find(any(), anyLong())).thenReturn(recordedInvoice);
        when(recordedInvoiceService.findById(recordedInvoice.getId())).thenReturn(recordedInvoice);

        RecordedInvoice result = recordedInvoiceService.removeLitigation(recordedInvoice, LITIGATION_REASON);

        assertNotNull(result);
        assertEquals(P, result.getMatchingStatus());
        assertEquals(LITIGATION_REASON, result.getLitigationReason());
        assertEquals(PPAID, result.getInvoice().getPaymentStatus());
    }

    @Test
    public void shouldRemoveLitigationStatusAndSetPaymentStatusToPending() {
        Date dueDate = from(Instant.parse("2023-12-31T00:00:00.000Z"));
        RecordedInvoice recordedInvoice = createLitigatedAO(false, dueDate, valueOf(0));

        when(entityManager.find(any(), anyLong())).thenReturn(recordedInvoice);
        when(recordedInvoiceService.findById(recordedInvoice.getId())).thenReturn(recordedInvoice);

        RecordedInvoice result = recordedInvoiceService.removeLitigation(recordedInvoice, LITIGATION_REASON);

        assertNotNull(result);
        assertEquals(O, result.getMatchingStatus());
        assertEquals(LITIGATION_REASON, result.getLitigationReason());
        assertEquals(PENDING, result.getInvoice().getPaymentStatus());
    }

    @Test
    public void shouldRemoveLitigationStatusAndSetPaymentStatusToUnpaid() {
        Date dueDate = from(Instant.parse("2023-11-30T00:00:00.000Z"));
        RecordedInvoice recordedInvoice = createLitigatedAO(false, dueDate, valueOf(0));

        when(entityManager.find(any(), anyLong())).thenReturn(recordedInvoice);
        when(recordedInvoiceService.findById(recordedInvoice.getId())).thenReturn(recordedInvoice);

        RecordedInvoice result = recordedInvoiceService.removeLitigation(recordedInvoice, null);

        assertNotNull(result);
        assertEquals(O, result.getMatchingStatus());
        assertNull(result.getLitigationReason());
        assertEquals(UNPAID, result.getInvoice().getPaymentStatus());
    }

    @Test
    public void shouldFailsIfAOToRemoveFromLitigationNotFound() {
        RecordedInvoice recordedInvoice = new RecordedInvoice();
        recordedInvoice.setId(2l);

        expectedException.expectMessage("Account operation not found");
        expectedException.expect(BusinessException.class);

        recordedInvoiceService.removeLitigation(recordedInvoice, LITIGATION_REASON);
    }

    @Test
    public void shouldFailToRemoveLitigationIfAONotInLitigation() {
        RecordedInvoice recordedInvoice = createRecordedInvoice(true, DEBIT, PAID);
        final String expectedErrorMessage = "Account operation [id=" + recordedInvoice.getId() + "] is not in litigation";

        when(entityManager.find(any(), anyLong())).thenReturn(recordedInvoice);
        when(recordedInvoiceService.findById(recordedInvoice.getId())).thenReturn(recordedInvoice);

        expectedException.expectMessage(expectedErrorMessage);
        expectedException.expect(BusinessException.class);

        recordedInvoiceService.removeLitigation(recordedInvoice, LITIGATION_REASON);
    }

    private RecordedInvoice createRecordedInvoice(boolean withInvoice,
                                                  OperationCategoryEnum transactionCategory,
                                                  InvoicePaymentStatusEnum paymentStatus) {
        RecordedInvoice recordedInvoice = new RecordedInvoice();
        recordedInvoice.setId(1L);
        Auditable auditable = new Auditable();
        auditable.setCreated(new Date());
        auditable.setCreator("CREATOR");
        auditable.setUpdated(new Date());
        auditable.setUpdater("UPDATER");
        recordedInvoice.setAuditable(auditable);
        recordedInvoice.setCode("RecordedInvoice_CODE");
        recordedInvoice.setInvoiceDate(new Date());
        recordedInvoice.setTransactionCategory(transactionCategory);
        recordedInvoice.setNetToPay(TEN);
        if(withInvoice) {
            Invoice invoice = new Invoice();
            invoice.setId(1L);
            invoice.setStatus(VALIDATED);
            invoice.setPaymentStatus(paymentStatus);
            recordedInvoice.setInvoice(invoice);
        }
        return recordedInvoice;
    }

    private RecordedInvoice createLitigatedAO(boolean fullyPaid, Date dueDate, BigDecimal matchingAmount) {
        RecordedInvoice recordedInvoice = new RecordedInvoice();
        recordedInvoice.setId(1L);
        Auditable auditable = new Auditable();
        auditable.setCreated(new Date());
        auditable.setCreator("CREATOR");
        auditable.setUpdated(new Date());
        auditable.setUpdater("UPDATER");
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setPaymentStatus(DISPUTED);
        invoice.setDueDate(dueDate);
        recordedInvoice.setInvoice(invoice);
        recordedInvoice.setAmount(TEN);
        recordedInvoice.setMatchingStatus(I);
        if (fullyPaid) {
            recordedInvoice.setMatchingAmount(recordedInvoice.getAmount());
            recordedInvoice.setUnMatchingAmount(ZERO);
        } else {
            recordedInvoice.setMatchingAmount(matchingAmount);
            recordedInvoice
                    .setUnMatchingAmount(recordedInvoice.getAmount().subtract(recordedInvoice.getMatchingAmount()));
        }
        return recordedInvoice;
    }
}