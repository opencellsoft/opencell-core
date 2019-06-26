package org.meveo.model.admin;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Custom generic entity code.
 *
 * @author Abdellatif BARI.
 * @since 7.0
 */
@Entity
@Cacheable
@ExportIdentifier("entityClass")
@Table(name = "adm_custom_generic_entity_code")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_custom_generic_entity_code_seq"), })

public class CustomGenericEntityCode extends AuditableEntity {

    private static final long serialVersionUID = 84222776645282176L;

    public CustomGenericEntityCode() {
    }

    @Column(name = "entity_class", length = 255)
    @Size(max = 255)
    @NotNull
    private String entityClass;

    @Column(name = "code_el", nullable = false, length = 255)
    @Size(max = 255, min = 1)
    @NotNull
    private String codeEL;

    @Column(name = "sequence_size")
    private Integer sequenceSize = 9;

    @Column(name = "sequence_current_value")
    private Long sequenceCurrentValue = 0L;

    /**
     * @return the entityClass
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass the entityClass to set
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return the codeEL
     */
    public String getCodeEL() {
        return codeEL;
    }

    /**
     * @param codeEL the codeEL to set
     */
    public void setCodeEL(String codeEL) {
        this.codeEL = codeEL;
    }

    /**
     * @return the sequenceSize
     */
    public Integer getSequenceSize() {
        return sequenceSize;
    }

    /**
     * @param sequenceSize the sequenceSize to set
     */
    public void setSequenceSize(Integer sequenceSize) {
        this.sequenceSize = sequenceSize;
    }

    /**
     * @return the sequenceCurrentValue
     */
    public Long getSequenceCurrentValue() {
        return sequenceCurrentValue;
    }

    /**
     * @param sequenceCurrentValue the sequenceCurrentValue to set
     */
    public void setSequenceCurrentValue(Long sequenceCurrentValue) {
        this.sequenceCurrentValue = sequenceCurrentValue;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((entityClass == null) ? 0 : entityClass.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomGenericEntityCode other = (CustomGenericEntityCode) obj;
        if (entityClass == null) {
            if (other.entityClass != null)
                return false;
        } else if (!entityClass.equals(other.entityClass))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CustomGenericEntityCode [entityClass=" + entityClass + ", codeEL=" + codeEL + ", sequenceSize=" + sequenceSize + ", sequenceCurrentValue=" + sequenceCurrentValue + "]";
    }

}
