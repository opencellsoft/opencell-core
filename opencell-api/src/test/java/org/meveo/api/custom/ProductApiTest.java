package org.meveo.api.custom;

import org.junit.Before;
import org.junit.Test;
import org.meveo.api.billing.CpqQuoteApi;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.enums.OperatorEnum;
import org.meveo.model.cpq.enums.RuleOperatorEnum;
import org.meveo.model.cpq.enums.RuleTypeEnum;
import org.meveo.model.cpq.enums.ScopeTypeEnum;
import org.meveo.model.cpq.trade.CommercialRuleHeader;
import org.meveo.model.cpq.trade.CommercialRuleItem;
import org.meveo.model.cpq.trade.CommercialRuleLine;
import org.meveo.service.cpq.rule.ReplacementResult;
import org.meveo.service.cpq.rule.ReplacementRulesExecutor;
import org.meveo.service.cpq.rule.SelectedAttributes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductApiTest {

    private CpqQuoteApi cpqQuoteApi = new CpqQuoteApi();
    private ReplacementRulesExecutor replacementRulesExecutor;
    private LinkedHashMap<String, Object> selectedProductAttributes;
    private LinkedHashMap<String, Object> selectedOfferAttributes;

    @Before
    public void setUp() throws Exception {
        replacementRulesExecutor = new ReplacementRulesExecutor(false);


        selectedProductAttributes = initSelectedAttributes("attr1", "value1");
        selectedProductAttributes.put("attr2", "value2");

        selectedOfferAttributes = initSelectedAttributes("attr1", "value3");
        selectedOfferAttributes.put("attr3", "value4");
    }

    @Test
    public void no_override_if_no_commercial_rule() {

        SelectedAttributes selectedProductAttributesContext = new SelectedAttributes(null, null, selectedProductAttributes);
        SelectedAttributes selectedOfferAttributesContext = new SelectedAttributes(null, null, selectedOfferAttributes);
        ReplacementResult replacementResult = replacementRulesExecutor.execute(Optional.of(selectedProductAttributesContext), Optional.of(selectedOfferAttributesContext), asList(selectedProductAttributesContext, selectedOfferAttributesContext), Collections.emptyList());
        assertThat(replacementResult.getSelectedProductAttributes().get("attr1")).isEqualTo("value1");
        assertThat(replacementResult.getSelectedProductAttributes().get("attr2")).isEqualTo("value2");
        assertThat(replacementResult.getSelectedOfferAttributes().get("attr1")).isEqualTo("value3");
        assertThat(replacementResult.getSelectedOfferAttributes().get("attr3")).isEqualTo("value4");
    }

    @Test
    public void can_override_product_attribute_from_product_attribute() {
        SelectedAttributes selectedProductAttributesContext = new SelectedAttributes(null, "code1", selectedProductAttributes);
        SelectedAttributes selectedOfferAttributesContext = new SelectedAttributes(null, null, selectedOfferAttributes);

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader(null, "code1", "attr1", null, "code1", "attr2", RuleOperatorEnum.EXISTS);

        ReplacementResult replacementResult = replacementRulesExecutor.execute(Optional.of(selectedProductAttributesContext), Optional.of(selectedOfferAttributesContext), asList(selectedProductAttributesContext, selectedOfferAttributesContext), singletonList(commercialRuleHeader));

        assertThat(replacementResult.getSelectedProductAttributes().get("attr1")).isEqualTo("value2");
        assertThat(replacementResult.getSelectedProductAttributes().get("attr2")).isEqualTo("value2");
        assertThat(replacementResult.getSelectedOfferAttributes().get("attr1")).isEqualTo("value3");
        assertThat(replacementResult.getSelectedOfferAttributes().get("attr3")).isEqualTo("value4");
    }

    @Test
    public void can_override_offer_attribute_from_product_attribute() {
        SelectedAttributes selectedProductAttributesContext = new SelectedAttributes(null, "code1", selectedProductAttributes);
        SelectedAttributes selectedOfferAttributesContext = new SelectedAttributes(null, null, selectedOfferAttributes);


        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader(null, null, "attr1", null, "code1", "attr2", RuleOperatorEnum.EXISTS);

        ReplacementResult replacementResult = replacementRulesExecutor.execute(Optional.of(selectedProductAttributesContext), Optional.of(selectedOfferAttributesContext), asList(selectedProductAttributesContext, selectedOfferAttributesContext), singletonList(commercialRuleHeader));

        assertThat(replacementResult.getSelectedProductAttributes().get("attr1")).isEqualTo("value1");
        assertThat(replacementResult.getSelectedProductAttributes().get("attr2")).isEqualTo("value2");
        assertThat(replacementResult.getSelectedOfferAttributes().get("attr1")).isEqualTo("value2");
        assertThat(replacementResult.getSelectedOfferAttributes().get("attr3")).isEqualTo("value4");
    }

    @Test
    public void can_override_offer_attribute_from_offer_attribute() {

        SelectedAttributes selectedProductAttributesContext = new SelectedAttributes(null, "product1", selectedProductAttributes);
        SelectedAttributes selectedOfferAttributesContext = new SelectedAttributes(null, null, selectedOfferAttributes);

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader(null, null, "attr1", null, null, "attr3", RuleOperatorEnum.EXISTS);

        ReplacementResult result = replacementRulesExecutor.execute(Optional.of(selectedProductAttributesContext), Optional.of(selectedOfferAttributesContext), asList(selectedProductAttributesContext, selectedOfferAttributesContext), singletonList(commercialRuleHeader));

        assertThat(result.getSelectedOfferAttributes().get("attr1")).isEqualTo("value4");
        assertThat(result.getSelectedOfferAttributes().get("attr3")).isEqualTo("value4");
        assertThat(result.getSelectedProductAttributes().get("attr1")).isEqualTo("value1");
        assertThat(result.getSelectedProductAttributes().get("attr2")).isEqualTo("value2");
    }

    @Test
    public void no_override_of_product_attribute_if_source_product_attribute_not_exist() {
        SelectedAttributes selectedProductAttributesContext = new SelectedAttributes(null, "product1", selectedProductAttributes);
        SelectedAttributes selectedOfferAttributesContext = new SelectedAttributes(null, null, selectedOfferAttributes);

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader(null, "product1", "attr1", null, null, "attr7", RuleOperatorEnum.EXISTS);

        ReplacementResult result = replacementRulesExecutor.execute(Optional.of(selectedProductAttributesContext), Optional.of(selectedOfferAttributesContext), asList(selectedProductAttributesContext, selectedOfferAttributesContext), singletonList(commercialRuleHeader));

        assertThat(result.getSelectedProductAttributes().size()).isEqualTo(2);
        assertThat(result.getSelectedProductAttributes().get("attr1")).isEqualTo("value1");
    }

    @Test
    public void can_override_product_attribute_from_an_other_product_attribute() {
        SelectedAttributes selectedProduct1AttributesContext = new SelectedAttributes(null, "product1", selectedProductAttributes);
        SelectedAttributes selectedOfferAttributesContext = new SelectedAttributes(null, null, selectedOfferAttributes);
        LinkedHashMap<String, Object> product2Attributes = initSelectedAttributes("attr4", "value5");
        SelectedAttributes selectedProduct2AttributesContext = new SelectedAttributes(null, "product2", product2Attributes);

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader(null, "product1", "attr1", null, "product2", "attr4", RuleOperatorEnum.EXISTS);

        ReplacementResult result = replacementRulesExecutor.execute(Optional.of(selectedProduct1AttributesContext), Optional.of(selectedOfferAttributesContext), asList(selectedProduct1AttributesContext, selectedProduct2AttributesContext, selectedOfferAttributesContext), singletonList(commercialRuleHeader));

        assertThat(result.getSelectedProductAttributes().get("attr1")).isEqualTo("value5");
    }

    @Test
    public void can_override_offer_attribute_from_offer_attribute_if_scope_is_quote() {
        SelectedAttributes selectedOffer1Context = new SelectedAttributes("offer1", null, initSelectedAttributes("attr1", "value1"));
        SelectedAttributes selectedOffer2Context = new SelectedAttributes("offer2", null, initSelectedAttributes("attr2", "value2"));

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader("offer1", null, "attr1", "offer2", null, "attr2", RuleOperatorEnum.EXISTS);

        ReplacementResult result = new ReplacementRulesExecutor(true).execute(Optional.empty(), Optional.of(selectedOffer1Context), asList(selectedOffer1Context, selectedOffer2Context), singletonList(commercialRuleHeader));

        assertThat(result.getSelectedOfferAttributes().get("attr1")).isEqualTo("value2");
    }

    @Test
    public void no_override_of_offer_attribute_from_offer_attribute_if_source_offer_not_exist() {
        SelectedAttributes selectedOffer1Context = new SelectedAttributes("offer1", null, initSelectedAttributes("attr1", "value1"));
        SelectedAttributes selectedOffer2Context = new SelectedAttributes("offer7", null, initSelectedAttributes("attr2", "value2"));

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader(null, null, "attr1", null, null, "attr2", RuleOperatorEnum.EXISTS);

        ReplacementResult result = new ReplacementRulesExecutor(true).execute(Optional.empty(), Optional.of(selectedOffer1Context), asList(selectedOffer1Context, selectedOffer2Context), singletonList(commercialRuleHeader));

        assertThat(result.getSelectedOfferAttributes().get("attr1")).isEqualTo("value1");
    }

    @Test
    public void can_not_override_offer_attribute_from_offer_attribute_if_score_is_not_quote() {
        SelectedAttributes selectedOffer1Context = new SelectedAttributes("offer1", null, initSelectedAttributes("attr1", "value1"));
        SelectedAttributes selectedOffer2Context = new SelectedAttributes("offer2", null, initSelectedAttributes("attr2", "value2"));

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader("offer1", null, "attr1", "offer2", null, "attr2", RuleOperatorEnum.EXISTS);

        ReplacementResult result = new ReplacementRulesExecutor(false).execute(Optional.empty(), Optional.of(selectedOffer1Context), asList(selectedOffer1Context, selectedOffer2Context), singletonList(commercialRuleHeader));

        assertThat(result.getSelectedOfferAttributes().get("attr1")).isEqualTo("value1");
    }

    @Test
    public void can_not_replace_product_attribute_from_other_offer_product_attribute_if_scope_not_quote() {
        SelectedAttributes selectedProduct1Context = new SelectedAttributes("offer1", "product1", initSelectedAttributes("attr1", "value1"));
        SelectedAttributes selectedProduct2Context = new SelectedAttributes("offer2", "product2", initSelectedAttributes("attr1", "value2"));

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader("offer1", "product1", "attr1", "offer2", "product2", "attr1", RuleOperatorEnum.EXISTS);

        ReplacementResult result = new ReplacementRulesExecutor(false).execute(Optional.of(selectedProduct1Context), Optional.empty(), asList(selectedProduct1Context, selectedProduct2Context), singletonList(commercialRuleHeader));

        assertThat(result.getSelectedProductAttributes().get("attr1")).isEqualTo("value1");
    }

    @Test
    public void can_replace_product_attribute_from_other_offer_product_attribute_if_scope_is_quote() {
        SelectedAttributes selectedProduct1Context = new SelectedAttributes("offer1", "product1", initSelectedAttributes("attr1", "value1"));
        SelectedAttributes selectedProduct2Context = new SelectedAttributes("offer2", "product2", initSelectedAttributes("attr1", "value2"));

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader("offer1", "product1", "attr1", "offer2", "product2", "attr1", RuleOperatorEnum.EXISTS);

        ReplacementResult result = new ReplacementRulesExecutor(true).execute(Optional.of(selectedProduct1Context), Optional.empty(), asList(selectedProduct1Context, selectedProduct2Context), singletonList(commercialRuleHeader));

        assertThat(result.getSelectedProductAttributes().get("attr1")).isEqualTo("value2");
    }

    @Test
    public void given_no_condition_upon_the_rule_target_offer_and_scope_is_not_quote_should_replace_target_attribute_by_source_attributes_on_other_offer() {
        SelectedAttributes selectedProduct1Context = new SelectedAttributes("offer1", "product1", initSelectedAttributes("attr1", "value1"));
        SelectedAttributes selectedProduct2Context = new SelectedAttributes("offer2", "product2", initSelectedAttributes("attr1", "value2"));

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader(null, "product1", "attr1", "offer2", "product2", "attr1", RuleOperatorEnum.EXISTS);

        ReplacementResult result = new ReplacementRulesExecutor(true).execute(Optional.of(selectedProduct1Context), Optional.empty(), asList(selectedProduct1Context, selectedProduct2Context), asList(commercialRuleHeader));

        assertThat(result.getSelectedProductAttributes().get("attr1")).isEqualTo("value2");
    }

    @Test
    public void given_no_condition_upon_the_rule_target_offer_and_scope_is_not_quote_should_not_replace_target_attribute_by_source_attributes_of_other_offer() {
        SelectedAttributes selectedProduct1Context = new SelectedAttributes("offer1", "product1", initSelectedAttributes("attr1", "value1"));
        SelectedAttributes selectedProduct2Context = new SelectedAttributes("offer2", "product2", initSelectedAttributes("attr1", "value2"));

        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader(null, "product1", "attr1", "offer2", "product2", "attr1", RuleOperatorEnum.EXISTS);

        ReplacementResult result = new ReplacementRulesExecutor(false).execute(Optional.of(selectedProduct1Context), Optional.empty(), asList(selectedProduct1Context, selectedProduct2Context), asList(commercialRuleHeader));

        assertThat(result.getSelectedProductAttributes().get("attr1")).isEqualTo("value1");
    }

    @Test
    public void given_no_condition_on_target_offer_should_replace_attributes_of_products_within_the_same_offer() {
        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader(null, "product1", "attr1",
                "offer1", "product2", "attr1", RuleOperatorEnum.EXISTS);

        SelectedAttributes selectedProduct1Context = new SelectedAttributes("offer1", "product1", initSelectedAttributes("attr1", "value1"));
        SelectedAttributes selectedProduct2Context = new SelectedAttributes("offer1", "product2", initSelectedAttributes("attr1", "value2"));

        ReplacementResult result = new ReplacementRulesExecutor(false).execute(Optional.of(selectedProduct1Context), Optional.empty(), asList(selectedProduct1Context, selectedProduct2Context), asList(commercialRuleHeader));

        assertThat(result.getSelectedProductAttributes().get("attr1")).isEqualTo("value2");
        System.out.println(null instanceof String);
    }

    @Test(expected = BusinessApiException.class)
    public void replacement_commercial_rule_allow_only_exist_operator() {
        CommercialRuleHeader commercialRuleHeader = buildCommercialRuleHeader(null, "product1", "attr1",
                "offer1", "product2", "attr1", RuleOperatorEnum.EQUAL);

        SelectedAttributes selectedProduct1Context = new SelectedAttributes("offer1", "product1", initSelectedAttributes("attr1", "value1"));
        SelectedAttributes selectedProduct2Context = new SelectedAttributes("offer1", "product2", initSelectedAttributes("attr1", "value1"));

       new ReplacementRulesExecutor(false).execute(Optional.of(selectedProduct1Context), Optional.empty(), asList(selectedProduct1Context, selectedProduct2Context), asList(commercialRuleHeader));



    }

    private CommercialRuleHeader buildCommercialRuleHeader(String targetOfferTemplateCode, String targetProductCode, String targetAttributeCode, String sourceOfferTemplateCode, String sourceProductCode, String sourceAttributeCode, RuleOperatorEnum lineOperator) {
        CommercialRuleHeader commercialRuleHeader = createCommercialRuleHeader(targetOfferTemplateCode, targetProductCode, createAttribute(targetAttributeCode));
        CommercialRuleItem item = createCommercialRuleItem(OperatorEnum.AND, singletonList(createCommercialRuleLine(sourceOfferTemplateCode, sourceProductCode, createAttribute(sourceAttributeCode), lineOperator)));
        commercialRuleHeader.setCommercialRuleItems(singletonList(item));
        return commercialRuleHeader;
    }


    private CommercialRuleItem createCommercialRuleItem(OperatorEnum operator, List<CommercialRuleLine> lines) {
        CommercialRuleItem commercialRuleItem = new CommercialRuleItem();
        commercialRuleItem.setOperator(operator);
        commercialRuleItem.setCommercialRuleLines(lines);
        return commercialRuleItem;
    }

    private CommercialRuleLine createCommercialRuleLine(String sourceOfferTemplateCode, String sourceProductCode, Attribute sourceAttribute, RuleOperatorEnum ruleOperator) {
        CommercialRuleLine commercialRuleLine = new CommercialRuleLine();
        commercialRuleLine.setSourceAttribute(sourceAttribute);
        if(sourceProductCode != null){
            Product sourceProduct = new Product();
            sourceProduct.setCode(sourceProductCode);
            commercialRuleLine.setSourceProduct(sourceProduct);
        }
        if(sourceOfferTemplateCode != null) {
            OfferTemplate sourceOfferTemplate = new OfferTemplate();
            sourceOfferTemplate.setCode(sourceOfferTemplateCode);
            commercialRuleLine.setSourceOfferTemplate(sourceOfferTemplate);
        }
        commercialRuleLine.setOperator(ruleOperator);
        return commercialRuleLine;
    }

    private CommercialRuleHeader createCommercialRuleHeader(String targetOfferTemplateCode, String targetProductCode, Attribute attribute) {
        CommercialRuleHeader commercialRuleHeader = new CommercialRuleHeader();
        commercialRuleHeader.setTargetAttribute(attribute);
        if(targetProductCode != null) {
            Product targetProduct = new Product();
            targetProduct.setCode(targetProductCode);
            commercialRuleHeader.setTargetProduct(targetProduct);
        }
        if(targetOfferTemplateCode != null) {
            OfferTemplate offerTemplate = new OfferTemplate();
            offerTemplate.setCode(targetOfferTemplateCode);
            commercialRuleHeader.setTargetOfferTemplate(offerTemplate);
        }
        commercialRuleHeader.setRuleType(RuleTypeEnum.REPLACEMENT);
        commercialRuleHeader.setScopeType(ScopeTypeEnum.QUOTE_OFFER);
        return commercialRuleHeader;
    }

    private Attribute createAttribute(String attributeCode) {
        Attribute attribute = new Attribute();
        attribute.setCode(attributeCode);
        return attribute;
    }

    private LinkedHashMap<String, Object> initSelectedAttributes(String attributecode, Object attributeValue) {
        LinkedHashMap<String, Object> selectedProductAttributes = new LinkedHashMap<>();
        selectedProductAttributes.put(attributecode, attributeValue);
        return selectedProductAttributes;
    }
}
