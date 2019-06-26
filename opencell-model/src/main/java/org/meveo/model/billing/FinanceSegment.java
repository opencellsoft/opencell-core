package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

/**
 * @author Edward P. Legaspi
 * @since 8 Apr 2018
 **/
@Entity
@Table(name = "billing_finance_segment", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_finance_segment_seq"), })
public class FinanceSegment extends BusinessEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -8609748543144401670L;

    /**
     * Any customizable entity className or Custom Entity
     */
    @NotNull
    @Column(name = "object_class", length = 100, nullable = false)
    private String objectClass;

    /**
     * The name of a field in the object class
     */
    @NotNull
    @Column(name = "field", length = 100, nullable = false)
    private String field;

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

}
