package org.meveo.service.payments.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.payments.MatchingStatusEnum;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CustomerAccountServiceTest {
    
    @InjectMocks
    private CustomerAccountService sut;
    
    @Mock
    private EntityManagerWrapper entityManagerWrapper;
    
    @Mock
    private EntityManager entityManager;
    
    @Mock(answer = Answers.CALLS_REAL_METHODS)
    private TypedQuery query;
    
    @Before
    public void init() {
        when(entityManagerWrapper.getEntityManager()).thenReturn(entityManager);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(entityManager.createNamedQuery(anyString(), (Class<?>) any(Class.class))).thenReturn(query);
    }
    
    @Test
    public void should_return_zero_no_record_from_database() {
        //Given
        CustomerAccount customerAccount = mock(CustomerAccount.class);
        when(query.getSingleResult()).thenReturn(null);
        //When
        BigDecimal balance = sut.computeCreditDebitBalances(customerAccount, MatchingStatusEnum.O, MatchingStatusEnum.P);
        //Then
        assertThat(balance).isEqualTo(BigDecimal.ZERO);
    }
    
}
