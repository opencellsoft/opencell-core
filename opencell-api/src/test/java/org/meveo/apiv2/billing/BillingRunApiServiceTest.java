package org.meveo.apiv2.billing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.meveo.model.billing.BillingRunStatusEnum.*;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.meveo.apiv2.billing.service.BillingRunApiService;
import org.meveo.model.billing.BillingRun;
import org.meveo.service.billing.impl.BillingRunService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.BadRequestException;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class BillingRunApiServiceTest {

    @InjectMocks
    private BillingRunApiService billingRunApiService;

    @Mock
    private BillingRunService billingRunService;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldUpdateBillingRunStatus() {
        BillingRun billingRun = new BillingRun();
        billingRun.setId(1L);
        billingRun.setStatus(DRAFT_INVOICES);
        billingRun.setDisabled(false);
        when(billingRunService.findById(1L)).thenReturn(billingRun);
        when(billingRunService.update(billingRun)).thenReturn(billingRun);

        BillingRun updatedBR = billingRunApiService.advancedStatus(1L, false).get();

        assertEquals(POSTVALIDATED, updatedBR.getStatus());
    }

    @Test
    public void shouldNotUpdateStatus() {
        BillingRun billingRun = new BillingRun();
        billingRun.setId(1L);
        billingRun.setStatus(NEW);
        when(billingRunService.findById(1L)).thenReturn(billingRun);

        BillingRun updatedBR = billingRunApiService.advancedStatus(1L, false).get();

        assertEquals(NEW, updatedBR.getStatus());
    }

    @Test
    public void shouldReturnEmptyIfBillingRunDoesNotExists() {
        when(billingRunService.findById(1L)).thenReturn(null);

        Optional<BillingRun> billingRun = billingRunApiService.advancedStatus(1L, false);

        assertFalse(billingRun.isPresent());
    }

    @Test
    public void shouldThrowExceptionForNonEligibleStatus() {
        BillingRun billingRun = new BillingRun();
        billingRun.setId(1L);
        billingRun.setStatus(VALIDATED);

        when(billingRunService.findById(1L)).thenReturn(billingRun);
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Billing run status must be either {NEW, OPEN, INVOICE_LINES_CREATED, DRAFT_INVOICES, REJECTED}");

        billingRunApiService.advancedStatus(1L, false).get();
    }
}