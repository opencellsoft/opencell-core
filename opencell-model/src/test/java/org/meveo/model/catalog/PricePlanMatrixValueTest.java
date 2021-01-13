package org.meveo.model.catalog;

import org.junit.Test;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PricePlanMatrixValueTest {

    @Test
    public void string_values_matches_if_equals() {
        Attribute stringAttribute = createAttribute("string_attribute", AttributeTypeEnum.TEXT);

        QuoteAttribute monthlyQuoteAttribute = new QuoteAttribute();
        monthlyQuoteAttribute.setAttribute(stringAttribute);
        monthlyQuoteAttribute.setStringValue("Monthly");

        PricePlanMatrixColumn bcColumn = createColumn(stringAttribute, ColumnTypeEnum.String);

        PricePlanMatrixValue monthlyValue = new PricePlanMatrixValue();
        monthlyValue.setPricePlanMatrixColumn(bcColumn);
        monthlyValue.setStringValue("Monthly");


        assertThat(monthlyValue.match(List.of(monthlyQuoteAttribute))).isTrue();

        QuoteAttribute annuallyQuoteAttribute = new QuoteAttribute();
        annuallyQuoteAttribute.setAttribute(stringAttribute);
        annuallyQuoteAttribute.setStringValue("Annually");

        assertThat(monthlyValue.match(List.of(annuallyQuoteAttribute))).isFalse();

    }

    @Test
    public void list_text_matches_if_value_in_the_list() {
        Attribute listAttribute = createAttribute("list_attribute", AttributeTypeEnum.LIST_TEXT);

        QuoteAttribute monthlyQuoteAttribute = new QuoteAttribute();
        monthlyQuoteAttribute.setAttribute(listAttribute);
        monthlyQuoteAttribute.setStringValue("Monthly ; Annually");

        PricePlanMatrixColumn bcColumn = createColumn(listAttribute, ColumnTypeEnum.String);

        PricePlanMatrixValue monthlyValue = new PricePlanMatrixValue();
        monthlyValue.setPricePlanMatrixColumn(bcColumn);
        monthlyValue.setStringValue("Monthly");

        assertThat(monthlyValue.match(List.of(monthlyQuoteAttribute))).isTrue();
    }

    private PricePlanMatrixColumn createColumn(Attribute attribute, ColumnTypeEnum columnType) {
        PricePlanMatrixColumn bcColumn = new PricePlanMatrixColumn();
        bcColumn.setType(columnType);
        bcColumn.setAttribute(attribute);
        return bcColumn;
    }

    private Attribute createAttribute(String code, AttributeTypeEnum type) {
        Attribute attribute = new Attribute();
        attribute.setCode(code);
        attribute.setAttributeType(type);
        return attribute;
    }
}
