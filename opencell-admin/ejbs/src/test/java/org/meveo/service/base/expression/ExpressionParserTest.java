package org.meveo.service.base.expression;

import org.junit.Test;
import org.meveo.service.base.expressions.ExpressionParser;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionParserTest {

    @Test
    public void default_expression_is_equal() {
        ExpressionParser expressionParser = getExpression("");

        assertThat(expressionParser.getCondition()).isEqualTo("eq");
        assertThat(expressionParser.getFieldName()).isEqualTo("");
        assertThat(expressionParser.getFieldName2()).isNull();
        assertThat(expressionParser.getAllFields()).isEmpty();
    }

    @Test
    public void one_word_expression_mean_eq_condition_with_one_field() {
        ExpressionParser expressionParser = getExpression("condition");
        assertThat(expressionParser.getCondition()).isEqualTo("eq");
        assertThat(expressionParser.getFieldName()).isEqualTo("condition");
        assertThat(expressionParser.getFieldName2()).isNull();
        assertThat(expressionParser.getAllFields()).isEmpty();
    }

    @Test
    public void two_words_mean_custom_expression_with_one_field() {
        ExpressionParser expressionParser = getExpression("condition field");
        assertThat(expressionParser.getCondition()).isEqualTo("condition");
        assertThat(expressionParser.getFieldName()).isEqualTo("field");
        assertThat(expressionParser.getFieldName2()).isNull();
        assertThat(expressionParser.getAllFields().length).isEqualTo(1);
        assertThat(expressionParser.getAllFields()).contains("field");
    }

    @Test
    public void three_words_mean_custom_expression_with_2_field() {
        ExpressionParser expressionParser = getExpression("condition field1 field2");
        assertThat(expressionParser.getCondition()).isEqualTo("condition");
        assertThat(expressionParser.getFieldName()).isEqualTo("field1");
        assertThat(expressionParser.getFieldName2()).isEqualTo("field2");
        assertThat(expressionParser.getAllFields().length).isEqualTo(2);
        assertThat(expressionParser.getAllFields()).contains("field1", "field2");
    }

    @Test
    public void all_other_fields_are_set_on_all_fields() {
        ExpressionParser expressionParser = getExpression("condition field1 field2 field3");
        assertThat(expressionParser.getCondition()).isEqualTo("condition");
        assertThat(expressionParser.getFieldName()).isEqualTo("field1");
        assertThat(expressionParser.getFieldName2()).isEqualTo("field2");
        assertThat(expressionParser.getAllFields().length).isEqualTo(3);
        assertThat(expressionParser.getAllFields()).contains("field1", "field2", "field3");
    }

    private ExpressionParser getExpression(String expressionLine) {
        return new ExpressionParser(expressionLine.split(" "));
    }
}
