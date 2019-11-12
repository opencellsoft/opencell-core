package org.meveo.service.medina.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccessServiceTest {

    @Mock
    private AccessService accessService;
    @Mock
    EntityManager entityManager;
    @Mock
    Query query;

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
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate()))
                .thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertFalse(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isNotDuplicateAndOverlaps4() {
        Access access = getAccess("01/12/2019", null);
        Access dbAccess1 = getAccess("01/11/2019", "01/12/2019");
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate()))
                .thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertFalse(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isNotDuplicateAndOverlaps2() {
        Access access = getAccess("03/12/2019", null);
        Access dbAccess1 = getAccess(null, "02/12/2019");
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate()))
                .thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertFalse(accessService.isDuplicateAndOverlaps(access));
    }
    @Test
    public void isNotDuplicateAndOverlaps3() {
        Access access = getAccess("01/12/2019", "30/12/2019");
        Access dbAccess1 = getAccess(null, "02/12/2019");
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate()))
                .thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertFalse(accessService.isDuplicateAndOverlaps(access));
    }


    @Test
    public void isDuplicateAndOverlaps1() {
        Access access = getAccess(null, "03/12/2019");
        Access dbAccess1 = getAccess("02/12/2019", null);
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate()))
                .thenCallRealMethod();
        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertTrue(accessService.isDuplicateAndOverlaps(access));
    }

    public boolean isDuplicateAndOverlaps(Access access, Access access2){
        if(access.getStartDate() != null){
            return isDateBetween(access.getStartDate(), access2.getStartDate(), access2.getEndDate()) || access.getEndDate() != null ?
                    isDateBetween(access.getEndDate(), access2.getStartDate(), access2.getEndDate()) : access.getStartDate().before(access2.getStartDate());
        }else if(access.getEndDate() != null){
            return access.getEndDate().after(access2.getStartDate());
        }
        return true;
    }
    private boolean isDateBetween(Date date, Date startDate, Date endDate) {
        return ((startDate != null && startDate.before(date)) && (endDate != null && endDate.after(date)));
    }

    @Test
    public void isDuplicateAndOverlaps2() {
        Access access = getAccess(null, null);
        Access dbAccess1 = getAccess(null, "02/12/2019");
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate()))
                .thenCallRealMethod();

        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertTrue(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isDuplicateAndOverlaps3() {
        Access access = getAccess(null, null);
        Access dbAccess1 = getAccess(null, null);
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate()))
                .thenCallRealMethod();

        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertTrue(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isDuplicateAndOverlaps4() {
        Access access = getAccess("01/11/2019", "01/12/2019");
        Access dbAccess1 = getAccess(null, null);
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate()))
                .thenCallRealMethod();

        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertTrue(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void isDuplicateAndOverlaps5() {
        Access access = getAccess("07/12/2019", null);
        Access dbAccess1 = getAccess("05/12/2019", null);
        when(accessService.isDuplicateAndOverlaps(access)).thenCallRealMethod();
        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate()))
                .thenCallRealMethod();

        when(entityManager.createQuery(anyString())).thenReturn(query);
        when(query.getResultList()).thenReturn(Collections.singletonList(dbAccess1));

        assertTrue(accessService.isDuplicateAndOverlaps(access));
    }

    @Test
    public void findByUserIdAndSubscription() {
        Access access = getAccess("01/11/2019", "01/12/2019");

        Access dbAccess1 = getAccess("01/11/2019", "01/12/2019");
        Access dbAccess2 = getAccess("02/12/2019", "20/12/2019");
        Access dbAccess3 = getAccess("21/12/2019", "30/12/2019");

        when(accessService.findByUserIdAndSubscription(access.getAccessUserId(), access.getSubscription(), access.getStartDate(), access.getEndDate())).thenCallRealMethod();
        when(query.getResultList()).thenReturn(Arrays.asList(dbAccess1,dbAccess2,dbAccess3));

        assertNotNull(accessService.findByUserIdAndSubscription(access.getAccessUserId(),access.getSubscription(),access.getStartDate(),access.getEndDate()));
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
            e.printStackTrace();
        }
        return null;
    }
}