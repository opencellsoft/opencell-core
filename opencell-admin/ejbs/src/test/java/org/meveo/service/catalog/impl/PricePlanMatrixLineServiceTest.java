package org.meveo.service.catalog.impl;

import org.junit.Test;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import java.util.List;
import java.util.Set;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

public class PricePlanMatrixLineServiceTest {



    @Test
    public void can_load_plan_price_matrix_line_by_quoted_attribute() {
        PricePlanMatrixLineService pricePlanMatrixService = new PricePlanMatrixLineServiceMock();
        PricePlanMatrix pricePlanMatrix = createPlanPriceMatrix();
        PricePlanMatrixVersion pricePlanMatrixVersion = createPpmVersion(pricePlanMatrix);


        Attribute attrb1 = createAttribute("billing_cycle", AttributeTypeEnum.LIST_TEXT);
        Attribute attrb2 = createAttribute("engagement_duration", AttributeTypeEnum.NUMERIC);

        QuoteAttribute monthlyQuotedAttribute = createQuoteAttribute(attrb1, "Monthly");
        QuoteAttribute twelveQuotedAttribute = createQuoteAttribute(attrb2, "12");


        List<PricePlanMatrixLine> ppmLines = pricePlanMatrixService.loadMatchedLines(pricePlanMatrixVersion, List.of(monthlyQuotedAttribute, twelveQuotedAttribute));

        assertThat(ppmLines.size()).isEqualTo(1);
        assertThat(ppmLines.get(0).getDescription()).isEqualTo("Price1");
        assertThat(ppmLines.get(0).getPricetWithoutTax()).isEqualTo(valueOf(24));

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

    private class PricePlanMatrixLineServiceMock extends PricePlanMatrixLineService {
        @Override
        public List<PricePlanMatrixLine> findByPricePlanMatrixVersion(PricePlanMatrixVersion pricePlanMatrixVersion) {

            Product product = createProduct();

            ProductVersion productVersion = new ProductVersion();
            productVersion.setProduct(product);
            productVersion.setStatus(VersionStatusEnum.DRAFT);
            productVersion.setCurrentVersion(1);

            Attribute attrb1 = createAttribute("billing_cycle", AttributeTypeEnum.LIST_TEXT);
            Attribute attrb2 = createAttribute("engagement_duration", AttributeTypeEnum.NUMERIC);

            productVersion.setAttributes(List.of(attrb1, attrb2));

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
            monthlyPrice1Line.setPricePlanMatrixValues(Set.of(monthlyBcValue, twelveSubscriptionDurationValue));
            monthlyPrice1Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

            PricePlanMatrixLine monthlyPrice2Line = new PricePlanMatrixLine();
            monthlyPrice2Line.setId(2L);
            monthlyPrice2Line.setPricetWithoutTax(valueOf(15));
            monthlyPrice2Line.setDescription("Price2");
            monthlyPrice2Line.setPricePlanMatrixValues(Set.of(monthlyBcValue, twentyFourSubscriptionDurationValue));
            monthlyPrice2Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

            PricePlanMatrixLine annuallyPrice3Line = new PricePlanMatrixLine();
            annuallyPrice3Line.setId(3L);
            annuallyPrice3Line.setDescription("Price3");
            annuallyPrice3Line.setPricetWithoutTax(valueOf(18));
            annuallyPrice3Line.setPricePlanMatrixValues(Set.of(annuallyBcValue, twelveSubscriptionDurationValue));
            annuallyPrice3Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

            PricePlanMatrixLine annuallyPrice4Line = new PricePlanMatrixLine();
            annuallyPrice4Line.setId(4L);
            annuallyPrice4Line.setPricetWithoutTax(valueOf(13));
            annuallyPrice4Line.setDescription("Price4");
            annuallyPrice4Line.setPricePlanMatrixValues(Set.of(annuallyBcValue, twentyFourSubscriptionDurationValue));
            annuallyPrice4Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

            return List.of(monthlyPrice1Line, monthlyPrice2Line, annuallyPrice3Line, annuallyPrice4Line);
        }
    }
}
