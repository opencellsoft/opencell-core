package org.meveo.model.billing;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Wallet operation Aggregation Line. Used to aggregate multiple Wo in one RatedTransaction.
 *
 * @author Khalid HORRI
 * @lastModifiedVersion 9.0
 */
@Entity
@Cacheable
@Table(name = "wo_aggregation_line")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wo_aggregation_line_seq"), })
public class WalletOperationAggregationLine extends AuditableEntity {

    /**
     * The wallet operation field's name
     */
    @Column(name = "field", length = 255)
    @Size(max = 255)
    private String field;
    /**
     * The action to be executed against the field. Example : sum(amountWithoutTax) for Empty and Key use the field value
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "action")
    private WalletOperationAggregationActionEnum action;

    /**
     *
     */
    @Column(name = "value", length = 255)
    @Size(max = 255)
    private String value;

    /**
     * Whether the aggregationLine is required or not.
     */
    @Column(name = "required")
    @Type(type = "numeric_boolean")
    private boolean required;

    /**
     * Whether the field is required or not.
     */
    @Column(name = "is_custom_field")
    @Type(type = "numeric_boolean")
    private boolean customField;

    /**
     * Function used in group by query clause.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "group_by")
    private GroupByAggregationFunctionEnum groupBy;

    /**
     * Paramter used in some group by function
     */
    @Column(name = "group_by_parameter", length = 25)
    @Size(max = 25)
    private String groupByParameter;

    /**
     * The alias used in the SQL query
     */
    @Column(name = "alias", length = 255)
    @Size(max = 255)
    private String alias;

    /**
     *
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wo_aggregation_settings_id")
    private WalletOperationAggregationSettings aggregationSettings;

    public String getField() {
        if (field != null) {
            field = field.trim();
        }
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public WalletOperationAggregationActionEnum getAction() {
        return action;
    }

    public void setAction(WalletOperationAggregationActionEnum action) {
        this.action = action;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public WalletOperationAggregationSettings getAggregationSettings() {
        return aggregationSettings;
    }

    public void setAggregationSettings(WalletOperationAggregationSettings aggregationSettings) {
        this.aggregationSettings = aggregationSettings;
    }

    public boolean isCustomField() {
        return customField;
    }

    public void setCustomField(boolean customField) {
        this.customField = customField;
    }

    public GroupByAggregationFunctionEnum getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(GroupByAggregationFunctionEnum groupBy) {
        this.groupBy = groupBy;
    }

    public String getGroupByParameter() {
        return groupByParameter;
    }

    public void setGroupByParameter(String groupByParameter) {
        this.groupByParameter = groupByParameter;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        WalletOperationAggregationLine that = (WalletOperationAggregationLine) o;
        return field.equals(that.field) && action == that.action && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), field, action, value);
    }
}
