package org.meveo.service.billing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.commons.utils.MethodCallingUtils;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.service.billing.impl.ReratingService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.test.JPAQuerySimulation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class ReratingServiceTest {

    @Spy
    @InjectMocks
    ReratingService reratingService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private MethodCallingUtils methodCallingUtils;

    @Mock
    private WalletOperationService walletOperationService;

    @Before
    public void setUp() {

        doReturn(entityManager).when(reratingService).getEntityManager();

        when(entityManager.find(eq(BillingRun.class), anyLong())).thenAnswer(new Answer<BillingRun>() {
            @Override
            public BillingRun answer(InvocationOnMock invocation) throws Throwable {

                Long brId = invocation.getArgument(1);

                BillingRun br = new BillingRun();
                br.setId(brId);

                if (brId.equals(401L)) {
                    BillingCycle bc = new BillingCycle();
                    bc.setAggregateUnitAmounts(true);
                    br.setBillingCycle(bc);
                }

                return br;
            }
        });

        when(entityManager.find(eq(WalletOperation.class), anyLong())).thenAnswer(new Answer<WalletOperation>() {
            @Override
            public WalletOperation answer(InvocationOnMock invocation) throws Throwable {

                Long woId = invocation.getArgument(1);

                WalletOperation wo = new WalletOperation();
                wo.setId(woId);
                if (woId.equals(0L)) {
                    wo.setStatus(WalletOperationStatusEnum.OPEN);
                } else {
                    wo.setStatus(WalletOperationStatusEnum.TO_RERATE);
                }

                if (woId.equals(1L)) {
                    RatedTransaction rt = new RatedTransaction();
                    rt.setId(201L);
                    wo.setRatedTransaction(rt);
                }

                UserAccount ua = new UserAccount();
                WalletInstance wallet = new WalletInstance();
                wallet.setUserAccount(ua);
                wo.setWallet(wallet);
                wo.setUserAccount(ua);

                return wo;
            }
        });

//1 ToRerate
// 2 Open
//  102 Rated
//     4 Open
//       7 Open
//        105 Rated
//           9 Treated
//             206 Billed
//                304 Open
//       8 Treated
//        205 Billed
//           301 Open
//  103 Rated
//     5 Treated
//       204 Billed
//          302 OPEN
// 3 Treated
//  104 Rated
//     6 Canceled
//  203 Billed
//     301 Open
// 101 Open
// 201 Open

        // WO is a single digit, EDRs are prefixed with 100, RTS with 200, ILs with 300 and BRs with 400
        // wo.id, wo.status, wo.rated_transaction_id, wo.discounted_wallet_operation_id
        Object[][] discountWos = new Object[][] { //
                { 2L, "OPEN", null, 1L }, //
                { 3L, "TREATED", 203L, 1L }, //
                { 7L, "OPEN", null, 4L }, //
                { 8L, "TREATED", 205L, 4L } };

        // edr.id, edr.status, wo.id as woid, wo.status as wostatus, wo.rated_transaction_id, edr.wallet_operation_id
        Object[][] edrs = new Object[][] { //
                { 101L, "OPEN", null, null, null, 1L }, // EDR triggered by WO 1
                { 102L, "RATED", 4L, "OPEN", null, 2L }, // EDR triggered by WO 2 that has WO but not RT
                { 103L, "RATED", 5L, "TREATED", 204L, 2L }, // EDR triggered by WO 2 that has WO and RT
                { 104L, "RATED", 6L, "CANCELED", null, 3L }, // EDR triggered by WO 3 and is canceled;
                { 105L, "RATED", 9L, "TREATED", 206L, 7L } }; // EDR triggered by WO 7 that has WO and RT

        // rt.id, rt.status, rt.amount_without_tax, rt.amount_with_tax, rt.amount_tax, rt.quantity, rt.invoice_line_id, il.status as ilstatus, il.billing_run_id
        Object[][] rts = new Object[][] { //
                { 201L, "OPEN", 5L, 7L, 2L, 1L, null, null, 401L }, //
                { 203L, "BILLED", 5L, 7L, 2L, 1L, 301L, "OPEN", 401L }, //
                { 204L, "BILLED", 5L, 7L, 2L, 1L, 302L, "OPEN", 401L }, //
                { 205L, "BILLED", 5L, 7L, 2L, 1L, 301L, "OPEN", 401L }, //
                { 206L, "BILLED", 5L, 7L, 2L, 1L, 304L, "OPEN", 402L } };

        JPAQuerySimulation<Object[]> discountWoSummaryQuery = new JPAQuerySimulation<Object[]>() {
            @SuppressWarnings("unchecked")
            @Override
            public List<Object[]> getResultList() {

                List<Object[]> values = new ArrayList<>();

                for (Long woId : (List<Long>) getParameterRaw("woIds")) {
                    for (Object[] discountInfo : discountWos) {
                        if (woId.equals(discountInfo[3])) {
                            values.add(new Object[] { BigInteger.valueOf((Long) discountInfo[0]), discountInfo[1], discountInfo[2] != null ? BigInteger.valueOf((Long) discountInfo[2]) : null });
                        }
                    }
                }
                return values;
            }
        };

        when(entityManager.createNamedQuery(eq("WalletOperation.discountWoSummaryForRerating"))).thenAnswer(new Answer<JPAQuerySimulation<Object[]>>() {
            public JPAQuerySimulation<Object[]> answer(InvocationOnMock invocation) throws Throwable {
                return discountWoSummaryQuery;
            }
        });

        JPAQuerySimulation<Object[]> triggeredEdrSummaryQuery = new JPAQuerySimulation<Object[]>() {
            @SuppressWarnings("unchecked")
            @Override
            public List<Object[]> getResultList() {

                List<Object[]> values = new ArrayList<>();

                for (Long woId : (List<Long>) getParameterRaw("woIds")) {
                    for (Object[] edrInfo : edrs) {
                        if (woId.equals(edrInfo[5])) {
                            values.add(new Object[] { BigInteger.valueOf((Long) edrInfo[0]), edrInfo[1] });
                        }
                    }
                }
                return values;
            }
        };

        when(entityManager.createNamedQuery(eq("EDR.triggeredEDRSummaryForRerating"))).thenAnswer(new Answer<JPAQuerySimulation<Object[]>>() {
            public JPAQuerySimulation<Object[]> answer(InvocationOnMock invocation) throws Throwable {
                return triggeredEdrSummaryQuery;
            }
        });

        JPAQuerySimulation<Object[]> woSummaryQuery = new JPAQuerySimulation<Object[]>() {
            @SuppressWarnings("unchecked")
            @Override
            public List<Object[]> getResultList() {

                List<Object[]> values = new ArrayList<>();

                for (Long edrId : (List<Long>) getParameterRaw("edrIds")) {
                    for (Object[] edrInfo : edrs) {
                        if (edrId.equals(edrInfo[0]) && edrInfo[3] != null && !"CANCELED".equals((String) edrInfo[3])) {
                            values.add(new Object[] { BigInteger.valueOf((Long) edrInfo[2]), edrInfo[3], edrInfo[4] != null ? BigInteger.valueOf((Long) edrInfo[4]) : null });
                        }
                    }
                }
                return values;
            }
        };

        when(entityManager.createNamedQuery(eq("WalletOperation.woSummaryForRerating"))).thenAnswer(new Answer<JPAQuerySimulation<Object[]>>() {
            public JPAQuerySimulation<Object[]> answer(InvocationOnMock invocation) throws Throwable {
                return woSummaryQuery;
            }
        });

        JPAQuerySimulation<Object[]> rtSummaryQuery = new JPAQuerySimulation<Object[]>() {
            @SuppressWarnings("unchecked")
            @Override
            public List<Object[]> getResultList() {
                List<Object[]> values = new ArrayList<>();

                for (Long rtId : (List<Long>) getParameterRaw("rtIds")) {
                    for (Object[] rtInfo : rts) {
                        if (rtId.equals(rtInfo[0])) {
                            values.add(new Object[] { BigInteger.valueOf((Long) rtInfo[0]), rtInfo[1], BigDecimal.valueOf((Long) rtInfo[2]), BigDecimal.valueOf((Long) rtInfo[3]), BigDecimal.valueOf((Long) rtInfo[4]),
                                    BigDecimal.valueOf((Long) rtInfo[5]), rtInfo[6] != null ? BigInteger.valueOf((Long) rtInfo[6]) : null, rtInfo[7], rtInfo[8] != null ? BigInteger.valueOf((Long) rtInfo[8]) : null });
                        }
                    }
                }
                return values;
            }
        };

        when(entityManager.createNamedQuery(eq("RatedTransaction.rtSummaryForRerating"))).thenAnswer(new Answer<JPAQuerySimulation<Object[]>>() {
            public JPAQuerySimulation<Object[]> answer(InvocationOnMock invocation) throws Throwable {
                return rtSummaryQuery;
            }
        });

        // Use the form "doAnswer().when().method" instead of "when().thenAnswer()" because on spied object the later will call a real method at the setup time, which will fail because of null values being passed.
        doAnswer(new Answer<RatingResult>() {
            public RatingResult answer(InvocationOnMock invocation) throws Throwable {

                RatingResult ratingResult = new RatingResult();
                ratingResult.addWalletOperation((WalletOperation) invocation.getArguments()[0]);

                return ratingResult;
            }
        }).when(reratingService).rateBareWalletOperation(any(), any(), any(), any(), any(), anyBoolean());

        doReturn(new ArrayList<EDR>()).when(reratingService).instantiateTriggeredEDRs(any(), any(), anyBoolean(), anyBoolean());

    }

    @SuppressWarnings("unchecked")
    @Test
    public void test_ValidateAndCancel() {

        JPAQuerySimulation<Object[]> edrCancelQuery = new JPAQuerySimulation<Object[]>() {
        };

        when(entityManager.createNamedQuery(eq("EDR.cancelEDRs"))).thenAnswer(new Answer<JPAQuerySimulation<Object[]>>() {
            public JPAQuerySimulation<Object[]> answer(InvocationOnMock invocation) throws Throwable {
                return edrCancelQuery;
            }
        });

        JPAQuerySimulation<Object[]> woCancelQuery = new JPAQuerySimulation<Object[]>() {
        };

        when(entityManager.createNamedQuery(eq("WalletOperation.cancelWOs"))).thenAnswer(new Answer<JPAQuerySimulation<Object[]>>() {
            public JPAQuerySimulation<Object[]> answer(InvocationOnMock invocation) throws Throwable {
                return woCancelQuery;
            }
        });

        JPAQuerySimulation<Object[]> rtCancelQuery = new JPAQuerySimulation<Object[]>() {
        };

        when(entityManager.createNamedQuery(eq("RatedTransaction.cancelRTs"))).thenAnswer(new Answer<JPAQuerySimulation<Object[]>>() {
            public JPAQuerySimulation<Object[]> answer(InvocationOnMock invocation) throws Throwable {
                return rtCancelQuery;
            }
        });

        JPAQuerySimulation<Object[]> ilUpdateQuery = new JPAQuerySimulation<Object[]>() {
        };

        when(entityManager.createNamedQuery(eq("InvoiceLine.updateByIncrementalModeWoutDates"))).thenAnswer(new Answer<JPAQuerySimulation<Object[]>>() {
            public JPAQuerySimulation<Object[]> answer(InvocationOnMock invocation) throws Throwable {
                return ilUpdateQuery;
            }
        });

        JPAQuerySimulation<Object[]> ilUpdateWithAverageQuery = new JPAQuerySimulation<Object[]>() {
        };

        when(entityManager.createNamedQuery(eq("InvoiceLine.updateByIncrementalModeWoutDatesWithAverageUnitAmounts"))).thenAnswer(new Answer<JPAQuerySimulation<Object[]>>() {
            public JPAQuerySimulation<Object[]> answer(InvocationOnMock invocation) throws Throwable {
                return ilUpdateWithAverageQuery;
            }
        });

        reratingService.reRate(1L, true);

        List<Long> edrIds = (List<Long>) edrCancelQuery.getParameterRawHistory().get(0).get("ids");
        assertThat(edrIds.size()).isEqualTo(5);
        assertThat(edrIds.containsAll(Arrays.asList(101L, 102L, 103L, 104L, 105L)));

        List<Long> woIds = (List<Long>) woCancelQuery.getParameterRawHistory().get(0).get("ids");
        assertThat(woIds.size()).isEqualTo(7);
        assertThat(woIds.containsAll(Arrays.asList(2L, 3L, 7L, 8L, 4L, 5L, 9L)));

        List<Long> rtIds = (List<Long>) rtCancelQuery.getParameterRawHistory().get(0).get("ids");
        assertThat(rtIds.size()).isEqualTo(5);
        assertThat(rtIds.containsAll(Arrays.asList(201L, 203L, 204L, 205L, 206L)));

        List<Map<String, Object>> parameterHistory = ilUpdateQuery.getParameterRawHistory();
        assertThat(parameterHistory.size()).isEqualTo(1);
        Map<String, Object> parameters = parameterHistory.get(0);
        assertThat(parameters.get("deltaAmountWithoutTax")).isEqualTo(new BigDecimal(-5));
        assertThat(parameters.get("deltaAmountWithTax")).isEqualTo(new BigDecimal(-7));
        assertThat(parameters.get("deltaAmountTax")).isEqualTo(new BigDecimal(-2));
        assertThat(parameters.get("deltaQuantity")).isEqualTo(new BigDecimal(-1));
        assertThat(parameters.get("id")).isEqualTo(304L);

        parameterHistory = ilUpdateWithAverageQuery.getParameterRawHistory();
        assertThat(parameterHistory.size()).isEqualTo(2);
        parameters = parameterHistory.get(0);
        assertThat(parameters.get("id")).isEqualTo(301L);
        assertThat(parameters.get("deltaAmountWithoutTax")).isEqualTo(new BigDecimal(-10));
        assertThat(parameters.get("deltaAmountWithTax")).isEqualTo(new BigDecimal(-14));
        assertThat(parameters.get("deltaAmountTax")).isEqualTo(new BigDecimal(-4));
        assertThat(parameters.get("deltaQuantity")).isEqualTo(new BigDecimal(-2));

        parameters = parameterHistory.get(1);
        assertThat(parameters.get("id")).isEqualTo(302L);
        assertThat(parameters.get("deltaAmountWithoutTax")).isEqualTo(new BigDecimal(-5));
        assertThat(parameters.get("deltaAmountWithTax")).isEqualTo(new BigDecimal(-7));
        assertThat(parameters.get("deltaAmountTax")).isEqualTo(new BigDecimal(-2));
        assertThat(parameters.get("deltaQuantity")).isEqualTo(new BigDecimal(-1));

//        .setParameter("deltaAmountWithoutTax", ilAdjustment.getAmountWithoutTax()).setParameter("deltaAmountWithTax", ilAdjustment.getAmountWithTax()).setParameter("deltaAmountTax", ilAdjustment.getAmountTax())
//        .setParameter("deltaQuantity", ilAdjustment.getQuantity()).setParameter("id", ilId).executeUpdate();
    }
}