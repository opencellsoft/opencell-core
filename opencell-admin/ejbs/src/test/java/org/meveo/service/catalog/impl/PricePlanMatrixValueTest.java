package org.meveo.service.catalog.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.catalog.ColumnTypeEnum;
import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.QuoteAttribute;
import org.meveo.model.cpq.enums.AttributeTypeEnum;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PricePlanMatrixValueTest {
	 
	@Mock
	private ParamBeanFactory paramBeanFactory;

	@Mock
	private ParamBean paramBean;
	 
	 @Before
	 public void setUp() {
		when(paramBeanFactory.getInstance()).thenReturn(paramBean);
		when(paramBean.getProperty("attribute.multivalues.separator", ";")).thenReturn(";");
	 }

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


        assertThat(monthlyValue.match(Set.of(monthlyQuoteAttribute))).isTrue();

        QuoteAttribute annuallyQuoteAttribute = new QuoteAttribute();
        annuallyQuoteAttribute.setAttribute(stringAttribute);
        annuallyQuoteAttribute.setStringValue("Annually");

        assertThat(monthlyValue.match(Set.of(annuallyQuoteAttribute))).isFalse();

    }

    @Test
    public void list_text_matches_if_value_in_the_list() {
        Attribute listAttribute = createAttribute("list_attribute", AttributeTypeEnum.LIST_TEXT);

        QuoteAttribute monthlyQuoteAttribute = new QuoteAttribute();
        monthlyQuoteAttribute.setAttribute(listAttribute);
        monthlyQuoteAttribute.setStringValue("Monthly;Annually");

        PricePlanMatrixColumn bcColumn = createColumn(listAttribute, ColumnTypeEnum.String);

        PricePlanMatrixValue monthlyValue = new PricePlanMatrixValue();
        monthlyValue.setPricePlanMatrixColumn(bcColumn);
        monthlyValue.setStringValue("Monthly");

        assertThat(monthlyValue.match(Set.of(monthlyQuoteAttribute))).isTrue();
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
