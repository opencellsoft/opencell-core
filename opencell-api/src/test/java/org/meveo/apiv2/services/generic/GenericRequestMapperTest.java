/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.apiv2.services.generic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.CustomerCategory;
import org.meveo.model.mediation.Access;
import org.meveo.model.payments.PaymentMethod;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.service.base.PersistenceService;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenericRequestMapperTest {

    private GenericRequestMapper requestMapper;

    @Before
    public void setUp() throws Exception {

        PersistenceService persistenceServiceMock = mock(PersistenceService.class);
        Function<Class, PersistenceService> serviceFunction = aClass -> persistenceServiceMock;
        requestMapper = new GenericRequestMapper(null, serviceFunction);
    }

    @Test
    public void evaluateFiltersObjectWithId() throws IOException {
        PersistenceService persistenceServiceMock = mock(PersistenceService.class);
        Function<Class, PersistenceService> serviceFunction = aClass -> persistenceServiceMock;
        CustomerCategory customerCategory = new CustomerCategory();
        customerCategory.setId(2L);
        EntityManager entityManager = mock(EntityManager.class);
        when(persistenceServiceMock.getEntityManager()).thenReturn(entityManager);
        when(entityManager.getReference(CustomerCategory.class, 2L)).thenReturn(customerCategory);
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
    public void evaluateFiltersObjectWithReferenceProperties() throws IOException {
        PersistenceService persistenceServiceMock = mock(PersistenceService.class);
        Function<Class, PersistenceService> serviceFunction = aClass -> persistenceServiceMock;
        requestMapper = new GenericRequestMapper(null, serviceFunction);
        String filter = "{\"customerCategory\":{\"code\":\"code-123\"}}";
        ObjectMapper objectMapper = new ObjectMapper();
        CustomerCategory customerCategory = new CustomerCategory();
        customerCategory.setId(2L);
        customerCategory.setCode("code-123");
        when(persistenceServiceMock.list((PaginationConfiguration) any())).thenReturn(Collections.singletonList(customerCategory));
        Map filtersMap = objectMapper.readValue(filter, Map.class);
        Map map = requestMapper.evaluateFilters(filtersMap, Customer.class);
        CustomerCategory returnedObject = (CustomerCategory) ((List) map.get("customerCategory")).get(0);
        assertEquals(returnedObject.getClass(), CustomerCategory.class);
        assertEquals(returnedObject.getCode(), "code-123");
    }

    @Test
    public void evaluateFiltersDate() throws IOException {
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
        String filter = "{\"paymentType\":\"WIRETRANSFER\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        Map filtersMap = objectMapper.readValue(filter, Map.class);
        Map<String, Object> map = requestMapper.evaluateFilters(filtersMap, PaymentMethod.class);
        assertEquals(map.get("paymentType").getClass(), PaymentMethodEnum.class);
        assertEquals(map.get("paymentType"), PaymentMethodEnum.WIRETRANSFER );
    }
}