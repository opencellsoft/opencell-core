package org.meveo.service.base.expression;

import org.junit.Test;
import org.meveo.service.base.expressions.Expression;

import static org.assertj.core.api.Assertions.assertThat;

public class ExpressionTest {

    @Test
    public void default_expression_is_equal() {
        Expression expression = getExpression("");

        assertThat(expression.getCondition()).isEqualTo("eq");
        assertThat(expression.getFieldName()).isEqualTo("");
        assertThat(expression.getFieldName2()).isNull();
        assertThat(expression.getAllFields()).isEmpty();
    }

    @Test
    public void one_word_expression_mean_eq_condition_with_one_field() {
        Expression expression = getExpression("condition");
        assertThat(expression.getCondition()).isEqualTo("eq");
        assertThat(expression.getFieldName()).isEqualTo("condition");
        assertThat(expression.getFieldName2()).isNull();
        assertThat(expression.getAllFields()).isEmpty();
    }

    @Test
    public void two_words_mean_custom_expression_with_one_field() {
        Expression expression = getExpression("condition field");
        assertThat(expression.getCondition()).isEqualTo("condition");
        assertThat(expression.getFieldName()).isEqualTo("field");
        assertThat(expression.getFieldName2()).isNull();
        assertThat(expression.getAllFields().length).isEqualTo(1);
        assertThat(expression.getAllFields()).contains("field");
    }

    @Test
    public void three_words_mean_custom_expression_with_2_field() {
        Expression expression = getExpression("condition field1 field2");
        assertThat(expression.getCondition()).isEqualTo("condition");
        assertThat(expression.getFieldName()).isEqualTo("field1");
        assertThat(expression.getFieldName2()).isEqualTo("field2");
        assertThat(expression.getAllFields().length).isEqualTo(2);
        assertThat(expression.getAllFields()).contains("field1", "field2");
    }

    @Test
    public void all_other_fields_are_set_on_all_fields() {
        Expression expression = getExpression("condition field1 field2 field3");
        assertThat(expression.getCondition()).isEqualTo("condition");
        assertThat(expression.getFieldName()).isEqualTo("field1");
        assertThat(expression.getFieldName2()).isEqualTo("field2");
        assertThat(expression.getAllFields().length).isEqualTo(3);
        assertThat(expression.getAllFields()).contains("field1", "field2", "field3");
    }

    private Expression getExpression(String expressionLine) {
        return new Expression(expressionLine.split(" "));
    }
}
