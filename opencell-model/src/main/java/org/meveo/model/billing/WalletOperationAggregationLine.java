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
import javax.validation.constraints.NotNull;
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
    @NotNull
    private boolean required;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aggregation_matrix_id")
    private WalletOperationAggregationMatrix aggregationMatrix;

    public String getField() {
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

    public WalletOperationAggregationMatrix getAggregationMatrix() {
        return aggregationMatrix;
    }

    public void setAggregationMatrix(WalletOperationAggregationMatrix aggregationMatrix) {
        this.aggregationMatrix = aggregationMatrix;
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
