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
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.quote.QuoteProduct;

import javax.ws.rs.BadRequestException;
import java.util.List;
import java.util.Set;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

public class PricePlanMatrixLineServiceTest {



    @Test
    public void can_load_plan_price_matrix_line_by_quoted_attribute() {
        PricePlanMatrixLineService pricePlanMatrixService = new PricePlanMatrixLineServiceMock();
        PricePlanMatrixVersion pricePlanMatrixVersion = createPricePlanMatrixVersion();


        Attribute attrb1 = createAttribute("billing_cycle", AttributeTypeEnum.LIST_TEXT);
        Attribute attrb2 = createAttribute("engagement_duration", AttributeTypeEnum.NUMERIC);

        QuoteProduct quoteProduct = createQuoteProduct();

        QuoteAttribute monthlyQuotedAttribute = createQuoteAttribute(attrb1, quoteProduct);
        monthlyQuotedAttribute.setStringValue("Monthly");

        QuoteAttribute sevenTeenEngagementDuration = createQuoteAttribute(attrb2, quoteProduct);
        sevenTeenEngagementDuration.setDoubleValue(12.0);


        List<PricePlanMatrixLine> ppmLines = pricePlanMatrixService.loadMatchedLinesForProductQuote(pricePlanMatrixVersion, Set.of(monthlyQuotedAttribute, sevenTeenEngagementDuration), quoteProduct.getId());

        assertThat(ppmLines.size()).isEqualTo(1);
        assertThat(ppmLines.get(0).getDescription()).isEqualTo("Price1");
        assertThat(ppmLines.get(0).getPriceWithoutTax()).isEqualTo(valueOf(24));
    }

    private PricePlanMatrixVersion createPricePlanMatrixVersion() {
        PricePlanMatrix pricePlanMatrix = createPlanPriceMatrix();
        return createPpmVersion(pricePlanMatrix);
    }

    @Test(expected = BadRequestException.class)
    public void if_no_attr_match_throw_exception() {
        PricePlanMatrixLineService pricePlanMatrixService = new PricePlanMatrixLineServiceMock();
        PricePlanMatrixVersion pricePlanMatrixVersion = createPricePlanMatrixVersion();

        Attribute attrb1 = createAttribute("billing_cycle", AttributeTypeEnum.LIST_TEXT);
        Attribute attrb2 = createAttribute("engagement_duration", AttributeTypeEnum.NUMERIC);

        QuoteProduct quoteProduct = createQuoteProduct();

        QuoteAttribute monthlyQuotedAttribute = createQuoteAttribute(attrb1, quoteProduct);
        monthlyQuotedAttribute.setStringValue("Monthly");

        QuoteAttribute sevenTeenEngagementDuration = createQuoteAttribute(attrb2, quoteProduct);
        sevenTeenEngagementDuration.setDoubleValue(17.0);

        pricePlanMatrixService.loadMatchedLinesForProductQuote(pricePlanMatrixVersion, Set.of(monthlyQuotedAttribute, sevenTeenEngagementDuration), quoteProduct.getId());
    }


    private QuoteProduct createQuoteProduct() {
        QuoteProduct quoteProduct = new QuoteProduct();
        quoteProduct.setId(1L);
        return quoteProduct;
    }

    private QuoteAttribute createQuoteAttribute(Attribute attribute, QuoteProduct quoteProduct) {
        QuoteAttribute sevenTeenEngagementDuration = new QuoteAttribute();
        sevenTeenEngagementDuration.setAttribute(attribute);
        sevenTeenEngagementDuration.setQuoteProduct(quoteProduct);
        return sevenTeenEngagementDuration;
    }

    private PricePlanMatrixVersion createPpmVersion(PricePlanMatrix pricePlanMatrix) {
        PricePlanMatrixVersion pricePlanMatrixVersion = new PricePlanMatrixVersion();
        pricePlanMatrixVersion.setPricePlanMatrix(pricePlanMatrix);
        pricePlanMatrixVersion.setVersion(1);
        pricePlanMatrixVersion.setStatus(VersionStatusEnum.PUBLISHED);
        return pricePlanMatrixVersion;
    }

    private Attribute createAttribute(String billing_cycle, AttributeTypeEnum listText) {
        Attribute attrb1 = new Attribute();
        attrb1.setCode(billing_cycle);
        attrb1.setAttributeType(listText);
        return attrb1;
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
            ProductVersionAttribute pdtVersionAttr1 = new ProductVersionAttribute();
            pdtVersionAttr1.setProductVersion(productVersion);
            pdtVersionAttr1.setAttribute(attrb1);
            pdtVersionAttr1.setSequence(0);
            ProductVersionAttribute pdtVersionAttr2 = new ProductVersionAttribute();
            pdtVersionAttr2.setProductVersion(productVersion);
            pdtVersionAttr2.setAttribute(attrb2);
            pdtVersionAttr2.setSequence(1);

            productVersion.setAttributes(List.of(pdtVersionAttr1, pdtVersionAttr2));

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

            billingCycleColumn.setPricePlanMatrixValues(Set.of(monthlyBcValue, annuallyBcValue));

            PricePlanMatrixValue twelveSubscriptionDurationValue = new PricePlanMatrixValue();
            twelveSubscriptionDurationValue.setPricePlanMatrixColumn(subscriptionDurationColumn);
            twelveSubscriptionDurationValue.setLongValue(12L);

            PricePlanMatrixValue twentyFourSubscriptionDurationValue = new PricePlanMatrixValue();
            twentyFourSubscriptionDurationValue.setPricePlanMatrixColumn(subscriptionDurationColumn);
            twentyFourSubscriptionDurationValue.setLongValue(24L);

            subscriptionDurationColumn.setPricePlanMatrixValues(Set.of(twelveSubscriptionDurationValue, twentyFourSubscriptionDurationValue));

            PricePlanMatrixLine monthlyPrice1Line = new PricePlanMatrixLine();
            monthlyPrice1Line.setId(1L);
            monthlyPrice1Line.setDescription("Price1");
            monthlyPrice1Line.setPriceWithoutTax(valueOf(24));
            monthlyPrice1Line.setPricePlanMatrixValues(Set.of(monthlyBcValue, twelveSubscriptionDurationValue));
            monthlyPrice1Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

            PricePlanMatrixLine monthlyPrice2Line = new PricePlanMatrixLine();
            monthlyPrice2Line.setId(2L);
            monthlyPrice2Line.setPriceWithoutTax(valueOf(15));
            monthlyPrice2Line.setDescription("Price2");
            monthlyPrice2Line.setPricePlanMatrixValues(Set.of(monthlyBcValue, twentyFourSubscriptionDurationValue));
            monthlyPrice2Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

            PricePlanMatrixLine annuallyPrice3Line = new PricePlanMatrixLine();
            annuallyPrice3Line.setId(3L);
            annuallyPrice3Line.setDescription("Price3");
            annuallyPrice3Line.setPriceWithoutTax(valueOf(18));
            annuallyPrice3Line.setPricePlanMatrixValues(Set.of(annuallyBcValue, twelveSubscriptionDurationValue));
            annuallyPrice3Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

            PricePlanMatrixLine annuallyPrice4Line = new PricePlanMatrixLine();
            annuallyPrice4Line.setId(4L);
            annuallyPrice4Line.setPriceWithoutTax(valueOf(13));
            annuallyPrice4Line.setDescription("Price4");
            annuallyPrice4Line.setPricePlanMatrixValues(Set.of(annuallyBcValue, twentyFourSubscriptionDurationValue));
            annuallyPrice4Line.setPricePlanMatrixVersion(pricePlanMatrixVersion);

            return List.of(monthlyPrice1Line, monthlyPrice2Line, annuallyPrice3Line, annuallyPrice4Line);
        }
    }
}
