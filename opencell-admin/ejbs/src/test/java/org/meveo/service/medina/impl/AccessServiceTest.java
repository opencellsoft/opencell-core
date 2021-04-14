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

package org.meveo.service.medina.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(MockitoJUnitRunner.class)
public class AccessServiceTest {

    @Mock
    private AccessService accessService;
    @Mock
    EntityManager entityManager;
    @Mock
    Query query;
    
    private static final Logger log = LoggerFactory.getLogger(AccessServiceTest.class);

    @Before
    public void setUp() throws Exception {
        when(accessService.getEntityManager()).thenReturn(entityManager);
        when(entityManager.createQuery(anyString())).thenReturn(query);
    }

    @Test
    public void isNotDuplicateAndOverlaps1() {
        Access access = getAccess("02/12/2019", null);
        Access dbAccess1 = getAccess("01/11/2019", "01/12/2019");
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertFalse(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isNotDuplicateAndOverlaps4() {
        Access access = getAccess("01/12/2019", null);
        Access dbAccess1 = getAccess("01/11/2019", "30/11/2019");
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertFalse(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isNotDuplicateAndOverlaps2() {
        Access access = getAccess("03/12/2019", null);
        Access dbAccess1 = getAccess(null, "02/12/2019");
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertFalse(accessService.isDuplicateAndOverlaps(access));
    }
    @Test
    public void isNotDuplicateAndOverlaps3() {
        Access access = getAccess("01/12/2019", "30/12/2019");
        Access dbAccess1 = getAccess(null, "30/11/2019");
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertFalse(accessService.isDuplicateAndOverlaps(access));
    }


    @Test
    public void isDuplicateAndOverlaps1() {
        Access access = getAccess(null, "03/12/2019");
        Access dbAccess1 = getAccess("02/12/2019", null);
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertTrue(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isDuplicateAndOverlaps2() {
        Access access = getAccess(null, null);
        Access dbAccess1 = getAccess(null, "02/12/2019");
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertTrue(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isDuplicateAndOverlaps3() {
        Access access = getAccess(null, null);
        Access dbAccess1 = getAccess(null, null);
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertTrue(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isDuplicateAndOverlaps4() {
        Access access = getAccess("01/11/2019", "01/12/2019");
        Access dbAccess1 = getAccess(null, null);
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertTrue(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isDuplicateAndOverlaps5() {
        Access access = getAccess("07/12/2019", null);
        Access dbAccess1 = getAccess("05/12/2019", null);
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertTrue(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void findByUserIdAndSubscription1() {
        Access access = getAccess("01/11/2019", "01/12/2019");
        Access dbAccess1 = getAccess("01/11/2019", "01/12/2019");
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate())).thenCallRealMethod();
        when(query.getResultList()).thenReturn(Arrays.asList(dbAccess1));
        Access byUserIdAndSubscription = accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate());
        assertNotNull(byUserIdAndSubscription);
    }

    @Test
    public void findByUserIdAndSubscription2() {
        Access access = getAccess("01/11/2019", null);
        Access dbAccess1 = getAccess("01/10/2019", null);
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate())).thenCallRealMethod();
        when(query.getResultList()).thenReturn(Arrays.asList(dbAccess1));

        Access byUserIdAndSubscription = accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate());
        assertNotNull(byUserIdAndSubscription);
    }

    @Test
    public void findByUserIdAndSubscription3() {
        Date usageDate = parse("09/11/2019");
        Subscription subscription= new Subscription();
        subscription.setId(1L);
        Access dbAccess1 = getAccess("01/11/2019", "01/12/2019");
        when(accessService.findByUserIdAndSubscription("AccessUserId", subscription, usageDate)).thenCallRealMethod();
        when(query.getResultList()).thenReturn(Arrays.asList(dbAccess1));
        Access byUserIdAndSubscription = accessService.findByUserIdAndSubscription("AccessUserId", subscription, usageDate);
        assertNotNull(byUserIdAndSubscription);
    }

    @Test
    public void findByUserIdAndSubscription4() {
        Access access = getAccess("01/11/2019", null);
        Access dbAccess1 = getAccess(null, null);
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate())).thenCallRealMethod();
        when(query.getResultList()).thenReturn(Arrays.asList(dbAccess1));

        Access byUserIdAndSubscription = accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate());
        assertNotNull(byUserIdAndSubscription);
    }

    @Test
    public void findByUserIdAndSubscription5() {
        Access access = getAccess(null, null);
        Access dbAccess1 = getAccess(null, null);
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate())).thenCallRealMethod();
        when(query.getResultList()).thenReturn(Arrays.asList(dbAccess1));

        Access byUserIdAndSubscription = accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate());
        assertNotNull(byUserIdAndSubscription);
    }

    @Test
    public void findByUserIdAndSubscription6() {
        Access access = getAccess("01/11/2019", "01/12/2019");
        Access dbAccess1 = getAccess(null, null);
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate())).thenCallRealMethod();
        when(query.getResultList()).thenReturn(Arrays.asList(dbAccess1));

        Access byUserIdAndSubscription = accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate());
        assertNotNull(byUserIdAndSubscription);
    }

    @Test
    public void findByUserIdAndSubscription7() {
        Access access = getAccess("01/11/2019", "01/12/2019");
        Access dbAccess1 = getAccess(null, null);
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate())).thenCallRealMethod();
        when(query.getResultList()).thenReturn(Arrays.asList(dbAccess1));

        Access byUserIdAndSubscription = accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate());
        assertNotNull(byUserIdAndSubscription);
    }

    @Test
    public void findByUserIdAndSubscription8() {
        Access access = getAccess("01/11/2019", "01/12/2019");
        Access dbAccess1 = getAccess("01/11/2019", null);
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate())).thenCallRealMethod();
        when(query.getResultList()).thenReturn(Arrays.asList(dbAccess1));

        Access byUserIdAndSubscription = accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate());
        assertNotNull(byUserIdAndSubscription);
    }

    @Test
    public void findByUserIdAndSubscription9() {
        Access access = getAccess("01/11/2019", "01/12/2019");
        Access dbAccess1 = getAccess(null, "01/12/2019");
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate())).thenCallRealMethod();
        when(query.getResultList()).thenReturn(Arrays.asList(dbAccess1));

        Access byUserIdAndSubscription = accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate());
        assertNotNull(byUserIdAndSubscription);
    }

    private Access getAccess(String startDate, String endDate) {
        Subscription subscription= new Subscription();
        subscription.setId(1L);
        Access access = new Access();
        access.setAccessUserId("AccessUserId");
        access.setStartDate(parse(startDate));
        access.setEndDate(parse(endDate));
        access.setSubscription(subscription);
        return access;
    }

    private Date parse(String date) {
        try {
            if(date != null){
                return new SimpleDateFormat("dd/MM/yyyy").parse(date);
            }
        } catch (ParseException e) {
            log.error("error = {}", e);
        }
        return null;
    }
}