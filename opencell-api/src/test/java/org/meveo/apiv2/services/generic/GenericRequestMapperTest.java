package org.meveo.apiv2.services.generic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.base.PersistenceService;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenericRequestMapperTest {
    @Test
    public void evaluateFiltersObjectWithId() throws IOException {
        PersistenceService persistenceServiceMock = mock(PersistenceService.class);
        Function<Class, PersistenceService> serviceFunction = aClass -> persistenceServiceMock;
        CustomerCategory customerCategory = new CustomerCategory();
        customerCategory.setId(2L);
        when(persistenceServiceMock.getEntityManager().getReference(CustomerCategory.class, 2L)).thenReturn(customerCategory);
        GenericRequestMapper requestMapper = new GenericRequestMapper(CustomerCategory.class, serviceFunction);

        String filter = "{\"customerCategory\":{\"id\":2}}";
        ObjectMapper objectMapper = new ObjectMapper();
        Map filtersMap = objectMapper.readValue(filter, Map.class);
        Map<String, Object> map = requestMapper.evaluateFilters(filtersMap, Customer.class);
        assertEquals(map.get("customerCategory").getClass(), CustomerCategory.class);
        Long customerCategoryId = ((CustomerCategory) map.get("customerCategory")).getId();
        assertTrue(customerCategoryId.equals(2L));
    }

    @Test
    public void evaluateFiltersObjectWithString() throws IOException {
        GenericRequestMapper requestMapper = new GenericRequestMapper(null, null);
        String filter = "{\"customerCategory\":{\"code\":\"code-123\"}}";
        ObjectMapper objectMapper = new ObjectMapper();
        Map filtersMap = objectMapper.readValue(filter, Map.class);
        Map map = requestMapper.evaluateFilters(filtersMap, Customer.class);
        assertEquals(map.get("customerCategory").getClass(), CustomerCategory.class);
        assertEquals(((CustomerCategory)map.get("customerCategory")).getCode(), "code-123");
    }

    @Test
    public void evaluateFiltersDate() throws IOException {
        GenericRequestMapper requestMapper = new GenericRequestMapper(null, null);
        long time = new Date().getTime();
        String filter = "{\"startDate\":" + time +"}";
        ObjectMapper objectMapper = new ObjectMapper();
        Map filtersMap = objectMapper.readValue(filter, Map.class);
        Map map = requestMapper.evaluateFilters(filtersMap, Access.class);
        assertEquals(map.get("startDate").getClass(), Date.class);
        assertEquals(((Date) map.get("startDate")).getTime(), time);
    }

    @Test
    public void evaluateFiltersObjectWithAuditable() throws IOException {
        GenericRequestMapper requestMapper = new GenericRequestMapper(null, null);
        long time = new Date().getTime();
        String filter = "{\"auditable\":{\"created\" : " + time + "}}";
        ObjectMapper objectMapper = new ObjectMapper();
        Map filtersMap = objectMapper.readValue(filter, Map.class);
        Map<String, Object> map = requestMapper.evaluateFilters(filtersMap, Customer.class);
        assertEquals( Date.class, ((Map)map.get("auditable")).get("created").getClass());
        Date customerCategoryAuditableDate = (Date) ((Map)map.get("auditable")).get("created");
        assertEquals(customerCategoryAuditableDate.getTime(), time );
    }

    @Test
    public void evaluateFiltersObjectWithEnum() throws IOException {
        GenericRequestMapper requestMapper = new GenericRequestMapper(null, null);
        String filter = "{\"paymentType\":\"WIRETRANSFER\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        Map filtersMap = objectMapper.readValue(filter, Map.class);
        Map<String, Object> map = requestMapper.evaluateFilters(filtersMap, PaymentMethod.class);
        assertEquals(map.get("paymentType").getClass(), PaymentMethodEnum.class);
        assertEquals(map.get("paymentType"), PaymentMethodEnum.WIRETRANSFER );
    }
}