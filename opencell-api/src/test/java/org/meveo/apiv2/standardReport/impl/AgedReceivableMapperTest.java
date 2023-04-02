package org.meveo.apiv2.standardReport.impl;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.meveo.api.dto.AgedReceivableDto;
import org.meveo.model.admin.Currency;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.DunningLevelEnum;
import org.meveo.model.shared.Name;
import org.meveo.model.shared.Title;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AgedReceivableMapperTest {

    private AgedReceivableMapper mapper = new AgedReceivableMapper();

    @Before
    public void setUp() {
        Provider provider = new Provider();
        Currency currency = new Currency();
        currency.setCurrencyCode("USD");
        provider.setCurrency(currency);
        mapper.setAppProvider(provider);
    }

    @Test
    public void shouldReturnDynamicResponse() {
        int responseDtoSize = 1;
        List<Object[]> input = new ArrayList<>();
        Object[] agedReceivable = new Object[] {1, ONE, ONE,
                new BigDecimal(100), new BigDecimal(80), new BigDecimal(20),
                new BigDecimal(100), new BigDecimal(80), new BigDecimal(20),
                ZERO, ZERO, ZERO,
                ZERO, ZERO, ZERO,
                DunningLevelEnum.R1, new Name(new Title(), "TEST", "TEST"),
                "CA_DESCRIPTION", "SELLER_DESCRIPTION", "SELLER_CODE",
                new Date(), "EUR", 1L, "INV_1000", new BigDecimal(100), "CA_CODE", new BigDecimal(100), 1L};
        input.add(agedReceivable);
        List<AgedReceivableDto> response = mapper.buildDynamicResponse(input, 2);

        assertNotNull(response);
        assertEquals(responseDtoSize, response.size());
        assertEquals(new BigDecimal(80), response.get(0).getGeneralTotal());
        assertEquals(DunningLevelEnum.R1, response.get(0).getDunningLevel());
        assertEquals("EUR", response.get(0).getTradingCurrency());
        assertEquals(List.of(new BigDecimal(20), ZERO), response.get(0).getTaxAmountByPeriod());
        assertEquals(List.of(new BigDecimal(80), ZERO), response.get(0).getTotalAmountByPeriod());
        assertEquals(List.of(new BigDecimal(100), ZERO), response.get(0).getNetAmountByPeriod());
        assertNotNull(response.get(0).getTransactionalGeneralTotal());
        assertNotNull(response.get(0).getTransactionalNotYetDue());
    }

    @Test
    public void shouldReturnDefaultResponse() {
        int responseDtoSize = 1;
        List<Object[]> input = new ArrayList<>();
        Object[] agedReceivable = new Object[] {1, ONE, ONE,
                new BigDecimal(100), new BigDecimal(80), new BigDecimal(20),
                new BigDecimal(200), new BigDecimal(180), new BigDecimal(20),
                new BigDecimal(300), new BigDecimal(280), new BigDecimal(20),
                new BigDecimal(300), new BigDecimal(280), new BigDecimal(20),

                new BigDecimal(100), new BigDecimal(80), new BigDecimal(20),
                new BigDecimal(200), new BigDecimal(180), new BigDecimal(20),
                new BigDecimal(300), new BigDecimal(280), new BigDecimal(20),
                new BigDecimal(300), new BigDecimal(280), new BigDecimal(20),

                DunningLevelEnum.R1, new Name(new Title(), "TEST", "TEST"),
                "CA_DESCRIPTION", "SELLER_CODE", "SELLER_DESCRIPTION", new Date(), "EUR", 1L, "INV_1000",
                new BigDecimal(100), "CA_CODE", new BigDecimal(100), 1L};
        input.add(agedReceivable);

        List<AgedReceivableDto> response = mapper.toEntityList(input);

        assertNotNull(response);
        assertEquals(responseDtoSize, response.size());
        assertEquals("EUR", response.get(0).getTradingCurrency());
        assertEquals(new BigDecimal(100), response.get(0).getSum1To30());
        assertEquals(new BigDecimal(820), response.get(0).getGeneralTotal());

        assertEquals(new BigDecimal(1), response.get(0).getTransactionalNotYetDue());
        assertEquals(new BigDecimal(820), response.get(0).getTransactionalGeneralTotal());
    }
}