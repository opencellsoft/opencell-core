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

    @Test
    public void name() {
        PricePlanMatrixService pricePlanMatrixService = spy(new PricePlanMatrixService());

        Product product = createProduct();

        ProductVersion productVersion = new ProductVersion();
        productVersion.setProduct(product);
        productVersion.setStatus(VersionStatusEnum.DRAFT);
        productVersion.setCurrentVersion(1);

        Attribute attrb1 = createAttribute("billing_cycle", AttributeTypeEnum.LIST_TEXT);
        Attribute attrb2 = createAttribute("engagement_duration", AttributeTypeEnum.NUMERIC);

        createQuoteAttribute(attrb1, "Monthly");

        createQuoteAttribute(attrb2, "12");

        productVersion.setAttributes(List.of(attrb1, attrb2));

        ProductChargeTemplate productChargeTemplate = new ProductChargeTemplate();
        productChargeTemplate.setCode("CH_SUB");

        PricePlanMatrix pricePlanMatrix = createPlanPriceMatrix();

        PricePlanMatrixVersion pricePlanMatrixVersion = createPpmVersion(pricePlanMatrix);
        pricePlanMatrix.setVersions(List.of(pricePlanMatrixVersion));

        PricePlanMatrixColumn billingCycleColumn = new PricePlanMatrixColumn();
        billingCycleColumn.setProduct(product);
        billingCycleColumn.setAttribute(attrb1);
        billingCycleColumn.setType(ColumnTypeEnum.String);

        PricePlanMatrixColumn subscriptionDurationColumn = new PricePlanMatrixColumn();
        subscriptionDurationColumn.setAttribute(attrb2);
        subscriptionDurationColumn.setProduct(product);
        subscriptionDurationColumn.setType(ColumnTypeEnum.Long);

        PricePlanMatrixValue monthlyBcValue = new PricePlanMatrixValue();
        monthlyBcValue.setPricePlanMatrixColumn(billingCycleColumn);
        monthlyBcValue.setStringValue("Monthly");
        PricePlanMatrixValue annuallyBcValue = new PricePlanMatrixValue();
        annuallyBcValue.setPricePlanMatrixColumn(billingCycleColumn);
        annuallyBcValue.setStringValue("Annually");
        billingCycleColumn.setPricePlanMatrixValues(List.of(monthlyBcValue, annuallyBcValue));

        PricePlanMatrixValue twelveSubDurationValue = new PricePlanMatrixValue();
        twelveSubDurationValue.setPricePlanMatrixColumn(subscriptionDurationColumn);
        twelveSubDurationValue.setLongValue(12L);
        PricePlanMatrixValue twentyFourSubDurationValue = new PricePlanMatrixValue();
        twentyFourSubDurationValue.setPricePlanMatrixColumn(subscriptionDurationColumn);
        twentyFourSubDurationValue.setLongValue(24L);
        subscriptionDurationColumn.setPricePlanMatrixValues(List.of(twelveSubDurationValue, twentyFourSubDurationValue));

        PricePlanMatrixLine monthlyPrice1Line = new PricePlanMatrixLine();
        monthlyPrice1Line.setId(1L);
        monthlyPrice1Line.setDescription("Price1");
        monthlyPrice1Line.setPricetWithoutTax(valueOf(24));
        monthlyPrice1Line.setPricePlanMatrixValues(List.of(monthlyBcValue, twelveSubDurationValue));
        monthlyPrice1Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

        PricePlanMatrixLine monthlyPrice2Line = new PricePlanMatrixLine();
        monthlyPrice2Line.setId(2L);
        monthlyPrice2Line.setPricetWithoutTax(valueOf(15));
        monthlyPrice2Line.setDescription("Price2");
        monthlyPrice2Line.setPricePlanMatrixValues(List.of(monthlyBcValue, twentyFourSubDurationValue));
        monthlyPrice2Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

        PricePlanMatrixLine annuallyPrice1Line = new PricePlanMatrixLine();
        annuallyPrice1Line.setId(3L);
        annuallyPrice1Line.setDescription("Price1");
        annuallyPrice1Line.setPricetWithoutTax(valueOf(18));
        annuallyPrice1Line.setPricePlanMatrixValues(List.of(annuallyBcValue, twelveSubDurationValue));
        annuallyPrice1Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

        PricePlanMatrixLine annuallyPrice2Line = new PricePlanMatrixLine();
        annuallyPrice2Line.setId(4L);
        annuallyPrice2Line.setPricetWithoutTax(valueOf(13));
        annuallyPrice2Line.setDescription("Price2");
        annuallyPrice2Line.setPricePlanMatrixValues(List.of(annuallyBcValue, twentyFourSubDurationValue));
        annuallyPrice2Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

        List<PricePlanMatrixDto> pricesPlanMatrixDto =  pricePlanMatrixService.findFor(productVersion, 10L);
    }

    @Test
    public void can_load_plan_price_matrix_line_by_quoted_attribute() {
        PricePlanMatrixService pricePlanMatrixService = Mockito.mock(PricePlanMatrixService.class);

        Product product = createProduct();
        createProductVersion(product);
        PricePlanMatrix pricePlanMatrix = createPlanPriceMatrix();
        PricePlanMatrixVersion pricePlanMatrixVersion = createPpmVersion(pricePlanMatrix);
        pricePlanMatrix.setVersions(List.of(pricePlanMatrixVersion));


        Attribute attrb1 = createAttribute("billing_cycle", AttributeTypeEnum.LIST_TEXT);
        Attribute attrb2 = createAttribute("engagement_duration", AttributeTypeEnum.NUMERIC);

        QuoteAttribute monthlyQuotedAttribute = createQuoteAttribute(attrb1, "Monthly");
        QuoteAttribute twelveQuotedAttribute = createQuoteAttribute(attrb2, "12");


        PricePlanMatrixColumn billingCycleColumn = new PricePlanMatrixColumn();
        billingCycleColumn.setProduct(product);
        billingCycleColumn.setAttribute(attrb1);
        billingCycleColumn.setType(ColumnTypeEnum.String);

        PricePlanMatrixColumn subscriptionDurationColumn = new PricePlanMatrixColumn();
        subscriptionDurationColumn.setProduct(product);
        subscriptionDurationColumn.setAttribute(attrb2);
        subscriptionDurationColumn.setType(ColumnTypeEnum.Long);

        PricePlanMatrixValue monthlyBcValue = new PricePlanMatrixValue();
        monthlyBcValue.setPricePlanMatrixColumn(billingCycleColumn);
        monthlyBcValue.setStringValue("Monthly");

        PricePlanMatrixValue annuallyBcValue = new PricePlanMatrixValue();
        annuallyBcValue.setPricePlanMatrixColumn(billingCycleColumn);
        annuallyBcValue.setStringValue("Annually");

        billingCycleColumn.setPricePlanMatrixValues(List.of(monthlyBcValue, annuallyBcValue));

        PricePlanMatrixValue twelveSubscriptionDurationValue = new PricePlanMatrixValue();
        twelveSubscriptionDurationValue.setPricePlanMatrixColumn(subscriptionDurationColumn);
        twelveSubscriptionDurationValue.setLongValue(12L);

        PricePlanMatrixValue twentyFourSubscriptionDurationValue = new PricePlanMatrixValue();
        twentyFourSubscriptionDurationValue.setPricePlanMatrixColumn(subscriptionDurationColumn);
        twentyFourSubscriptionDurationValue.setLongValue(24L);

        subscriptionDurationColumn.setPricePlanMatrixValues(List.of(twelveSubscriptionDurationValue, twentyFourSubscriptionDurationValue));

        PricePlanMatrixLine monthlyPrice1Line = new PricePlanMatrixLine();
        monthlyPrice1Line.setId(1L);
        monthlyPrice1Line.setDescription("Price1");
        monthlyPrice1Line.setPricetWithoutTax(valueOf(24));
        monthlyPrice1Line.setPricePlanMatrixValues(List.of(monthlyBcValue, twelveSubscriptionDurationValue));
        monthlyPrice1Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

        PricePlanMatrixLine monthlyPrice2Line = new PricePlanMatrixLine();
        monthlyPrice2Line.setId(2L);
        monthlyPrice2Line.setPricetWithoutTax(valueOf(15));
        monthlyPrice2Line.setDescription("Price2");
        monthlyPrice2Line.setPricePlanMatrixValues(List.of(monthlyBcValue, twentyFourSubscriptionDurationValue));
        monthlyPrice2Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

        PricePlanMatrixLine annuallyPrice3Line = new PricePlanMatrixLine();
        annuallyPrice3Line.setId(3L);
        annuallyPrice3Line.setDescription("Price3");
        annuallyPrice3Line.setPricetWithoutTax(valueOf(18));
        annuallyPrice3Line.setPricePlanMatrixValues(List.of(annuallyBcValue, twelveSubscriptionDurationValue));
        annuallyPrice3Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

        PricePlanMatrixLine annuallyPrice4Line = new PricePlanMatrixLine();
        annuallyPrice4Line.setId(4L);
        annuallyPrice4Line.setPricetWithoutTax(valueOf(13));
        annuallyPrice4Line.setDescription("Price4");
        annuallyPrice4Line.setPricePlanMatrixValues(List.of(annuallyBcValue, twentyFourSubscriptionDurationValue));
        annuallyPrice4Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

        List<PricePlanMatrixLine> ppmLines = pricePlanMatrixService.selectPpmLine(List.of(monthlyPrice1Line, monthlyPrice2Line, annuallyPrice3Line, annuallyPrice4Line), List.of(monthlyQuotedAttribute, twelveQuotedAttribute));
    }

    private PricePlanMatrixVersion createPpmVersion(PricePlanMatrix pricePlanMatrix) {
        PricePlanMatrixVersion pricePlanMatrixVersion = new PricePlanMatrixVersion();
        pricePlanMatrixVersion.setPricePlanMatrix(pricePlanMatrix);
        pricePlanMatrixVersion.setVersion(1);
        pricePlanMatrixVersion.setStatus(VersionStatusEnum.PUBLISHED);
        return pricePlanMatrixVersion;
    }

    private QuoteAttribute createQuoteAttribute(Attribute attribute, String value) {
        QuoteAttribute quotedBcAttribute = new QuoteAttribute();
        quotedBcAttribute.setAttribute(attribute);
        quotedBcAttribute.setValue(value);
        return quotedBcAttribute;
    }

    private Attribute createAttribute(String billing_cycle, AttributeTypeEnum listText) {
        Attribute attrb1 = new Attribute();
        attrb1.setCode(billing_cycle);
        attrb1.setAttributeType(listText);
        return attrb1;
    }

    private void createProductVersion(Product product) {
        ProductVersion productVersion = new ProductVersion();
        productVersion.setProduct(product);
        productVersion.setStatus(VersionStatusEnum.DRAFT);
        productVersion.setCurrentVersion(1);
    }

    private PricePlanMatrix createPlanPriceMatrix() {
        PricePlanMatrix pricePlanMatrix = new PricePlanMatrix();
        pricePlanMatrix.setCode("PPM_1");
        pricePlanMatrix.setEventCode("CH_SUB");
        return pricePlanMatrix;
    }

    private Product createProduct() {
        Product product = new Product();
        product.setDescription("Premium subscription");
        return product;
    }
}