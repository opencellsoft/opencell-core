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

package org.meveo.service.payments.impl;

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

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
