package org.meveo.apiv2.generic.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.admin.Seller;
import org.meveo.service.base.NativePersistenceService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class GenericApiLoadServiceTest {
    @Spy
    @InjectMocks
    private GenericApiLoadService loadService;

    @Mock
    private NativePersistenceService nativePersistenceService;

    @Mock
    private EntityManager entityManager;
    private Set<String> fetchFieldsSet;

    @Before
    public void setup() {
        Mockito.when(nativePersistenceService.getEntityManager()).thenReturn(entityManager);
    }

    @Test
    public void return_sum_of_ids_test(){
        QueryBuilder queryBuilder = Mockito.mock(QueryBuilder.class);
        PaginationConfiguration searchConfig = Mockito.mock(PaginationConfiguration.class);
        Object[] objects= new Object[1];
        objects[0]="5,dd,11".split(",");
        Mockito.when(nativePersistenceService.getAggregateQuery(Seller.class.getCanonicalName(), searchConfig, null)).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.find(entityManager)).thenReturn(Arrays.asList(objects));
        fetchFieldsSet = new LinkedHashSet<>();
        fetchFieldsSet.addAll(Arrays.asList("SUM(id)", "code", "field2"));
        String paginatedRecords = loadService.findPaginatedRecords(false, Seller.class, searchConfig, fetchFieldsSet, null, null, null, null);
        Assert.assertEquals("{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"SUM(id)\":\"5\",\"code\":\"dd\",\"field2\":\"11\"}]}", paginatedRecords);
    }
    @Test
    public void return_avg_of_ids_test(){
        QueryBuilder queryBuilder = Mockito.mock(QueryBuilder.class);
        PaginationConfiguration searchConfig = Mockito.mock(PaginationConfiguration.class);
        Object[] objects= new Object[1];
        objects[0]="3,dd".split(",");
        Mockito.when(nativePersistenceService.getAggregateQuery(Seller.class.getCanonicalName(), searchConfig, null)).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.find(entityManager)).thenReturn(Arrays.asList(objects));
        fetchFieldsSet = new LinkedHashSet<>();
        fetchFieldsSet.addAll(Arrays.asList("AVG(id)", "code"));
        String paginatedRecords = loadService.findPaginatedRecords(false, Seller.class, searchConfig, fetchFieldsSet, null, null, null, null);
        Assert.assertEquals("{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"AVG(id)\":\"3\",\"code\":\"dd\"}]}", paginatedRecords);
    }
    @Test
    public void return_count_of_ids_test(){
        QueryBuilder queryBuilder = Mockito.mock(QueryBuilder.class);
        PaginationConfiguration searchConfig = Mockito.mock(PaginationConfiguration.class);
        Object[] objects= new Object[1];
        objects[0]="4,dd,11".split(",");
        Mockito.when(nativePersistenceService.getAggregateQuery(Seller.class.getCanonicalName(), searchConfig, null)).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.find(entityManager)).thenReturn(Arrays.asList(objects));
        fetchFieldsSet = new LinkedHashSet<>();
        fetchFieldsSet.addAll(Arrays.asList("COUNT(id)", "code", "field2"));
        String paginatedRecords = loadService.findPaginatedRecords(false, Seller.class, searchConfig, fetchFieldsSet, null, null, null, null);
        Assert.assertEquals("{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"COUNT(id)\":\"4\",\"code\":\"dd\",\"field2\":\"11\"}]}", paginatedRecords);
    }
    @Test
    public void return_min_of_ids_test(){
        QueryBuilder queryBuilder = Mockito.mock(QueryBuilder.class);
        PaginationConfiguration searchConfig = Mockito.mock(PaginationConfiguration.class);
        Object[] objects= new Object[1];
        objects[0]="1,dd,11".split(",");
        Mockito.when(nativePersistenceService.getAggregateQuery(Seller.class.getCanonicalName(), searchConfig, null)).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.find(entityManager)).thenReturn(Arrays.asList(objects));
        fetchFieldsSet = new LinkedHashSet<>();
        fetchFieldsSet.addAll(Arrays.asList("MIN(id)", "code", "field2"));
        String paginatedRecords = loadService.findPaginatedRecords(false, Seller.class, searchConfig, fetchFieldsSet, null, null, null, null);
        Assert.assertEquals("{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"MIN(id)\":\"1\",\"code\":\"dd\",\"field2\":\"11\"}]}", paginatedRecords);
    }
    @Test
    public void return_max_of_ids_test(){
        QueryBuilder queryBuilder = Mockito.mock(QueryBuilder.class);
        PaginationConfiguration searchConfig = Mockito.mock(PaginationConfiguration.class);
        Object[] objects= new Object[1];
        objects[0]="5,dd,11".split(",");
        Mockito.when(nativePersistenceService.getAggregateQuery(Seller.class.getCanonicalName(), searchConfig, null)).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.find(entityManager)).thenReturn(Arrays.asList(objects));
        fetchFieldsSet = new LinkedHashSet<>();
        fetchFieldsSet.addAll(Arrays.asList("MAX(id)", "code", "field2"));
        String paginatedRecords = loadService.findPaginatedRecords(false, Seller.class, searchConfig, fetchFieldsSet, null, null, null, null);
        Assert.assertEquals("{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"MAX(id)\":\"5\",\"code\":\"dd\",\"field2\":\"11\"}]}", paginatedRecords);
    }
    @Test
    public void return_avg_of_ids_single_value_test(){
        QueryBuilder queryBuilder = Mockito.mock(QueryBuilder.class);
        PaginationConfiguration searchConfig = Mockito.mock(PaginationConfiguration.class);
        Double[] objects= new Double[1];
        objects[0]=5.;
        Mockito.when(nativePersistenceService.getAggregateQuery(Seller.class.getCanonicalName(), searchConfig, null)).thenReturn(queryBuilder);
        Mockito.when(queryBuilder.find(entityManager)).thenReturn(Arrays.asList(objects));
        fetchFieldsSet = new LinkedHashSet<>();
        fetchFieldsSet.addAll(Arrays.asList("AVG(id)"));
        String paginatedRecords = loadService.findPaginatedRecords(false, Seller.class, searchConfig, fetchFieldsSet, null, null, null, null);
        Assert.assertEquals("{\"total\":1,\"limit\":0,\"offset\":0,\"data\":[{\"AVG(id)\":5.0}]}", paginatedRecords);
    }
}

