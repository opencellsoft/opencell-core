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

package org.meveo.service.catalog.impl;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.catalog.PricePlanMatrixDto;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
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

    @Test(expected = BusinessException.class)
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