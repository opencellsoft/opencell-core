package org.meveo.service.billing.impl;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.catalog.PricePlanMatrixValueForRating;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.ProductVersion;
import org.meveo.model.cpq.ProductVersionAttribute;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.test.JPAQuerySimulation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class PricePlanSelectionServiceTest {

    @Spy
    @InjectMocks
    private PricePlanSelectionService pricePlanSelectionService;

    @Mock
    private EntityManager entityManager;

    @Mock
    ParamBean paramBean;

    private Map<String, Attribute> attributes = getAttributes();

    private Map<Long, PricePlanMatrixLine> ppLines = getPricePlanLines();

    @Before
    public void setUp() {
        doReturn(entityManager).when(pricePlanSelectionService).getEntityManager();

//        MockedStatic<ParamBean> paramBeanStatic = Mockito.mockStatic(ParamBean.class);
//        paramBeanStatic.when(() -> ParamBean.getInstance()).thenReturn(paramBean);
//        when(paramBean.getProperty(eq("attribute.multivalues.separator"), any())).thenReturn(";");

        JPAQuerySimulation<PricePlanMatrixValueForRating> ppValueForRatingQuery = new JPAQuerySimulation<PricePlanMatrixValueForRating>() {
            @Override
            public List<PricePlanMatrixValueForRating> getResultList() {
                return getPricePlanValuesForRating(true);
            }
        };

        when(entityManager.createNamedQuery(eq("PricePlanMatrixValue.findByPPVersionForRating"), eq(PricePlanMatrixValueForRating.class))).thenAnswer(new Answer<JPAQuerySimulation<PricePlanMatrixValueForRating>>() {
            public JPAQuerySimulation<PricePlanMatrixValueForRating> answer(InvocationOnMock invocation) throws Throwable {
                return ppValueForRatingQuery;
            }
        });

        JPAQuerySimulation<PricePlanMatrixLine> ppDefaultLineQuery = new JPAQuerySimulation<PricePlanMatrixLine>() {
            @Override
            public PricePlanMatrixLine getSingleResult() {
                return ppLines.get(5L);
            }
        };

        when(entityManager.createNamedQuery(eq("PricePlanMatrixLine.findDefaultByPricePlanMatrixVersion"), eq(PricePlanMatrixLine.class))).thenAnswer(new Answer<JPAQuerySimulation<PricePlanMatrixLine>>() {
            public JPAQuerySimulation<PricePlanMatrixLine> answer(InvocationOnMock invocation) throws Throwable {
                return ppDefaultLineQuery;
            }
        });

        when(entityManager.find(eq(PricePlanMatrixLine.class), anyLong())).thenAnswer(new Answer<PricePlanMatrixLine>() {
            @Override
            public PricePlanMatrixLine answer(InvocationOnMock invocation) throws Throwable {

                Long ppLineId = invocation.getArgument(1);

                return ppLines.get(ppLineId);
            }
        });
    }

    @Test
    public void determinePPLineByQuoteAttribute_1() {

        PricePlanMatrixVersion pricePlanMatrixVersion = createPricePlanMatrixVersion();

        QuoteProduct quoteProduct = createQuoteProduct();

        QuoteAttribute monthlyQuotedAttribute = createQuoteAttribute(attributes.get("billing_cycle"), quoteProduct);
        monthlyQuotedAttribute.setStringValue("Monthly");

        QuoteAttribute sevenTeenEngagementDuration = createQuoteAttribute(attributes.get("engagement_duration"), quoteProduct);
        sevenTeenEngagementDuration.setDoubleValue(12.0);

        PricePlanMatrixLine ppmLine = pricePlanSelectionService.determinePricePlanLine(pricePlanMatrixVersion, Set.of(monthlyQuotedAttribute, sevenTeenEngagementDuration));

        assertThat(ppmLine.getDescription()).isEqualTo("Price1");
        assertThat(ppmLine.getPriceWithoutTax()).isEqualTo(valueOf(24));
    }

    @Test
    public void determinePPLineByQuoteAttribute_2() {

        PricePlanMatrixVersion pricePlanMatrixVersion = createPricePlanMatrixVersion();

        QuoteProduct quoteProduct = createQuoteProduct();

        QuoteAttribute monthlyQuotedAttribute = createQuoteAttribute(attributes.get("billing_cycle"), quoteProduct);
        monthlyQuotedAttribute.setStringValue("Monthly");

        QuoteAttribute sevenTeenEngagementDuration = createQuoteAttribute(attributes.get("engagement_duration"), quoteProduct);
        sevenTeenEngagementDuration.setDoubleValue(24.0);

        PricePlanMatrixLine ppmLine = pricePlanSelectionService.determinePricePlanLine(pricePlanMatrixVersion, Set.of(monthlyQuotedAttribute, sevenTeenEngagementDuration));

        assertThat(ppmLine.getDescription()).isEqualTo("Price2");
        assertThat(ppmLine.getPriceWithoutTax()).isEqualTo(valueOf(15));
    }
    @Test
    public void determinePPLineByQuoteAttribute_3() {
        PricePlanMatrixVersion pricePlanMatrixVersion = createPricePlanMatrixVersion();
        QuoteProduct quoteProduct = createQuoteProduct();
        QuoteAttribute monthlyQuotedAttribute = createQuoteAttribute(attributes.get("billing_cycle"), quoteProduct);
        monthlyQuotedAttribute.setStringValue("Annually");
        QuoteAttribute sevenTeenEngagementDuration = createQuoteAttribute(attributes.get("engagement_duration"), quoteProduct);
        sevenTeenEngagementDuration.setDoubleValue(12.0);
        PricePlanMatrixLine ppmLine = pricePlanSelectionService.determinePricePlanLine(pricePlanMatrixVersion, Set.of(monthlyQuotedAttribute, sevenTeenEngagementDuration));
        assertThat(ppmLine.getDescription()).isEqualTo("Price3");
        assertThat(ppmLine.getPriceWithoutTax()).isEqualTo(valueOf(18));
    }
    @Test
    public void determinePPLineByQuoteAttribute_singlePPValue() {
        JPAQuerySimulation<PricePlanMatrixValueForRating> ppValueForRatingQuery = new JPAQuerySimulation<PricePlanMatrixValueForRating>() {
            @Override
            public List<PricePlanMatrixValueForRating> getResultList() {
                return getPricePlanValuesForRatingShort();
            }
        };
        when(entityManager.createNamedQuery(eq("PricePlanMatrixValue.findByPPVersionForRating"), eq(PricePlanMatrixValueForRating.class))).thenAnswer(new Answer<JPAQuerySimulation<PricePlanMatrixValueForRating>>() {
            public JPAQuerySimulation<PricePlanMatrixValueForRating> answer(InvocationOnMock invocation) throws Throwable {
                return ppValueForRatingQuery;
            }
        });
        PricePlanMatrixVersion pricePlanMatrixVersion = createPricePlanMatrixVersion();
        QuoteProduct quoteProduct = createQuoteProduct();
        QuoteAttribute monthlyQuotedAttribute = createQuoteAttribute(attributes.get("billing_cycle"), quoteProduct);
        monthlyQuotedAttribute.setStringValue("Monthly");
        QuoteAttribute sevenTeenEngagementDuration = createQuoteAttribute(attributes.get("engagement_duration"), quoteProduct);
        sevenTeenEngagementDuration.setDoubleValue(19.0);
        PricePlanMatrixLine ppmLine = pricePlanSelectionService.determinePricePlanLine(pricePlanMatrixVersion, Set.of(monthlyQuotedAttribute, sevenTeenEngagementDuration));
        assertThat(ppmLine.getDescription()).isEqualTo("Price1");
        assertThat(ppmLine.getPriceWithoutTax()).isEqualTo(valueOf(24));
    }

    @Test(expected = NoPricePlanException.class)
    public void determinePPLineNoConcreteMatchNoDefaultPPLineReturnedFromDB() {

        JPAQuerySimulation<PricePlanMatrixValueForRating> ppValueForRatingQuery = new JPAQuerySimulation<PricePlanMatrixValueForRating>() {
            @Override
            public List<PricePlanMatrixValueForRating> getResultList() {
                return getPricePlanValuesForRating(false);
            }
        };

        when(entityManager.createNamedQuery(eq("PricePlanMatrixValue.findByPPVersionForRating"), eq(PricePlanMatrixValueForRating.class))).thenAnswer(new Answer<JPAQuerySimulation<PricePlanMatrixValueForRating>>() {
            public JPAQuerySimulation<PricePlanMatrixValueForRating> answer(InvocationOnMock invocation) throws Throwable {
                return ppValueForRatingQuery;
            }
        });

        PricePlanMatrixVersion pricePlanMatrixVersion = createPricePlanMatrixVersion();

        QuoteProduct quoteProduct = createQuoteProduct();

        QuoteAttribute monthlyQuotedAttribute = createQuoteAttribute(attributes.get("billing_cycle"), quoteProduct);
        monthlyQuotedAttribute.setStringValue("Monthly");

        QuoteAttribute sevenTeenEngagementDuration = createQuoteAttribute(attributes.get("engagement_duration"), quoteProduct);
        sevenTeenEngagementDuration.setDoubleValue(17.0);

        pricePlanSelectionService.determinePricePlanLine(pricePlanMatrixVersion, Set.of(monthlyQuotedAttribute, sevenTeenEngagementDuration));
    }

    @Test
    public void determinePPLineNoConcreteMatch_SoMatchDefault() {

        PricePlanMatrixVersion pricePlanMatrixVersion = createPricePlanMatrixVersion();

        QuoteProduct quoteProduct = createQuoteProduct();

        QuoteAttribute monthlyQuotedAttribute = createQuoteAttribute(attributes.get("billing_cycle"), quoteProduct);
        monthlyQuotedAttribute.setStringValue("Monthly");

        QuoteAttribute sevenTeenEngagementDuration = createQuoteAttribute(attributes.get("engagement_duration"), quoteProduct);
        sevenTeenEngagementDuration.setDoubleValue(17.0);

        PricePlanMatrixLine ppmLine = pricePlanSelectionService.determinePricePlanLine(pricePlanMatrixVersion, Set.of(monthlyQuotedAttribute, sevenTeenEngagementDuration));

        assertThat(ppmLine.getDescription()).isEqualTo("Default Price");
        assertThat(ppmLine.getPriceWithoutTax()).isEqualTo(valueOf(18));
    }

    @Test
    public void determinePPLineNoAttributes_UseDefaultPPLine() {
        PricePlanMatrixVersion pricePlanMatrixVersion = createPricePlanMatrixVersion();

        PricePlanMatrixLine ppmLine = pricePlanSelectionService.determinePricePlanLine(pricePlanMatrixVersion, (Set) null);

        assertThat(ppmLine.getDescription()).isEqualTo("Default Price");
        assertThat(ppmLine.getPriceWithoutTax()).isEqualTo(valueOf(18));
    }

    private PricePlanMatrixVersion createPricePlanMatrixVersion() {
        PricePlanMatrix pricePlanMatrix = createPlanPriceMatrix();
        return createPpmVersion(pricePlanMatrix);
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

    private Map<String, Attribute> getAttributes() {

        Map<String, Attribute> attributes = new HashMap<String, Attribute>();

        Attribute attrb = createAttribute(1L, "billing_cycle", AttributeTypeEnum.LIST_TEXT);
        attributes.put(attrb.getCode(), attrb);
        attrb = createAttribute(2L, "engagement_duration", AttributeTypeEnum.NUMERIC);
        attributes.put(attrb.getCode(), attrb);

        return attributes;
    }

    private Attribute createAttribute(Long id, String code, AttributeTypeEnum type) {
        Attribute attrb = new Attribute();
        attrb.setId(id);
        attrb.setCode(code);
        attrb.setAttributeType(type);
        return attrb;
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

    private List<PricePlanMatrixValueForRating> getPricePlanValuesForRating(boolean includeDefaultPP) {

        List<PricePlanMatrixValueForRating> ppValuesForRating = new ArrayList<PricePlanMatrixValueForRating>();

        Collection<PricePlanMatrixLine> ppLines = getPricePlanLines().values();

        for (PricePlanMatrixLine ppLine : ppLines) {

            // Handle a default price plan line
            if (ppLine.getRatingAccuracy() == 0) {

                // Default PP line has no values
                if (includeDefaultPP) {
                    PricePlanMatrixValueForRating ppValueForRating = new PricePlanMatrixValueForRating(null, null, ppLine.getId(), true, null, null, null, null, null, null, null, null, null);

                    ppValuesForRating.add(ppValueForRating);
                }
                continue;
            }

            for (PricePlanMatrixValue ppValue : ppLine.getPricePlanMatrixValues()) {

                PricePlanMatrixValueForRating ppValueForRating = new PricePlanMatrixValueForRating();
                ppValueForRating.setAttributeId(ppValue.getPricePlanMatrixColumn().getAttribute().getId());
                ppValueForRating.setBooleanValue(ppValue.getBooleanValue());
                ppValueForRating.setDateValue(ppValue.getDateValue());
                ppValueForRating.setDoubleValue(ppValue.getDoubleValue());
                ppValueForRating.setFromDateValue(ppValue.getFromDateValue());
                ppValueForRating.setToDateValue(ppValue.getToDateValue());
                ppValueForRating.setFromDoubleValue(ppValue.getFromDoubleValue());
                ppValueForRating.setToDoubleValue(ppValue.getToDoubleValue());
                ppValueForRating.setLongValue(ppValue.getLongValue());
                ppValueForRating.setPricePlanMatrixLineId(ppLine.getId());
                ppValueForRating.setPricePlanMatrixColumnType(ppValue.getPricePlanMatrixColumn().getType());
                ppValueForRating.setStringValue(ppValue.getStringValue());

                ppValuesForRating.add(ppValueForRating);
            }
        }
        return ppValuesForRating;
    }

    private List<PricePlanMatrixValueForRating> getPricePlanValuesForRatingShort() {
        List<PricePlanMatrixValueForRating> shortList = new ArrayList<PricePlanMatrixValueForRating>();
        for (PricePlanMatrixValueForRating ppValue : getPricePlanValuesForRating(false)) {
            if (ppValue.getPricePlanMatrixLineId() == 1 && "Monthly".equals(ppValue.getStringValue())) {
                shortList.add(ppValue);
            }
        }
        return shortList;
    }

    private Map<Long, PricePlanMatrixLine> getPricePlanLines() {

        Map<Long, PricePlanMatrixLine> ppLines = new LinkedHashMap<>();

        Product product = createProduct();

        ProductVersion productVersion = new ProductVersion();
        productVersion.setProduct(product);
        productVersion.setStatus(VersionStatusEnum.DRAFT);
        productVersion.setCurrentVersion(1);

        ProductVersionAttribute pdtVersionAttr1 = new ProductVersionAttribute();
        pdtVersionAttr1.setProductVersion(productVersion);
        pdtVersionAttr1.setAttribute(attributes.get("billing_cycle"));
        pdtVersionAttr1.setSequence(0);

        ProductVersionAttribute pdtVersionAttr2 = new ProductVersionAttribute();
        pdtVersionAttr2.setProductVersion(productVersion);
        pdtVersionAttr2.setAttribute(attributes.get("engagement_duration"));
        pdtVersionAttr2.setSequence(1);

        productVersion.setAttributes(Set.of(pdtVersionAttr1, pdtVersionAttr2));

        PricePlanMatrixColumn billingCycleColumn = new PricePlanMatrixColumn();
        billingCycleColumn.setProduct(product);
        billingCycleColumn.setAttribute(attributes.get("billing_cycle"));
        billingCycleColumn.setType(ColumnTypeEnum.String);

        PricePlanMatrixColumn subscriptionDurationColumn = new PricePlanMatrixColumn();
        subscriptionDurationColumn.setProduct(product);
        subscriptionDurationColumn.setAttribute(attributes.get("engagement_duration"));
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
        twelveSubscriptionDurationValue.setDoubleValue(12D);

        PricePlanMatrixValue twentyFourSubscriptionDurationValue = new PricePlanMatrixValue();
        twentyFourSubscriptionDurationValue.setPricePlanMatrixColumn(subscriptionDurationColumn);
        twentyFourSubscriptionDurationValue.setDoubleValue(24D);

        subscriptionDurationColumn.setPricePlanMatrixValues(Set.of(twelveSubscriptionDurationValue, twentyFourSubscriptionDurationValue));

        PricePlanMatrixLine monthlyPrice1Line = new PricePlanMatrixLine();
        monthlyPrice1Line.setId(1L);
        monthlyPrice1Line.setDescription("Price1");
        monthlyPrice1Line.setPriceWithoutTax(valueOf(24));
        monthlyPrice1Line.setPricePlanMatrixValues(Set.of(monthlyBcValue, twelveSubscriptionDurationValue));
        monthlyPrice1Line.setRatingAccuracy(2);
        ppLines.put(monthlyPrice1Line.getId(), monthlyPrice1Line);

        PricePlanMatrixLine monthlyPrice2Line = new PricePlanMatrixLine();
        monthlyPrice2Line.setId(2L);
        monthlyPrice2Line.setPriceWithoutTax(valueOf(15));
        monthlyPrice2Line.setDescription("Price2");
        monthlyPrice2Line.setPricePlanMatrixValues(Set.of(monthlyBcValue, twentyFourSubscriptionDurationValue));
        monthlyPrice2Line.setRatingAccuracy(2);
        ppLines.put(monthlyPrice2Line.getId(), monthlyPrice2Line);

        PricePlanMatrixLine annuallyPrice3Line = new PricePlanMatrixLine();
        annuallyPrice3Line.setId(3L);
        annuallyPrice3Line.setDescription("Price3");
        annuallyPrice3Line.setPriceWithoutTax(valueOf(18));
        annuallyPrice3Line.setPricePlanMatrixValues(Set.of(annuallyBcValue, twelveSubscriptionDurationValue));
        annuallyPrice3Line.setRatingAccuracy(2);
        ppLines.put(annuallyPrice3Line.getId(), annuallyPrice3Line);

        PricePlanMatrixLine annuallyPrice4Line = new PricePlanMatrixLine();
        annuallyPrice4Line.setId(4L);
        annuallyPrice4Line.setPriceWithoutTax(valueOf(13));
        annuallyPrice4Line.setDescription("Price4");
        annuallyPrice4Line.setPricePlanMatrixValues(Set.of(annuallyBcValue, twentyFourSubscriptionDurationValue));
        annuallyPrice4Line.setRatingAccuracy(2);
        ppLines.put(annuallyPrice4Line.getId(), annuallyPrice4Line);

        PricePlanMatrixLine defaultPriceLine = new PricePlanMatrixLine();
        defaultPriceLine.setId(5L);
        defaultPriceLine.setPriceWithoutTax(valueOf(18));
        defaultPriceLine.setDescription("Default Price");
        defaultPriceLine.setRatingAccuracy(0);
        ppLines.put(defaultPriceLine.getId(), defaultPriceLine);

        return ppLines;
    }
}