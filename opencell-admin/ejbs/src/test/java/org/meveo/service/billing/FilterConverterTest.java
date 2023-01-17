package org.meveo.service.billing;

import static java.math.RoundingMode.HALF_UP;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.service.billing.impl.FilterConverter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class FilterConverterTest {

    private Class<?> targetEntity;
    private FilterConverter converter;

    @Before
    public void setUp() {
        targetEntity = RatedTransaction.class;
        converter = new FilterConverter(targetEntity);
    }

    @Test
    public void shouldConvertFiltersToTargetTypes() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("code", "code_test");
        filters.put("id", "1");
        filters.put("unitAmountWithTax", "20.23");
        filters.put("status", "OPEN");
        filters.put("doNotTriggerInvoicing", "false");

        Map<String, Object> convertedFilters = converter.convertFilters(filters);

        assertEquals(filters.size(), convertedFilters.size());
        assertThat(convertedFilters.get("code"), instanceOf(String.class));
        assertThat(convertedFilters.get("id"), instanceOf(Long.class));
        assertThat(convertedFilters.get("unitAmountWithTax"), instanceOf(BigDecimal.class));
        assertThat(convertedFilters.get("status"), instanceOf(RatedTransactionStatusEnum.class));
        assertThat(convertedFilters.get("doNotTriggerInvoicing"), instanceOf(Boolean.class));
    }

    @Test
    public void shouldConvertFiltersWithOperatorsToTargetTypes() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("criteriaLike code", "code_test");
        filters.put("fromRange unitAmountWithTax", "20.23");
        filters.put("ne status", "PROCESSED");

        Map<String, Object> convertedFilters = converter.convertFilters(filters);

        assertEquals(filters.size(), convertedFilters.size());
        assertThat(convertedFilters.get("criteriaLike code"), instanceOf(String.class));
        assertThat(convertedFilters.get("fromRange unitAmountWithTax"), instanceOf(BigDecimal.class));
        assertThat(convertedFilters.get("ne status"), instanceOf(RatedTransactionStatusEnum.class));
    }

    @Test
    public void shouldConvertFiltersToTargetTypesWithSameValues() {
        Map<String, Object> filters = new HashMap<>();
        filters.put("criteriaLike code", "code_test");
        filters.put("fromRange unitAmountWithTax", "20.23");
        filters.put("ne status", "PROCESSED");
        filters.put("id", "1");
        filters.put("doNotTriggerInvoicing", "false");

        Map<String, Object> convertedFilters = converter.convertFilters(filters);

        assertEquals(filters.size(), convertedFilters.size());
        assertThat(convertedFilters.get("criteriaLike code"), instanceOf(String.class));
        assertThat(convertedFilters.get("fromRange unitAmountWithTax"), instanceOf(BigDecimal.class));
        assertThat(convertedFilters.get("ne status"), instanceOf(RatedTransactionStatusEnum.class));

        assertEquals(RatedTransactionStatusEnum.PROCESSED, convertedFilters.get("ne status"));
        assertEquals(new BigDecimal(20.23).setScale(2, HALF_UP),
                ((BigDecimal) convertedFilters.get("fromRange unitAmountWithTax")).setScale(2, HALF_UP));
        assertEquals(false, convertedFilters.get("doNotTriggerInvoicing"));
        assertEquals(1L, convertedFilters.get("id"));
        assertEquals(filters.get("code_test"), convertedFilters.get("code_test"));
    }
}
