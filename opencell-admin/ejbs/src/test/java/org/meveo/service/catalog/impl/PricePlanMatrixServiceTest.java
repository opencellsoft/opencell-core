package org.meveo.service.catalog.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.service.base.PersistenceService;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PricePlanMatrixServiceTest {

    @Test
    public void create_test() throws ParseException {
        PricePlanMatrixService spyPricePlanMatrixService = spy(new PricePlanMatrixService());
        RecurringChargeTemplate recurringChargeTemplate = new RecurringChargeTemplate();
        recurringChargeTemplate.setCode("REC_CODE");

        recurringChargeTemplate.setProrataOnPriceChange(true);

        PricePlanMatrix pricePlanMatrix1 = new PricePlanMatrix();
        pricePlanMatrix1.setId(1L);
        pricePlanMatrix1.setEventCode("REC_CODE");
        pricePlanMatrix1.setValidityDate(new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01"));
        pricePlanMatrix1.setValidityFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01"));

        PricePlanMatrix pricePlanMatrix2 = new PricePlanMatrix();
        pricePlanMatrix2.setId(2L);
        pricePlanMatrix2.setEventCode("REC_CODE");
        pricePlanMatrix2.setValidityFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01"));
        pricePlanMatrix2.setValidityDate(new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01"));


        PricePlanMatrix pricePlanMatrix3 = spy(new PricePlanMatrix());
        pricePlanMatrix3.setId(3L);
        pricePlanMatrix3.setEventCode("REC_CODE");
        pricePlanMatrix3.setValidityFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01"));
        pricePlanMatrix3.setValidityDate(new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01"));

        doNothing().when((PersistenceService)spyPricePlanMatrixService).create(pricePlanMatrix3);
        doReturn(Arrays.asList(pricePlanMatrix1, pricePlanMatrix2)).when(spyPricePlanMatrixService).listByChargeCode("REC_CODE");

        spyPricePlanMatrixService.createPP(pricePlanMatrix3);
    }

    @Test(expected = ExceptionInInitializerError.class)
    public void create_price_plan_overlapped_validity_date_test() throws ParseException {
        PricePlanMatrixService spyPricePlanMatrixService = spy(new PricePlanMatrixService());
        RecurringChargeTemplate recurringChargeTemplate = new RecurringChargeTemplate();
        recurringChargeTemplate.setCode("REC_CODE");

        recurringChargeTemplate.setProrataOnPriceChange(true);

        PricePlanMatrix pricePlanMatrix1 = new PricePlanMatrix();
        pricePlanMatrix1.setId(1L);
        pricePlanMatrix1.setEventCode("REC_CODE");
        pricePlanMatrix1.setValidityFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2017-01-01"));
        pricePlanMatrix1.setValidityDate(new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01"));

        PricePlanMatrix pricePlanMatrix2 = new PricePlanMatrix();
        pricePlanMatrix2.setId(2L);
        pricePlanMatrix2.setEventCode("REC_CODE");
        pricePlanMatrix2.setValidityFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2018-01-01"));
        pricePlanMatrix2.setValidityDate(new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01"));

        PricePlanMatrix pricePlanMatrix3 = new PricePlanMatrix();
        pricePlanMatrix3.setId(1L);
        pricePlanMatrix3.setEventCode("REC_CODE");
        pricePlanMatrix3.setValidityFrom(new SimpleDateFormat("yyyy-MM-dd").parse("2018-07-01"));
        pricePlanMatrix3.setValidityDate(new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01"));

        doReturn(Arrays.asList(pricePlanMatrix1, pricePlanMatrix2)).when(spyPricePlanMatrixService).listByChargeCode("REC_CODE");

        spyPricePlanMatrixService.createPP(pricePlanMatrix3);
    }
}