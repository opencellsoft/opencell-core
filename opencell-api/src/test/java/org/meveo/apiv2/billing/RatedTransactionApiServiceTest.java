package org.meveo.apiv2.billing;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.meveo.model.billing.RatedTransactionStatusEnum.BILLED;
import static org.meveo.model.billing.RatedTransactionStatusEnum.OPEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.apiv2.billing.service.RatedTransactionApiService;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.Query;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class RatedTransactionApiServiceTest {

    @Mock
    private RatedTransactionService ratedTransactionService;

    @Mock
    private QueryBuilder queryBuilder;

    @Mock
    private Query query;

    @InjectMocks
    private RatedTransactionApiService ratedTransactionApiService;

    @Before
    public void setUp() {
        when(ratedTransactionService.getQuery(any())).thenReturn(queryBuilder);
        when(queryBuilder.getQuery(any())).thenReturn(query);
    }

    @Test
    public void shouldCancelRatedTransactions() {
        List<RatedTransaction> ratedTransactions = new ArrayList<>();
        RatedTransaction ratedTransaction = new RatedTransaction();
        ratedTransaction.setId(1L);
        ratedTransaction.setCode("1L");
        ratedTransactions.add(ratedTransaction);
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", OPEN);

        when(query.getResultList()).thenReturn(ratedTransactions);

        Map.Entry<String, String> result =
                ratedTransactionApiService.cancelRatedTransactions(filters, false, false);
        String expectedMessage = "1 RTs cancelled";

        assertNotNull(result);
        assertEquals("SUCCESS", result.getKey());
        assertEquals(expectedMessage, result.getValue());
    }

    @Test
    public void shouldFailIfNoRatedTransactionFound() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", OPEN);

        when(query.getResultList()).thenReturn(emptyList());

        Exception exception = assertThrows(NotFoundException.class, () ->
                ratedTransactionApiService.cancelRatedTransactions(filters, false, false));

        String expectedMessage = "No rated transaction found to cancel";

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void testFailOnIncorrectStatusOption() {
        List<RatedTransaction> ratedTransactions = new ArrayList<>();
        RatedTransaction ratedTransaction = new RatedTransaction();
        ratedTransaction.setId(1L);
        ratedTransaction.setCode("1L");
        ratedTransaction.setStatus(OPEN);
        RatedTransaction ratedTransaction2 = new RatedTransaction();
        ratedTransaction2.setId(2L);
        ratedTransaction2.setCode("2L");
        ratedTransaction2.setStatus(BILLED);
        ratedTransactions.add(ratedTransaction);
        ratedTransactions.add(ratedTransaction2);
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", OPEN);

        when(query.getResultList()).thenReturn(ratedTransactions);

        Map.Entry<String, String> result =
                ratedTransactionApiService.cancelRatedTransactions(filters, true, false);

        assertNotNull(result);
        assertEquals("FAILS", result.getKey());
        assertTrue(result.getValue().contains("Cancellation process stopped"));
    }

    @Test
    public void testReturnRTsOption() {
        List<RatedTransaction> ratedTransactions = new ArrayList<>();
        RatedTransaction ratedTransaction = new RatedTransaction();
        ratedTransaction.setId(1L);
        ratedTransaction.setCode("1L");
        ratedTransaction.setStatus(OPEN);
        RatedTransaction ratedTransaction2 = new RatedTransaction();
        ratedTransaction2.setId(2L);
        ratedTransaction2.setCode("2L");
        ratedTransaction2.setStatus(OPEN);
        ratedTransactions.add(ratedTransaction);
        ratedTransactions.add(ratedTransaction2);
        Map<String, Object> filters = new HashMap<>();
        filters.put("status", OPEN);

        when(query.getResultList()).thenReturn(ratedTransactions);

        Map.Entry<String, String> result =
                ratedTransactionApiService.cancelRatedTransactions(filters, false, true);

        assertNotNull(result);
        assertEquals("SUCCESS", result.getKey());
        assertTrue(result.getValue().contains(" RTs cancelled, having ids :"));
    }
}
