package org.meveo.service.payments.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerAccountServiceTest {

    @Spy
    @InjectMocks
    private CustomerAccountService sut;
    
    @Mock
    private EntityManagerWrapper entityManagerWrapper;
    
    @Mock
    private EntityManager entityManager;
    
    @Mock
    private TypedQuery query;

    @Mock
    QueryBuilder queryBuilder;
    
    @Before
    public void init() {
        when(entityManagerWrapper.getEntityManager()).thenReturn(entityManager);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        doReturn(queryBuilder).when(sut).getQueryBuilder(anyString());
        when(queryBuilder.getQuery(any(EntityManager.class))).thenReturn(query);
    }
    
    @Test
    public void should_return_zero_when_there_is_no_record_from_database() {
        //Given
        CustomerAccount customerAccount = mock(CustomerAccount.class);
        when(query.getSingleResult()).thenReturn(null);
        //When
        BigDecimal balance = sut.computeCreditDebitBalances(customerAccount, false, false, new Date(), MatchingStatusEnum.O, MatchingStatusEnum.P);
        //Then
        assertThat(balance).isEqualTo(BigDecimal.ZERO);
    }
    
}
